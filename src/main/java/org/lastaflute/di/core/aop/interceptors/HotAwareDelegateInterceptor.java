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
package org.lastaflute.di.core.aop.interceptors;

import java.lang.reflect.Method;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.aop.frame.MethodInvocation;
import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.util.LdiMethodUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class HotAwareDelegateInterceptor implements MethodInterceptor {

    protected LaContainer container;

    protected String targetName;

    public HotAwareDelegateInterceptor() {
    }

    /**
     * @param container
     */
    public void setContainer(LaContainer container) {
        this.container = container.getRoot();
    }

    /**
     * @param targetName
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (targetName == null) {
            throw new EmptyRuntimeException("targetName");
        }
        Method method = invocation.getMethod();
        if (!LdiMethodUtil.isAbstract(method)) {
            return invocation.proceed();
        } else {
            final Object target = container.getComponent(targetName);
            return LdiMethodUtil.invoke(method, target, invocation.getArguments());
        }
    }

}
