/*
 * Copyright 2015-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.lastaflute.di.core.aop.javassist;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.lastaflute.di.core.aop.InterType;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class EnhancedClassGenerator extends AbstractGenerator {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String enhancedClassName; // not null
    protected CtClass enhancedCtClass; // not null after setup but null allowed after toClass()

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public EnhancedClassGenerator(final ClassPool classPool, final Class<?> targetClass, final String enhancedClassName) {
        super(classPool, targetClass);
        this.enhancedClassName = enhancedClassName;

        setupClass();
        setupInterface();
        setupConstructor();
    }

    // ===================================================================================
    //                                                                        Set up Basic
    //                                                                        ============
    // #for_now jflute can these methods be protected? (2024/06/18)
    public void setupClass() {
        final Class<?> superClass = (targetClass.isInterface()) ? Object.class : targetClass;
        enhancedCtClass = createCtClass(enhancedClassName, superClass);
    }

    public void setupInterface() {
        if (targetClass.isInterface()) {
            setInterface(enhancedCtClass, targetClass);
        }
    }

    public void setupConstructor() {
        final Constructor<?>[] constructors = targetClass.getDeclaredConstructors();
        if (constructors.length == 0) {
            createDefaultConstructor(enhancedCtClass);
        } else { // has explicit constructor
            for (int i = 0; i < constructors.length; ++i) {
                final int modifier = constructors[i].getModifiers();
                final Package pkg = targetClass.getPackage();
                if (canCreateExplicitConstructor(modifier, pkg)) {
                    createConstructor(enhancedCtClass, constructors[i]);
                }
            }
        }
    }

    protected boolean canCreateExplicitConstructor(final int modifier, final Package pkg) {
        return Modifier.isPublic(modifier) || Modifier.isProtected(modifier)
                || (!Modifier.isPrivate(modifier) && !targetClass.getName().startsWith("java.") && (pkg == null || !pkg.isSealed()));
    }

    // ===================================================================================
    //                                                                       Create Method
    //                                                                       =============
    public void createTargetMethod(final Method method, final String methodInvocationClassName) {
        final String methodSource = createTargetMethodSource(method, methodInvocationClassName);
        createMethod(enhancedCtClass, method, methodSource);
    }

    public void createInvokeSuperMethod(final Method method, final String invokeSuperMethodName) {
        final String methodSource = createInvokeSuperMethodSource(method);
        createMethod(enhancedCtClass, method.getModifiers(), method.getReturnType(), invokeSuperMethodName, method.getParameterTypes(),
                method.getExceptionTypes(), methodSource);
    }

    // ===================================================================================
    //                                                                    Apply Inter Type
    //                                                                    ================
    // various members in class e.g. getter, setter
    public void applyInterType(final InterType interType) {
        interType.introduce(targetClass, enhancedCtClass);
    }

    // ===================================================================================
    //                                                                    Define the Class
    //                                                                    ================
    public Class<?> toClass(final ClassLoader classLoader) { // with closing
        final Class<?> clazz = toClass(classLoader, enhancedCtClass);
        enhancedCtClass.detach();
        enhancedCtClass = null;
        return clazz;
    }

    // ===================================================================================
    //                                                                  Expression Utility
    //                                                                  ==================
    public static String createTargetMethodSource(final Method method, final String methodInvocationClassName) {
        final StringBuffer sb = new StringBuffer(200);
        sb.append("Object result = new ").append(methodInvocationClassName).append("(this, $args).proceed();");
        final Class<?> returnType = method.getReturnType();
        if (returnType.equals(void.class)) {
            sb.append("return;");
        } else if (returnType.isPrimitive()) {
            sb.append("return ($r) ((result == null) ? ");
            if (returnType.equals(boolean.class)) {
                sb.append("false : ");
            } else {
                sb.append("0 : ");
            }
            sb.append(fromObject(returnType, "result")).append(");");
        } else {
            sb.append("return ($r) result;");
        }
        String code = new String(sb);

        final Class<?>[] exceptionTypes = normalizeExceptionTypes(method.getExceptionTypes());
        if (exceptionTypes.length != 1 || !exceptionTypes[0].equals(Throwable.class)) {
            code = aroundTryCatchBlock(exceptionTypes, code);
        }

        return "{" + code + "}";
    }

    public static String createInvokeSuperMethodSource(final Method method) {
        return "{" + "return ($r) super." + method.getName() + "($$);" + "}";
    }

    public static Class<?>[] normalizeExceptionTypes(final Class<?>[] exceptionTypes) {
        final List<Class<?>> list = new LinkedList<Class<?>>();
        outer: for (int i = 0; i < exceptionTypes.length; ++i) {
            final Class<?> currentException = exceptionTypes[i];
            final ListIterator<Class<?>> it = list.listIterator();
            while (it.hasNext()) {
                final Class<?> comparisonException = (Class<?>) it.next();
                if (comparisonException.isAssignableFrom(currentException)) {
                    continue outer;
                }
                if (currentException.isAssignableFrom(comparisonException)) {
                    it.remove();
                }
            }
            list.add(currentException);
        }
        return (Class[]) list.toArray(new Class[list.size()]);
    }

    public static String aroundTryCatchBlock(final Class<?>[] exceptionTypes, final String code) {
        final TryBlockSupport tryBlock = new TryBlockSupport(code);

        boolean needRuntimeException = true;
        boolean needError = true;
        for (int i = 0; i < exceptionTypes.length; ++i) {
            final Class<?> exceptionType = exceptionTypes[i];
            tryBlock.addCatchBlock(exceptionType, "throw e;");
            if (exceptionType.equals(RuntimeException.class)) {
                needRuntimeException = false;
            }
            if (exceptionType.equals(Error.class)) {
                needError = false;
            }
        }

        if (needRuntimeException) {
            tryBlock.addCatchBlock(RuntimeException.class, "throw e;");
        }
        if (needError) {
            tryBlock.addCatchBlock(Error.class, "throw e;");
        }
        tryBlock.addCatchBlock(Throwable.class, "throw new java.lang.reflect.UndeclaredThrowableException(e);");

        return tryBlock.getSourceCode();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getEnhancedClassName() {
        return enhancedClassName;
    }

    public CtClass getEnhancedCtClass() { // null allowed depending on call timing
        return enhancedCtClass;
    }
}