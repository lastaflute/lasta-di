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
package org.dbflute.lasta.di.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * {@link Iterator}を {@link Enumeration}にするためのアダブタです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class EnumerationAdapter implements Enumeration {

    private Iterator iterator;

    /**
     * {@link EnumerationAdapter}を作成します。
     * 
     * @param iterator
     */
    public EnumerationAdapter(Iterator iterator) {
        this.iterator = iterator;
    }

    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    public Object nextElement() {
        return iterator.next();
    }
}