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
package org.lastaflute.di.helper.beans.converter;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.exception.ParseRuntimeException;
import org.lastaflute.di.helper.beans.Converter;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * 数値用のコンバータです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class NumberConverter implements Converter {

    /**
     * 数値のパターンです。
     */
    protected String pattern;

    /**
     * インスタンスを構築します。
     * 
     * @param pattern
     *            数値のパターン
     */
    public NumberConverter(String pattern) {
        if (LdiStringUtil.isEmpty(pattern)) {
            throw new EmptyRuntimeException("pattern");
        }
        this.pattern = pattern;
    }

    public Object getAsObject(String value) {
        if (LdiStringUtil.isEmpty(value)) {
            return null;
        }
        try {
            return new DecimalFormat(pattern).parse(value);
        } catch (ParseException e) {
            throw new ParseRuntimeException(e);
        }

    }

    public String getAsString(Object value) {
        return new DecimalFormat(pattern).format(value);
    }

    public boolean isTarget(Class clazz) {
        return Number.class.isAssignableFrom(clazz);
    }

}
