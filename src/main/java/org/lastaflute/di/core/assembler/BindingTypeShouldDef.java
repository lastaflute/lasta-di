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

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.util.BindingUtil;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.log.LaLogger;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class BindingTypeShouldDef extends AbstractBindingTypeDef {

    private static final LaLogger logger = LaLogger.getLogger(BindingTypeShouldDef.class);

    protected BindingTypeShouldDef(String name) {
        super(name);
    }

    protected void doBindProperty(ComponentDef componentDef, PropertyDesc propertyDesc, Object component) {
        if (!bindAutoProperty(componentDef, propertyDesc, component)
                && BindingUtil.isPropertyAutoBindable(propertyDesc.getPropertyType())) {
            logger.log("WSSR0008",
                    new Object[] { BindingUtil.getComponentClass(componentDef, component).getName(), propertyDesc.getPropertyName() });
        }
    }

    protected void doBindResourceField(ComponentDef componentDef, Field field, Object component) {
        if (!bindAutoResourceField(componentDef, field, component) && BindingUtil.isFieldAutoBindable(field.getType())) {
            logger.log("WSSR0008", new Object[] { BindingUtil.getComponentClass(componentDef, component).getName(), field.getName() });
        }
    }

    @Override
    public String toString() {
        return "shouldDef:{" + name + "}";
    }
}
