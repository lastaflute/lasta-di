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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lastaflute.di.util.LdiMapUtil.MapFactory;

/**
 * {@link java.util.concurrent.ConcurrentHashMap}を作成するファクトリの実装です。
 * 
 * @author modified by jflute (originated in Seasar)
 */
@SuppressWarnings("unchecked")
public class ConcurrentMapFactory implements MapFactory {

    public Map create() {
        return new ConcurrentHashMap();
    }

    public Map create(final int initialCapacity) {
        return new ConcurrentHashMap(initialCapacity);
    }

    public Map create(final int initialCapacity, final float loadFactor) {
        return new ConcurrentHashMap(initialCapacity, loadFactor, initialCapacity);
    }

}
