/*
 * Copyright 2014-2015 the original author or authors.
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

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import org.lastaflute.di.core.aop.LaMethodInvocation;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiMethodUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class MethodInvocationClassGenerator extends AbstractGenerator {

    protected final String enhancedClassName;

    protected CtClass methodInvocationClass;

    /**
     * @param classPool
     * @param invocationClassName
     * @param targetClassName
     */
    public MethodInvocationClassGenerator(final ClassPool classPool, final String invocationClassName, final String targetClassName) {
        super(classPool);
        this.enhancedClassName = targetClassName;
        this.methodInvocationClass = getAndRenameCtClass(MethodInvocationTemplate.class, invocationClassName);
    }

    /**
     * @param targetMethod
     * @param invokeSuperMethodName
     */
    public void createProceedMethod(final Method targetMethod, final String invokeSuperMethodName) {
        final CtMethod method = getDeclaredMethod(methodInvocationClass, "proceed", null);
        setMethodBody(method, createProceedMethodSource(targetMethod, enhancedClassName, invokeSuperMethodName));
    }

    /**
     * @param classLoader
     * @return
     */
    public Class toClass(final ClassLoader classLoader) {
        final Class clazz = toClass(classLoader, methodInvocationClass);
        methodInvocationClass.detach();
        methodInvocationClass = null;
        return clazz;
    }

    /**
     * @param targetMethod
     * @param enhancedClassName
     * @param invokeSuperMethodName
     * @return 
     */
    public static String createProceedMethodSource(final Method targetMethod, final String enhancedClassName,
            final String invokeSuperMethodName) {
        final StringBuffer buf = new StringBuffer(1000);
        buf.append("{");
        buf.append("if (interceptorsIndex < interceptors.length) {");
        buf.append("return interceptors[interceptorsIndex++].invoke(this);");
        buf.append("}");
        buf.append(createReturnStatement(targetMethod, enhancedClassName, invokeSuperMethodName));
        buf.append("}");
        return new String(buf);
    }

    /**
     * @param targetMethod
     * @param enhancedClassName
     * @param invokeSuperMethodName
     * @return 
     */
    public static String createReturnStatement(final Method targetMethod, final String enhancedClassName, final String invokeSuperMethodName) {
        if (LdiMethodUtil.isAbstract(targetMethod)) {
            return createThrowStatement(targetMethod, enhancedClassName);
        }

        final String invokeSuper =
                "((" + enhancedClassName + ") target)." + invokeSuperMethodName + "("
                        + createArgumentString(targetMethod.getParameterTypes()) + ")";

        final Class returnType = targetMethod.getReturnType();
        if (returnType.equals(void.class)) {
            return invokeSuper + ";" + "return null;";
        }
        return "return " + toObject(returnType, invokeSuper) + ";";
    }

    /**
     * @param targetMethod
     * @param enhancedClassName
     * @return 
     */
    public static String createThrowStatement(final Method targetMethod, final String enhancedClassName) {
        return "throw new java.lang.NoSuchMethodError(\"" + enhancedClassName + "." + targetMethod.getName() + "("
                + createArgumentTypeString(targetMethod.getParameterTypes()) + ")\");";

    }

    /**
     * @param argTypes
     * @return 
     */
    public static String createArgumentString(final Class[] argTypes) {
        if (argTypes == null || argTypes.length == 0) {
            return "";
        }

        final StringBuffer buf = new StringBuffer(1000);
        for (int i = 0; i < argTypes.length; ++i) {
            buf.append(fromObject(argTypes[i], "arguments[" + i + "]")).append(", ");
        }
        buf.setLength(buf.length() - 2);
        return new String(buf);
    }

    /**
     * @param argTypes
     * @return 
     */
    public static String createArgumentTypeString(final Class[] argTypes) {
        if (argTypes == null || argTypes.length == 0) {
            return "";
        }

        final StringBuffer buf = new StringBuffer(1000);
        for (int i = 0; i < argTypes.length; ++i) {
            buf.append(LdiClassUtil.getSimpleClassName(argTypes[i])).append(", ");
        }
        buf.setLength(buf.length() - 2);
        return new String(buf);
    }

    public static class MethodInvocationTemplate implements LaMethodInvocation {
        private static Class targetClass;

        private static Method method;

        static MethodInterceptor[] interceptors;

        private static Map parameters;

        private Object target;

        private Object[] arguments;

        int interceptorsIndex;

        /**
         * @param target
         * @param arguments
         */
        public MethodInvocationTemplate(final Object target, final Object[] arguments) {
            this.target = target;
            this.arguments = arguments;
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

        public Object proceed() throws Throwable {
            return null;
        }
    }
}