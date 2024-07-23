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
package org.lastaflute.di.core.external;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractExternalContextMap extends AbstractMap<String, Object> {

    private Set<Map.Entry<String, Object>> entrySet;
    private Set<String> keySet;
    private Collection<Object> values;

    public AbstractExternalContextMap() {
    }

    @Override
    public void clear() {
        final List<String> list = new ArrayList<String>(); // to avoid ConcurrentModificationException
        for (Iterator<String> it = getAttributeNames(); it.hasNext();) {
            list.add(it.next());
        }
        clearReally(list);
    }

    private void clearReally(List<String> keys) {
        for (Iterator<String> itr = keys.iterator(); itr.hasNext();) {
            removeAttribute(itr.next());
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return (getAttribute(key.toString()) != null);
    }

    @Override
    public boolean containsValue(Object value) {
        if (value != null) {
            for (Iterator<String> it = getAttributeNames(); it.hasNext();) {
                if (value.equals(getAttribute(it.next()))) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet(this);
        }
        return entrySet;
    }

    @Override
    public Object get(Object key) {
        return getAttribute(key.toString());
    }

    @Override
    public Object put(String key, Object value) {
        String keyStr = key.toString();
        Object o = getAttribute(keyStr);
        setAttribute(keyStr, value);
        return o;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> map) {
        for (Iterator<?> itr = map.entrySet().iterator(); itr.hasNext();) {
            @SuppressWarnings("unchecked")
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
            setAttribute(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean isEmpty() {
        return !getAttributeNames().hasNext();
    }

    @Override
    public Set<String> keySet() {
        if (keySet == null) {
            keySet = new KeySet(this);
        }
        return keySet;
    }

    @Override
    public Object remove(Object key) {
        String keyStr = key.toString();
        Object o = getAttribute(keyStr);
        removeAttribute(keyStr);
        return o;
    }

    @Override
    public Collection<Object> values() {
        if (values == null) {
            values = new ValuesCollection(this);
        }
        return values;
    }

    protected abstract Object getAttribute(String key);

    protected abstract void setAttribute(String key, Object value);

    protected abstract Iterator<String> getAttributeNames();

    protected abstract void removeAttribute(String key);

    abstract class AbstractExternalContextSet<ELEMENT> extends AbstractSet<ELEMENT> {

        public int size() {
            int size = 0;
            for (Iterator<ELEMENT> itr = iterator(); itr.hasNext(); size++) {
                itr.next();
            }
            return size;
        }
    }

    class EntrySet extends AbstractExternalContextSet<Map.Entry<String, Object>> {

        private AbstractExternalContextMap contextMap;

        public EntrySet(AbstractExternalContextMap contextMap) {
            this.contextMap = contextMap;
        }

        public Iterator<Map.Entry<String, Object>> iterator() {
            return new EntryIterator(contextMap);
        }

        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) o;
            return contextMap.remove(entry.getKey()) != null;
        }
    }

    class KeySet extends AbstractExternalContextSet<String> {

        private AbstractExternalContextMap contextMap;

        public KeySet(AbstractExternalContextMap contextMap) {
            this.contextMap = contextMap;
        }

        public Iterator<String> iterator() {
            return new KeyIterator(contextMap);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof String)) {
                return false;
            }
            String s = (String) o;
            return contextMap.remove(s) != null;
        }
    }

    class ValuesCollection extends AbstractCollection<Object> {

        private AbstractExternalContextMap contextMap;

        public ValuesCollection(AbstractExternalContextMap contextMap) {
            this.contextMap = contextMap;
        }

        public int size() {
            int size = 0;
            for (Iterator<Object> itr = iterator(); itr.hasNext(); size++) {
                itr.next();
            }
            return size;
        }

        public Iterator<Object> iterator() {
            return new ValuesIterator(contextMap);
        }
    }

    abstract class AbstractExternalContextIterator<ELEMENT> implements Iterator<ELEMENT> {

        private final Iterator<String> iterator;

        private final AbstractExternalContextMap contextMap;

        private String currentKey;

        private boolean removeCalled = false;

        public AbstractExternalContextIterator(final AbstractExternalContextMap contextMap) {
            iterator = contextMap.getAttributeNames();
            this.contextMap = contextMap;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public ELEMENT next() {
            currentKey = iterator.next();
            try {
                return doNext();
            } finally {
                removeCalled = false;
            }
        }

        @Override
        public void remove() {
            if (currentKey != null && !removeCalled) {
                doRemove();
                removeCalled = true;
            } else {
                throw new IllegalStateException();
            }
        }

        protected String getCurrentKey() {
            return currentKey;
        }

        protected Object getValueFromMap(String key) {
            return contextMap.get(key);
        }

        protected void removeKeyFromMap(String key) {
            contextMap.remove(key);
        }

        protected void removeValueFromMap(Object value) {
            if (containsValue(value)) {
                for (Iterator<Map.Entry<String, Object>> itr = entrySet().iterator(); itr.hasNext();) {
                    Map.Entry<String, Object> entry = itr.next();
                    if (value.equals(entry.getValue())) {
                        contextMap.remove(entry.getKey());
                    }
                }
            }

        }

        protected abstract ELEMENT doNext();

        protected abstract void doRemove();
    }

    class EntryIterator extends AbstractExternalContextIterator<Map.Entry<String, Object>> {

        public EntryIterator(AbstractExternalContextMap contextMap) {
            super(contextMap);
        }

        protected Map.Entry<String, Object> doNext() {
            String key = getCurrentKey();
            return new ImmutableEntry(key, getValueFromMap(key));
        }

        protected void doRemove() {
            removeKeyFromMap(getCurrentKey());
        }
    }

    class KeyIterator extends AbstractExternalContextIterator<String> {

        public KeyIterator(AbstractExternalContextMap contextMap) {
            super(contextMap);
        }

        protected String doNext() {
            return getCurrentKey();
        }

        protected void doRemove() {
            removeKeyFromMap(getCurrentKey());
        }
    }

    class ValuesIterator extends AbstractExternalContextIterator<Object> {

        public ValuesIterator(AbstractExternalContextMap contextMap) {
            super(contextMap);
        }

        protected Object doNext() {
            return getValueFromMap(getCurrentKey());
        }

        protected void doRemove() {
            removeValueFromMap(getValueFromMap(getCurrentKey()));
        }
    }

    protected static class ImmutableEntry implements Map.Entry<String, Object> {

        private final String key;
        private final Object value;

        public ImmutableEntry(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object arg0) {
            throw new UnsupportedOperationException("Immutable entry.");
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof ImmutableEntry)) {
                return false;
            }
            ImmutableEntry entry = (ImmutableEntry) obj;
            Object k = entry.getKey();
            Object v = entry.getValue();
            return (k == key || (k != null && k.equals(key))) && (v == value || (v != null && v.equals(value)));
        }

        @Override
        public int hashCode() {
            return ((key != null) ? key.hashCode() : 0) ^ ((value != null) ? value.hashCode() : 0);
        }
    }
}
