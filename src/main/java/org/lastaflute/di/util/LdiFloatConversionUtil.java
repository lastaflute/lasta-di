/*
 * Copyright 2015-2022 the original author or authors.
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
public class LdiFloatConversionUtil {

    protected LdiFloatConversionUtil() {
    }

    /**
     * @param o
     * @return {@link Float}
     */
    public static Float toFloat(Object o) {
        return toFloat(o, null);
    }

    /**
     * @param o
     * @param pattern
     * @return {@link Float}
     */
    public static Float toFloat(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Float) {
            return (Float) o;
        } else if (o instanceof Number) {
            return new Float(((Number) o).floatValue());
        } else if (o instanceof String) {
            return toFloat((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Float(new SimpleDateFormat(pattern).format(o));
            }
            return new Float(((java.util.Date) o).getTime());
        } else {
            return toFloat(o.toString());
        }
    }

    private static Float toFloat(String s) {
        if (LdiStringUtil.isEmpty(s)) {
            return null;
        }
        return new Float(LdiDecimalFormatUtil.normalize(s));
    }

    /**
     * @param o
     * @return float
     */
    public static float toPrimitiveFloat(Object o) {
        return toPrimitiveFloat(o, null);
    }

    /**
     * @param o
     * @param pattern
     * @return float
     */
    public static float toPrimitiveFloat(Object o, String pattern) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).floatValue();
        } else if (o instanceof String) {
            return toPrimitiveFloat((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Float.parseFloat(new SimpleDateFormat(pattern).format(o));
            }
            return ((java.util.Date) o).getTime();
        } else {
            return toPrimitiveFloat(o.toString());
        }
    }

    private static float toPrimitiveFloat(String s) {
        if (LdiStringUtil.isEmpty(s)) {
            return 0;
        }
        return Float.parseFloat(LdiDecimalFormatUtil.normalize(s));
    }
}
