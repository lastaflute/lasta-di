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
package org.lastaflute.di.core.autoregister;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.aop.Pointcut;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.factory.AspectDefFactory;
import org.lastaflute.di.core.meta.AspectDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class InterfaceAspectAutoRegister {

    public static final String INIT_METHOD = "registerAll";

    private LaContainer container;
    private MethodInterceptor interceptor;
    private Class<?> targetInterface;
    private Pointcut pointcut;

    public void setContainer(LaContainer container) {
        this.container = container;
    }

    public void setInterceptor(MethodInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public void setTargetInterface(Class<?> targetInterface) {
        if (!targetInterface.isInterface()) {
            throw new IllegalArgumentException(targetInterface.getName());
        }
        this.targetInterface = targetInterface;
        this.pointcut = AspectDefFactory.createPointcut(targetInterface);
    }

    public void registerAll() {
        for (int i = 0; i < container.getComponentDefSize(); ++i) {
            ComponentDef cd = container.getComponentDef(i);
            register(cd);
        }
    }

    protected void register(ComponentDef componentDef) {
        Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }
        if (!targetInterface.isAssignableFrom(componentClass)) {
            return;
        }
        registerInterceptor(componentDef);
    }

    protected void registerInterceptor(ComponentDef componentDef) {
        AspectDef aspectDef = AspectDefFactory.createAspectDef(interceptor, pointcut);
        componentDef.addAspectDef(aspectDef);
    }
}