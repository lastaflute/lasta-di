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
package org.lastaflute.di.core.customizer;

import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.expression.ScriptingExpression;
import org.lastaflute.di.core.meta.InterTypeDef;
import org.lastaflute.di.core.meta.impl.InterTypeDefImpl;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class InterTypeCustomizer extends AbstractCustomizer {

    protected final List<String> interTypeNames = new ArrayList<String>();

    public void setInterTypeName(final String interTypeName) {
        interTypeNames.clear();
        interTypeNames.add(interTypeName);
    }

    public void addInterTypeName(final String interTypeName) {
        interTypeNames.add(interTypeName);
    }

    protected void doCustomize(final ComponentDef componentDef) {
        for (int i = 0; i < interTypeNames.size(); ++i) {
            final InterTypeDef interTypeDef = new InterTypeDefImpl();
            interTypeDef.setExpression(new ScriptingExpression((String) interTypeNames.get(i)));
            componentDef.addInterTypeDef(interTypeDef);
        }
    }
}
