/*
 * Copyright 2015-2017 the original author or authors.
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
public class LdiDoubleConversionUtil {

    protected LdiDoubleConversionUtil() {
    }

    /**
     * @param o
     * @return {@link Double}
     */
    public static Double toDouble(Object o) {
        return toDouble(o, null);
    }

    /**
     * @param o
     * @param pattern
     * @return {@link Double}
     */
    public static Double toDouble(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Double) {
            return (Double) o;
        } else if (o instanceof Number) {
            return new Double(((Number) o).doubleValue());
        } else if (o instanceof String) {
            return toDouble((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Double(new SimpleDateFormat(pattern).format(o));
            }
            return new Double(((java.util.Date) o).getTime());
        } else {
            return toDouble(o.toString());
        }
    }

    private static Double toDouble(String s) {
        if (LdiStringUtil.isEmpty(s)) {
            return null;
        }
        return new Double(LdiDecimalFormatUtil.normalize(s));
    }

    /**
     * @param o
     * @return double
     */
    public static double toPrimitiveDouble(Object o) {
        return toPrimitiveDouble(o, null);
    }

    /**
     * @param o
     * @param pattern
     * @return double
     */
    public static double toPrimitiveDouble(Object o, String pattern) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).doubleValue();
        } else if (o instanceof String) {
            return toPrimitiveDouble((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Double.parseDouble(new SimpleDateFormat(pattern).format(o));
            }
            return ((java.util.Date) o).getTime();
        } else {
            return toPrimitiveDouble(o.toString());
        }
    }

    private static double toPrimitiveDouble(String s) {
        if (LdiStringUtil.isEmpty(s)) {
            return 0;
        }
        return Double.parseDouble(LdiDecimalFormatUtil.normalize(s));
    }
}
