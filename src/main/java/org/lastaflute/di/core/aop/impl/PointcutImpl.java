/*
 * Copyright 2015-2022 the original author or authors.
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
package org.lastaflute.di.core.aop.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.lastaflute.di.core.aop.Pointcut;
import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.util.LdiMethodUtil;
import org.lastaflute.di.util.LdiModifierUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class PointcutImpl implements Pointcut, Serializable {

    private static final long serialVersionUID = 0L;

    private String[] methodNames;
    private Pattern[] patterns;
    private Method method;

    public PointcutImpl(Class<?> targetClass) throws EmptyRuntimeException {
        if (targetClass == null) {
            throw new EmptyRuntimeException("targetClass");
        }
        setMethodNames(getMethodNames(targetClass));
    }

    public PointcutImpl(String[] methodNames) throws EmptyRuntimeException {
        if (methodNames == null || methodNames.length == 0) {
            throw new EmptyRuntimeException("methodNames");
        }
        setMethodNames(methodNames);
    }

    public PointcutImpl(Method method) {
        this.method = method;
    }

    public boolean isApplied(Method targetMethod) {
        if (method != null) {
            return method.equals(targetMethod);
        }
        final String methodName = targetMethod.getName();
        for (int i = 0; i < patterns.length; ++i) {
            if (patterns[i].matcher(methodName).matches()) {
                return true;
            }
        }
        return false;
    }

    public String[] getMethodNames() {
        return methodNames;
    }

    private void setMethodNames(String[] methodNames) {
        this.methodNames = methodNames;
        patterns = new Pattern[methodNames.length];
        for (int i = 0; i < patterns.length; ++i) {
            patterns[i] = Pattern.compile(methodNames[i]);
        }
    }

    private static String[] getMethodNames(Class<?> targetClass) {
        final Set<String> methodNameSet = new HashSet<String>();
        if (targetClass.isInterface()) {
            addInterfaceMethodNames(methodNameSet, targetClass);
        }
        for (Class<?> clazz = targetClass; clazz != Object.class && clazz != null; clazz = clazz.getSuperclass()) {
            final Class<?>[] interfaces = clazz.getInterfaces();
            for (int i = 0; i < interfaces.length; ++i) {
                addInterfaceMethodNames(methodNameSet, interfaces[i]);
            }
        }
        if (methodNameSet.isEmpty()) {
            addClassMethodNames(methodNameSet, targetClass);
        }
        return (String[]) methodNameSet.toArray(new String[methodNameSet.size()]);

    }

    private static void addInterfaceMethodNames(Set<String> methodNameSet, Class<?> interfaceClass) {
        final Method[] methods = interfaceClass.getDeclaredMethods();
        for (int j = 0; j < methods.length; j++) {
            methodNameSet.add(methods[j].getName());
        }
        final Class<?>[] interfaces = interfaceClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            addInterfaceMethodNames(methodNameSet, interfaces[i]);
        }
    }

    private static void addClassMethodNames(Set<String> methodNameSet, Class<?> clazz) {
        final Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            if (LdiMethodUtil.isSyntheticMethod(method) || LdiMethodUtil.isBridgeMethod(method)) {
                continue;
            }
            if (LdiModifierUtil.isFinal(method)) {
                continue;
            }
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            methodNameSet.add(methods[i].getName());
        }
    }
}
