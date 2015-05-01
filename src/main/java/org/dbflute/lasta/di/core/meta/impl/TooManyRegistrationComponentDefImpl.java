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

import java.util.ArrayList;
import java.util.List;

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.exception.TooManyRegistrationComponentException;
import org.dbflute.lasta.di.core.exception.TooManyRegistrationRuntimeException;
import org.dbflute.lasta.di.core.meta.TooManyRegistrationComponentDef;
import org.dbflute.lasta.di.helper.misc.LdiExceptionMessageBuilder;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TooManyRegistrationComponentDefImpl extends SimpleComponentDef implements TooManyRegistrationComponentDef {

    private final Object key;
    private List componentDefs = new ArrayList();

    public TooManyRegistrationComponentDefImpl(Object key) {
        this.key = key;
    }

    public void addComponentDef(ComponentDef cd) {
        componentDefs.add(cd);
    }

    public Object getComponent() throws TooManyRegistrationRuntimeException {
        throwTooManyRegistrationRuntimeException();
        return null; // unreachacle
    }

    protected void throwTooManyRegistrationRuntimeException() {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Too many registration components.");
        br.addItem("Component Key");
        br.addElement(key);
        br.addItem("Registered Components");
        for (ComponentDef def : getComponentDefs()) {
            final String componentName = def.getComponentName();
            final String typeName = def.getComponentClass().getName();
            final String definedPath = def.getContainer().getPath();
            br.addElement("name=" + componentName + ", type=" + typeName + ", path=" + definedPath);
        }
        final String msg = br.buildExceptionMessage();
        throw new TooManyRegistrationComponentException(msg);
    }

    public int getComponentDefSize() {
        return componentDefs.size();
    }

    public ComponentDef getComponentDef(int index) {
        return (ComponentDef) componentDefs.get(index);
    }

    public ComponentDef[] getComponentDefs() {
        return (ComponentDef[]) componentDefs.toArray(new ComponentDef[getComponentDefSize()]);
    }

    public Class[] getComponentClasses() {
        final Class[] classes = new Class[getComponentDefSize()];
        for (int i = 0; i < classes.length; ++i) {
            classes[i] = getComponentDef(i).getComponentClass();
        }
        return classes;
    }
}