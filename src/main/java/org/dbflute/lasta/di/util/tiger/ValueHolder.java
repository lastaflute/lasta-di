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
package org.dbflute.lasta.di.util.tiger;

/**
 * 値を保持するクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * @since 2.4.18
 * @param <T>
 *            値の型
 */
public class ValueHolder<T> {

    /** 値 */
    protected T value;

    /**
     * インスタンスを構築します。
     */
    public ValueHolder() {
    }

    /**
     * インスタンスを構築します。
     * 
     * @param value
     *            値
     */
    public ValueHolder(T value) {
        this.value = value;
    }

    /**
     * 値を返します。
     * 
     * @return 値
     */
    public T getValue() {
        return value;
    }

    /**
     * 値を設定します。
     * 
     * @param value
     *            値
     */
    public void setValue(final T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
