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
package org.lastaflute.di.core.factory.annohandler.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.assembler.AccessTypeDefFactory;
import org.lastaflute.di.core.assembler.AutoBindingDefFactory;
import org.lastaflute.di.core.assembler.BindingTypeDefFactory;
import org.lastaflute.di.core.expression.ScriptingExpression;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandler;
import org.lastaflute.di.core.meta.AccessTypeDef;
import org.lastaflute.di.core.meta.AutoBindingDef;
import org.lastaflute.di.core.meta.BindingTypeDef;
import org.lastaflute.di.core.meta.DestroyMethodDef;
import org.lastaflute.di.core.meta.InitMethodDef;
import org.lastaflute.di.core.meta.InstanceDef;
import org.lastaflute.di.core.meta.PropertyDef;
import org.lastaflute.di.core.meta.impl.ComponentDefImpl;
import org.lastaflute.di.core.meta.impl.InstanceDefFactory;
import org.lastaflute.di.core.meta.impl.PropertyDefImpl;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.beans.factory.BeanDescFactory;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractAnnotationHandler implements AnnotationHandler {

    protected static final String COMPONENT = "COMPONENT";
    protected static final String NAME = "name";
    protected static final String INSTANCE = "instance";
    protected static final String AUTO_BINDING = "autoBinding";
    protected static final String BINDING_SUFFIX = "_BINDING";
    protected static final String BINDING_TYPE = "bindingType";
    protected static final String EXTERNAL_BINDING = "externalBinding";
    protected static final String VALUE = "value";
    protected static final String ASPECT = "ASPECT";
    protected static final String INTER_TYPE = "INTER_TYPE";
    protected static final String INIT_METHOD = "INIT_METHOD";
    protected static final String DESTROY_METHOD = "DESTROY_METHOD";
    protected static final String INTERCEPTOR = "interceptor";
    protected static final String POINTCUT = "pointcut";

    @Override
    public ComponentDef createComponentDef(String className, InstanceDef instanceDef) {
        return createComponentDef(LdiClassUtil.forName(className), instanceDef);
    }

    @Override
    public ComponentDef createComponentDef(String className, InstanceDef instanceDef, AutoBindingDef autoBindingDef) {
        return createComponentDef(LdiClassUtil.forName(className), instanceDef, autoBindingDef);
    }

    @Override
    public ComponentDef createComponentDef(String className, InstanceDef instanceDef, AutoBindingDef autoBindingDef,
            boolean externalBinding) {
        return createComponentDef(LdiClassUtil.forName(className), instanceDef, autoBindingDef, externalBinding);
    }

    @Override
    public ComponentDef createComponentDef(Class<?> componentClass, InstanceDef instanceDef) {
        return createComponentDef(componentClass, instanceDef, null);
    }

    @Override
    public ComponentDef createComponentDef(Class<?> componentClass, InstanceDef instanceDef, AutoBindingDef autoBindingDef) {
        return createComponentDef(componentClass, instanceDef, autoBindingDef, false);
    }

    @Override
    public void appendDI(ComponentDef componentDef) { // #injection_point
        final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(componentDef.getComponentClass());
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            final PropertyDesc pd = beanDesc.getPropertyDesc(i);
            if (!pd.isWritable()) {
                continue;
            }
            final PropertyDef propDef = createPropertyDef(beanDesc, pd);
            if (propDef == null) { // e.g. non-annotation
                continue;
            }
            componentDef.addPropertyDef(propDef);
        }
        for (int i = 0; i < beanDesc.getFieldSize(); ++i) {
            final Field field = beanDesc.getField(i);
            if (componentDef.hasPropertyDef(field.getName())) {
                continue;
            }
            if (!isFieldInjectionTarget(field)) {
                continue;
            }
            final PropertyDef propDef = createPropertyDef(beanDesc, field);
            if (propDef == null) { // e.g. non-annotation
                continue;
            }
            componentDef.addPropertyDef(propDef);
        }
    }

    protected InstanceDef getInstanceDef(String name, InstanceDef defaultInstanceDef) {
        InstanceDef instanceDef = getInstanceDef(name);
        if (instanceDef != null) {
            return instanceDef;
        }
        return defaultInstanceDef;
    }

    protected InstanceDef getInstanceDef(String name) {
        if (LdiStringUtil.isEmpty(name)) {
            return null;
        }
        return InstanceDefFactory.getInstanceDef(name);
    }

    protected AutoBindingDef getAutoBindingDef(String name) {
        if (LdiStringUtil.isEmpty(name)) {
            return null;
        }
        return AutoBindingDefFactory.getAutoBindingDef(name);
    }

    protected ComponentDef createComponentDef(Class<?> componentClass, String name, InstanceDef instanceDef, AutoBindingDef autoBindingDef,
            boolean externalBinding) {
        ComponentDef componentDef = new ComponentDefImpl(componentClass);
        if (!LdiStringUtil.isEmpty(name)) {
            componentDef.setComponentName(name);
        }
        if (instanceDef != null) {
            componentDef.setInstanceDef(instanceDef);
        }
        if (autoBindingDef != null) {
            componentDef.setAutoBindingDef(autoBindingDef);
        }
        componentDef.setExternalBinding(externalBinding);
        return componentDef;
    }

    protected PropertyDef createPropertyDef(String propertyName, String expression, String bindingTypeName, String accessTypeName) {
        PropertyDef propertyDef = new PropertyDefImpl(propertyName);
        if (!LdiStringUtil.isEmpty(bindingTypeName)) {
            BindingTypeDef bindingTypeDef = BindingTypeDefFactory.getBindingTypeDef(bindingTypeName);
            propertyDef.setBindingTypeDef(bindingTypeDef);
        }
        if (!LdiStringUtil.isEmpty(accessTypeName)) {
            AccessTypeDef accessTypeDef = AccessTypeDefFactory.getAccessTypeDef(accessTypeName);
            propertyDef.setAccessTypeDef(accessTypeDef);
        }
        if (!LdiStringUtil.isEmpty(expression)) {
            propertyDef.setExpression(new ScriptingExpression(expression));
        }
        return propertyDef;
    }

    public boolean isInitMethodRegisterable(ComponentDef cd, String methodName) {
        if (LdiStringUtil.isEmpty(methodName)) {
            return false;
        }
        for (int i = 0; i < cd.getInitMethodDefSize(); ++i) {
            InitMethodDef other = cd.getInitMethodDef(i);
            if (methodName.equals(other.getMethodName()) && other.getArgDefSize() == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isDestroyMethodRegisterable(ComponentDef cd, String methodName) {
        if (LdiStringUtil.isEmpty(methodName)) {
            return false;
        }
        for (int i = 0; i < cd.getDestroyMethodDefSize(); ++i) {
            DestroyMethodDef other = cd.getDestroyMethodDef(i);
            if (methodName.equals(other.getMethodName()) && other.getArgDefSize() == 0) {
                return false;
            }
        }
        return true;
    }

    protected boolean isFieldInjectionTarget(Field field) {
        return !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers());
    }
}