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
package org.lastaflute.di.core.meta.impl;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.external.ExternalContextComponentDefRegister;
import org.lastaflute.di.core.meta.MetaDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ThreadSafeLaContainerImpl extends LaContainerImpl {

    public void addMetaDef(final MetaDef metaDef) {
        synchronized (getRoot()) {
            super.addMetaDef(metaDef);
        }
    }

    public void addParent(final LaContainer parent) {
        synchronized (getRoot()) {
            super.addParent(parent);
        }
    }

    public void destroy() {
        synchronized (getRoot()) {
            super.destroy();
        }
    }

    public ComponentDef[] findAllComponentDefs(final Object componentKey) {
        synchronized (getRoot()) {
            return super.findAllComponentDefs(componentKey);
        }
    }

    public ComponentDef[] findLocalComponentDefs(final Object componentKey) {
        synchronized (getRoot()) {
            return super.findLocalComponentDefs(componentKey);
        }
    }

    public LaContainer getChild(final int index) {
        synchronized (getRoot()) {
            return super.getChild(index);
        }
    }

    public int getChildSize() {
        synchronized (getRoot()) {
            return super.getChildSize();
        }
    }

    public ClassLoader getClassLoader() {
        synchronized (getRoot()) {
            return super.getClassLoader();
        }
    }

    public ComponentDef getComponentDef(final int index) {
        synchronized (getRoot()) {
            return super.getComponentDef(index);
        }
    }

    public int getComponentDefSize() {
        synchronized (getRoot()) {
            return super.getComponentDefSize();
        }
    }

    protected int getContainerPosition(LaContainer container) {
        synchronized (getRoot()) {
            return super.getContainerPosition(container);
        }
    }

    public LaContainer getDescendant(final String path) {
        synchronized (getRoot()) {
            return super.getDescendant(path);
        }
    }

    @Override
    public ExternalContext getExternalContext() {
        synchronized (getRoot()) {
            return super.getExternalContext();
        }
    }

    @Override
    public ExternalContextComponentDefRegister getExternalContextComponentDefRegister() {
        synchronized (getRoot()) {
            return super.getExternalContextComponentDefRegister();
        }
    }

    @Override
    public MetaDef getMetaDef(final int index) {
        synchronized (getRoot()) {
            return super.getMetaDef(index);
        }
    }

    @Override
    public MetaDef getMetaDef(final String name) {
        synchronized (getRoot()) {
            return super.getMetaDef(name);
        }
    }

    @Override
    public MetaDef[] getMetaDefs(final String name) {
        synchronized (getRoot()) {
            return super.getMetaDefs(name);
        }
    }

    @Override
    public int getMetaDefSize() {
        synchronized (getRoot()) {
            return super.getMetaDefSize();
        }
    }

    @Override
    public String getNamespace() {
        synchronized (getRoot()) {
            return super.getNamespace();
        }
    }

    @Override
    public LaContainer getParent(final int index) {
        synchronized (getRoot()) {
            return super.getParent(index);
        }
    }

    @Override
    public int getParentSize() {
        synchronized (getRoot()) {
            return super.getParentSize();
        }
    }

    @Override
    public boolean hasDescendant(final String path) {
        synchronized (getRoot()) {
            return super.hasDescendant(path);
        }
    }

    @Override
    public void include(final LaContainer child) {
        synchronized (getRoot()) {
            super.include(child);
        }
    }

    @Override
    public void init() {
        synchronized (getRoot()) {
            super.init();
        }
    }

    @Override
    protected ComponentDef internalGetComponentDef(final Object key) {
        synchronized (getRoot()) {
            return super.internalGetComponentDef(key);
        }
    }

    public void register(final ComponentDef componentDef) {
        synchronized (getRoot()) {
            super.register(componentDef);
        }
    }

    public void register0(final ComponentDef componentDef) {
        synchronized (getRoot()) {
            super.register0(componentDef);
        }
    }

    public void registerDescendant(final LaContainer descendant) {
        synchronized (getRoot()) {
            super.registerDescendant(descendant);
        }
    }

    public void registerMap(final Object key, final ComponentDef componentDef, final LaContainer container) {
        synchronized (getRoot()) {
            super.registerMap(key, componentDef, container);
        }
    }

    public void setClassLoader(final ClassLoader classLoader) {
        synchronized (getRoot()) {
            super.setClassLoader(classLoader);
        }
    }

    public void setExternalContext(final ExternalContext externalContext) {
        synchronized (getRoot()) {
            super.setExternalContext(externalContext);
        }
    }

    public void setExternalContextComponentDefRegister(final ExternalContextComponentDefRegister register) {
        synchronized (getRoot()) {
            super.setExternalContextComponentDefRegister(register);
        }
    }

    public void setNamespace(final String namespace) {
        synchronized (getRoot()) {
            super.setNamespace(namespace);
        }
    }

}
