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
package org.lastaflute.di.core.factory.annohandler.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.exception.IllegalDestroyMethodAnnotationRuntimeException;
import org.lastaflute.di.core.exception.IllegalInitMethodAnnotationRuntimeException;
import org.lastaflute.di.core.expression.ScriptingExpression;
import org.lastaflute.di.core.factory.AspectDefFactory;
import org.lastaflute.di.core.meta.AccessTypeDef;
import org.lastaflute.di.core.meta.AspectDef;
import org.lastaflute.di.core.meta.AutoBindingDef;
import org.lastaflute.di.core.meta.DestroyMethodDef;
import org.lastaflute.di.core.meta.InitMethodDef;
import org.lastaflute.di.core.meta.InstanceDef;
import org.lastaflute.di.core.meta.InterTypeDef;
import org.lastaflute.di.core.meta.PropertyDef;
import org.lastaflute.di.core.meta.impl.DestroyMethodDefImpl;
import org.lastaflute.di.core.meta.impl.InitMethodDefImpl;
import org.lastaflute.di.core.meta.impl.InterTypeDefImpl;
import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.beans.factory.BeanDescFactory;
import org.lastaflute.di.util.LdiFieldUtil;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ConstantAnnotationHandler extends AbstractAnnotationHandler {

    public ComponentDef createComponentDef(Class<?> componentClass, InstanceDef defaultInstanceDef, AutoBindingDef defaultAutoBindingDef,
            boolean defaultExternalBinding) {

        String name = null;
        InstanceDef instanceDef = defaultInstanceDef;
        AutoBindingDef autoBindingDef = defaultAutoBindingDef;
        boolean externalBinding = defaultExternalBinding;
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(componentClass);
        if (!beanDesc.hasField(COMPONENT)) {
            return createComponentDef(componentClass, name, instanceDef, autoBindingDef, externalBinding);
        }
        Field field = beanDesc.getField(COMPONENT);
        if (!isConstantAnnotationField(field)) {
            return createComponentDef(componentClass, name, instanceDef, autoBindingDef, externalBinding);
        }
        String componentStr = (String) LdiFieldUtil.get(field, null);
        String[] array = LdiStringUtil.split(componentStr, "=, ");
        for (int i = 0; i < array.length; i += 2) {
            String key = array[i].trim();
            String value = array[i + 1].trim();
            if (NAME.equalsIgnoreCase(key)) {
                name = value;
            } else if (INSTANCE.equalsIgnoreCase(key)) {
                instanceDef = getInstanceDef(value, defaultInstanceDef);
            } else if (AUTO_BINDING.equalsIgnoreCase(key)) {
                autoBindingDef = getAutoBindingDef(value);
            } else if (EXTERNAL_BINDING.equalsIgnoreCase(key)) {
                externalBinding = Boolean.valueOf(value).booleanValue();
            } else {
                throw new IllegalArgumentException(componentStr);
            }
        }
        return createComponentDef(componentClass, name, instanceDef, autoBindingDef, externalBinding);
    }

    protected boolean isConstantAnnotationField(Field field) {
        final int modifiers = field.getModifiers();
        return Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers);
    }

    public PropertyDef createPropertyDef(BeanDesc beanDesc, PropertyDesc propertyDesc) {
        String propName = propertyDesc.getPropertyName();
        String fieldName = propName + BINDING_SUFFIX;
        if (!beanDesc.hasField(fieldName)) {
            return null;
        }
        String bindingStr = (String) beanDesc.getFieldValue(fieldName, null);
        String bindingTypeName = null;
        String expression = null;
        if (bindingStr != null) {
            String[] array = LdiStringUtil.split(bindingStr, "=, ");
            if (array.length == 1) {
                expression = array[0];
            } else {
                for (int i = 0; i < array.length; i += 2) {
                    String key = array[i].trim();
                    String value = array[i + 1].trim();
                    if (BINDING_TYPE.equalsIgnoreCase(key)) {
                        bindingTypeName = value;
                    } else if (VALUE.equalsIgnoreCase(key)) {
                        expression = value;
                    } else {
                        throw new IllegalArgumentException(bindingStr);
                    }
                }
            }
        }
        return createPropertyDef(propName, expression, bindingTypeName, AccessTypeDef.PROPERTY_NAME);
    }

    public PropertyDef createPropertyDef(BeanDesc beanDesc, Field field) {
        return null;
    }

    public void appendAspect(ComponentDef componentDef) {
        Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(componentClass);
        if (!beanDesc.hasField(ASPECT)) {
            return;
        }
        String aspectStr = (String) beanDesc.getFieldValue(ASPECT, null);
        String[] array = LdiStringUtil.split(aspectStr, "=, ");
        String interceptor = null;
        String pointcut = null;
        if (array.length == 1) {
            interceptor = array[0];
        } else {
            for (int i = 0; i < array.length; i += 2) {
                String key = array[i].trim();
                String value = array[i + 1].trim();
                if (VALUE.equalsIgnoreCase(key)) {
                    interceptor = value;
                } else if (POINTCUT.equalsIgnoreCase(key)) {
                    pointcut = value;
                } else {
                    throw new IllegalArgumentException(aspectStr);
                }
            }
        }
        appendAspect(componentDef, interceptor, pointcut);
    }

    protected void appendAspect(ComponentDef componentDef, String interceptor, String pointcut) {

        if (interceptor == null) {
            throw new EmptyRuntimeException("interceptor");
        }
        AspectDef aspectDef = AspectDefFactory.createAspectDef(interceptor, pointcut);
        componentDef.addAspectDef(aspectDef);
    }

    protected void appendAspect(ComponentDef componentDef, String interceptor, Method targetMethod) {

        if (interceptor == null) {
            throw new EmptyRuntimeException("interceptor");
        }
        AspectDef aspectDef = AspectDefFactory.createAspectDef(interceptor, targetMethod);
        componentDef.addAspectDef(aspectDef);
    }

    public void appendInterType(ComponentDef componentDef) {
        Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(componentClass);
        if (!beanDesc.hasField(INTER_TYPE)) {
            return;
        }
        String interTypeStr = (String) beanDesc.getFieldValue(INTER_TYPE, null);
        String[] array = LdiStringUtil.split(interTypeStr, ", ");
        for (int i = 0; i < array.length; i += 2) {
            String interTypeName = array[i].trim();
            appendInterType(componentDef, interTypeName);
        }
    }

    protected void appendInterType(ComponentDef componentDef, String interTypeName) {
        InterTypeDef interTypeDef = new InterTypeDefImpl();
        interTypeDef.setExpression(new ScriptingExpression(interTypeName));
        componentDef.addInterTypeDef(interTypeDef);
    }

    public void appendInitMethod(ComponentDef componentDef) {
        Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(componentClass);
        if (!beanDesc.hasField(INIT_METHOD)) {
            return;
        }
        String initMethodStr = (String) beanDesc.getFieldValue(INIT_METHOD, null);
        if (LdiStringUtil.isEmpty(initMethodStr)) {
            return;
        }
        String[] array = LdiStringUtil.split(initMethodStr, ", ");
        for (int i = 0; i < array.length; ++i) {
            String methodName = array[i].trim();
            if (!beanDesc.hasMethod(methodName)) {
                throw new IllegalInitMethodAnnotationRuntimeException(componentClass, methodName);
            }
            Method[] methods = beanDesc.getMethods(methodName);
            if (methods.length != 1 || methods[0].getParameterTypes().length != 0) {
                throw new IllegalInitMethodAnnotationRuntimeException(componentClass, methodName);
            }
            if (!isInitMethodRegisterable(componentDef, methodName)) {
                continue;
            }
            appendInitMethod(componentDef, methodName);
        }
    }

    protected void appendInitMethod(ComponentDef componentDef, Method method) {
        InitMethodDef initMethodDef = new InitMethodDefImpl(method);
        componentDef.addInitMethodDef(initMethodDef);
    }

    protected void appendInitMethod(ComponentDef componentDef, String methodName) {
        InitMethodDef initMethodDef = new InitMethodDefImpl(methodName);
        componentDef.addInitMethodDef(initMethodDef);
    }

    public void appendDestroyMethod(ComponentDef componentDef) {
        Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(componentClass);
        if (!beanDesc.hasField(DESTROY_METHOD)) {
            return;
        }
        String destroyMethodStr = (String) beanDesc.getFieldValue(DESTROY_METHOD, null);
        if (LdiStringUtil.isEmpty(destroyMethodStr)) {
            return;
        }
        String[] array = LdiStringUtil.split(destroyMethodStr, ", ");
        for (int i = 0; i < array.length; ++i) {
            String methodName = array[i].trim();
            if (!beanDesc.hasMethod(methodName)) {
                throw new IllegalDestroyMethodAnnotationRuntimeException(componentClass, methodName);
            }
            Method[] methods = beanDesc.getMethods(methodName);
            if (methods.length != 1 || methods[0].getParameterTypes().length != 0) {
                throw new IllegalDestroyMethodAnnotationRuntimeException(componentClass, methodName);
            }
            if (!isDestroyMethodRegisterable(componentDef, methodName)) {
                continue;
            }
            appendDestroyMethod(componentDef, methodName);
        }
    }

    protected void appendDestroyMethod(ComponentDef componentDef, Method targetMethod) {
        DestroyMethodDef destroyMethodDef = new DestroyMethodDefImpl(targetMethod);
        componentDef.addDestroyMethodDef(destroyMethodDef);
    }

    protected void appendDestroyMethod(ComponentDef componentDef, String methodName) {
        DestroyMethodDef destroyMethodDef = new DestroyMethodDefImpl(methodName);
        componentDef.addDestroyMethodDef(destroyMethodDef);
    }
}
