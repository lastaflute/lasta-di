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

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class CaseInsensitiveSet extends AbstractSet<String>implements Set<String>, Serializable {

    static final long serialVersionUID = 0L;
    private static final Object PRESENT = new Object();

    private transient Map<String, Object> map;

    public CaseInsensitiveSet() {
        map = new CaseInsensitiveMap();
    }

    public CaseInsensitiveSet(Collection<String> c) {
        map = new CaseInsensitiveMap(Math.max((int) (c.size() / .75f) + 1, 16));
        addAll(c);
    }

    public CaseInsensitiveSet(int initialCapacity) {
        map = new CaseInsensitiveMap(initialCapacity);
    }

    public Iterator<String> iterator() {
        return map.keySet().iterator();
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    public boolean add(String o) {
        return map.put(o, PRESENT) == null;
    }

    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    public void clear() {
        map.clear();
    }
}
