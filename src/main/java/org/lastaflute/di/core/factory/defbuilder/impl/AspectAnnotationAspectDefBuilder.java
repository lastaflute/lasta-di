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
import java.lang.reflect.Modifier;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.annotation.Aspect;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandler;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AspectAnnotationAspectDefBuilder extends AbstractAspectDefBuilder {

    public void appendAspectDef(final AnnotationHandler annotationHandler, final ComponentDef componentDef) {
        processClass(componentDef);
        processMethod(componentDef);
    }

    /**
     * @param componentDef
     */
    protected void processClass(final ComponentDef componentDef) {
        final Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }

        final Aspect aspect = componentClass.getAnnotation(Aspect.class);
        if (aspect != null) {
            String interceptor = aspect.value();
            String pointcut = aspect.pointcut();
            appendAspect(componentDef, interceptor, pointcut);
        }
    }

    /**
     * @param componentDef
     */
    protected void processMethod(final ComponentDef componentDef) {
        final Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }

        final Method[] methods = componentClass.getMethods();
        for (final Method method : methods) {
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            final int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }
            final Aspect aspect = method.getAnnotation(Aspect.class);
            if (aspect != null) {
                String interceptor = aspect.value();
                appendAspect(componentDef, interceptor, method);
            }
        }
    }

}
