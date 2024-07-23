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

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.core.aop.frame.MethodInvocation;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class MockInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 6438214603532050462L;

    private final Map<String, Object> returnValueMap = new HashMap<String, Object>();
    private final Map<String, Throwable> throwableMap = new HashMap<String, Throwable>();
    private final Map<String, Boolean> invokedMap = new HashMap<String, Boolean>();
    private final Map<String, Object[]> argsMap = new HashMap<String, Object[]>();

    public MockInterceptor() {
    }

    public MockInterceptor(Object value) {
        setReturnValue(value);
    }

    public boolean isInvoked(String methodName) {
        return invokedMap.containsKey(methodName);
    }

    public Object[] getArgs(String methodName) {
        return (Object[]) argsMap.get(methodName);
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        final String methodName = invocation.getMethod().getName();
        invokedMap.put(invocation.getMethod().getName(), Boolean.TRUE);
        argsMap.put(methodName, invocation.getArguments());
        if (throwableMap.containsKey(methodName)) {
            throw (Throwable) throwableMap.get(methodName);
        } else if (throwableMap.containsKey(null)) {
            throw (Throwable) throwableMap.get(null);
        } else if (returnValueMap.containsKey(methodName)) {
            return returnValueMap.get(methodName);
        } else {
            return returnValueMap.get(null);
        }
    }

    public void setReturnValue(Object returnValue) {
        setReturnValue(null, returnValue);
    }

    public void setReturnValue(String methodName, Object returnValue) {
        returnValueMap.put(methodName, returnValue);
    }

    public void setThrowable(Throwable throwable) {
        setThrowable(null, throwable);
    }

    public void setThrowable(String methodName, Throwable throwable) {
        throwableMap.put(methodName, throwable);
    }
}