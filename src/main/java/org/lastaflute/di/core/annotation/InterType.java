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
package org.lastaflute.di.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * クラスにインタータイプを適用することを示します。
 * <p>
 * diconファイルの<code>&lt;interType&gt;</code>要素で指定する項目を設定するためのアノテーションです。
 * </p>
 * 
 * @author modified by jflute (originated in Seasar)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InterType {

    /**
     * 適用するインタータイプを示すOGNL式です。
     * 
     * @return 適用するインタータイプを示すOGNL式
     */
    String[] value();

}
