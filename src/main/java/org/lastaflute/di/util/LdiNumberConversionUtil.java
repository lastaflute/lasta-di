/*
 * Copyright 2014-2015 the original author or authors.
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
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiNumberConversionUtil {

    protected LdiNumberConversionUtil() {
    }

    public static Object convertNumber(Class<?> type, Object o) {
        if (type == Integer.class) {
            return LdiIntegerConversionUtil.toInteger(o);
        } else if (type == BigDecimal.class) {
            return LdiBigDecimalConversionUtil.toBigDecimal(o);
        } else if (type == Double.class) {
            return LdiDoubleConversionUtil.toDouble(o);
        } else if (type == Long.class) {
            return LdiLongConversionUtil.toLong(o);
        } else if (type == Float.class) {
            return LdiFloatConversionUtil.toFloat(o);
        } else if (type == Short.class) {
            return LdiShortConversionUtil.toShort(o);
        } else if (type == BigInteger.class) {
            return LdiBigIntegerConversionUtil.toBigInteger(o);
        } else if (type == Byte.class) {
            return LdiByteConversionUtil.toByte(o);
        }
        return o;
    }

    public static Object convertPrimitiveWrapper(Class<?> type, Object o) {
        if (type == int.class) {
            Integer i = LdiIntegerConversionUtil.toInteger(o);
            if (i != null) {
                return i;
            }
            return Integer.valueOf(0);
        } else if (type == double.class) {
            Double d = LdiDoubleConversionUtil.toDouble(o);
            if (d != null) {
                return d;
            }
            return new Double(0);
        } else if (type == long.class) {
            Long l = LdiLongConversionUtil.toLong(o);
            if (l != null) {
                return l;
            }
            return Long.valueOf(0L);
        } else if (type == float.class) {
            Float f = LdiFloatConversionUtil.toFloat(o);
            if (f != null) {
                return f;
            }
            return new Float(0);
        } else if (type == short.class) {
            Short s = LdiShortConversionUtil.toShort(o);
            if (s != null) {
                return s;
            }
            return new Short((short) 0);
        } else if (type == boolean.class) {
            Boolean b = LdiBooleanConversionUtil.toBoolean(o);
            if (b != null) {
                return b;
            }
            return Boolean.FALSE;
        } else if (type == byte.class) {
            Byte b = LdiByteConversionUtil.toByte(o);
            if (b != null) {
                return b;
            }
            return new Byte((byte) 0);
        }
        return o;
    }

    public static String removeDelimeter(String value, Locale locale) {
        String groupingSeparator = findGroupingSeparator(locale);
        if (groupingSeparator != null) {
            value = LdiStringUtil.replace(value, groupingSeparator, "");
        }
        return value;
    }

    public static String findGroupingSeparator(Locale locale) {
        DecimalFormatSymbols symbol = getDecimalFormatSymbols(locale);
        return Character.toString(symbol.getGroupingSeparator());
    }

    public static String findDecimalSeparator(Locale locale) {
        DecimalFormatSymbols symbol = getDecimalFormatSymbols(locale);
        return Character.toString(symbol.getDecimalSeparator());
    }

    private static DecimalFormatSymbols getDecimalFormatSymbols(Locale locale) {
        DecimalFormatSymbols symbol;
        if (locale != null) {
            symbol = LdiDecimalFormatSymbolsUtil.getDecimalFormatSymbols(locale);
        } else {
            symbol = LdiDecimalFormatSymbolsUtil.getDecimalFormatSymbols();
        }
        return symbol;
    }
}