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
package org.lastaflute.di.core.external;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractUnmodifiableExternalContextMap extends AbstractExternalContextMap {

    public AbstractUnmodifiableExternalContextMap() {
    }

    public final Set<Map.Entry<String, Object>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    public final Set<String> keySet() {
        return Collections.unmodifiableSet(super.keySet());
    }

    public final Collection<Object> values() {
        return Collections.unmodifiableCollection(super.values());
    }

    public final void clear() {
        throw new UnsupportedOperationException();
    }

    public final Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    public final void putAll(Map<? extends String, ? extends Object> map) {
        throw new UnsupportedOperationException();
    }

    public final Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    protected final void setAttribute(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    protected final void removeAttribute(String key) {
        throw new UnsupportedOperationException();
    }
}
