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
package org.dbflute.lasta.di.core.customizer;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.lasta.di.core.ComponentDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class CustomizerChain extends AbstractCustomizer {

    private final List<ComponentCustomizer> customizers = new ArrayList<ComponentCustomizer>();

    public int getCustomizerSize() {
        return customizers.size();
    }

    public ComponentCustomizer getCustomizer(int index) {
        return customizers.get(index);
    }

    public void addCustomizer(ComponentCustomizer customizer) {
        customizers.add(customizer);
    }

    public void addAspectCustomizer(final String interceptorName) {
        AspectCustomizer customizer = newAspectCustomizer();
        customizer.setInterceptorName(interceptorName);
        addCustomizer(customizer);
    }

    public void addAspectCustomizer(final String interceptorName, final String pointcut) {
        AspectCustomizer customizer = newAspectCustomizer();
        customizer.setInterceptorName(interceptorName);
        customizer.setPointcut(pointcut);
        addCustomizer(customizer);
    }

    public void addAspectCustomizer(final String interceptorName, final boolean useLookupAdapter) {
        AspectCustomizer customizer = newAspectCustomizer();
        customizer.setInterceptorName(interceptorName);
        customizer.setUseLookupAdapter(useLookupAdapter);
        addCustomizer(customizer);
    }

    public void addAspectCustomizer(final String interceptorName, final String pointcut, final boolean useLookupAdapter) {
        AspectCustomizer customizer = newAspectCustomizer();
        customizer.setInterceptorName(interceptorName);
        customizer.setPointcut(pointcut);
        customizer.setUseLookupAdapter(useLookupAdapter);
        addCustomizer(customizer);
    }

    protected AspectCustomizer newAspectCustomizer() {
        return new AspectCustomizer();
    }

    @Override
    protected void doCustomize(ComponentDef componentDef) {
        for (int i = 0; i < getCustomizerSize(); ++i) {
            ComponentCustomizer customizer = getCustomizer(i);
            customizer.customize(componentDef);
        }
    }
}
