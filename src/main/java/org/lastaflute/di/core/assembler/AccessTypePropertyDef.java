/*
 * Copyright 2015-2016 the original author or authors.
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

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.meta.AccessTypeDef;
import org.lastaflute.di.core.meta.BindingTypeDef;
import org.lastaflute.di.core.meta.PropertyDef;
import org.lastaflute.di.core.util.BindingUtil;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AccessTypePropertyDef implements AccessTypeDef {

    public String getName() {
        return PROPERTY_NAME;
    }

    public void bind(ComponentDef componentDef, PropertyDef propertyDef, Object component) {
        final BindingTypeDef bindingTypeDef = propertyDef.getBindingTypeDef();
        bind(componentDef, propertyDef, bindingTypeDef, component);
    }

    public void bind(ComponentDef componentDef, PropertyDef propertyDef, BindingTypeDef bindingTypeDef, Object component) {
        final BeanDesc beanDesc = BindingUtil.getBeanDesc(componentDef, component);
        final PropertyDesc propertyDesc = beanDesc.getPropertyDesc(propertyDef.getPropertyName());
        bindingTypeDef.bind(componentDef, propertyDef, propertyDesc, component);
    }

    @Override
    public String toString() {
        return "fieldDef:{}";
    }
}
