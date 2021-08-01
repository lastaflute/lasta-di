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

import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiDecimalFormatUtil {

    protected LdiDecimalFormatUtil() {
    }

    /**
     * @param s
     * @return 
     * @see #normalize(String, Locale)
     */
    public static String normalize(String s) {
        return normalize(s, Locale.getDefault());
    }

    public static String normalize(String s, Locale locale) {
        if (s == null) {
            return null;
        }
        DecimalFormatSymbols symbols = LdiDecimalFormatSymbolsUtil.getDecimalFormatSymbols(locale);
        char decimalSep = symbols.getDecimalSeparator();
        char groupingSep = symbols.getGroupingSeparator();
        StringBuffer buf = new StringBuffer(20);
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == groupingSep) {
                continue;
            } else if (c == decimalSep) {
                c = '.';
            }
            buf.append(c);
        }
        return buf.toString();
    }
}
