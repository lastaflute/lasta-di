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
package org.lastaflute.di.core.deployer;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.exception.CyclicReferenceComponentException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SingletonComponentDeployer extends AbstractComponentDeployer {

    private Object component;
    private boolean instantiating = false;

    public SingletonComponentDeployer(ComponentDef componentDef) {
        super(componentDef);
    }

    @Override
    public void init() {
        deploy();
    }

    @Override
    public Object deploy() {
        if (component == null) {
            assemble();
        }
        return component;
    }

    private void assemble() {
        if (instantiating) {
            throw new CyclicReferenceComponentException(getComponentDef().getComponentClass());
        }
        instantiating = true;
        try {
            component = getConstructorAssembler().assemble();
        } finally {
            instantiating = false;
        }
        getPropertyAssembler().assemble(component);
        getInitMethodAssembler().assemble(component);
    }

    @Override
    public void injectDependency(Object component) {
        throw new UnsupportedOperationException("injectDependency");
    }

    @Override
    public void destroy() {
        if (component == null) {
            return;
        }
        getDestroyMethodAssembler().assemble(component);
        component = null;
    }
}