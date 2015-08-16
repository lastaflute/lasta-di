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
package org.lastaflute.di.core.deployer;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.exception.ClassUnmatchRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class OuterComponentDeployer extends AbstractComponentDeployer {

    public OuterComponentDeployer(ComponentDef componentDef) {
        super(componentDef);
    }

    public Object deploy() {
        throw new UnsupportedOperationException("deploy");
    }

    public void injectDependency(Object outerComponent) {
        checkComponentClass(outerComponent);
        getPropertyAssembler().assemble(outerComponent);
        getInitMethodAssembler().assemble(outerComponent);
    }

    private void checkComponentClass(Object outerComponent) throws ClassUnmatchRuntimeException {
        Class<?> componentClass = getComponentDef().getComponentClass();
        if (componentClass == null) {
            return;
        }
        if (!componentClass.isInstance(outerComponent)) {
            throw new ClassUnmatchRuntimeException(componentClass, outerComponent.getClass());
        }
    }

    public void init() {
    }

    public void destroy() {
    }
}
