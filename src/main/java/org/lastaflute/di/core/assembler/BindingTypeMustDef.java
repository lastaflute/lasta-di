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
package org.lastaflute.di.core.assembler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.exception.AutoBindingFailureException;
import org.lastaflute.di.core.exception.IllegalAutoBindingPropertyRuntimeException;
import org.lastaflute.di.core.util.BindingUtil;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class BindingTypeMustDef extends AbstractBindingTypeDef {

    protected BindingTypeMustDef(String name) {
        super(name);
    }

    @Override
    protected void doBindProperty(ComponentDef componentDef, PropertyDesc propertyDesc, Object component) {
        if (!bindAutoProperty(componentDef, propertyDesc, component)) {
            // TODO jflute lastaflute: [E] fitting: DI :: cannot find component error handling
            final Class<?> componentClass = BindingUtil.getComponentClass(componentDef, component);
            final String propertyName = propertyDesc.getPropertyName();
            throw new IllegalAutoBindingPropertyRuntimeException(componentClass, propertyName);
        }
    }

    @Override
    protected void doBindResourceField(ComponentDef componentDef, Field field, Object component) {
        if (!bindAutoResourceField(componentDef, field, component)) {
            throwAutoBindingFailureException(componentDef, field, component);
        }
    }

    protected void throwAutoBindingFailureException(ComponentDef componentDef, Field field, Object component) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to auto-inject to the resource field.");
        br.addItem("Component Def");
        br.addElement(componentDef);
        br.addItem("Declaring Bean");
        br.addElement(component);
        br.addItem("Resource Field");
        br.addElement(Modifier.toString(field.getModifiers()) + " " + field.getType().getName() + " " + field.getName());
        final String msg = br.buildExceptionMessage();
        throw new AutoBindingFailureException(msg);
    }

    @Override
    public String toString() {
        return "mustDef:{" + name + "}";
    }
}