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

import java.io.Serializable;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ContainerConstants;
import org.lastaflute.di.core.aop.Aspect;
import org.lastaflute.di.core.aop.LaMethodInvocation;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.aop.frame.MethodInvocation;
import org.lastaflute.di.core.aop.impl.AspectImpl;
import org.lastaflute.di.core.aop.impl.PointcutImpl;
import org.lastaflute.di.core.aop.proxy.AopProxy;

/**
 * @author modified by jflute (originated in Seasar)
 * 
 */
public abstract class AbstractInterceptor implements MethodInterceptor, Serializable {

    static final long serialVersionUID = 0L;

    /**
     * @param proxyClass
     * @return
     */
    public Object createProxy(Class proxyClass) {
        Aspect aspect = new AspectImpl(this, new PointcutImpl(new String[] { ".*" }));
        return new AopProxy(proxyClass, new Aspect[] { aspect }).create();
    }

    /**
     * @param invocation
     * @return 
     */
    protected Class getTargetClass(MethodInvocation invocation) {
        return ((LaMethodInvocation) invocation).getTargetClass();
    }

    /**
     * @param invocation
     * @return 
     */
    protected ComponentDef getComponentDef(MethodInvocation invocation) {
        if (invocation instanceof LaMethodInvocation) {
            LaMethodInvocation impl = (LaMethodInvocation) invocation;
            return (ComponentDef) impl.getParameter(ContainerConstants.COMPONENT_DEF_NAME);
        }
        return null;
    }
}