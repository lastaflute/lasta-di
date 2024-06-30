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
package org.lastaflute.di.core.factory.defbuilder.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.assembler.AccessTypeDefFactory;
import org.lastaflute.di.core.assembler.BindingTypeDefFactory;
import org.lastaflute.di.core.expression.ScriptingExpression;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandler;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandlerFactory;
import org.lastaflute.di.core.factory.defbuilder.PropertyDefBuilder;
import org.lastaflute.di.core.meta.AccessTypeDef;
import org.lastaflute.di.core.meta.InstanceDef;
import org.lastaflute.di.core.meta.PropertyDef;
import org.lastaflute.di.core.meta.impl.InstanceDefFactory;
import org.lastaflute.di.core.meta.impl.PropertyDefImpl;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @param <ANNO> The type of annotation.
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractPropertyDefBuilder<ANNO extends Annotation> implements PropertyDefBuilder {

    protected final AnnotationHandler handler = AnnotationHandlerFactory.getAnnotationHandler();

    public AbstractPropertyDefBuilder() {
    }

    @Override
    public PropertyDef createPropertyDef(AnnotationHandler annotationHandler, BeanDesc beanDesc, PropertyDesc propertyDesc) {
        if (!propertyDesc.hasWriteMethod()) {
            return null;
        }
        final Method method = propertyDesc.getWriteMethod();
        final ANNO annotation = method.getAnnotation(getAnnotationType());
        return annotation != null ? createPropertyDef(propertyDesc.getPropertyName(), AccessTypeDefFactory.PROPERTY, annotation) : null;
    }

    @Override
    public PropertyDef createPropertyDef(AnnotationHandler annotationHandler, BeanDesc beanDesc, Field field) { // #injection_point
        final ANNO annotation = field.getAnnotation(getAnnotationType());
        return annotation != null ? createPropertyDef(field.getName(), AccessTypeDefFactory.FIELD, annotation) : null;
    }

    protected abstract Class<ANNO> getAnnotationType();

    protected abstract PropertyDef createPropertyDef(String name, AccessTypeDef accessTypeDef, ANNO annotation);

    protected ComponentDef createComponentDef(final Class<?> componentClass) {
        return createComponentDef(componentClass, InstanceDefFactory.SINGLETON);
    }

    protected ComponentDef createComponentDef(final Class<?> componentClass, final InstanceDef instanceDef) {
        final ComponentDef componentDef = handler.createComponentDef(componentClass, instanceDef);
        handler.appendDI(componentDef);
        handler.appendAspect(componentDef);
        handler.appendInterType(componentDef);
        handler.appendInitMethod(componentDef);
        handler.appendDestroyMethod(componentDef);
        return componentDef;
    }

    protected PropertyDef createPropertyDef(final String propertyName, final AccessTypeDef accessTypeDef) {
        return createPropertyDef(propertyName, accessTypeDef, "");
    }

    protected PropertyDef createPropertyDef(final String propertyName, final AccessTypeDef accessTypeDef, final String expression) {
        return createPropertyDef(propertyName, accessTypeDef, expression, null);
    }

    protected PropertyDef createPropertyDef(final String propertyName, final AccessTypeDef accessTypeDef, final ComponentDef child) {
        return createPropertyDef(propertyName, accessTypeDef, "", child);
    }

    protected PropertyDef createPropertyDef(final String propertyName, final AccessTypeDef accessTypeDef, final String expression,
            final ComponentDef child) {
        final PropertyDef propertyDef = new PropertyDefImpl(propertyName);
        propertyDef.setAccessTypeDef(accessTypeDef);
        propertyDef.setBindingTypeDef(BindingTypeDefFactory.MUST);
        if (!LdiStringUtil.isEmpty(expression)) {
            propertyDef.setExpression(new ScriptingExpression(expression));
        }
        if (child != null) {
            propertyDef.setChildComponentDef(child);
        }
        return propertyDef;
    }
}
