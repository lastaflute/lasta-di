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
 * @author modified by jflute (originated in Seasar)
 */
public interface ArgDefAware {

    /**
     * 引数定義を追加します。
     * 
     * @param argDef
     *            引数定義
     */
    public void addArgDef(ArgDef argDef);

    /**
     * 登録されている{@link ArgDef 引数定義}の数を返します。
     * 
     * @return 登録されている引数定義の数
     */
    public int getArgDefSize();

    /**
     * 指定されたインデックス番号<code>index</code>の引数定義を返します。
     * <p>
     * インデックス番号は、 登録した順番に 0,1,2,… となります。
     * </p>
     * 
     * @param index
     *            引数定義を指定するインデックス番号
     * @return 引数定義
     */
    public ArgDef getArgDef(int index);
}
