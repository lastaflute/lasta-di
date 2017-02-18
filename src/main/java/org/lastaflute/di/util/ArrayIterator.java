/*
 * Copyright 2015-2017 the original author or authors.
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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ArrayIterator implements Iterator<Object> {

    private Object[] items_;
    private int index_ = 0;

    public ArrayIterator(Object items[]) {
        items_ = items;
    }

    public boolean hasNext() {
        return index_ < items_.length;
    }

    public Object next() {
        try {
            Object o = items_[index_];
            index_++;
            return o;
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException("index=" + index_);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
