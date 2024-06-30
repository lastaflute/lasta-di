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
package org.lastaflute.di.util.tiger;

/**
 * @author modified by jflute (originated in Seasar)
 * @since 2.4.18
 * @param <T1>
 * @param <T2>
 */
public class Pair<T1, T2> {

    protected T1 first;

    protected T2 second;

    public static <T1, T2> Pair<T1, T2> pair(final T1 first, final T2 second) {
        return new Pair<T1, T2>(first, second);
    }

    public Pair() {
    }

    public Pair(final T1 first, final T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {
        return first;
    }

    public void setFirst(final T1 first) {
        this.first = first;
    }

    public T2 getSecond() {
        return second;
    }

    public void setSecond(final T2 second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "{" + first + ", " + second + "}";
    }

}
