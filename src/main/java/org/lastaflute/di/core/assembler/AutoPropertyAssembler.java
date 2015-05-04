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

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.meta.BindingTypeDef;
import org.lastaflute.di.core.meta.PropertyDef;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AutoPropertyAssembler extends AbstractPropertyAssembler {

    public AutoPropertyAssembler(ComponentDef componentDef) {
        super(componentDef);
    }

    @Override
    public void assemble(Object component) { // #injection_point
        if (component == null) {
            return;
        }
        final BeanDesc beanDesc = getBeanDesc(component);
        final ComponentDef componentDef = getComponentDef();
        final int defSize = componentDef.getPropertyDefSize();
        final Set<String> names = new HashSet<String>();
        for (int i = 0; i < defSize; ++i) {
            final PropertyDef propDef = componentDef.getPropertyDef(i);
            propDef.getAccessTypeDef().bind(componentDef, propDef, component);
            names.add(propDef.getPropertyName());
        }
        if (componentDef.isExternalBinding()) {
            bindExternally(beanDesc, componentDef, component, names);
        }
        setupPlainProperty(component, beanDesc, componentDef, names);
    }

    protected void setupPlainProperty(Object component, BeanDesc beanDesc, ComponentDef componentDef, Set<String> names) {
        // #lasta_di not support plain property DI, annotation DI only supported
        // but DBFlute and LastaFlute uses it so special handling here
        final BindingTypeDef bindingTypeDef = BindingTypeDefFactory.getBindingTypeDef(BindingTypeDef.SHOULD_NAME);
        final int descSize = beanDesc.getPropertyDescSize();
        for (int i = 0; i < descSize; ++i) {
            final PropertyDesc propDesc = beanDesc.getPropertyDesc(i);
            if (needsPlainPropertyInjection(propDesc)) {
                if (!names.contains(propDesc.getPropertyName())) {
                    bindingTypeDef.bind(componentDef, null, propDesc, component);
                }
            }
        }
    }

    protected boolean needsPlainPropertyInjection(PropertyDesc propDesc) {
        final Method writeMethod = propDesc.getWriteMethod();
        if (writeMethod == null) { // e.g. getter only or public field, are out of target 
            return false;
        }
        // TODO jflute lastaflute: [A] fitting: DI :: setter DI from LastaDiProperties
        // the property has setter
        final String fqcn = writeMethod.getDeclaringClass().getName();
        return fqcn.startsWith("org.lastaflute.") || fqcn.startsWith("org.dbflute.") || fqcn.startsWith("org.codelibs.robot.db");
    }
}