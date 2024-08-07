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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Map;

import org.lastaflute.di.core.aop.LaMethodInvocation;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiMethodUtil;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class MethodInvocationClassGenerator extends AbstractGenerator {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** e.g. org.docksidestage.Sea$$EnhancedByLastaDi$$2d66530f (NotNull) */
    protected final String enhancedClassName;

    /**
     * e.g. org.lastaflute.di.core.aop.javassist.OrgDocksidestageSea$$EnhancedByLastaDi$$2d66530f$$MethodInvocation$$mystic$$0
     * (NotNull: after setup but null allowed after toClass())
     */
    protected CtClass methodInvocationCtClass; // not null after setup but null allowed after toClass() 

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public MethodInvocationClassGenerator(final ClassPool classPool, final String enhancedClassName,
            final String methodInvocationClassName) {
        super(classPool, /*targetClass*/getTemplateClass()); // you can use it as targetClass after here
        this.enhancedClassName = enhancedClassName;
        this.methodInvocationCtClass = getAndRenameCtClass(targetClass, methodInvocationClassName);
    }

    // ===================================================================================
    //                                                                       Create Method
    //                                                                       =============
    public void createProceedMethod(final Method targetMethod, final String invokeSuperMethodName) {
        final CtMethod method = getDeclaredMethod(methodInvocationCtClass, "proceed", null);
        setMethodBody(method, createProceedMethodSource(targetMethod, enhancedClassName, invokeSuperMethodName));
    }

    // ===================================================================================
    //                                                                    Define the Class
    //                                                                    ================
    public Class<?> toClass(final ClassLoader classLoader) { // with closing
        final Class<?> clazz = toClass(classLoader, methodInvocationCtClass);
        methodInvocationCtClass.detach();
        methodInvocationCtClass = null;
        return clazz;
    }

    @Override
    protected boolean isInterfaceDefineEnabled() {
        return true; // read comment on super class
    }

    // ===================================================================================
    //                                                                  Expression Utility
    //                                                                  ==================
    public static String createProceedMethodSource(final Method targetMethod, final String enhancedClassName,
            final String invokeSuperMethodName) {
        final StringBuilder buf = new StringBuilder(1000);
        buf.append("{");
        buf.append("if (interceptorsIndex < interceptors.length) {");
        buf.append("return interceptors[interceptorsIndex++].invoke(this);");
        buf.append("}");
        buf.append(createReturnStatement(targetMethod, enhancedClassName, invokeSuperMethodName));
        buf.append("}");
        return new String(buf);
    }

    public static String createReturnStatement(final Method targetMethod, final String enhancedClassName,
            final String invokeSuperMethodName) {
        if (LdiMethodUtil.isAbstract(targetMethod)) {
            return createThrowStatement(targetMethod, enhancedClassName);
        }

        final String invokeSuper = "((" + enhancedClassName + ") target)." + invokeSuperMethodName + "("
                + createArgumentString(targetMethod.getParameterTypes()) + ")";

        final Class<?> returnType = targetMethod.getReturnType();
        if (returnType.equals(void.class)) {
            return invokeSuper + ";" + "return null;";
        }
        return "return " + toObject(returnType, invokeSuper) + ";";
    }

    public static String createThrowStatement(final Method targetMethod, final String enhancedClassName) {
        return "throw new java.lang.NoSuchMethodError(\"" + enhancedClassName + "." + targetMethod.getName() + "("
                + createArgumentTypeString(targetMethod.getParameterTypes()) + ")\");";

    }

    public static String createArgumentString(final Class<?>[] argTypes) {
        if (argTypes == null || argTypes.length == 0) {
            return "";
        }

        final StringBuilder sb = new StringBuilder(1000);
        for (int i = 0; i < argTypes.length; ++i) {
            sb.append(fromObject(argTypes[i], "arguments[" + i + "]")).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return new String(sb);
    }

    public static String createArgumentTypeString(final Class<?>[] argTypes) {
        if (argTypes == null || argTypes.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(1000);
        for (int i = 0; i < argTypes.length; ++i) {
            sb.append(LdiClassUtil.getSimpleClassName(argTypes[i])).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return new String(sb);
    }

    // ===================================================================================
    //                                                                      Template Class
    //                                                                      ==============
    public static Class<?> getTemplateClass() { // is enhancement target class
        return MethodInvocationTemplate.class;
    }

    // before enhancement
    public static class MethodInvocationTemplate implements LaMethodInvocation {

        private static Class<?> targetClass;
        private static Method method;
        static MethodInterceptor[] interceptors; // why not private?
        private static Map<String, Object> parameters;

        private Object target;
        private Object[] arguments;

        int interceptorsIndex;

        public MethodInvocationTemplate(final Object target, final Object[] arguments) {
            this.target = target;
            this.arguments = arguments;
        }

        public Object proceed() throws Throwable {
            return null;
        }

        public Class<?> getTargetClass() {
            return targetClass;
        }

        public Method getMethod() {
            return method;
        }

        public AccessibleObject getStaticPart() {
            return method;
        }

        public Object getParameter(String name) {
            if (parameters == null) {
                return null;
            }
            return parameters.get(name);
        }

        public Object getThis() {
            return target;
        }

        public Object[] getArguments() {
            return arguments;
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getEnhancedClassName() {
        return enhancedClassName;
    }

    public CtClass getMethodInvocationCtClass() { // null allowed depending on call timing
        return methodInvocationCtClass;
    }
}