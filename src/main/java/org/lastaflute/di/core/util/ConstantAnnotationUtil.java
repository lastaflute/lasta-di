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
package org.lastaflute.di.core.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.util.LdiModifierUtil;
import org.lastaflute.di.util.LdiStringUtil;
import org.lastaflute.di.util.Tokenizer;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ConstantAnnotationUtil {

    protected ConstantAnnotationUtil() {
    }

    public static Map<String, String> convertExpressionToMap(String expression) {
        if (LdiStringUtil.isEmpty(expression)) {
            return null;
        }
        MyTokenizer tokenizer = new MyTokenizer(expression);
        Map<String, String> ret = new HashMap<String, String>();
        for (int token = tokenizer.nextToken(); token != MyTokenizer.TT_EOF; token = tokenizer.nextToken()) {
            String s = tokenizer.getStringValue();
            token = tokenizer.nextToken();
            if (token == MyTokenizer.TT_EQUAL) {
                token = tokenizer.nextToken();
                String s2 = tokenizer.getStringValue();
                ret.put(s, s2);
                tokenizer.nextToken();
            } else if (token == MyTokenizer.TT_COMMA) {
                ret.put(null, s);
            } else if (token == MyTokenizer.TT_EOF) {
                ret.put(null, s);
                break;
            }
        }
        return ret;
    }

    public static boolean isConstantAnnotation(Field field) {
        return LdiModifierUtil.isPublicStaticFinalField(field) && field.getType().equals(String.class);
    }

    protected static class MyTokenizer extends Tokenizer {

        public static final int TT_EQUAL = '=';
        public static final int TT_COMMA = ',';
        private static byte[] defaultCtype = new byte[256];

        static {
            setup(defaultCtype);
        }

        public MyTokenizer(String str) {
            super(str, defaultCtype);
        }

        public MyTokenizer(String str, byte[] ctype) {
            super(str, ctype);
        }

        protected static void setup(byte[] ctype2) {
            Tokenizer.setup(ctype2);
            ordinaryChar(ctype2, '=');
            ordinaryChar(ctype2, ',');
            wordChar(ctype2, '-');
        }
    }
}