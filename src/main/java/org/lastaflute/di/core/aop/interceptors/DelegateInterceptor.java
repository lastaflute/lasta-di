/*
 * Copyright 2015-2018 the original author or authors.
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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.core.aop.frame.MethodInvocation;
import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.exception.BeanMethodNotFoundException;
import org.lastaflute.di.helper.beans.factory.BeanDescFactory;
import org.lastaflute.di.util.LdiMethodUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class DelegateInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 3613140488663554089L;

    private Object target;
    private BeanDesc beanDesc;
    private final Map<String, String> methodNameMap = new HashMap<String, String>();

    public DelegateInterceptor() {
    }

    public DelegateInterceptor(Object target) {
        setTarget(target);
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
        beanDesc = BeanDescFactory.getBeanDesc(target.getClass());
    }

    public void addMethodNameMap(String methodName, String targetMethodName) {
        methodNameMap.put(methodName, targetMethodName);
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (target == null) {
            throw new EmptyRuntimeException("target");
        }
        Method method = invocation.getMethod();
        String methodName = method.getName();
        if (methodNameMap.containsKey(methodName)) {
            methodName = (String) methodNameMap.get(methodName);
        }
        if (!LdiMethodUtil.isAbstract(method)) {
            return invocation.proceed();
        } else if (beanDesc.hasMethod(methodName)) {
            return beanDesc.invoke(target, methodName, invocation.getArguments());
        } else {
            throw new BeanMethodNotFoundException(getTargetClass(invocation), methodName, invocation.getArguments());
        }
    }
}