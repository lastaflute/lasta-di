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
package org.lastaflute.di.core.assembler;

import java.lang.reflect.Field;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ContainerConstants;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.exception.IllegalPropertyDefinitionException;
import org.lastaflute.di.core.meta.BindingTypeDef;
import org.lastaflute.di.core.meta.PropertyDef;
import org.lastaflute.di.core.util.BindingUtil;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.beans.exception.BeanIllegalPropertyException;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;
import org.lastaflute.di.util.LdiFieldUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractBindingTypeDef implements BindingTypeDef {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String name;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected AbstractBindingTypeDef(String name) {
        this.name = name;
    }

    // ===================================================================================
    //                                                                               Bind
    //                                                                              ======
    // -----------------------------------------------------
    //                                              Property
    //                                              --------
    @Override
    public void bind(ComponentDef componentDef, PropertyDef propertyDef, PropertyDesc propertyDesc, Object component) {
        if (propertyDef != null && propertyDef.isValueGettable()) {
            if (propertyDesc != null && propertyDesc.isWritable()) {
                bindManualProperty(componentDef, propertyDef, propertyDesc, component);
            }
        } else {
            if (propertyDesc != null && propertyDesc.isWritable()) {
                doBindProperty(componentDef, propertyDesc, component);
            }
        }
    }

    protected void bindManualProperty(ComponentDef componentDef, PropertyDef propertyDef, PropertyDesc propertyDesc, Object component) {
        final Object value = getValue(componentDef, propertyDef, component, propertyDesc.getPropertyType());
        setPropertyValue(componentDef, propertyDesc, component, value);
    }

    protected boolean bindAutoProperty(ComponentDef componentDef, PropertyDesc propertyDesc, Object component) {
        final LaContainer container = componentDef.getContainer();
        final String propName = propertyDesc.getPropertyName();
        final Class<?> propType = propertyDesc.getPropertyType();
        final boolean hasComponentByType = container.hasComponentDef(propType);
        if (hasComponentByType) {
            final ComponentDef cd = container.getComponentDef(propType);
            if (isSimpleNamingAutoBindable(propName, propType, cd)) {
                final Object value = getComponent(componentDef, propType, component, propName);
                setPropertyValue(componentDef, propertyDesc, component, value);
                return true;
            }
        }
        if (container.hasComponentDef(propName)) {
            final Object value = getComponent(componentDef, propName, component, propName);
            if (propType.isInstance(value)) {
                setPropertyValue(componentDef, propertyDesc, component, value);
                return true;
            }
        }
        if (isPropertyAutoBindable(propType)) {
            if (hasComponentByType) {
                final Object value = getComponent(componentDef, propType, component, propName);
                setPropertyValue(componentDef, propertyDesc, component, value);
                return true;
            }
            if (propType.isAssignableFrom(ComponentDef.class)) {
                setPropertyValue(componentDef, propertyDesc, component, componentDef);
                return true;
            }
        }
        if (BindingUtil.isAutoBindableArray(propType)) {
            final Class<?> clazz = propType.getComponentType();
            final Object[] values = container.findAllComponents(clazz);
            if (values.length > 0) {
                setPropertyValue(componentDef, propertyDesc, component, values);
                return true;
            }
        }
        return false;
    }

    protected boolean isPropertyAutoBindable(Class<?> clazz) {
        return BindingUtil.isPropertyAutoBindable(clazz);
    }

    protected void setPropertyValue(ComponentDef componentDef, PropertyDesc propertyDesc, Object component, Object value)
            throws BeanIllegalPropertyException {
        if (value == null) {
            return;
        }
        try {
            propertyDesc.setValue(component, value); // method or non-annotation public field
        } catch (NumberFormatException ex) {
            throw new BeanIllegalPropertyException(componentDef.getComponentClass(), propertyDesc.getPropertyName(), ex);
        }
    }

    protected abstract void doBindProperty(ComponentDef componentDef, PropertyDesc propertyDesc, Object component);

    // -----------------------------------------------------
    //                                        Resource Field
    //                                        --------------
    @Override
    public void bind(ComponentDef componentDef, PropertyDef propertyDef, Field field, Object component) {
        if (propertyDef != null && propertyDef.isValueGettable()) {
            if (field != null) {
                bindManualResourceField(componentDef, propertyDef, field, component);
            }
        } else {
            if (propertyDef != null && field != null) {
                doBindResourceField(componentDef, field, component);
            }
        }
    }

    protected void bindManualResourceField(ComponentDef componentDef, PropertyDef propertyDef, Field field, Object component) {
        final Object value = getValue(componentDef, propertyDef, component, field.getType());
        setResourceFieldValue(componentDef, field, component, value);
    }

    protected boolean bindAutoResourceField(ComponentDef componentDef, Field field, Object component) { // #injection_point
        final LaContainer container = componentDef.getContainer();
        final String propName = field.getName();
        final Class<?> propType = field.getType();
        final boolean hasComponentByType = container.hasComponentDef(propType);
        if (hasComponentByType) {
            final ComponentDef cd = container.getComponentDef(propType);
            if (isSimpleNamingAutoBindable(propName, propType, cd)) {
                Object value = getComponent(componentDef, propType, component, propName);
                setResourceFieldValue(componentDef, field, component, value);
                return true;
            }
        }
        if (container.hasComponentDef(propName)) {
            final Object value = getComponent(componentDef, propName, component, propName);
            if (propType.isInstance(value)) {
                setResourceFieldValue(componentDef, field, component, value);
                return true;
            }
        }
        if (isFieldAutoBindable(propType)) {
            if (hasComponentByType) {
                final Object value = getComponent(componentDef, propType, component, propName);
                setResourceFieldValue(componentDef, field, component, value);
                return true;
            }
            if (propType.isAssignableFrom(ComponentDef.class)) {
                setResourceFieldValue(componentDef, field, component, componentDef);
                return true;
            }
        }
        if (BindingUtil.isAutoBindableArray(propType)) {
            final Class<?> clazz = propType.getComponentType();
            final Object[] values = container.findAllComponents(clazz);
            if (values.length > 0) {
                setResourceFieldValue(componentDef, field, component, values);
                return true;
            }
        }
        return false;
    }

    protected boolean isFieldAutoBindable(Class<?> clazz) {
        return BindingUtil.isFieldAutoBindable(clazz);
    }

    protected void setResourceFieldValue(ComponentDef componentDef, Field field, Object component, Object value)
            throws BeanIllegalPropertyException {
        if (value == null) {
            return;
        }
        try {
            LdiFieldUtil.set(field, component, value); // annotation field (contains public, private)
        } catch (NumberFormatException ex) {
            throw new BeanIllegalPropertyException(componentDef.getComponentClass(), field.getName(), ex);
        }
    }

    protected abstract void doBindResourceField(ComponentDef componentDef, Field field, Object component);

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected boolean isSimpleNamingAutoBindable(String propertyName, Class<?> propertyType, ComponentDef cd) {
        final String componentName = cd.getComponentName();
        if (componentName == null) {
            return false;
        }
        if (componentName.equals(propertyName)) { // e.g. seaLogic for SeaLogic
            return true;
        }
        if (componentName.endsWith(ContainerConstants.PACKAGE_SEP + propertyName)) { // e.g. sea[_landLogic]
            return true;
        }
        return false;
    }

    protected Object getValue(ComponentDef componentDef, PropertyDef propertyDef, Object component, Class<?> resultType)
            throws BeanIllegalPropertyException {
        try {
            return propertyDef.getValue(resultType);
        } catch (RuntimeException cause) {
            throwPropertyValueGetFailureException(componentDef, propertyDef, component, cause);
            return null; // unreachable
        }
    }

    protected void throwPropertyValueGetFailureException(ComponentDef componentDef, PropertyDef propertyDef, Object component,
            RuntimeException cause) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to get the value for the property.");
        if (componentDef != null) {
            br.addItem("Component Def");
            final String componentName = componentDef.getComponentName();
            final String typeName = componentDef.getComponentClass().getName();
            final String definedPath = componentDef.getContainer().getPath();
            br.addElement("name=" + componentName + ", type=" + typeName + ", path=" + definedPath);
        }
        br.addItem("Property Owner");
        br.addElement(component);
        br.addItem("Property Definition");
        br.addElement(propertyDef);
        final String msg = br.buildExceptionMessage();
        throw new IllegalPropertyDefinitionException(msg, cause);
    }

    protected Object getComponent(ComponentDef componentDef, Object key, Object component, String propertyName)
            throws BeanIllegalPropertyException {
        try {
            return componentDef.getContainer().getComponent(key);
        } catch (RuntimeException cause) {
            throwPropertyComponentGetFailureException(componentDef, key, component, propertyName, cause);
            return null; // unreachable
        }
    }

    protected void throwPropertyComponentGetFailureException(ComponentDef componentDef, Object key, Object component, String propertyName,
            RuntimeException cause) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to get the component for the property by the key.");
        br.addItem("Component Key");
        br.addElement(key);
        if (componentDef != null) {
            br.addItem("Component Def");
            final String componentName = componentDef.getComponentName();
            final String typeName = componentDef.getComponentClass().getName();
            final String definedPath = componentDef.getContainer().getPath();
            br.addElement("name=" + componentName + ", type=" + typeName + ", path=" + definedPath);
        }
        br.addItem("Property Owner");
        br.addElement(component);
        br.addItem("Property Name");
        br.addElement(propertyName);
        final String msg = br.buildExceptionMessage();
        throw new IllegalPropertyDefinitionException(msg, cause);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BindingTypeDef)) {
            return false;
        }
        BindingTypeDef other = (BindingTypeDef) o;
        return name == null ? other.getName() == null : name.equals(other.getName());
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getName() {
        return name;
    }
}