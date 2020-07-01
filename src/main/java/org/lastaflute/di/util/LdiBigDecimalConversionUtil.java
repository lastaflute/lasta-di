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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiBigDecimalConversionUtil {

    protected static final String TIGER_NORMALIZER_CLASS_NAME = TigerBigDecimalConversion.class.getName();

    protected static BigDecimalNormalizer normalizer = new DefaultNormalizer();

    static {
        try {
            final Class<?> clazz = Class.forName(TIGER_NORMALIZER_CLASS_NAME);
            normalizer = (BigDecimalNormalizer) clazz.newInstance();
        } catch (Exception ignore) {}
    }

    protected LdiBigDecimalConversionUtil() {
    }

    public static BigDecimal toBigDecimal(Object o) {
        return toBigDecimal(o, null);
    }

    public static BigDecimal toBigDecimal(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new BigDecimal(new SimpleDateFormat(pattern).format(o));
            }
            return new BigDecimal(Long.toString(((java.util.Date) o).getTime()));
        } else if (o instanceof String) {
            String s = (String) o;
            if (LdiStringUtil.isEmpty(s)) {
                return null;
            }
            return normalizer.normalize(new BigDecimal(s));
        } else {
            return normalizer.normalize(new BigDecimal(o.toString()));
        }
    }

    public static String toString(BigDecimal dec) {
        return normalizer.toString(dec);
    }

    public interface BigDecimalNormalizer {

        BigDecimal normalize(BigDecimal dec);

        String toString(BigDecimal dec);
    }

    public static class DefaultNormalizer implements BigDecimalNormalizer {

        public BigDecimal normalize(final BigDecimal dec) {
            return dec;
        }

        public String toString(final BigDecimal dec) {
            return dec.toString();
        }
    }
}
