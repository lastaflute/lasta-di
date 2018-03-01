/*
 * Copyright 2015-2018 the original author or authors.
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

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.meta.MetaDef;
import org.lastaflute.di.core.meta.impl.MetaDefImpl;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class MetaCustomizer extends AbstractCustomizer {

    protected ComponentDef componentDef;

    protected void doCustomize(final ComponentDef componentDef) {
        final MetaDef metaDef = getMetaDef();
        if (metaDef == null) {
            return;
        }
        for (int i = 0; i < metaDef.getMetaDefSize(); ++i) {
            final MetaDef meta = metaDef.getMetaDef(i);
            componentDef.addMetaDef(new MetaDefImpl(meta.getName(), meta.getValue(Object.class)));
        }
    }

    protected MetaDef getMetaDef() {
        return componentDef.getMetaDef("autoRegister");
    }

    public void setComponentDef(final ComponentDef componentDef) {
        this.componentDef = componentDef;
    }
}
