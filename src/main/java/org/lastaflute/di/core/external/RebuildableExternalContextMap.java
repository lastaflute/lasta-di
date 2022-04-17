/*
 * Copyright 2015-2022 the original author or authors.
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

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.lastaflute.di.core.smart.hot.HotdeployClassLoader;
import org.lastaflute.di.core.smart.hot.HotdeployUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class RebuildableExternalContextMap extends AbstractExternalContextMap {

    protected static WeakReference<ClassLoader> hotdeployClassLoader = new WeakReference<ClassLoader>(null);
    protected static Set<Object> rebuiltNames = new HashSet<Object>(64);

    public Object get(final Object key) {
        final Object value = getAttribute(key.toString());
        if (value == null || !isHotdeployMode()) {
            return value;
        }
        if (rebuiltNames.contains(key)) {
            return value;
        }
        final Object rebuiltValue = HotdeployUtil.rebuildValue(value);
        rebuiltNames.add(key);
        setAttribute(key.toString(), rebuiltValue);
        return rebuiltValue;
    }

    public Object put(String key, Object value) {
        final Object oldValue = super.put(key, value);
        if (isHotdeployMode()) {
            rebuiltNames.add(key);
        }
        return oldValue;
    }

    public void putAll(Map<? extends String, ? extends Object> map) {
        for (final Iterator<?> it = map.entrySet().iterator(); it.hasNext();) {
            @SuppressWarnings("unchecked")
            final Entry<String, Object> entry = (Entry<String, Object>) it.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    protected boolean isHotdeployMode() {
        if (!HotdeployUtil.isHotdeploy()) {
            return false;
        }
        final ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        if (!(currentLoader instanceof HotdeployClassLoader)) {
            return false;
        }
        if (currentLoader != hotdeployClassLoader.get()) {
            hotdeployClassLoader = new WeakReference<ClassLoader>(currentLoader);
            rebuiltNames.clear();
        }
        return true;
    }
}
