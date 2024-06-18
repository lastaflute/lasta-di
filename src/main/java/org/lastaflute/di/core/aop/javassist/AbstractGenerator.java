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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Arrays;

import org.lastaflute.di.core.aop.intertype.PropertyInterType;
import org.lastaflute.di.core.exception.CannotDefineClassException;
import org.lastaflute.di.core.util.ClassPoolUtil;
import org.lastaflute.di.exception.CannotCompileRuntimeException;
import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.di.exception.IllegalAccessRuntimeException;
import org.lastaflute.di.exception.InvocationTargetRuntimeException;
import org.lastaflute.di.exception.NoSuchMethodRuntimeException;
import org.lastaflute.di.exception.NotFoundRuntimeException;
import org.lastaflute.di.helper.log.LaLogger;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.tiger.LdiReflectionUtil;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AbstractGenerator {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final LaLogger logger = LaLogger.getLogger(PropertyInterType.class);

    protected static final String DEFINE_CLASS_METHOD_NAME = "defineClass"; // of ClassLoader
    protected static final ProtectionDomain protectionDomain; // not null

    /**
     * Reflection to ClassLoader@defineClass() as private-accessible. (NotNull) <br>
     * but it may be not accessible when Java17 without add-opens option.
     */
    protected static final Method defineClassMethod;

    /** Reflection to MethodHandles@privateLookupIn() for java9 or later. (NullAllowed: when java8) */
    protected static final Method privateLookupInMethod;
    protected static final Method lookupDefineClassMethod;

    // static initializer
    static {
        protectionDomain = (ProtectionDomain) AccessController.doPrivileged(createAspectWeaverPrivilegedAction());
        defineClassMethod = (Method) AccessController.doPrivileged(createDefineClassPrivilegedAction());

        privateLookupInMethod = prepareMethodHandlesPrivateLookupInMethod();
        lookupDefineClassMethod = prepareMethodHandlesLookupDefineClassMethod();
    }

    // -----------------------------------------------------
    //                                       ClassLoader Way
    //                                       ---------------
    protected static PrivilegedAction<Object> createAspectWeaverPrivilegedAction() {
        return new PrivilegedAction<Object>() {
            public Object run() {
                return AspectWeaver.class.getProtectionDomain();
            }
        };
    }

    protected static PrivilegedAction<Object> createDefineClassPrivilegedAction() {
        return new PrivilegedAction<Object>() {
            public Object run() {
                return prepareClassLoaderDefineClassMethod();
            }
        };
    }

    protected static Method prepareClassLoaderDefineClassMethod() {
        final Class<ClassLoader> typeAsResource = ClassLoader.class;
        final String defineClassMethodName = DEFINE_CLASS_METHOD_NAME;
        final Class<?>[] paramTypes = new Class[] { String.class, byte[].class, int.class, int.class, ProtectionDomain.class };
        final Method method;
        try {
            final Class<?> typeByCurrentContext = LdiClassUtil.forName(typeAsResource.getName()); // for what? by jflute
            method = typeByCurrentContext.getDeclaredMethod(defineClassMethodName, paramTypes);
            try {
                method.setAccessible(true); // depends on add-opens option since java17
                logger.debug("ClassLoader@defineClass() can be called for AOP");
            } catch (RuntimeException e) {
                final String fqcn = e.getClass().getName();
                if ("java.lang.reflect.InaccessibleObjectException".equals(fqcn)) { // means java9 or later
                    // private access may not be allowed in the environment
                    // so attempt other ways later so ignore the exception here
                } else {
                    throw e;
                }
            }
        } catch (final NoSuchMethodException e) { // basically framework mistake
            throw new NoSuchMethodRuntimeException(typeAsResource, defineClassMethodName, paramTypes, e);
        }
        return method;
    }

    // -----------------------------------------------------
    //                                     MethodHandles Way
    //                                     -----------------
    // test this by copying to java9 or later environment
    protected static Method prepareMethodHandlesPrivateLookupInMethod() {
        final Class<?>[] argTypes = new Class[] { Class.class, MethodHandles.Lookup.class };
        final Method method;
        try {
            method = LdiReflectionUtil.getMethod(MethodHandles.class, "privateLookupIn", argTypes);
        } catch (NoSuchMethodRuntimeException ignored) { // when java8, basically no way (already checked)
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("MethodHandles@privateLookupIn() can be called for AOP: method=" + method);
        }
        return method;
    }

    protected static Method prepareMethodHandlesLookupDefineClassMethod() {
        final Class<?>[] argTypes = new Class[] { byte[].class };
        final Method method;
        try {
            method = LdiReflectionUtil.getMethod(MethodHandles.Lookup.class, "defineClass", argTypes);
        } catch (NoSuchMethodRuntimeException ignored) { // when java8, basically no way (already checked)
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("MethodHandles.Lookup@defineClass() can be called for AOP: method=" + method);
        }
        return method;
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ClassPool classPool; // not null
    protected final Class<?> targetClass; // before enhancement, not null

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected AbstractGenerator(final ClassPool classPool, Class<?> targetClass) {
        this.classPool = classPool;
        this.targetClass = targetClass;
    }

    // ===================================================================================
    //                                                                      Class Handling
    //                                                                      ==============
    protected CtClass toCtClass(final Class<?> clazz) {
        return ClassPoolUtil.toCtClass(classPool, clazz);
    }

    protected CtClass toCtClass(final String className) {
        return ClassPoolUtil.toCtClass(classPool, className);
    }

    protected CtClass[] toCtClassArray(final String[] classNames) {
        return ClassPoolUtil.toCtClassArray(classPool, classNames);
    }

    protected CtClass[] toCtClassArray(final Class<?>[] classes) {
        return ClassPoolUtil.toCtClassArray(classPool, classes);
    }

    protected CtClass createCtClass(final String name) {
        return ClassPoolUtil.createCtClass(classPool, name);
    }

    protected CtClass createCtClass(final String name, final Class<?> superClass) {
        return ClassPoolUtil.createCtClass(classPool, name, superClass);
    }

    protected CtClass createCtClass(final String name, final CtClass superClass) {
        return ClassPoolUtil.createCtClass(classPool, name, superClass);
    }

    protected CtClass getAndRenameCtClass(final Class<?> orgClass, final String newName) {
        return getAndRenameCtClass(LdiClassUtil.getSimpleClassName(orgClass), newName);
    }

    protected CtClass getAndRenameCtClass(final String orgName, final String newName) {
        try {
            return classPool.getAndRename(orgName, newName);
        } catch (final NotFoundException e) {
            throw new NotFoundRuntimeException(e);
        }
    }

    // -----------------------------------------------------
    //                                      Define the Class
    //                                      ----------------
    public Class<?> toClass(final ClassLoader classLoader, final CtClass ctClass) {
        Class<?> enhancedClass = null;

        // java8 or add-opens option
        if (defineClassMethod.isAccessible()) {
            enhancedClass = invokeClassLoaderDefineClass(classLoader, ctClass);
        }
        if (enhancedClass != null) {
            return enhancedClass;
        }

        // private-access not allowed here e.g. java9 or later
        enhancedClass = invokeMethodHandlesDefineClass(classLoader, ctClass);
        if (enhancedClass != null) {
            return enhancedClass;
        }

        throw new CannotDefineClassException(buildCannotDefineClassMessage(classLoader, ctClass));
    }

    protected Class<?> invokeClassLoaderDefineClass(final ClassLoader classLoader, final CtClass ctClass) {
        try {
            final String className = ctClass.getName();
            final byte[] bytecode = convertCtClassToBytecode(ctClass);
            final Integer off = 0;
            final Integer len = bytecode.length;
            final Object[] args = new Object[] { className, bytecode, off, len, protectionDomain };
            return (Class<?>) defineClassMethod.invoke(classLoader, args);
        } catch (final IllegalAccessException e) {
            throw new IllegalAccessRuntimeException(ClassLoader.class, e);
        } catch (final InvocationTargetException e) {
            throw new InvocationTargetRuntimeException(ClassLoader.class, e);
        }
    }

    protected Class<?> invokeMethodHandlesDefineClass(final ClassLoader classLoader, final CtClass ctClass) {
        if (privateLookupInMethod == null) {
            return null;
        }
        // java9 or later here
        final MethodHandles.Lookup lookup =
                LdiReflectionUtil.invoke(privateLookupInMethod, null, new Object[] { targetClass, MethodHandles.lookup() });
        if (lookupDefineClassMethod == null) { // basically no way
            String msg = "privateLookupInMethod exists but lookupDefineClassMethod is null: " + privateLookupInMethod;
            throw new IllegalStateException(msg);
        }
        final byte[] bytecode = convertCtClassToBytecode(ctClass);
        return LdiReflectionUtil.invoke(lookupDefineClassMethod, lookup, new Object[] { bytecode });
    }

    protected byte[] convertCtClassToBytecode(final CtClass ctClass) {
        final byte[] bytecode;
        try {
            bytecode = ctClass.toBytecode();
        } catch (CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot convert the class to bytecode by Javassist.");
            br.addItem("CtClass");
            br.addElement(ctClass);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return bytecode;
    }

    protected String buildCannotDefineClassMessage(final ClassLoader classLoader, final CtClass ctClass) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Cannot define the class to class loader.");
        br.addItem("Advice");
        br.addElement("To define the class to class loader is required to enhance class.");
        br.addElement("But both ClassLoader way and MethodHandles way don't work in your environment.");
        br.addElement(" ClassLoader way: call ClassLoader@defineClass() by reflection private-access.");
        br.addElement(" MethodHandles way: call MethodHandles.Lookup@defineClass() since java9.");
        br.addElement("");
        br.addElement("The private-access to java.lang is disabled as default since java16");
        br.addElement("so MethodHandles way is implemented as secondary.");
        br.addElement("But maybe the way does not always work...");
        br.addElement("");
        br.addElement("java8: no problem, ClassLoader way");
        br.addElement("java9~15: basically no problem, ClassLoader way (permitted as default)");
        br.addElement("java16~: MethodHandles way or ClassLoader way by option");
        br.addElement("");
        br.addElement("If you MethodHandles way does not work in your environment (since java16)");
        br.addElement("consider 'add-opens' option of java command for ClassLoader way.");
        br.addItem("ClassLoader");
        br.addElement(classLoader);
        br.addItem("CtClass");
        br.addElement(ctClass);
        return br.buildExceptionMessage();
    }

    // ===================================================================================
    //                                                                  Interface Handling
    //                                                                  ==================
    protected void setInterface(final CtClass clazz, final Class<?> interfaceType) {
        clazz.setInterfaces(new CtClass[] { toCtClass(interfaceType) });
    }

    protected void setInterfaces(final CtClass clazz, final Class<?>[] interfaces) {
        clazz.setInterfaces(toCtClassArray(interfaces));
    }

    // ===================================================================================
    //                                                                Constructor Handling
    //                                                                ====================
    protected CtConstructor createDefaultConstructor(final Class<?> clazz) {
        return createDefaultConstructor(toCtClass(clazz));
    }

    protected CtConstructor createDefaultConstructor(final CtClass clazz) {
        try {
            final CtConstructor ctConstructor = CtNewConstructor.defaultConstructor(clazz);
            clazz.addConstructor(ctConstructor);
            return ctConstructor;
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot make or add the default constructor to the class by Javassist.");
            br.addItem("CtClass");
            br.addElement(clazz);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        }
    }

    protected CtConstructor createConstructor(final CtClass clazz, final Constructor<?> constructor) {
        return createConstructor(clazz, toCtClassArray(constructor.getParameterTypes()), toCtClassArray(constructor.getExceptionTypes()));
    }

    protected CtConstructor createConstructor(final CtClass clazz, final CtClass[] parameterTypes, final CtClass[] exceptionTypes) {
        try {
            final CtConstructor ctConstructor = CtNewConstructor.make(parameterTypes, exceptionTypes, clazz);
            clazz.addConstructor(ctConstructor);
            return ctConstructor;
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot make or add the constructor to the class by Javassist.");
            br.addItem("CtClass");
            br.addElement(clazz);
            br.addItem("parameterTypes");
            br.addElement(parameterTypes != null ? Arrays.asList(parameterTypes) : null);
            br.addItem("exceptionTypes");
            br.addElement(exceptionTypes != null ? Arrays.asList(exceptionTypes) : null);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        }
    }

    // ===================================================================================
    //                                                                     Method Handling
    //                                                                     ===============
    protected CtMethod getDeclaredMethod(final CtClass clazz, final String name, final CtClass[] argTypes) {
        try {
            return clazz.getDeclaredMethod(name, argTypes);
        } catch (final NotFoundException e) {
            throw new NotFoundRuntimeException(e);
        }
    }

    protected CtMethod createMethod(final CtClass clazz, final String src) {
        try {
            final CtMethod ctMethod = CtNewMethod.make(src, clazz);
            clazz.addMethod(ctMethod);
            return ctMethod;
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot make or add the method to the class by Javassist.");
            br.addItem("CtClass");
            br.addElement(clazz);
            br.addItem("Method Source");
            br.addElement(src);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        }
    }

    protected CtMethod createMethod(final CtClass clazz, final Method method, final String body) {
        return createMethod(clazz, method.getModifiers(), method.getReturnType(), method.getName(), method.getParameterTypes(),
                method.getExceptionTypes(), body);
    }

    protected CtMethod createMethod(final CtClass clazz, final int modifier, final Class<?> returnType, final String methodName,
            final Class<?>[] parameterTypes, final Class<?>[] exceptionTypes, final String body) {
        final int modifiers = modifier & ~(Modifier.ABSTRACT | Modifier.NATIVE);
        final CtClass returnCtClass = toCtClass(returnType);
        final CtClass[] paramCtClasses = toCtClassArray(parameterTypes);
        final CtClass[] expCtClasses = toCtClassArray(exceptionTypes);
        try {
            final CtMethod ctMethod = CtNewMethod.make(modifiers, returnCtClass, methodName, paramCtClasses, expCtClasses, body, clazz);
            clazz.addMethod(ctMethod);
            return ctMethod;
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot make or add the method to the class by Javassist.");
            br.addItem("CtClass");
            br.addElement(clazz);
            br.addItem("Modifiers");
            br.addElement(modifiers);
            br.addItem("Return Type");
            br.addElement(returnType);
            br.addItem("Method Name");
            br.addElement(methodName);
            br.addItem("Parameter Types");
            br.addElement(parameterTypes != null ? Arrays.asList(parameterTypes) : null);
            br.addItem("Exception Types");
            br.addElement(exceptionTypes != null ? Arrays.asList(exceptionTypes) : null);
            br.addItem("Method Body");
            br.addElement(body);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        }
    }

    protected void setMethodBody(final CtMethod method, final String src) {
        try {
            method.setBody(src);
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot set the body to the method by Javassist.");
            br.addItem("CtMethod");
            br.addElement(method);
            br.addItem("Method Source");
            br.addElement(src);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        }
    }

    // ===================================================================================
    //                                                                  Expression Utility
    //                                                                  ==================
    protected static String fromObject(final Class<?> type, final String expr) {
        if (type.equals(void.class) || type.equals(Object.class)) {
            return expr;
        }
        if (type.equals(boolean.class) || type.equals(char.class)) {
            final Class<?> wrapper = LdiClassUtil.getWrapperClass(type);
            return "((" + wrapper.getName() + ") " + expr + ")." + type.getName() + "Value()";
        }
        if (type.isPrimitive()) {
            return "((java.lang.Number) " + expr + ")." + type.getName() + "Value()";
        }
        return "(" + LdiClassUtil.getSimpleClassName(type) + ") " + expr;
    }

    protected static String toObject(final Class<?> type, final String expr) {
        if (type.isPrimitive()) {
            final Class<?> wrapper = LdiClassUtil.getWrapperClass(type);
            return "new " + wrapper.getName() + "(" + expr + ")";
        }
        return expr;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ClassPool getClassPool() {
        return classPool;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}