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
public abstract class AbstractExternalContextMap extends AbstractMap {

    private Set entrySet;
    private Set keySet;
    private Collection values;

    public AbstractExternalContextMap() {
    }

    @Override
    public void clear() {
        final List list = new ArrayList(); // to avoid ConcurrentModificationException
        for (Iterator it = getAttributeNames(); it.hasNext();) {
            String key = (String) it.next();
            list.add(key);
        }
        clearReally(list);
    }

    private void clearReally(List keys) {
        for (Iterator itr = keys.iterator(); itr.hasNext();) {
            String key = (String) itr.next();
            removeAttribute(key);
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return (getAttribute(key.toString()) != null);
    }

    @Override
    public boolean containsValue(Object value) {
        if (value != null) {
            for (Iterator it = getAttributeNames(); it.hasNext();) {
                String key = (String) it.next();
                Object attributeValue = getAttribute(key);
                if (value.equals(attributeValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set entrySet() {
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
    public Object put(Object key, Object value) {
        String keyStr = key.toString();
        Object o = getAttribute(keyStr);
        setAttribute(keyStr, value);
        return o;
    }

    @Override
    public void putAll(Map map) {
        for (Iterator itr = map.entrySet().iterator(); itr.hasNext();) {
            Map.Entry entry = (Map.Entry) itr.next();
            String key = (String) entry.getKey();
            setAttribute(key, entry.getValue());
        }
    }

    @Override
    public boolean isEmpty() {
        return !getAttributeNames().hasNext();
    }

    @Override
    public Set keySet() {
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
    public Collection values() {
        if (values == null) {
            values = new ValuesCollection(this);
        }
        return values;
    }

    protected abstract Object getAttribute(String key);

    protected abstract void setAttribute(String key, Object value);

    protected abstract Iterator getAttributeNames();

    protected abstract void removeAttribute(String key);

    abstract class AbstractExternalContextSet extends AbstractSet {

        public int size() {
            int size = 0;
            for (Iterator itr = iterator(); itr.hasNext(); size++) {
                itr.next();
            }
            return size;
        }
    }

    class EntrySet extends AbstractExternalContextSet {

        private AbstractExternalContextMap contextMap;

        public EntrySet(AbstractExternalContextMap contextMap) {
            this.contextMap = contextMap;
        }

        public Iterator iterator() {
            return new EntryIterator(contextMap);
        }

        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) o;
            Object returnObj = contextMap.remove(entry.getKey());
            return (returnObj != null);
        }
    }

    class KeySet extends AbstractExternalContextSet {

        private AbstractExternalContextMap contextMap;

        public KeySet(AbstractExternalContextMap contextMap) {
            this.contextMap = contextMap;
        }

        public Iterator iterator() {
            return new KeyIterator(contextMap);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof String)) {
                return false;
            }
            String s = (String) o;
            Object returnObj = contextMap.remove(s);
            return (returnObj != null);
        }
    }

    class ValuesCollection extends AbstractCollection {

        private AbstractExternalContextMap contextMap;

        public ValuesCollection(AbstractExternalContextMap contextMap) {
            this.contextMap = contextMap;
        }

        public int size() {
            int size = 0;
            for (Iterator itr = iterator(); itr.hasNext(); size++) {
                itr.next();
            }
            return size;
        }

        public Iterator iterator() {
            return new ValuesIterator(contextMap);
        }
    }

    abstract class AbstractExternalContextIterator implements Iterator {

        private final Iterator iterator;

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

        public Object next() {
            currentKey = (String) iterator.next();
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
                for (Iterator itr = entrySet().iterator(); itr.hasNext();) {
                    Map.Entry e = (Map.Entry) itr.next();
                    if (value.equals(e.getValue())) {
                        contextMap.remove(e.getKey());
                    }
                }
            }

        }

        protected abstract Object doNext();

        protected abstract void doRemove();
    }

    class EntryIterator extends AbstractExternalContextIterator {

        public EntryIterator(AbstractExternalContextMap contextMap) {
            super(contextMap);
        }

        protected Object doNext() {
            String key = getCurrentKey();
            return new ImmutableEntry(key, getValueFromMap(key));
        }

        protected void doRemove() {
            String key = getCurrentKey();
            removeKeyFromMap(key);
        }

    }

    class KeyIterator extends AbstractExternalContextIterator {

        public KeyIterator(AbstractExternalContextMap contextMap) {
            super(contextMap);
        }

        protected Object doNext() {
            return getCurrentKey();
        }

        protected void doRemove() {
            removeKeyFromMap(getCurrentKey());
        }
    }

    class ValuesIterator extends AbstractExternalContextIterator {

        public ValuesIterator(AbstractExternalContextMap contextMap) {
            super(contextMap);
        }

        protected Object doNext() {
            String key = getCurrentKey();
            return getValueFromMap(key);
        }

        protected void doRemove() {
            String key = getCurrentKey();
            Object value = getValueFromMap(key);
            removeValueFromMap(value);
        }
    }

    protected static class ImmutableEntry implements Map.Entry {

        private final Object key;

        private final Object value;

        public ImmutableEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
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
