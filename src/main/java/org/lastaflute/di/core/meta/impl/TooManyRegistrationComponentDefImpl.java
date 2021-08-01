/*
 * Copyright 2015-2021 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.exception.TooManyRegistrationComponentException;
import org.lastaflute.di.core.meta.TooManyRegistrationComponentDef;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TooManyRegistrationComponentDefImpl extends SimpleComponentDef implements TooManyRegistrationComponentDef {

    private final Object key;
    private final List<ComponentDef> componentDefs = new ArrayList<ComponentDef>();

    public TooManyRegistrationComponentDefImpl(Object key) {
        this.key = key;
    }

    public void addComponentDef(ComponentDef cd) {
        componentDefs.add(cd);
    }

    public Object getComponent() throws TooManyRegistrationComponentException {
        // *no needed because of HotdeployBehavior synchronization by jflute (2017/06/25)
        //final Object foundByCreator = findComponentByCreatorThreadCode();
        //if (foundByCreator != null) {
        //    return foundByCreator;
        //}
        throwTooManyRegistrationComponentException();
        return null; // unreachacle
    }

    protected void throwTooManyRegistrationComponentException() {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Too many registration components.");
        br.addItem("Component Key");
        br.addElement(key);
        br.addItem("Registered Components");
        for (ComponentDef def : getComponentDefs()) {
            final String componentName = def.getComponentName();
            final String componentType = def.getComponentClass().getName();
            Object componentInstance = null;
            try {
                componentInstance = def.getComponent();
            } catch (RuntimeException ignored) { // nested too many...no way but just in case
            }
            final String definedDiXml = def.getContainer().getPath();
            br.addElement("componentName: " + componentName);
            br.addElement("  componentType: " + componentType);
            if (componentInstance != null) { // just in case
                br.addElement("  componentInstance: " + componentInstance);
                br.addElement("  classLoader: " + componentInstance.getClass().getClassLoader());
            }
            br.addElement("  definedDiXml: " + definedDiXml);
        }
        final String msg = br.buildExceptionMessage();
        throw new TooManyRegistrationComponentException(msg, key, componentDefs);
    }

    public int getComponentDefSize() {
        return componentDefs.size();
    }

    public ComponentDef getComponentDef(int index) {
        return componentDefs.get(index);
    }

    public ComponentDef[] getComponentDefs() {
        return componentDefs.toArray(new ComponentDef[getComponentDefSize()]);
    }

    public Class<?>[] getComponentClasses() {
        final Class<?>[] classes = new Class[getComponentDefSize()];
        for (int i = 0; i < classes.length; ++i) {
            classes[i] = getComponentDef(i).getComponentClass();
        }
        return classes;
    }
}