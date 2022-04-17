/*
 * Copyright 2015-2022 the original author or authors.
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

import static org.lastaflute.di.util.tiger.LdiGenericUtil.getActualClass;
import static org.lastaflute.di.util.tiger.LdiGenericUtil.getTypeVariableMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import org.lastaflute.di.helper.beans.ParameterizedClassDesc;
import org.lastaflute.di.helper.beans.factory.ParameterizedClassDescFactory.Provider;
import org.lastaflute.di.helper.beans.impl.ParameterizedClassDescImpl;
import org.lastaflute.di.util.tiger.LdiGenericUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ParameterizedClassDescFactoryProvider implements Provider {

    public Map<TypeVariable<?>, Type> getTypeVariables(Class<?> beanClass) {
        return getTypeVariableMap(beanClass);
    }

    public ParameterizedClassDesc createParameterizedClassDesc(Field field, Map<TypeVariable<?>, Type> map) {
        return createParameterizedClassDesc(field.getGenericType(), map);
    }

    public ParameterizedClassDesc createParameterizedClassDesc(Method method, int index, Map<TypeVariable<?>, Type> map) {
        return createParameterizedClassDesc(method.getGenericParameterTypes()[index], map);
    }

    public ParameterizedClassDesc createParameterizedClassDesc(Method method, Map<TypeVariable<?>, Type> map) {
        return createParameterizedClassDesc(method.getGenericReturnType(), map);
    }

    public ParameterizedClassDesc createParameterizedClassDesc(Type type, Map<TypeVariable<?>, Type> map) {
        final Class<?> rowClass = getActualClass(type, map);
        if (rowClass == null) {
            return null;
        }
        final Type[] parameterTypes = LdiGenericUtil.getGenericParameterTypes(type);
        if (parameterTypes == null || parameterTypes.length == 0) {
            return new ParameterizedClassDescImpl(type, rowClass);
        } else {
            final ParameterizedClassDesc[] parameterDescs = new ParameterizedClassDesc[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; ++i) {
                parameterDescs[i] = createParameterizedClassDesc(parameterTypes[i], map);
            }
            return new ParameterizedClassDescImpl(type, rowClass, parameterDescs);
        }
    }
}
