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
package org.lastaflute.di.core.meta;


/**
 * コンポーネントの付加情報を定義するためのインターフェースです。
 * <p>
 * <code>&lt;components&gt;</code>、<code>&lt;component&gt;</code>、<code>&lt;arg&gt;</code>、<code>&lt;property&gt;</code>タグで
 * 定義したコンポーネントやプロパティに対し、<code>&lt;meta&gt;</code>タグで定義したメタデータ定義を保持します。
 * </p>
 * 
 * @author modified by jflute (originated in Seasar)
 * @author Tsuyoshi Yamamoto
 */
public interface MetaDef extends ArgDef {

    /**
     * メタデータ定義の名前を返します。
     * 
     * @return メタデータ定義名
     */
    public String getName();
}