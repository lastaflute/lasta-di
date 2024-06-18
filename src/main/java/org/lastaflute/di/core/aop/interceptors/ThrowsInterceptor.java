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
package org.lastaflute.di.core.aop.interceptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.core.aop.frame.MethodInvocation;
import org.lastaflute.di.helper.beans.exception.BeanMethodNotFoundException;
import org.lastaflute.di.util.LdiMethodUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class ThrowsInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 1L;
    public static final String METHOD_NAME = "handleThrowable";

    private final Map<Class<?>, Method> methodMap = new HashMap<Class<?>, Method>();

    public ThrowsInterceptor() {
        final Method[] methods = getClass().getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            if (LdiMethodUtil.isBridgeMethod(method) || LdiMethodUtil.isSyntheticMethod(method)) {
                continue;
            }
            if (isHandleThrowable(method)) {
                methodMap.put(method.getParameterTypes()[0], method);
            }
        }
        if (methodMap.size() == 0) {
            throw new BeanMethodNotFoundException(getClass(), METHOD_NAME, new Class[] { Throwable.class, MethodInvocation.class });
        }
    }

    private boolean isHandleThrowable(Method method) {
        return METHOD_NAME.equals(method.getName()) && method.getParameterTypes().length == 2
                && Throwable.class.isAssignableFrom(method.getParameterTypes()[0])
                && MethodInvocation.class.isAssignableFrom(method.getParameterTypes()[1]);
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable t) {
            Method method = getMethod(t);
            if (method != null) {
                try {
                    return method.invoke(this, new Object[] { t, invocation });
                } catch (InvocationTargetException ite) {
                    throw ite.getTargetException();
                }
            }
            throw t;
        }
    }

    private Method getMethod(Throwable cause) {
        Class<?> clazz = cause.getClass();
        Method method = (Method) methodMap.get(clazz);
        while (method == null && !clazz.equals(Throwable.class)) {
            clazz = clazz.getSuperclass();
            method = (Method) methodMap.get(clazz);
        }
        return method;
    }
}
