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

import java.util.Iterator;
import java.util.Map;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class CaseInsensitiveMap extends ArrayMap {

    private static final long serialVersionUID = 1L;

    public CaseInsensitiveMap() {
        super();
    }

    public CaseInsensitiveMap(int capacity) {
        super(capacity);
    }

    public final boolean containsKey(String key) {
        return super.containsKey(convertKey(key));
    }

    public final Object get(Object key) {
        return super.get(convertKey(key));
    }

    public final Object put(Object key, Object value) {
        return super.put(convertKey(key), value);
    }

    public final void putAll(Map map) {
        for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            put(convertKey(entry.getKey()), entry.getValue());
        }
    }

    public final Object remove(Object key) {
        return super.remove(convertKey(key));
    }

    public boolean containsKey(Object key) {
        return super.containsKey(convertKey(key));
    }

    private static String convertKey(Object key) {
        return ((String) key).toLowerCase();
    }

}
