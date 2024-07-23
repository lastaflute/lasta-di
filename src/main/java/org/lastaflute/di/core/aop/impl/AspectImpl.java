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
package org.lastaflute.di.core.aop.impl;

import java.io.Serializable;

import org.lastaflute.di.core.aop.Aspect;
import org.lastaflute.di.core.aop.Pointcut;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AspectImpl implements Aspect, Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 0L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final MethodInterceptor methodInterceptor; // not null
    private Pointcut pointcut; // setter-mutable

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AspectImpl(MethodInterceptor methodInterceptor) {
        this(methodInterceptor, null);
    }

    public AspectImpl(MethodInterceptor methodInterceptor, Pointcut pointcut) {
        this.methodInterceptor = methodInterceptor;
        this.pointcut = pointcut;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }
}
