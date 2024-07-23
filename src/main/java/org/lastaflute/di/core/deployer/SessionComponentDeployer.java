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
package org.lastaflute.di.core.deployer;

import java.util.Map;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.exception.EmptyRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SessionComponentDeployer extends AbstractComponentDeployer {

    public SessionComponentDeployer(ComponentDef componentDef) {
        super(componentDef);
    }

    public Object deploy() {
        ComponentDef cd = getComponentDef();
        ExternalContext extCtx = cd.getContainer().getRoot().getExternalContext();
        if (extCtx == null) {
            throw new EmptyRuntimeException("externalContext");
        }
        Map<String, Object> sessionMap = extCtx.getSessionMap();
        String componentName = getComponentName();
        Object component = sessionMap.get(componentName);
        if (component == null) {
            component = getConstructorAssembler().assemble();
            getPropertyAssembler().assemble(component);
            getInitMethodAssembler().assemble(component);
            sessionMap.put(componentName, component);
        }
        return component;
    }

    public void injectDependency(Object component) {
        throw new UnsupportedOperationException("injectDependency");
    }

    public void init() {
    }

    public void destroy() {
    }
}