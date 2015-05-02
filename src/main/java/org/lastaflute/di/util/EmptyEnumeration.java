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
package org.lastaflute.di.util;

import java.util.Enumeration;

/**
 * 空の {@link Enumeration}です。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class EmptyEnumeration extends EnumerationAdapter {

    /**
     * {@link EmptyEnumeration}を作成します。
     */
    public EmptyEnumeration() {
        super(new EmptyIterator());
    }
}
