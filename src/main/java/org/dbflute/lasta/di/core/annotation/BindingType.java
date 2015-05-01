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
package org.dbflute.lasta.di.core.annotation;

import org.dbflute.lasta.di.core.exception.IllegalAutoBindingPropertyRuntimeException;
import org.dbflute.lasta.di.core.meta.BindingTypeDef;

/**
 * {@link BindingTypeDef バインディングタイプ}の列挙型です。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public enum BindingType {

    /**
     * バインディングが必須です。
     * <p>
     * バインディングできない場合は{@link IllegalAutoBindingPropertyRuntimeException}がスローされます。
     * </p>
     */
    MUST,

    /**
     * バインディングすべきです。
     * <p>
     * バインディングできない場合は警告メッセージが出力されます。
     * </p>
     */
    SHOULD,

    /**
     * バインディングは任意です。
     * <p>
     * バインディングできなくても何も起きません。
     * </p>
     */
    MAY,

    /**
     * バインディングしません。
     * <p>
     * 自動バインディングが有効になっている場合に特定のプロパティのみDI非対象にするために使用されます。
     * </p>
     */
    NONE;

    /**
     * バインディングの名前を返します。
     * 
     * @return バインディングの名前
     */
    public String getName() {
        return toString().toLowerCase();
    }

}
