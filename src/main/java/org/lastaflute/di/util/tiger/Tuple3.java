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
 * @param <T3>
 */
public class Tuple3<T1, T2, T3> {

    protected T1 value1;

    protected T2 value2;

    protected T3 value3;

    public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple3(final T1 value1, final T2 value2, final T3 value3) {
        return new Tuple3<T1, T2, T3>(value1, value2, value3);
    }

    public Tuple3() {
    }

    public Tuple3(final T1 value1, final T2 value2, final T3 value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    public T1 getValue1() {
        return value1;
    }

    public void setValue1(final T1 value1) {
        this.value1 = value1;
    }

    public T2 getValue2() {
        return value2;
    }

    public void setValue2(final T2 value2) {
        this.value2 = value2;
    }

    public T3 getValue3() {
        return value3;
    }

    public void setValue3(final T3 value3) {
        this.value3 = value3;
    }

    @Override
    public String toString() {
        return "{" + value1 + ", " + value2 + ", " + value3 + "}";
    }

}
