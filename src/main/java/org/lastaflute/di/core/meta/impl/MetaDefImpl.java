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
package org.lastaflute.di.core.meta.impl;

import org.lastaflute.di.core.meta.MetaDef;

/**
 * {@link MetaDef}の実装クラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class MetaDefImpl extends ArgDefImpl implements MetaDef {

    private String name;

    /**
     * {@link MetaDefImpl}を作成します。
     */
    public MetaDefImpl() {
    }

    /**
     * {@link MetaDefImpl}を作成します。
     * 
     * @param name
     */
    public MetaDefImpl(String name) {
        this.name = name;
    }

    /**
     * {@link MetaDefImpl}を作成します。
     * 
     * @param name
     * @param value
     */
    public MetaDefImpl(String name, Object value) {
        super(value);
        this.name = name;
    }

    /**
     * @see org.lastaflute.di.core.meta.MetaDef#getName()
     */
    public String getName() {
        return name;
    }
}