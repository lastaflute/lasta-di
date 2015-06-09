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
package org.lastaflute.di.core.aop.interceptors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.aop.frame.MethodInvocation;
import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.exception.MethodNotFoundRuntimeException;
import org.lastaflute.di.helper.beans.factory.BeanDescFactory;
import org.lastaflute.di.util.LdiMethodUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class PrototypeDelegateInterceptor extends AbstractInterceptor {
    private static final long serialVersionUID = -6138917007687873314L;

    private LaContainer container;

    private String targetName;

    private BeanDesc beanDesc;

    private Map methodNameMap = new HashMap();

    /**
     * @param container
     */
    public PrototypeDelegateInterceptor(final LaContainer container) {
        this.container = container;
    }

    /**
     * @return 
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * @param targetName
     */
    public void setTargetName(final String targetName) {
        this.targetName = targetName;
    }

    /**
     * @param methodName
     * @param targetMethodName
     */
    public void addMethodNameMap(final String methodName, final String targetMethodName) {
        methodNameMap.put(methodName, targetMethodName);
    }

    /**
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (targetName == null) {
            throw new EmptyRuntimeException("targetName");
        }

        final Method method = invocation.getMethod();
        if (!LdiMethodUtil.isAbstract(method)) {
            return invocation.proceed();
        }

        String methodName = method.getName();
        if (methodNameMap.containsKey(methodName)) {
            methodName = (String) methodNameMap.get(methodName);
        }

        final Object target = container.getComponent(targetName);
        if (beanDesc == null) {
            beanDesc = BeanDescFactory.getBeanDesc(target.getClass());
        }

        if (!beanDesc.hasMethod(methodName)) {
            throw new MethodNotFoundRuntimeException(getTargetClass(invocation), methodName, invocation.getArguments());
        }

        return beanDesc.invoke(target, methodName, invocation.getArguments());
    }
}