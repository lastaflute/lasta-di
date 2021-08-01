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
package org.lastaflute.di.helper.beans.factory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.Map;

import org.lastaflute.di.helper.beans.ParameterizedClassDesc;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ParameterizedClassDescFactory {

    protected static final String PROVIDER_CLASS_NAME = ParameterizedClassDescFactory.class.getName() + "Provider";
    protected static final Provider provider = createProvider();

    public static Map<TypeVariable<?>, Type> getTypeVariables(Class<?> beanClass) {
        if (provider == null) {
            return Collections.emptyMap();
        }
        return provider.getTypeVariables(beanClass);
    }

    public static ParameterizedClassDesc createParameterizedClassDesc(final Field field, final Map<TypeVariable<?>, Type> map) {
        if (provider == null) {
            return null;
        }
        return provider.createParameterizedClassDesc(field, map);
    }

    public static ParameterizedClassDesc createParameterizedClassDesc(final Method method, final int index,
            final Map<TypeVariable<?>, Type> map) {
        if (provider == null) {
            return null;
        }
        return provider.createParameterizedClassDesc(method, index, map);
    }

    public static ParameterizedClassDesc createParameterizedClassDesc(final Method method, final Map<TypeVariable<?>, Type> map) {
        if (provider == null) {
            return null;
        }
        return provider.createParameterizedClassDesc(method, map);
    }

    protected static Provider createProvider() {
        try {
            final Class<?> clazz = Class.forName(PROVIDER_CLASS_NAME);
            return (Provider) clazz.newInstance();
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * @author modified by jflute (originated in Seasar)
     */
    public interface Provider {

        Map<TypeVariable<?>, Type> getTypeVariables(Class<?> beanClass);

        ParameterizedClassDesc createParameterizedClassDesc(Field field, Map<TypeVariable<?>, Type> map);

        ParameterizedClassDesc createParameterizedClassDesc(Method method, int index, Map<TypeVariable<?>, Type> map);

        ParameterizedClassDesc createParameterizedClassDesc(Method method, Map<TypeVariable<?>, Type> map);
    }
}
