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
package org.lastaflute.di.core;

import org.lastaflute.di.core.exception.ClassUnmatchRuntimeException;
import org.lastaflute.di.core.exception.ComponentNotFoundRuntimeException;
import org.lastaflute.di.core.exception.ContainerNotRegisteredRuntimeException;
import org.lastaflute.di.core.exception.CyclicReferenceRuntimeException;
import org.lastaflute.di.core.exception.TooManyRegistrationRuntimeException;
import org.lastaflute.di.core.external.ExternalContextComponentDefRegister;
import org.lastaflute.di.core.meta.MetaDefAware;

/**
 * 
 * @author modified by jflute (originated in Seasar)
 * @author vestige &amp; SeasarJavaDoc Committers
 */
public interface LaContainer extends MetaDefAware {

    <COMPONENT> COMPONENT getComponent(Object componentKey) throws ComponentNotFoundRuntimeException, TooManyRegistrationRuntimeException,
            CyclicReferenceRuntimeException;

    Object[] findComponents(Object componentKey) throws CyclicReferenceRuntimeException;

    Object[] findAllComponents(Object componentKey) throws CyclicReferenceRuntimeException;

    /**
     * @param componentKey The key of the component. (NotNull)
     * @return The array of found components. (NotNull: if not found, returns empty)
     * @throws CyclicReferenceRuntimeException
     */
    Object[] findLocalComponents(Object componentKey) throws CyclicReferenceRuntimeException;

    void injectDependency(Object outerComponent) throws ClassUnmatchRuntimeException;

    void injectDependency(Object outerComponent, Class<?> componentClass) throws ClassUnmatchRuntimeException;

    void injectDependency(Object outerComponent, String componentName) throws ClassUnmatchRuntimeException;

    void register(Object component); // as anonymous component

    void register(Object component, String componentName);

    void register(Class<?> componentClass);

    void register(Class<?> componentClass, String componentName);

    void register(ComponentDef componentDef);

    void registerByClass(ComponentDef componentDef); // for e.g. expression lazy type

    int getComponentDefSize();

    ComponentDef getComponentDef(int index);

    ComponentDef getComponentDef(Object componentKey) throws ComponentNotFoundRuntimeException;

    ComponentDef[] findComponentDefs(Object componentKey);

    ComponentDef[] findAllComponentDefs(Object componentKey);

    ComponentDef[] findLocalComponentDefs(Object componentKey);

    boolean hasComponentDef(Object componentKey);

    boolean hasDescendant(String path);

    LaContainer getDescendant(String path) throws ContainerNotRegisteredRuntimeException;

    void registerDescendant(LaContainer descendant);

    void include(LaContainer child);

    LaContainer findChild(String namespace);

    int getChildSize();

    LaContainer getChild(int index);

    int getParentSize();

    LaContainer getParent(int index);

    void addParent(LaContainer parent);

    void init();

    void destroy();

    void registerMap(Object key, ComponentDef componentDef, LaContainer container);

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    String getNamespace();

    void setNamespace(String namespace);

    boolean isInitializeOnCreate();

    void setInitializeOnCreate(boolean initializeOnCreate);

    String getPath();

    void setPath(String path);

    LaContainer getRoot();

    void setRoot(LaContainer root);

    ExternalContext getExternalContext();

    void setExternalContext(ExternalContext externalContext);

    ExternalContextComponentDefRegister getExternalContextComponentDefRegister();

    void setExternalContextComponentDefRegister(ExternalContextComponentDefRegister externalContextComponentDefRegister);

    ClassLoader getClassLoader();

    void setClassLoader(ClassLoader classLoader);
}