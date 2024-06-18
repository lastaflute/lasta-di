/*
 * Copyright 2015-2024 the original author or authors.
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
package org.lastaflute.di.helper.beans.impl;

import java.lang.reflect.Type;

import org.lastaflute.di.helper.beans.ParameterizedClassDesc;
import org.lastaflute.di.util.tiger.LdiGenericUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ParameterizedClassDescImpl implements ParameterizedClassDesc {

    protected final Type parameterizedType; // not null
    protected final Class<?> rawClass; // not null
    protected ParameterizedClassDesc[] arguments; // null or not empty array

    public ParameterizedClassDescImpl(Type parameterizedType, Class<?> rawClass) {
        this.parameterizedType = parameterizedType;
        this.rawClass = rawClass;
    }

    public ParameterizedClassDescImpl(Type parameterizedType, Class<?> rawClass, ParameterizedClassDesc[] arguments) {
        this.parameterizedType = parameterizedType;
        this.rawClass = rawClass;
        this.arguments = arguments;
    }

    public boolean isParameterizedClass() {
        return arguments != null;
    }

    public Type getParameterizedType() {
        return parameterizedType;
    }

    public Class<?> getRawClass() {
        return rawClass;
    }

    public ParameterizedClassDesc[] getArguments() {
        return arguments;
    }

    public void setArguments(ParameterizedClassDesc[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public Class<?> getGenericFirstType() {
        return LdiGenericUtil.getGenericFirstClass(parameterizedType);
    }

    @Override
    public Class<?> getGenericSecondType() {
        return LdiGenericUtil.getGenericSecondClass(parameterizedType);
    }
}
