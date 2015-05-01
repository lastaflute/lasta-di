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
package org.dbflute.lasta.di.core.autoregister;

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.meta.MetaDef;
import org.dbflute.lasta.di.core.meta.impl.MetaDefImpl;

/**
 * メタ定義を自動登録するためのクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class MetaAutoRegister extends AbstractComponentTargetAutoRegister {

    /**
     * {@link ComponentDef}です。
     */
    protected ComponentDef componentDef;

    /**
     * コンポーネント定義を返します。
     * 
     * @return
     */
    public ComponentDef getComponentDef() {
        return componentDef;
    }

    /**
     * コンポーネント定義を設定します。
     * 
     * @param componentDef
     */
    public void setComponentDef(final ComponentDef componentDef) {
        this.componentDef = componentDef;
    }

    protected void register(final ComponentDef cd) {
        final MetaDef metaDef = componentDef.getMetaDef("autoRegister");
        if (metaDef == null) {
            return;
        }
        for (int i = 0; i < metaDef.getMetaDefSize(); ++i) {
            final MetaDef meta = metaDef.getMetaDef(i);
            cd.addMetaDef(new MetaDefImpl(meta.getName(), meta.getValue()));
        }
    }
}
