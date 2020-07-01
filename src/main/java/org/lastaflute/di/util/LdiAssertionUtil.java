/*
 * Copyright 2015-2020 the original author or authors.
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

import org.lastaflute.di.exception.EmptyRuntimeException;

/**
 * @author shot
 */
public class LdiAssertionUtil {

    protected LdiAssertionUtil() {
    }

    public static void assertNotNull(String message, Object obj) throws NullPointerException {
        if (obj == null) {
            throw new NullPointerException(message);
        }
    }

    public static void assertNotEmpty(String message, String s) throws EmptyRuntimeException {
        if (LdiStringUtil.isEmpty(s)) {
            throw new EmptyRuntimeException(message);
        }
    }

    public static void assertIntegerNotNegative(String message, int num) throws IllegalArgumentException {
        if (num < 0) {
            throw new IllegalArgumentException(message);
        }
    }

}
