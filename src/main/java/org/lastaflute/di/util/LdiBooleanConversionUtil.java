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
package org.lastaflute.di.util;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiBooleanConversionUtil {

    protected LdiBooleanConversionUtil() {
    }

    public static Boolean toBoolean(Object obj) { // similar with DfTypeUtil.toBoolean()
        if (obj == null) {
            return (Boolean) obj;
        } else if (obj instanceof Boolean) {
            return (Boolean) obj;
        } else if (obj instanceof Number) {
            final int num = ((Number) obj).intValue();
            if (num == 1) {
                return Boolean.TRUE;
            } else if (num == 0) {
                return Boolean.FALSE;
            } else {
                String msg = "Failed to parse the boolean number: number=" + num;
                throw new IllegalStateException(msg);
            }
        } else if (obj instanceof String) {
            final String str = (String) obj;
            if ("true".equalsIgnoreCase(str)) {
                return Boolean.TRUE;
            } else if ("false".equalsIgnoreCase(str)) {
                return Boolean.FALSE;
            } else if (str.equalsIgnoreCase("1")) {
                return Boolean.TRUE;
            } else if (str.equalsIgnoreCase("0")) {
                return Boolean.FALSE;
            } else if (str.equalsIgnoreCase("t")) {
                return Boolean.TRUE;
            } else if (str.equalsIgnoreCase("f")) {
                return Boolean.FALSE;
            } else {
                String msg = "Failed to parse the boolean string:";
                msg = msg + " value=" + str;
                throw new IllegalStateException(msg);
            }
        } else {
            return Boolean.FALSE; // couldn't parse
        }
    }

    public static boolean toPrimitiveBoolean(Object o) {
        Boolean b = toBoolean(o);
        if (b != null) {
            return b.booleanValue();
        }
        return false;
    }
}
