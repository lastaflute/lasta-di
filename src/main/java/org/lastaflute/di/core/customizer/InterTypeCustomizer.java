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
package org.lastaflute.di.core.customizer;

import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.expression.ScriptingExpression;
import org.lastaflute.di.core.meta.InterTypeDef;
import org.lastaflute.di.core.meta.impl.InterTypeDefImpl;

/**
 * {@link org.lastaflute.di.core.ComponentDef コンポーネント定義}に
 * {@link org.lastaflute.di.core.meta.InterTypeDef インタータイプ定義}を
 * 登録するコンポーネントカスタマイザです。
 * <p>
 * カスタマイザには、インタータイプのコンポーネント名を複数設定することができます。
 * インタータイプ名が複数設定された場合は、設定された順にインタータイプ定義をコンポーネント定義に登録します。
 * 最初に設定された名前を持つインタータイプが、後に設定された名前を持つインタータイプよりも先に呼び出されることになります。
 * </p>
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class InterTypeCustomizer extends AbstractCustomizer {

    /**
     * インタータイプ名のリストです。
     */
    protected final List interTypeNames = new ArrayList();

    /**
     * コンポーネント定義に登録するインタータイプのコンポーネント名を設定します。
     * <p>
     * すでに設定されているインタータイプ名は破棄されます。
     * </p>
     * 
     * @param interTypeName
     *            インタータイプのコンポーネント名
     */
    public void setInterTypeName(final String interTypeName) {
        interTypeNames.clear();
        interTypeNames.add(interTypeName);
    }

    /**
     * コンポーネント定義に登録するインタータイプのコンポーネント名を追加します。
     * 
     * @param interTypeName
     *            インタータイプのコンポーネント名
     */
    public void addInterTypeName(final String interTypeName) {
        interTypeNames.add(interTypeName);
    }

    /**
     * カスタマイズ対象のコンポーネント定義をカスタマイズをします。
     * <p>
     * 設定されたインタータイプ名を持つインタータイプ定義をコンポーネント定義に登録します。
     * インタータイプ名が複数設定された場合は、設定された順にインタータイプ定義をコンポーネント定義に登録します。
     * </p>
     * 
     * @param componentDef
     *            コンポーネント定義
     */
    protected void doCustomize(final ComponentDef componentDef) {
        for (int i = 0; i < interTypeNames.size(); ++i) {
            final InterTypeDef interTypeDef = new InterTypeDefImpl();
            interTypeDef.setExpression(new ScriptingExpression((String) interTypeNames.get(i)));
            componentDef.addInterTypeDef(interTypeDef);
        }
    }

}
