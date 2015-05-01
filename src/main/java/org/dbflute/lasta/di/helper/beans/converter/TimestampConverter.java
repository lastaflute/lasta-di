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
package org.dbflute.lasta.di.helper.beans.converter;

import java.util.Date;

import org.dbflute.lasta.di.exception.EmptyRuntimeException;
import org.dbflute.lasta.di.helper.beans.Converter;
import org.dbflute.lasta.di.util.LdiStringConversionUtil;
import org.dbflute.lasta.di.util.LdiStringUtil;
import org.dbflute.lasta.di.util.LdiTimestampConversionUtil;

/**
 * 日時用のコンバータです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class TimestampConverter implements Converter {

    /**
     * 日時のパターンです。
     */
    protected String pattern;

    /**
     * インスタンスを構築します。
     * 
     * @param pattern
     *            日時のパターン
     */
    public TimestampConverter(String pattern) {
        if (LdiStringUtil.isEmpty(pattern)) {
            throw new EmptyRuntimeException("pattern");
        }
        this.pattern = pattern;
    }

    public Object getAsObject(String value) {
        if (LdiStringUtil.isEmpty(value)) {
            return null;
        }
        return LdiTimestampConversionUtil.toTimestamp(value, pattern);
    }

    public String getAsString(Object value) {
        return LdiStringConversionUtil.toString((Date) value, pattern);
    }

    public boolean isTarget(Class clazz) {
        return clazz == java.sql.Timestamp.class;
    }

}
