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

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.core.meta.AutoBindingDef;
import org.lastaflute.di.core.meta.PropertyDef;
import org.lastaflute.di.helper.beans.exception.IllegalPropertyRuntimeException;

/**
 * プロパティ・インジェクションやフィールド・インジェクションを実行してコンポーネントを組み立てます。
 * <p>
 * インジェクションの実行は、 {@link PropertyDef プロパティ定義}に基づいて行います。 プロパティ定義が指定されていない場合の動作は、
 * {@link AutoBindingDef 自動バインディング定義}に基づきます。
 * </p>
 * <p>
 * また、 {@link ComponentDef コンポーネント定義}の<code>externalBinding</code>属性が<code>true</code>の場合、
 * {@link ExternalContext}の保持している値もバインディングの対象とします。
 * </p>
 * 
 * @author modified by jflute (originated in Seasar)
 * @author modified by jflute (originated in Seasar)
 * 
 * @see PropertyDef
 * @see AutoBindingDef
 * @see ExternalContext
 */
public interface PropertyAssembler {

    /**
     * 指定された<code>component</code>に対して、 プロパティ・インジェクションやフィールド・インジェクションを実行します。
     * コンポーネント定義の<code>externalBinding</code>属性が<code>true</code>にも関わらず、
     * {@link ExternalContext}がS2コンテナに設定されていない場合には、
     * EmptyRuntimeExceptionをスローします。
     * 
     * @param component
     *            S2コンテナ上のコンポーネントがセットされる対象
     * @throws org.lastaflute.di.helper.beans.exception.IllegalPropertyRuntimeException
     *             プロパティが見つからないなどの理由でインジェクションに失敗した場合
     * @throws org.lastaflute.di.exception.EmptyRuntimeException
     *             ExternalContextがS2コンテナに設定されていない場合
     */
    public void assemble(Object component) throws IllegalPropertyRuntimeException;
}
