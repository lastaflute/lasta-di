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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @param <KEY> The type of key.
 * @param <VALUE> The type of value.
 * @author modified by jflute (originated in Seasar)
 */
public class ArrayMap<KEY, VALUE> extends AbstractMap<KEY, VALUE> implements Map<KEY, VALUE>, Cloneable, Externalizable {

    static final long serialVersionUID = 1L;

    private static final int INITIAL_CAPACITY = 17;

    private static final float LOAD_FACTOR = 0.75f;

    private transient int threshold;
    private transient Entry<KEY, VALUE>[] mapTable;
    private transient Entry<KEY, VALUE>[] listTable;
    private transient int size = 0;
    private transient Set<Entry<KEY, VALUE>> entrySet = null;

    public ArrayMap() {
        this(INITIAL_CAPACITY);
    }

    public ArrayMap(int initialCapacity) {
        if (initialCapacity <= 0) {
            initialCapacity = INITIAL_CAPACITY;
        }
        mapTable = new Entry[initialCapacity];
        listTable = new Entry[initialCapacity];
        threshold = (int) (initialCapacity * LOAD_FACTOR);
    }

    public ArrayMap(Map<KEY, VALUE> map) {
        this((int) (map.size() / LOAD_FACTOR) + 1);
        putAll(map);
    }

    public final int size() {
        return size;
    }

    public final boolean isEmpty() {
        return size == 0;
    }

    public final boolean containsValue(Object value) {
        return indexOf(value) >= 0;
    }

    public final int indexOf(Object value) {
        if (value != null) {
            for (int i = 0; i < size; i++) {
                if (value.equals(listTable[i].value)) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (listTable[i].value == null) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean containsKey(final Object key) {
        Entry<KEY, VALUE>[] tbl = mapTable;
        if (key != null) {
            int hashCode = key.hashCode();
            int index = (hashCode & 0x7FFFFFFF) % tbl.length;
            for (Entry<KEY, VALUE> e = tbl[index]; e != null; e = e.next) {
                if (e.hashCode == hashCode && key.equals(e.key)) {
                    return true;
                }
            }
        } else {
            for (Entry<KEY, VALUE> e = tbl[0]; e != null; e = e.next) {
                if (e.key == null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public VALUE get(final Object key) {
        Entry<KEY, VALUE>[] tbl = mapTable;
        if (key != null) {
            int hashCode = key.hashCode();
            int index = (hashCode & 0x7FFFFFFF) % tbl.length;
            for (Entry<KEY, VALUE> e = tbl[index]; e != null; e = e.next) {
                if (e.hashCode == hashCode && key.equals(e.key)) {
                    return e.value;
                }
            }
        } else {
            for (Entry<KEY, VALUE> e = tbl[0]; e != null; e = e.next) {
                if (e.key == null) {
                    return e.value;
                }
            }
        }
        return null;
    }

    public final VALUE get(final int index) {
        return getEntry(index).value;
    }

    public final Object getKey(final int index) {
        return getEntry(index).key;
    }

    public final Entry<KEY, VALUE> getEntry(final int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("Index:" + index + ", Size:" + size);
        }
        return listTable[index];
    }

    public VALUE put(final KEY key, final VALUE value) {
        int hashCode = 0;
        int index = 0;

        if (key != null) {
            hashCode = key.hashCode();
            index = (hashCode & 0x7FFFFFFF) % mapTable.length;
            for (Entry<KEY, VALUE> e = mapTable[index]; e != null; e = e.next) {
                if ((e.hashCode == hashCode) && key.equals(e.key)) {
                    return swapValue(e, value);
                }
            }
        } else {
            for (Entry<KEY, VALUE> e = mapTable[0]; e != null; e = e.next) {
                if (e.key == null) {
                    return swapValue(e, value);
                }
            }
        }
        ensureCapacity();
        index = (hashCode & 0x7FFFFFFF) % mapTable.length;
        Entry<KEY, VALUE> e = new Entry<KEY, VALUE>(hashCode, key, value, mapTable[index]);
        mapTable[index] = e;
        listTable[size++] = e;
        return null;
    }

    public final void set(final int index, final VALUE value) {
        getEntry(index).setValue(value);
    }

    @Override
    public VALUE remove(final Object key) {
        Entry<KEY, VALUE> e = removeMap(key);
        if (e != null) {
            VALUE value = e.value;
            removeList(indexOf(e));
            e.clear();
            return value;
        }
        return null;
    }

    public final VALUE remove(int index) {
        final Entry<KEY, VALUE> entry = removeList(index);
        final VALUE value = entry.value;
        removeMap(entry.key);
        entry.value = null;
        return value;
    }

    @Override
    public void putAll(Map<? extends KEY, ? extends VALUE> map) {
        for (Map.Entry<? extends KEY, ? extends VALUE> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public final void clear() {
        for (int i = 0; i < mapTable.length; i++) {
            mapTable[i] = null;
        }
        for (int i = 0; i < listTable.length; i++) {
            listTable[i] = null;
        }
        size = 0;
    }

    public final Object[] toArray() {
        Object[] array = new Object[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = get(i);
        }
        return array;
    }

    public final Object[] toArray(final Object proto[]) {
        Object[] array = proto;
        if (proto.length < size) {
            array = (Object[]) Array.newInstance(proto.getClass().getComponentType(), size);
        }
        for (int i = 0; i < array.length; i++) {
            array[i] = get(i);
        }
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    public final boolean equals(Object o) {
        if (!getClass().isInstance(o)) {
            return false;
        }
        ArrayMap e = (ArrayMap) o;
        if (size != e.size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!listTable[i].equals(e.listTable[i])) {
                return false;
            }
        }
        return true;
    }

    public final Set entrySet() {
        if (entrySet == null) {
            entrySet = new AbstractSet() {
                public Iterator iterator() {
                    return new ArrayMapIterator();
                }

                public boolean contains(Object o) {
                    if (!(o instanceof Entry)) {
                        return false;
                    }
                    Entry entry = (Entry) o;
                    int index = (entry.hashCode & 0x7FFFFFFF) % mapTable.length;
                    for (Entry e = mapTable[index]; e != null; e = e.next) {
                        if (e.equals(entry)) {
                            return true;
                        }
                    }
                    return false;
                }

                public boolean remove(Object o) {
                    if (!(o instanceof Entry)) {
                        return false;
                    }
                    Entry entry = (Entry) o;
                    return ArrayMap.this.remove(entry.key) != null;
                }

                public int size() {
                    return size;
                }

                public void clear() {
                    ArrayMap.this.clear();
                }
            };
        }
        return entrySet;
    }

    public final void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt(listTable.length);
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            out.writeObject(listTable[i].key);
            out.writeObject(listTable[i].value);
        }
    }

    public final void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        int num = in.readInt();
        mapTable = new Entry[num];
        listTable = new Entry[num];
        threshold = (int) (num * LOAD_FACTOR);
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            KEY key = (KEY) in.readObject();
            @SuppressWarnings("unchecked")
            VALUE value = (VALUE) in.readObject();
            put(key, value);
        }
    }

    @Override
    public Object clone() {
        ArrayMap<KEY, VALUE> copy = new ArrayMap<KEY, VALUE>();
        copy.threshold = threshold;
        copy.mapTable = mapTable;
        copy.listTable = listTable;
        copy.size = size;
        return copy;
    }

    private final int indexOf(final Entry entry) {
        for (int i = 0; i < size; i++) {
            if (listTable[i] == entry) {
                return i;
            }
        }
        return -1;
    }

    private final Entry<KEY, VALUE> removeMap(Object key) {
        int hashCode = 0;
        int index = 0;

        if (key != null) {
            hashCode = key.hashCode();
            index = (hashCode & 0x7FFFFFFF) % mapTable.length;
            for (Entry<KEY, VALUE> e = mapTable[index], prev = null; e != null; prev = e, e = e.next) {
                if ((e.hashCode == hashCode) && key.equals(e.key)) {
                    if (prev != null) {
                        prev.next = e.next;
                    } else {
                        mapTable[index] = e.next;
                    }
                    return e;
                }
            }
        } else {
            for (Entry<KEY, VALUE> e = mapTable[index], prev = null; e != null; prev = e, e = e.next) {
                if ((e.hashCode == hashCode) && e.key == null) {
                    if (prev != null) {
                        prev.next = e.next;
                    } else {
                        mapTable[index] = e.next;
                    }
                    return e;
                }
            }
        }
        return null;
    }

    private final Entry<KEY, VALUE> removeList(int index) {
        Entry<KEY, VALUE> e = listTable[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(listTable, index + 1, listTable, index, numMoved);
        }
        listTable[--size] = null;
        return e;
    }

    private final void ensureCapacity() {
        if (size >= threshold) {
            Entry[] oldTable = listTable;
            int newCapacity = oldTable.length * 2 + 1;
            Entry[] newMapTable = new Entry[newCapacity];
            Entry[] newListTable = new Entry[newCapacity];
            threshold = (int) (newCapacity * LOAD_FACTOR);
            System.arraycopy(oldTable, 0, newListTable, 0, size);
            for (int i = 0; i < size; i++) {
                Entry<KEY, VALUE> old = oldTable[i];
                int index = (old.hashCode & 0x7FFFFFFF) % newCapacity;
                Entry<KEY, VALUE> e = old;
                old = old.next;
                e.next = newMapTable[index];
                newMapTable[index] = e;
            }
            mapTable = newMapTable;
            listTable = newListTable;
        }
    }

    private final VALUE swapValue(final Entry<KEY, VALUE> entry, final VALUE value) {
        VALUE old = entry.value;
        entry.value = value;
        return old;
    }

    private class ArrayMapIterator implements Iterator {

        private int current = 0;

        private int last = -1;

        public boolean hasNext() {
            return current != size;
        }

        public Object next() {
            try {
                Object n = listTable[current];
                last = current++;
                return n;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (last == -1) {
                throw new IllegalStateException();
            }
            ArrayMap.this.remove(last);
            if (last < current) {
                current--;
            }
            last = -1;
        }
    }

    private static class Entry<KEY, VALUE> implements Map.Entry<KEY, VALUE>, Externalizable {

        private static final long serialVersionUID = -6625980241350717177L;

        transient int hashCode;

        transient KEY key;

        transient VALUE value;

        transient Entry<KEY, VALUE> next;

        public Entry(final int hashCode, final KEY key, final VALUE value, final Entry<KEY, VALUE> next) {
            this.hashCode = hashCode;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public KEY getKey() {
            return key;
        }

        public VALUE getValue() {
            return value;
        }

        public VALUE setValue(final VALUE value) {
            VALUE oldValue = value;
            this.value = value;
            return oldValue;
        }

        public void clear() {
            key = null;
            value = null;
            next = null;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            @SuppressWarnings("unchecked")
            final Entry<KEY, VALUE> e = (Entry<KEY, VALUE>) o;
            return (key != null ? key.equals(e.key) : e.key == null) && (value != null ? value.equals(e.value) : e.value == null);
        }

        public int hashCode() {
            return hashCode;
        }

        public String toString() {
            return key + "=" + value;
        }

        public void writeExternal(final ObjectOutput s) throws IOException {
            s.writeInt(hashCode);
            s.writeObject(key);
            s.writeObject(value);
            s.writeObject(next);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void readExternal(final ObjectInput s) throws IOException, ClassNotFoundException {
            hashCode = s.readInt();
            key = (KEY) s.readObject();
            value = (VALUE) s.readObject();
            next = (Entry<KEY, VALUE>) s.readObject();
        }
    }
}