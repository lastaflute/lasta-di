/*
 * Copyright 2014-2015 the original author or authors.
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
package org.dbflute.lasta.di.core.meta.impl;

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.LaContainer;
import org.dbflute.lasta.di.core.assembler.AutoBindingDefFactory;
import org.dbflute.lasta.di.core.exception.TooManyRegistrationRuntimeException;
import org.dbflute.lasta.di.core.expression.Expression;
import org.dbflute.lasta.di.core.meta.ArgDef;
import org.dbflute.lasta.di.core.meta.AspectDef;
import org.dbflute.lasta.di.core.meta.AutoBindingDef;
import org.dbflute.lasta.di.core.meta.DestroyMethodDef;
import org.dbflute.lasta.di.core.meta.InitMethodDef;
import org.dbflute.lasta.di.core.meta.InstanceDef;
import org.dbflute.lasta.di.core.meta.InterTypeDef;
import org.dbflute.lasta.di.core.meta.MetaDef;
import org.dbflute.lasta.di.core.meta.PropertyDef;
import org.dbflute.lasta.di.helper.beans.exception.PropertyNotFoundRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SimpleComponentDef implements ComponentDef {

    private static final MetaDef[] EMPTY_META_DEFS = new MetaDef[0];

    private Object component;
    private Class<?> componentClass;
    private String componentName;
    private LaContainer container;

    public SimpleComponentDef() {
    }

    public SimpleComponentDef(Class<?> componentClass, String componentName) {
        this(null, componentClass, componentName);
    }

    public SimpleComponentDef(Object component) {
        this(component, component.getClass());
    }

    public SimpleComponentDef(Object component, Class<?> componentClass) {
        this(component, componentClass, null);
    }

    public SimpleComponentDef(Object component, String componentName) {
        this(component, component.getClass(), componentName);
    }

    public SimpleComponentDef(Object component, Class<?> componentClass, String componentName) {
        this.component = component;
        this.componentClass = componentClass;
        this.componentName = componentName;
    }

    public Object getComponent() throws TooManyRegistrationRuntimeException {
        return component;
    }

    public void injectDependency(Object outerComponent) {
        throw new UnsupportedOperationException("injectDependency");
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Class<?> getConcreteClass() {
        return componentClass;
    }

    public LaContainer getContainer() {
        return container;
    }

    public void setContainer(LaContainer container) {
        this.container = container;
    }

    public InstanceDef getInstanceDef() {
        return InstanceDefFactory.SINGLETON;
    }

    public void setInstanceDef(InstanceDef instanceDef) {
        throw new UnsupportedOperationException("setInstanceDef");
    }

    public AutoBindingDef getAutoBindingDef() {
        return AutoBindingDefFactory.NONE;
    }

    public void setAutoBindingDef(AutoBindingDef autoBindingDef) {
        throw new UnsupportedOperationException("setAutoBindingDef");
    }

    public boolean isExternalBinding() {
        return false;
    }

    public void setExternalBinding(boolean externalBinding) {
        throw new UnsupportedOperationException("setExternalBinding");
    }

    public Expression getExpression() {
        return null;
    }

    public void setExpression(Expression expression) {
        throw new UnsupportedOperationException("setExpression");
    }

    public void addArgDef(ArgDef constructorArgDef) {
        throw new UnsupportedOperationException("addArgDef");
    }

    public int getArgDefSize() {
        return 0;
    }

    public ArgDef getArgDef(int index) {
        throw new ArrayIndexOutOfBoundsException(0);
    }

    public void addPropertyDef(PropertyDef propertyDef) {
        throw new UnsupportedOperationException("addPropertyDef");
    }

    public int getPropertyDefSize() {
        return 0;
    }

    public PropertyDef getPropertyDef(int index) {
        throw new ArrayIndexOutOfBoundsException(0);
    }

    public boolean hasPropertyDef(String propertyName) {
        return false;
    }

    public PropertyDef getPropertyDef(String propertyName) {
        throw new PropertyNotFoundRuntimeException(componentClass, propertyName);
    }

    public void addInitMethodDef(InitMethodDef methodDef) {
        throw new UnsupportedOperationException("addInitMethodDef");
    }

    public int getInitMethodDefSize() {
        return 0;
    }

    public InitMethodDef getInitMethodDef(int index) {
        throw new ArrayIndexOutOfBoundsException(0);
    }

    public void addDestroyMethodDef(DestroyMethodDef methodDef) {
        throw new UnsupportedOperationException("addDestroyMethodDef");
    }

    public int getDestroyMethodDefSize() {
        return 0;
    }

    public DestroyMethodDef getDestroyMethodDef(int index) {
        throw new ArrayIndexOutOfBoundsException(0);
    }

    public void addAspectDef(AspectDef aspectDef) {
        throw new UnsupportedOperationException("addAspectDef");
    }

    public void addAspectDef(int index, AspectDef aspectDef) {
        throw new UnsupportedOperationException("addAspectDef");
    }

    public int getAspectDefSize() {
        return 0;
    }

    public AspectDef getAspectDef(int index) {
        throw new ArrayIndexOutOfBoundsException(0);
    }

    public void addInterTypeDef(InterTypeDef interTypeDef) {
        throw new UnsupportedOperationException("addInterTypeDef");
    }

    public int getInterTypeDefSize() {
        return 0;
    }

    public InterTypeDef getInterTypeDef(int index) {
        throw new ArrayIndexOutOfBoundsException(0);
    }

    public void addMetaDef(MetaDef metaDef) {
        throw new UnsupportedOperationException("addMetaDef");
    }

    public int getMetaDefSize() {
        return 0;
    }

    public MetaDef getMetaDef(int index) {
        throw new ArrayIndexOutOfBoundsException(0);
    }

    public MetaDef getMetaDef(String name) {
        return null;
    }

    public MetaDef[] getMetaDefs(String name) {
        return EMPTY_META_DEFS;
    }

    public void init() {
    }

    public void destroy() {
    }
}
