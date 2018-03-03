/*
 * Copyright 2015-2018 the original author or authors.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiMapUtil {

    protected static final MapFactory factory = getMapFactory();

    public static <KEY, VALUE> Map<KEY, VALUE> createHashMap() { // thread safe
        return factory.create();
    }

    public static <KEY, VALUE> Map<KEY, VALUE> createHashMap(final int initialCapacity) { // thread safe
        return factory.create(initialCapacity);
    }

    public static <KEY, VALUE> Map<KEY, VALUE> createHashMap(final int initialCapacity, final float loadFactor) {
        return factory.create(initialCapacity, loadFactor);
    }

    protected static MapFactory getMapFactory() {
        try {
            final Class<?> clazz = ConcurrentMapFactory.class;
            return (MapFactory) clazz.newInstance();
        } catch (final Throwable ignore) {}
        return new SynchronizedMapFactory();
    }

    /**
     * @author modified by jflute (originated in Seasar)
     */
    interface MapFactory {

        <KEY, VALUE> Map<KEY, VALUE> create();

        <KEY, VALUE> Map<KEY, VALUE> create(int initialCapacity);

        <KEY, VALUE> Map<KEY, VALUE> create(int initialCapacity, float loadFactor);
    }

    /**
     * @author modified by jflute (originated in Seasar)
     */
    public static class SynchronizedMapFactory implements MapFactory {

        public <KEY, VALUE> Map<KEY, VALUE> create() {
            return Collections.synchronizedMap(new HashMap<KEY, VALUE>());
        }

        public <KEY, VALUE> Map<KEY, VALUE> create(final int initialCapacity) {
            return Collections.synchronizedMap(new HashMap<KEY, VALUE>(initialCapacity));
        }

        public <KEY, VALUE> Map<KEY, VALUE> create(final int initialCapacity, final float loadFactor) {
            return Collections.synchronizedMap(new HashMap<KEY, VALUE>(initialCapacity, loadFactor));
        }
    }
}
