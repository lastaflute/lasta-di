/*
 * Copyright 2015-2016 the original author or authors.
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

import org.lastaflute.di.core.aop.Aspect;
import org.lastaflute.di.core.aop.Pointcut;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;

/**
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class AspectImpl implements Aspect, Serializable {

    static final long serialVersionUID = 0L;

    private MethodInterceptor methodInterceptor;

    private Pointcut pointcut;

    /**
     * @param methodInterceptor
     */
    public AspectImpl(MethodInterceptor methodInterceptor) {
        this(methodInterceptor, null);
    }

    /**
     * @param methodInterceptor
     * @param pointcut
     */
    public AspectImpl(MethodInterceptor methodInterceptor, Pointcut pointcut) {
        this.methodInterceptor = methodInterceptor;
        this.pointcut = pointcut;
    }

    /**
     * @see org.lastaflute.di.core.aop.Aspect#getMethodInterceptor()
     */
    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    /**
     * @see org.lastaflute.di.core.aop.Aspect#getPointcut()
     */
    public Pointcut getPointcut() {
        return pointcut;
    }

    /**
     * @see org.lastaflute.di.core.aop.Aspect#setPointcut(org.lastaflute.di.core.aop.Pointcut)
     */
    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

}
