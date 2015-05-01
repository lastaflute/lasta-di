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
package org.dbflute.lasta.di.util;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

/**
 * {@link DecimalFormatSymbols}用のユーティリティクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class LdiDecimalFormatSymbolsUtil {

    private static Map cache = LdiMapUtil.createHashMap();

    /**
     * インスタンスを構築します。
     */
    protected LdiDecimalFormatSymbolsUtil() {
    }

    /**
     * {@link DecimalFormatSymbols}を返します。
     * 
     * @return {@link DecimalFormatSymbols}
     */
    public static DecimalFormatSymbols getDecimalFormatSymbols() {
        return getDecimalFormatSymbols(Locale.getDefault());
    }

    /**
     * {@link DecimalFormatSymbols}を返します。
     * 
     * @param locale
     * @return {@link DecimalFormatSymbols}
     */
    public static DecimalFormatSymbols getDecimalFormatSymbols(Locale locale) {
        DecimalFormatSymbols symbols = (DecimalFormatSymbols) cache.get(locale);
        if (symbols == null) {
            symbols = new DecimalFormatSymbols(locale);
            cache.put(locale, symbols);
        }
        return symbols;
    }
}
