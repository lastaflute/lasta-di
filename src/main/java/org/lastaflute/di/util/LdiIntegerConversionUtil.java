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
package org.lastaflute.di.util;

import java.text.SimpleDateFormat;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiIntegerConversionUtil {

    protected LdiIntegerConversionUtil() {
    }

    /**
     * @param o
     * @return {@link Integer}
     */
    public static Integer toInteger(Object o) {
        return toInteger(o, null);
    }

    /**
     * @param o
     * @param pattern
     * @return {@link Integer}
     */
    public static Integer toInteger(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Integer) {
            return (Integer) o;
        } else if (o instanceof Number) {
            return new Integer(((Number) o).intValue());
        } else if (o instanceof String) {
            return toInteger((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Integer(new SimpleDateFormat(pattern).format(o));
            }
            return new Integer((int) ((java.util.Date) o).getTime());
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? new Integer(1) : new Integer(0);
        } else {
            return toInteger(o.toString());
        }
    }

    private static Integer toInteger(String s) {
        if (LdiStringUtil.isEmpty(s)) {
            return null;
        }
        return new Integer(LdiDecimalFormatUtil.normalize(s));
    }

    public static int toPrimitiveInt(Object o) {
        return toPrimitiveInt(o, null);
    }

    public static int toPrimitiveInt(Object o, String pattern) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).intValue();
        } else if (o instanceof String) {
            return toPrimitiveInt((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Integer.parseInt(new SimpleDateFormat(pattern).format(o));
            }
            return (int) ((java.util.Date) o).getTime();
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? 1 : 0;
        } else {
            return toPrimitiveInt(o.toString());
        }
    }

    private static int toPrimitiveInt(String s) {
        if (LdiStringUtil.isEmpty(s)) {
            return 0;
        }
        return Integer.parseInt(LdiDecimalFormatUtil.normalize(s));
    }
}
