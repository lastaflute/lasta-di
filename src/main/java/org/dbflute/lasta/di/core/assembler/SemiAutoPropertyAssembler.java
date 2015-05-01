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
package org.dbflute.lasta.di.core.assembler;

import java.util.HashSet;
import java.util.Set;

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.meta.BindingTypeDef;
import org.dbflute.lasta.di.core.meta.PropertyDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SemiAutoPropertyAssembler extends AbstractPropertyAssembler {

    public SemiAutoPropertyAssembler(ComponentDef componentDef) {
        super(componentDef);
    }

    public void assemble(Object component) {
        if (component == null) {
            return;
        }
        final ComponentDef cd = getComponentDef();
        final BindingTypeDef bindingTypeDef = BindingTypeDefFactory.getBindingTypeDef(BindingTypeDef.SHOULD_NAME);
        final Set<String> names = new HashSet<String>();
        final int defSize = cd.getPropertyDefSize();
        for (int i = 0; i < defSize; ++i) {
            final PropertyDef propDef = cd.getPropertyDef(i);
            propDef.getAccessTypeDef().bind(cd, propDef, bindingTypeDef, component);
            names.add(propDef.getPropertyName());
        }
        if (cd.isExternalBinding()) {
            bindExternally(getBeanDesc(component), cd, component, names);
        }
    }
}
