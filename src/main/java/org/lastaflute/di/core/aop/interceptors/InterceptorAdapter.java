/*
 * Copyright 2015-2020 the original author or authors.
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

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.aop.LaMethodInvocation;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.aop.frame.MethodInvocation;
import org.lastaflute.di.core.aop.impl.NestedMethodInvocation;
import org.lastaflute.di.util.LdiArrayUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class InterceptorAdapter extends AbstractInterceptor {

    private static final long serialVersionUID = 1L;

    protected LaContainer container;

    protected ComponentDef[] interceptorDefs = new ComponentDef[0];

    /**
     * @param container
     */
    public void setContainer(final LaContainer container) {
        this.container = container;
    }

    /**
     * @param interceptorNames
     */
    public void add(final String interceptorNames) {
        interceptorDefs = (ComponentDef[]) LdiArrayUtil.add(interceptorDefs, container.getComponentDef(interceptorNames));
    }

    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final MethodInterceptor[] interceptors = new MethodInterceptor[interceptorDefs.length];
        for (int i = 0; i < interceptors.length; ++i) {
            interceptors[i] = (MethodInterceptor) interceptorDefs[i].getComponent();
        }
        final MethodInvocation nestInvocation = new NestedMethodInvocation((LaMethodInvocation) invocation, interceptors);
        return nestInvocation.proceed();
    }
}