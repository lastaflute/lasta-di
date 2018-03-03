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

import java.text.SimpleDateFormat;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiLongConversionUtil {

    protected LdiLongConversionUtil() {
    }

    /**
     * @param o
     * @return {@link Long}
     */
    public static Long toLong(Object o) {
        return toLong(o, null);
    }

    /**
     * @param o
     * @param pattern
     * @return {@link Long}
     */
    public static Long toLong(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Long) {
            return (Long) o;
        } else if (o instanceof Number) {
            return new Long(((Number) o).longValue());
        } else if (o instanceof String) {
            return toLong((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Long(new SimpleDateFormat(pattern).format(o));
            }
            return new Long(((java.util.Date) o).getTime());
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? new Long(1) : new Long(0);
        } else {
            return toLong(o.toString());
        }
    }

    private static Long toLong(String s) {
        if (LdiStringUtil.isEmpty(s)) {
            return null;
        }
        return new Long(LdiDecimalFormatUtil.normalize(s));
    }

    /**
     * @param o
     * @return long
     */
    public static long toPrimitiveLong(Object o) {
        return toPrimitiveLong(o, null);
    }

    /**
     * @param o
     * @param pattern
     * @return long
     */
    public static long toPrimitiveLong(Object o, String pattern) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).longValue();
        } else if (o instanceof String) {
            return toPrimitiveLong((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Long.parseLong(new SimpleDateFormat(pattern).format(o));
            }
            return ((java.util.Date) o).getTime();
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? 1 : 0;
        } else {
            return toPrimitiveLong(o.toString());
        }
    }

    private static long toPrimitiveLong(String s) {
        if (LdiStringUtil.isEmpty(s)) {
            return 0;
        }
        return Long.parseLong(LdiDecimalFormatUtil.normalize(s));
    }
}
