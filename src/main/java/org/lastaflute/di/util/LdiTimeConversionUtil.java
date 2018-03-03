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

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.lastaflute.di.exception.ParseRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiTimeConversionUtil {

    protected LdiTimeConversionUtil() {
    }

    /**
     * @param o
     * @return 
     */
    public static Time toTime(Object o) {
        return toTime(o, null);
    }

    /**
     * @param o
     * @param pattern
     * @return 
     */
    public static Time toTime(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return toTime((String) o, pattern);
        } else if (o instanceof Time) {
            return (Time) o;
        } else if (o instanceof Calendar) {
            return new Time(((Calendar) o).getTime().getTime());
        } else {
            return toTime(o.toString(), pattern);
        }
    }

    /**
     * @param s
     * @param pattern
     * @return 
     */
    public static Time toTime(String s, String pattern) {
        return toTime(s, pattern, Locale.getDefault());
    }

    /**
     * @param s
     * @param pattern
     * @param locale
     * @return 
     */
    public static Time toTime(String s, String pattern, Locale locale) {
        if (LdiStringUtil.isEmpty(s)) {
            return null;
        }
        SimpleDateFormat sdf = getDateFormat(s, pattern, locale);
        try {
            return new Time(sdf.parse(s).getTime());
        } catch (ParseException ex) {
            throw new ParseRuntimeException(ex);
        }
    }

    /**
     * @param s
     * @param pattern
     * @param locale
     * @return 
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
     * @return 
     */
    public static SimpleDateFormat getDateFormat(String s, Locale locale) {
        String pattern = getPattern(locale);
        if (s.length() == pattern.length()) {
            return new SimpleDateFormat(pattern);
        }
        String shortPattern = convertShortPattern(pattern);
        if (s.length() == shortPattern.length()) {
            return new SimpleDateFormat(shortPattern);
        }
        return new SimpleDateFormat(pattern);
    }

    public static String getPattern(Locale locale) {
        return "HH:mm:ss";
    }

    public static String convertShortPattern(String pattern) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < pattern.length(); ++i) {
            char c = pattern.charAt(i);
            if (c == 'h' || c == 'H' || c == 'm' || c == 's') {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
