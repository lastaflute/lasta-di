/*
 * Copyright 2015-2024 the original author or authors.
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
package org.lastaflute.di.util.tiger;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * @param <E>
 * @author modified by jflute (originated in Seasar)
 */
public class IterableAdapter<E> implements Iterable<E>, Iterator<E> {

    Enumeration<E> enumeration;

    public static <E> IterableAdapter<E> iterable(Enumeration<E> enumeration) {
        return new IterableAdapter<E>(enumeration);
    }

    public IterableAdapter(final Enumeration<E> enumeration) {
        this.enumeration = enumeration;
    }

    public Iterator<E> iterator() {
        return this;
    }

    /**
     * @see java.util.Iterator#hasNext
     */
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    /**
     * @see java.util.Iterator#hasNext
     */
    public E next() {
        return enumeration.nextElement();
    }

    /**
     * @see java.util.Iterator#remove
     */
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
