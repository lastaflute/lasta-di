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
package org.lastaflute.di.util.tiger;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * Map<String, Integer> map = map("a", 1).$("b", 2).$("c", 3).$();
 * </pre>
 * @param <K> The type of key.
 * @param <V> The type of value.
 * @author modified by jflute (originated in Seasar)
 */
public class Maps<K, V> {

    protected Map<K, V> map;

    public static <KEY, VALUE> Maps<KEY, VALUE> map(KEY key, VALUE value) {
        return linkedHashMap(key, value);
    }

    public static <KEY, VALUE> Maps<KEY, VALUE> concurrentHashMap(KEY key, VALUE value) {
        return new Maps<KEY, VALUE>(new ConcurrentHashMap<KEY, VALUE>()).$(key, value);
    }

    public static <KEY, VALUE> Maps<KEY, VALUE> hashMap(KEY key, VALUE value) {
        return new Maps<KEY, VALUE>(new HashMap<KEY, VALUE>()).$(key, value);
    }

    public static <KEY, VALUE> Maps<KEY, VALUE> hashtable(KEY key, VALUE value) {
        return new Maps<KEY, VALUE>(new Hashtable<KEY, VALUE>()).$(key, value);
    }

    public static <KEY, VALUE> Maps<KEY, VALUE> identityHashMap(KEY key, VALUE value) {
        return new Maps<KEY, VALUE>(new IdentityHashMap<KEY, VALUE>()).$(key, value);
    }

    public static <KEY, VALUE> Maps<KEY, VALUE> linkedHashMap(KEY key, VALUE value) {
        return new Maps<KEY, VALUE>(new LinkedHashMap<KEY, VALUE>()).$(key, value);
    }

    public static <KEY, VALUE> Maps<KEY, VALUE> treeMap(KEY key, VALUE value) {
        return new Maps<KEY, VALUE>(new TreeMap<KEY, VALUE>()).$(key, value);
    }

    public static <KEY, VALUE> Maps<KEY, VALUE> weakHashMap(KEY key, VALUE value) {
        return new Maps<KEY, VALUE>(new WeakHashMap<KEY, VALUE>()).$(key, value);
    }

    protected Maps(Map<K, V> map) {
        this.map = map;
    }

    public Maps<K, V> $(K key, V value) {
        map.put(key, value);
        return this;
    }

    public Map<K, V> $() {
        return map;
    }

}
