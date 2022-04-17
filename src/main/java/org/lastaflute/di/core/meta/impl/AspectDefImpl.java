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
package org.lastaflute.di.core.meta.impl;

import org.lastaflute.di.core.aop.Aspect;
import org.lastaflute.di.core.aop.Pointcut;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.aop.impl.AspectImpl;
import org.lastaflute.di.core.meta.AspectDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AspectDefImpl extends ArgDefImpl implements AspectDef {

    private Pointcut pointcut;

    public AspectDefImpl() {
    }

    public AspectDefImpl(Pointcut pointcut) {
        setPointcut(pointcut);
    }

    public AspectDefImpl(MethodInterceptor interceptor) {
        setValue(interceptor);
    }

    public AspectDefImpl(MethodInterceptor interceptor, Pointcut pointcut) {
        setValue(interceptor);
        setPointcut(pointcut);
    }

    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    public Aspect getAspect() {
        final MethodInterceptor interceptor = (MethodInterceptor) getValue(Object.class);
        return new AspectImpl(interceptor, pointcut);
    }
}
