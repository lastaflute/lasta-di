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
package org.lastaflute.di.util.tiger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class LdiCollectionsUtil {

    protected LdiCollectionsUtil() {
    }

    /**
     * @param <E>
     * @param capacity
     * @return 
     * @see ArrayBlockingQueue#ArrayBlockingQueue(int)
     */
    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(final int capacity) {
        return new ArrayBlockingQueue<E>(capacity);
    }

    /**
     * @param <E>
     * @param capacity
     * @param fair
     * @return 
     * @see ArrayBlockingQueue#ArrayBlockingQueue(int, boolean)
     */
    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(final int capacity, final boolean fair) {
        return new ArrayBlockingQueue<E>(capacity, fair);
    }

    /**
     * @param <E>
     * @param capacity
     * @param fair
     * @param c
     * @return 
     * @see ArrayBlockingQueue#ArrayBlockingQueue(int, boolean, Collection)
     */
    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(final int capacity, final boolean fair, final Collection<? extends E> c) {
        return new ArrayBlockingQueue<E>(capacity, fair, c);
    }

    /**
     * @param <E>
     * @return 
     * @see ArrayList#ArrayList()
     */
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see ArrayList#ArrayList(Collection)
     */
    public static <E> ArrayList<E> newArrayList(final Collection<? extends E> c) {
        return new ArrayList<E>(c);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @return 
     * @see ArrayList#ArrayList(int)
     */
    public static <E> ArrayList<E> newArrayList(final int initialCapacity) {
        return new ArrayList<E>(initialCapacity);
    }

    /**
     * @param <K>
     * @param <V>
     * @return 
     * @see ConcurrentHashMap#ConcurrentHashMap()
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<K, V>();
    }

    /**
     * @param <K>
     * @param <V>
     * @param initialCapacity
     * @return 
     * @see ConcurrentHashMap#ConcurrentHashMap(int)
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(final int initialCapacity) {
        return new ConcurrentHashMap<K, V>(initialCapacity);
    }

    /**
     * @param <K>
     * @param <V>
     * @param initialCapacity
     * @param loadFactor
     * @param concurrencyLevel
     * @return 
     * @see ConcurrentHashMap#ConcurrentHashMap(int, float, int)
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(final int initialCapacity, final float loadFactor,
            final int concurrencyLevel) {
        return new ConcurrentHashMap<K, V>(initialCapacity, loadFactor, concurrencyLevel);
    }

    /**
     * @param <K>
     * @param <V>
     * @param m
     * @return 
     * @see ConcurrentHashMap#ConcurrentHashMap(Map)
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(final Map<? extends K, ? extends V> m) {
        return new ConcurrentHashMap<K, V>(m);
    }

    /**
     * @param <E>
     * @return 
     * @see ConcurrentLinkedQueue#ConcurrentLinkedQueue()
     */
    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see ConcurrentLinkedQueue#ConcurrentLinkedQueue(Collection)
     */
    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue(final Collection<? extends E> c) {
        return new ConcurrentLinkedQueue<E>(c);
    }

    /**
     * @param <E>
     * @return 
     * @see CopyOnWriteArrayList#CopyOnWriteArrayList()
     */
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see CopyOnWriteArrayList#CopyOnWriteArrayList(Collection)
     */
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(final Collection<? extends E> c) {
        return new CopyOnWriteArrayList<E>(c);
    }

    /**
     * @param <E>
     * @param toCopyIn
     * @return 
     * @see CopyOnWriteArrayList#CopyOnWriteArrayList(Object[])
     */
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(final E[] toCopyIn) {
        return new CopyOnWriteArrayList<E>(toCopyIn);
    }

    /**
     * @param <E>
     * @return 
     * @see CopyOnWriteArraySet#CopyOnWriteArraySet()
     */
    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
        return new CopyOnWriteArraySet<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see CopyOnWriteArraySet#CopyOnWriteArraySet(Collection)
     */
    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(final Collection<? extends E> c) {
        return new CopyOnWriteArraySet<E>(c);
    }

    /**
     * @param <E>
     * @return 
     * @see DelayQueue#DelayQueue()
     */
    public static <E extends Delayed> DelayQueue<E> newDelayQueue() {
        return new DelayQueue<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see DelayQueue#DelayQueue(Collection)
     */
    public static <E extends Delayed> DelayQueue<E> newDelayQueue(final Collection<? extends E> c) {
        return new DelayQueue<E>(c);
    }

    /**
     * @param <K>
     * @param <V>
     * @return 
     * @see HashMap#HashMap()
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    /**
     * @param <K>
     * @param <V>
     * @param initialCapacity
     * @return 
     * @see HashMap#HashMap(int)
     */
    public static <K, V> HashMap<K, V> newHashMap(final int initialCapacity) {
        return new HashMap<K, V>(initialCapacity);
    }

    /**
     * @param <K>
     * @param <V>
     * @param initialCapacity
     * @param loadFactor
     * @return 
     * @see HashMap#HashMap(int, float)
     */
    public static <K, V> HashMap<K, V> newHashMap(final int initialCapacity, final float loadFactor) {
        return new HashMap<K, V>(initialCapacity, loadFactor);
    }

    /**
     * @param <K>
     * @param <V>
     * @param m
     * @return 
     * @see HashMap#HashMap(int, float)
     */
    public static <K, V> HashMap<K, V> newHashMap(final Map<? extends K, ? extends V> m) {
        return new HashMap<K, V>(m);
    }

    /**
     * @param <E>
     * @return 
     * @see HashSet#HashSet()
     */
    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see HashSet#HashSet()
     */
    public static <E> HashSet<E> newHashSet(final Collection<? extends E> c) {
        return new HashSet<E>(c);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @return 
     * @see HashSet#HashSet()
     */
    public static <E> HashSet<E> newHashSet(final int initialCapacity) {
        return new HashSet<E>(initialCapacity);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @param loadFactor
     * @return 
     * @see HashSet#HashSet()
     */
    public static <E> HashSet<E> newHashSet(final int initialCapacity, final float loadFactor) {
        return new HashSet<E>(initialCapacity, loadFactor);
    }

    /**
     * @param <K>
     * @param <V>
     * @return 
     * @see Hashtable#Hashtable()
     */
    public static <K, V> Hashtable<K, V> newHashtable() {
        return new Hashtable<K, V>();
    }

    /**
     * @param <K>
     * @param <V>
     * @param initialCapacity
     * @return 
     * @see Hashtable#Hashtable(int)
     */
    public static <K, V> Hashtable<K, V> newHashtable(final int initialCapacity) {
        return new Hashtable<K, V>(initialCapacity);
    }

    /**
     * @param <K>
     * @param <V>
     * @param initialCapacity
     * @param loadFactor
     * @return 
     * @see Hashtable#Hashtable(int, float)
     */
    public static <K, V> Hashtable<K, V> newHashtable(final int initialCapacity, final float loadFactor) {
        return new Hashtable<K, V>(initialCapacity, loadFactor);
    }

    /**
     * @param <K>
     * @param <V>
     * @param m
     * @return 
     * @see Hashtable#Hashtable(Map)
     */
    public static <K, V> Hashtable<K, V> newHashtable(final Map<? extends K, ? extends V> m) {
        return new Hashtable<K, V>(m);
    }

    /**
     * @param <K>
     * @param <V>
     * @return 
     * @see IdentityHashMap#IdentityHashMap()
     */
    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap<K, V>();
    }

    /**
     * @param <K>
     * @param <V>
     * @param expectedMaxSize
     * @return 
     * @see IdentityHashMap#IdentityHashMap(int)
     */
    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap(final int expectedMaxSize) {
        return new IdentityHashMap<K, V>(expectedMaxSize);
    }

    /**
     * @param <K>
     * @param <V>
     * @param m
     * @return 
     * @see IdentityHashMap#IdentityHashMap(Map)
     */
    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap(final Map<? extends K, ? extends V> m) {
        return new IdentityHashMap<K, V>(m);
    }

    /**
     * @param <E>
     * @return 
     * @see LinkedBlockingQueue#LinkedBlockingQueue()
     */
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue() {
        return new LinkedBlockingQueue<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see LinkedBlockingQueue#LinkedBlockingQueue(Collection)
     */
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(final Collection<? extends E> c) {
        return new LinkedBlockingQueue<E>(c);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @return 
     * @see LinkedBlockingQueue#LinkedBlockingQueue(int)
     */
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(final int initialCapacity) {
        return new LinkedBlockingQueue<E>(initialCapacity);
    }

    /**
     * @param <K>
     * @param <V>
     * @return 
     * @see LinkedHashMap#LinkedHashMap()
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }

    /**
     * @param <K>
     * @param <V>
     * @param initialCapacity
     * @return 
     * @see LinkedHashMap#LinkedHashMap(int)
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final int initialCapacity) {
        return new LinkedHashMap<K, V>(initialCapacity);
    }

    /**
     * @param <K>
     * @param <V>
     * @param initialCapacity
     * @param loadFactor
     * @return 
     * @see LinkedHashMap#LinkedHashMap(int, float)
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final int initialCapacity, final float loadFactor) {
        return new LinkedHashMap<K, V>(initialCapacity, loadFactor);
    }

    /**
     * @param <K>
     * @param <V>
     * @param m
     * @return 
     * @see LinkedHashMap#LinkedHashMap(Map)
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final Map<? extends K, ? extends V> m) {
        return new LinkedHashMap<K, V>(m);
    }

    /**
     * @param <E>
     * @return 
     * @see LinkedHashSet#LinkedHashSet()
     */
    public static <E> LinkedHashSet<E> newLinkedHashSet() {
        return new LinkedHashSet<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see LinkedHashSet#LinkedHashSet(Collection)
     */
    public static <E> LinkedHashSet<E> newLinkedHashSet(final Collection<? extends E> c) {
        return new LinkedHashSet<E>(c);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @return 
     * @see LinkedHashSet#LinkedHashSet(int)
     */
    public static <E> LinkedHashSet<E> newLinkedHashSet(final int initialCapacity) {
        return new LinkedHashSet<E>(initialCapacity);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @param loadFactor
     * @return 
     * @see LinkedHashSet#LinkedHashSet(int, float)
     */
    public static <E> LinkedHashSet<E> newLinkedHashSet(final int initialCapacity, final float loadFactor) {
        return new LinkedHashSet<E>(initialCapacity, loadFactor);
    }

    /**
     * @param <E>
     * @return 
     * @see LinkedList#LinkedList()
     */
    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see LinkedList#LinkedList(Collection)
     */
    public static <E> LinkedList<E> newLinkedList(final Collection<? extends E> c) {
        return new LinkedList<E>(c);
    }

    /**
     * @param <E>
     * @return 
     * @see PriorityBlockingQueue#PriorityBlockingQueue()
     */
    public static <E> PriorityBlockingQueue<E> newPriorityBlockingQueue() {
        return new PriorityBlockingQueue<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see PriorityBlockingQueue#PriorityBlockingQueue(Collection)
     */
    public static <E> PriorityBlockingQueue<E> newPriorityBlockingQueue(final Collection<? extends E> c) {
        return new PriorityBlockingQueue<E>(c);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @return 
     * @see PriorityBlockingQueue#PriorityBlockingQueue(int)
     */
    public static <E> PriorityBlockingQueue<E> newPriorityBlockingQueue(final int initialCapacity) {
        return new PriorityBlockingQueue<E>(initialCapacity);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @param comparator
     * @return 
     * @see PriorityBlockingQueue#PriorityBlockingQueue(int, Comparator)
     */
    public static <E> PriorityBlockingQueue<E> newPriorityBlockingQueue(final int initialCapacity, final Comparator<? super E> comparator) {
        return new PriorityBlockingQueue<E>(initialCapacity, comparator);
    }

    /**
     * @param <E>
     * @return 
     * @see PriorityQueue#PriorityQueue()
     */
    public static <E> PriorityQueue<E> newPriorityQueue() {
        return new PriorityQueue<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see PriorityQueue#PriorityQueue(Collection)
     */
    public static <E> PriorityQueue<E> newPriorityQueue(final Collection<? extends E> c) {
        return new PriorityQueue<E>(c);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @return 
     * @see PriorityQueue#PriorityQueue(int)
     */
    public static <E> PriorityQueue<E> newPriorityQueue(final int initialCapacity) {
        return new PriorityQueue<E>(initialCapacity);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @param comparator
     * @return 
     * @see PriorityQueue#PriorityQueue(int, Comparator)
     */
    public static <E> PriorityQueue<E> newPriorityQueue(final int initialCapacity, final Comparator<? super E> comparator) {
        return new PriorityQueue<E>(initialCapacity, comparator);
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see PriorityQueue#PriorityQueue(PriorityQueue)
     */
    public static <E> PriorityQueue<E> newPriorityQueue(final PriorityQueue<? extends E> c) {
        return new PriorityQueue<E>(c);
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see PriorityQueue#PriorityQueue(SortedSet)
     */
    public static <E> PriorityQueue<E> newPriorityQueue(final SortedSet<? extends E> c) {
        return new PriorityQueue<E>(c);
    }

    /**
     * @param <E>
     * @return 
     * @see Stack#Stack()
     */
    public static <E> Stack<E> newStack() {
        return new Stack<E>();
    }

    /**
     * @param <K>
     * @param <V>
     * @return 
     * @see TreeMap#TreeMap()
     */
    public static <K, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<K, V>();
    }

    /**
     * @param <K>
     * @param <V>
     * @param c
     * @return 
     * @see TreeMap#TreeMap()
     */
    public static <K, V> TreeMap<K, V> newTreeMap(final Comparator<? super K> c) {
        return new TreeMap<K, V>(c);
    }

    /**
     * @param <K>
     * @param <V>
     * @param m
     * @return 
     * @see TreeMap#TreeMap(Map)
     */
    public static <K, V> TreeMap<K, V> newTreeMap(final Map<? extends K, ? extends V> m) {
        return new TreeMap<K, V>(m);
    }

    /**
     * @param <K>
     * @param <V>
     * @param m
     * @return 
     * @see TreeMap#TreeMap(SortedMap)
     */
    public static <K, V> TreeMap<K, V> newTreeMap(final SortedMap<K, ? extends V> m) {
        return new TreeMap<K, V>(m);
    }

    /**
     * @param <E>
     * @return 
     * @see TreeSet#TreeSet()
     */
    public static <E> TreeSet<E> newTreeSet() {
        return new TreeSet<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see TreeSet#TreeSet(Collection)
     */
    public static <E> TreeSet<E> newTreeSet(final Collection<? extends E> c) {
        return new TreeSet<E>(c);
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see TreeSet#TreeSet(Comparator)
     */
    public static <E> TreeSet<E> newTreeSet(final Comparator<? super E> c) {
        return new TreeSet<E>(c);
    }

    /**
     * @param <E>
     * @param s
     * @return 
     * @see TreeSet#TreeSet(SortedSet)
     */
    public static <E> TreeSet<E> newTreeSet(final SortedSet<? extends E> s) {
        return new TreeSet<E>(s);
    }

    /**
     * @param <E>
     * @return 
     * @see Vector#Vector()
     */
    public static <E> Vector<E> newVector() {
        return new Vector<E>();
    }

    /**
     * @param <E>
     * @param c
     * @return 
     * @see Vector#Vector(Collection)
     */
    public static <E> Vector<E> newVector(final Collection<? extends E> c) {
        return new Vector<E>(c);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @return 
     * @see Vector#Vector(int)
     */
    public static <E> Vector<E> newVector(final int initialCapacity) {
        return new Vector<E>(initialCapacity);
    }

    /**
     * @param <E>
     * @param initialCapacity
     * @param capacityIncrement
     * @return 
     * @see Vector#Vector(int, int)
     */
    public static <E> Vector<E> newVector(final int initialCapacity, final int capacityIncrement) {
        return new Vector<E>(initialCapacity, capacityIncrement);
    }

    /**
     * @param <K>
     * @param <V>
     * @return 
     * @see WeakHashMap#WeakHashMap()
     */
    public static <K, V> WeakHashMap<K, V> newWeakHashMap() {
        return new WeakHashMap<K, V>();
    }

    /**
     * @param <K>
     * @param <V>
     * @param initialCapacity
     * @return 
     * @see WeakHashMap#WeakHashMap(int)
     */
    public static <K, V> WeakHashMap<K, V> newWeakHashMap(final int initialCapacity) {
        return new WeakHashMap<K, V>(initialCapacity);
    }

    /**
     * @param <K>
     * @param <V>
     * @param initialCapacity
     * @param loadFactor
     * @return 
     * @see WeakHashMap#WeakHashMap(int, float)
     */
    public static <K, V> WeakHashMap<K, V> newWeakHashMap(final int initialCapacity, final float loadFactor) {
        return new WeakHashMap<K, V>(initialCapacity, loadFactor);
    }

    /**
     * @param <K>
     * @param <V>
     * @param m
     * @return 
     * @see WeakHashMap#WeakHashMap(Map)
     */
    public static <K, V> WeakHashMap<K, V> newWeakHashMap(final Map<? extends K, ? extends V> m) {
        return new WeakHashMap<K, V>(m);
    }

    /**
     * @param <K>
     * @param <V>
     * @param map
     * @param key
     * @param value
     * @return 
     * @see ConcurrentHashMap#putIfAbsent(Object, Object)
     */
    public static <K, V> V putIfAbsent(final ConcurrentMap<K, V> map, final K key, final V value) {
        V exists = map.putIfAbsent(key, value);
        if (exists != null) {
            return exists;
        }
        return value;
    }

}
