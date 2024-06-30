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
public class LdiByteConversionUtil {

    protected LdiByteConversionUtil() {
    }

    /**
     * @param o
     * @return {@link Byte}
     */
    public static Byte toByte(Object o) {
        return toByte(o, null);
    }

    /**
     * @param o
     * @param pattern
     * @return {@link Byte}
     */
    public static Byte toByte(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Byte) {
            return (Byte) o;
        } else if (o instanceof Number) {
            return new Byte(((Number) o).byteValue());
        } else if (o instanceof String) {
            return toByte((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Byte(new SimpleDateFormat(pattern).format(o));
            }
            return new Byte((byte) ((java.util.Date) o).getTime());
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? new Byte((byte) 1) : new Byte((byte) 0);
        } else {
            return toByte(o.toString());
        }
    }

    private static Byte toByte(String s) {
        if (LdiStringUtil.isEmpty(s)) {
            return null;
        }
        return new Byte(LdiDecimalFormatUtil.normalize(s));
    }

    /**
     * @param o
     * @return byte
     */
    public static byte toPrimitiveByte(Object o) {
        return toPrimitiveByte(o, null);
    }

    /**
     * @param o
     * @param pattern
     * @return byte
     */
    public static byte toPrimitiveByte(Object o, String pattern) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).byteValue();
        } else if (o instanceof String) {
            return toPrimitiveByte((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Byte.parseByte(new SimpleDateFormat(pattern).format(o));
            }
            return (byte) ((java.util.Date) o).getTime();
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? (byte) 1 : (byte) 0;
        } else {
            return toPrimitiveByte(o.toString());
        }
    }

    private static byte toPrimitiveByte(String s) {
        if (LdiStringUtil.isEmpty(s)) {
            return 0;
        }
        return Byte.parseByte(LdiDecimalFormatUtil.normalize(s));
    }
}
