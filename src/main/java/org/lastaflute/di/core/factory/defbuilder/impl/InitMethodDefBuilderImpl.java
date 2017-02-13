/*
 * Copyright 2015-2017 the original author or authors.
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

import javax.annotation.PostConstruct;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.exception.IllegalInitMethodAnnotationRuntimeException;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandler;
import org.lastaflute.di.core.factory.defbuilder.InitMethodDefBuilder;
import org.lastaflute.di.core.meta.impl.InitMethodDefImpl;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class InitMethodDefBuilderImpl implements InitMethodDefBuilder {

    @Override
    public void appendInitMethodDef(AnnotationHandler annotationHandler, ComponentDef componentDef) {
        final Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }
        for (final Method method : componentClass.getMethods()) {
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            final PostConstruct initMethod = method.getAnnotation(PostConstruct.class);
            if (initMethod == null) {
                continue;
            }
            if (method.getParameterTypes().length != 0) {
                throw new IllegalInitMethodAnnotationRuntimeException(componentClass, method.getName());
            }
            if (!annotationHandler.isInitMethodRegisterable(componentDef, method.getName())) {
                continue;
            }
            componentDef.addInitMethodDef(newInitMethodDefImpl(method));
        }
    }

    protected InitMethodDefImpl newInitMethodDefImpl(Method method) {
        return new InitMethodDefImpl(method);
    }
}
