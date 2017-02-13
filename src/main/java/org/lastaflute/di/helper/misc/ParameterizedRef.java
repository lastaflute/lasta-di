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
package org.lastaflute.di.helper.misc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @param <PARAM> The type of parameter.
 * @author jflute
 */
public class ParameterizedRef<PARAM> {

    public ParameterizedType getType() {
        final Type tp = getClass().getGenericSuperclass();
        if (tp instanceof ParameterizedType) {
            final Type[] argTypes = ((ParameterizedType) tp).getActualTypeArguments();
            if (argTypes != null && argTypes.length > 0) {
                final Type firstType = argTypes[0];
                if (firstType instanceof ParameterizedType) {
                    return (ParameterizedType) firstType;
                }
            }
            String msg = "Cannot get parameterized type: argTypes=" + Arrays.asList(argTypes);
            throw new IllegalStateException(msg);
        } else {
            String msg = "Cannot get parameterized type: genericSuperclass=" + tp;
            throw new IllegalStateException(msg);
        }
    }
}
