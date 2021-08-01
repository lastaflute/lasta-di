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
package org.lastaflute.di.core.assembler;

import java.lang.reflect.Field;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.helper.beans.PropertyDesc;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class BindingTypeNoneDef extends AbstractBindingTypeDef {

    protected BindingTypeNoneDef(String name) {
        super(name);
    }

    protected void doBindProperty(ComponentDef componentDef, PropertyDesc propertyDesc, Object component) {
    }

    protected void doBindResourceField(ComponentDef componentDef, Field field, Object component) {
    }
}