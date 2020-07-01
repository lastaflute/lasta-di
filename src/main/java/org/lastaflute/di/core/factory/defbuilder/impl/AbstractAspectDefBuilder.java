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
package org.lastaflute.di.core.factory.defbuilder.impl;

import java.lang.reflect.Method;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.factory.AspectDefFactory;
import org.lastaflute.di.core.factory.defbuilder.AspectDefBuilder;
import org.lastaflute.di.core.meta.AspectDef;
import org.lastaflute.di.exception.EmptyRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractAspectDefBuilder implements AspectDefBuilder {

    /**
     * @param componentDef
     * @param interceptor
     * @param pointcut
     */
    protected void appendAspect(final ComponentDef componentDef, final String interceptor, final String pointcut) {
        if (interceptor == null) {
            throw new EmptyRuntimeException("interceptor");
        }
        final AspectDef aspectDef = AspectDefFactory.createAspectDef(interceptor, pointcut);
        componentDef.addAspectDef(aspectDef);
    }

    /**
     * @param componentDef
     * @param interceptor
     * @param pointcut
     */
    protected void appendAspect(final ComponentDef componentDef, final String interceptor, final Method pointcut) {
        if (interceptor == null) {
            throw new EmptyRuntimeException("interceptor");
        }
        final AspectDef aspectDef = AspectDefFactory.createAspectDef(interceptor, pointcut);
        componentDef.addAspectDef(aspectDef);
    }

}
