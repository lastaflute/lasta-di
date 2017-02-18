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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.lastaflute.di.exception.ParseRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiDateConversionUtil {

    protected LdiDateConversionUtil() {
    }

    /**
     * @param o
     * @return {@link Date}
     */
    public static Date toDate(Object o) {
        return toDate(o, null);
    }

    /**
     * @param o
     * @param pattern
     * @return {@link Date}
     */
    public static Date toDate(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return toDate((String) o, pattern);
        } else if (o instanceof Date) {
            return (Date) o;
        } else if (o instanceof Calendar) {
            return ((Calendar) o).getTime();
        } else {
            return toDate(o.toString(), pattern);
        }
    }

    /**
     * @param s
     * @param pattern
     * @return {@link Date}
     */
    public static Date toDate(String s, String pattern) {
        return toDate(s, pattern, Locale.getDefault());
    }

    public static Date toDate(String s, String pattern, Locale locale) {
        if (LdiStringUtil.isEmpty(s)) {
            return null;
        }
        SimpleDateFormat sdf = getDateFormat(s, pattern, locale);
        try {
            return sdf.parse(s);
        } catch (ParseException ex) {
            throw new ParseRuntimeException(ex);
        }
    }

    /**
     * @param s
     * @param pattern
     * @param locale
     * @return {@link SimpleDateFormat}
     */
    public static SimpleDateFormat getDateFormat(String s, String pattern, Locale locale) {
        if (pattern != null) {
            return new SimpleDateFormat(pattern);
        }
        return getDateFormat(s, locale);
    }

    /**
     * @param s
     * @param locale
     * @return {@link SimpleDateFormat}
     */
    public static SimpleDateFormat getDateFormat(String s, Locale locale) {
        String pattern = getPattern(locale);
        String shortPattern = removeDelimiter(pattern);
        String delimitor = findDelimiter(s);
        if (delimitor == null) {
            if (s.length() == shortPattern.length()) {
                return new SimpleDateFormat(shortPattern);
            }
            if (s.length() == shortPattern.length() + 2) {
                return new SimpleDateFormat(LdiStringUtil.replace(shortPattern, "yy", "yyyy"));
            }
        } else {
            String[] array = LdiStringUtil.split(s, delimitor);
            for (int i = 0; i < array.length; ++i) {
                if (array[i].length() == 4) {
                    pattern = LdiStringUtil.replace(pattern, "yy", "yyyy");
                    break;
                }
            }
            return new SimpleDateFormat(pattern);
        }
        return new SimpleDateFormat();
    }

    /**
     * @param locale
     * @return {@link SimpleDateFormat}
     */
    public static SimpleDateFormat getDateFormat(Locale locale) {
        return new SimpleDateFormat(getPattern(locale));
    }

    /**
     * @param locale
     * @return 
     */
    public static SimpleDateFormat getY4DateFormat(Locale locale) {
        return new SimpleDateFormat(getY4Pattern(locale));
    }

    public static String getY4Pattern(Locale locale) {
        String pattern = getPattern(locale);
        if (pattern.indexOf("yyyy") < 0) {
            pattern = LdiStringUtil.replace(pattern, "yy", "yyyy");
        }
        return pattern;
    }

    public static String getPattern(Locale locale) {
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        String pattern = df.toPattern();
        int index = pattern.indexOf(' ');
        if (index > 0) {
            pattern = pattern.substring(0, index);
        }
        if (pattern.indexOf("MM") < 0) {
            pattern = LdiStringUtil.replace(pattern, "M", "MM");
        }
        if (pattern.indexOf("dd") < 0) {
            pattern = LdiStringUtil.replace(pattern, "d", "dd");
        }
        return pattern;
    }

    public static String findDelimiter(String value) {
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (Character.isDigit(c)) {
                continue;
            }
            return Character.toString(c);
        }
        return null;
    }

    public static String findDelimiterFromPattern(String pattern) {
        String ret = null;
        for (int i = 0; i < pattern.length(); ++i) {
            char c = pattern.charAt(i);
            if (c != 'y' && c != 'M' && c != 'd') {
                ret = String.valueOf(c);
                break;
            }
        }
        return ret;
    }

    public static String removeDelimiter(String pattern) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < pattern.length(); ++i) {
            char c = pattern.charAt(i);
            if (c == 'y' || c == 'M' || c == 'd') {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
