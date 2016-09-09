/*
 * Copyright 2015-2016 the original author or authors.
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

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ContainerConstants;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.assembler.AutoBindingDefFactory;
import org.lastaflute.di.core.deployer.ComponentDeployer;
import org.lastaflute.di.core.exception.ComponentPropertyNotFoundException;
import org.lastaflute.di.core.expression.Expression;
import org.lastaflute.di.core.meta.ArgDef;
import org.lastaflute.di.core.meta.AspectDef;
import org.lastaflute.di.core.meta.AutoBindingDef;
import org.lastaflute.di.core.meta.DestroyMethodDef;
import org.lastaflute.di.core.meta.InitMethodDef;
import org.lastaflute.di.core.meta.InstanceDef;
import org.lastaflute.di.core.meta.InterTypeDef;
import org.lastaflute.di.core.meta.MetaDef;
import org.lastaflute.di.core.meta.PropertyDef;
import org.lastaflute.di.core.util.AopProxyUtil;
import org.lastaflute.di.core.util.ArgDefSupport;
import org.lastaflute.di.core.util.AspectDefSupport;
import org.lastaflute.di.core.util.DestroyMethodDefSupport;
import org.lastaflute.di.core.util.InitMethodDefSupport;
import org.lastaflute.di.core.util.InterTypeDefSupport;
import org.lastaflute.di.core.util.MetaDefSupport;
import org.lastaflute.di.core.util.PropertyDefSupport;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ComponentDefImpl implements ComponentDef, ContainerConstants {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Class<?> componentClass; // null allowed, may be set later (e.g. in assembly)
    private String componentName;
    private Class<?> concreteClass;
    private LaContainer container;
    private Expression expression;
    private ArgDefSupport argDefSupport = new ArgDefSupport();
    private PropertyDefSupport propertyDefSupport = new PropertyDefSupport();
    private InitMethodDefSupport initMethodDefSupport = new InitMethodDefSupport();
    private DestroyMethodDefSupport destroyMethodDefSupport = new DestroyMethodDefSupport();
    private AspectDefSupport aspectDefSupport = new AspectDefSupport();
    private InterTypeDefSupport interTypeDefSupport = new InterTypeDefSupport();
    private MetaDefSupport metaDefSupport = new MetaDefSupport();
    private InstanceDef instanceDef = InstanceDefFactory.SINGLETON;
    private AutoBindingDef autoBindingDef = AutoBindingDefFactory.AUTO;
    private ComponentDeployer componentDeployer;
    private boolean externalBinding = false;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ComponentDefImpl(Class<?> componentClass) {
        this(componentClass, null);
    }

    public ComponentDefImpl(Class<?> componentClass, String componentName) {
        this.componentClass = componentClass;
        setComponentName(componentName);
    }

    @Override
    public void init() {
        getConcreteClass();
        getComponentDeployer().init();
    }

    // ===================================================================================
    //                                                                           Injection
    //                                                                           =========
    @Override
    public Object getComponent() {
        return getComponentDeployer().deploy();
    }

    @Override
    public void injectDependency(Object outerComponent) {
        getComponentDeployer().injectDependency(outerComponent);
    }

    // ===================================================================================
    //                                                                             Destroy
    //                                                                             =======
    public void destroy() {
        getComponentDeployer().destroy();
        componentClass = null;
        componentName = null;
        concreteClass = null;
        container = null;
        expression = null;
        argDefSupport = null;
        propertyDefSupport = null;
        initMethodDefSupport = null;
        destroyMethodDefSupport = null;
        aspectDefSupport = null;
        interTypeDefSupport = null;
        metaDefSupport = null;
        instanceDef = null;
        autoBindingDef = null;
        componentDeployer = null;
    }

    // ===================================================================================
    //                                                                    Support Delegate
    //                                                                    ================
    public void addArgDef(ArgDef argDef) {
        argDefSupport.addArgDef(argDef);
    }

    public void addPropertyDef(PropertyDef propertyDef) {
        propertyDefSupport.addPropertyDef(propertyDef);
    }

    public void addInitMethodDef(InitMethodDef methodDef) {
        initMethodDefSupport.addInitMethodDef(methodDef);
    }

    public void addDestroyMethodDef(DestroyMethodDef methodDef) {
        destroyMethodDefSupport.addDestroyMethodDef(methodDef);
    }

    public void addAspectDef(AspectDef aspectDef) {
        aspectDefSupport.addAspectDef(aspectDef);
        concreteClass = null;
    }

    public void addAspectDef(int index, AspectDef aspectDef) {
        aspectDefSupport.addAspectDef(index, aspectDef);
        concreteClass = null;
    }

    public void addInterTypeDef(InterTypeDef interTypeDef) {
        interTypeDefSupport.addInterTypeDef(interTypeDef);
        concreteClass = null;
    }

    public int getArgDefSize() {
        return argDefSupport.getArgDefSize();
    }

    public int getPropertyDefSize() {
        return propertyDefSupport.getPropertyDefSize();
    }

    public int getInitMethodDefSize() {
        return initMethodDefSupport.getInitMethodDefSize();
    }

    public int getDestroyMethodDefSize() {
        return destroyMethodDefSupport.getDestroyMethodDefSize();
    }

    public int getAspectDefSize() {
        return aspectDefSupport.getAspectDefSize();
    }

    public int getInterTypeDefSize() {
        return interTypeDefSupport.getInterTypeDefSize();
    }

    public ArgDef getArgDef(int index) {
        return argDefSupport.getArgDef(index);
    }

    public PropertyDef getPropertyDef(int index) {
        return propertyDefSupport.getPropertyDef(index);
    }

    public PropertyDef getPropertyDef(String propertyName) {
        if (hasPropertyDef(propertyName)) {
            return propertyDefSupport.getPropertyDef(propertyName);
        }
        String exp = (componentClass != null ? componentClass.getName() : componentName) + "@" + propertyName;
        String msg = "Not found the property of the component: " + exp;
        throw new ComponentPropertyNotFoundException(msg, componentClass, propertyName);
    }

    public boolean hasPropertyDef(String propertyName) {
        return propertyDefSupport.hasPropertyDef(propertyName);
    }

    public InitMethodDef getInitMethodDef(int index) {
        return initMethodDefSupport.getInitMethodDef(index);
    }

    public DestroyMethodDef getDestroyMethodDef(int index) {
        return destroyMethodDefSupport.getDestroyMethodDef(index);
    }

    public AspectDef getAspectDef(int index) {
        return aspectDefSupport.getAspectDef(index);
    }

    public InterTypeDef getInterTypeDef(int index) {
        return interTypeDefSupport.getInterTypeDef(index);
    }

    public void addMetaDef(MetaDef metaDef) {
        metaDefSupport.addMetaDef(metaDef);
    }

    public MetaDef getMetaDef(int index) {
        return metaDefSupport.getMetaDef(index);
    }

    public MetaDef getMetaDef(String name) {
        return metaDefSupport.getMetaDef(name);
    }

    public MetaDef[] getMetaDefs(String name) {
        return metaDefSupport.getMetaDefs(name);
    }

    public int getMetaDefSize() {
        return metaDefSupport.getMetaDefSize();
    }

    public ComponentDeployer getComponentDeployer() {
        if (componentDeployer == null) {
            componentDeployer = instanceDef.createComponentDeployer(this);
        }
        return componentDeployer;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final String typeExp = componentClass != null ? componentClass.getSimpleName() : null;
        final String hash = Integer.toHexString(hashCode());
        return getClass().getSimpleName() + ":{" + componentName + ", " + typeExp + "}@" + hash;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
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
        if (concreteClass == null) {
            ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
            try {
                ClassLoader loader = (container != null ? container.getClassLoader() : null);
                if (loader != null) {
                    Thread.currentThread().setContextClassLoader(loader);
                }
                concreteClass = AopProxyUtil.getConcreteClass(this);
            } finally {
                Thread.currentThread().setContextClassLoader(oldLoader);
            }
        }
        return concreteClass;
    }

    public LaContainer getContainer() {
        return container;
    }

    public void setContainer(LaContainer container) {
        this.container = container;
        argDefSupport.setContainer(container);
        metaDefSupport.setContainer(container);
        propertyDefSupport.setContainer(container);
        initMethodDefSupport.setContainer(container);
        destroyMethodDefSupport.setContainer(container);
        aspectDefSupport.setContainer(container);
        interTypeDefSupport.setContainer(container);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public InstanceDef getInstanceDef() {
        return instanceDef;
    }

    public void setInstanceDef(InstanceDef instanceDef) {
        this.instanceDef = instanceDef;
    }

    public AutoBindingDef getAutoBindingDef() {
        return autoBindingDef;
    }

    public void setAutoBindingDef(AutoBindingDef autoBindingDef) {
        this.autoBindingDef = autoBindingDef;
    }

    public boolean isExternalBinding() {
        return externalBinding;
    }

    public void setExternalBinding(boolean externalBinding) {
        this.externalBinding = externalBinding;
    }
}