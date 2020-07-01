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
package org.lastaflute.di.util.tiger;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class LdiGenericUtil {

    protected static final Type[] EMPTY_TYPES = new Type[0];

    protected LdiGenericUtil() {
    }

    public static boolean isTypeOf(final Type type, final Class<?> clazz) {
        if (Class.class.isInstance(type)) {
            return clazz.isAssignableFrom(Class.class.cast(type));
        }
        if (ParameterizedType.class.isInstance(type)) {
            final ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
            return isTypeOf(parameterizedType.getRawType(), clazz);
        }
        return false;
    }

    /**
     * @param type The type that has the generic type. (NotNull)
     * @return The first generic type for the specified type. (NullAllowed: e.g. not found)
     */
    public static Class<?> getGenericFirstClass(Type type) {
        return findGenericClass(type, 0);
    }

    /**
     * @param type The type that has the generic type. (NotNull)
     * @return The second generic type for the specified type. (NullAllowed: e.g. not found)
     */
    public static Class<?> getGenericSecondClass(Type type) {
        return findGenericClass(type, 1);
    }

    protected static Class<?> findGenericClass(Type type, int index) {
        return getRawClass(getGenericParameterType(type, index));
    }

    public static Class<?> getRawClass(final Type type) {
        if (Class.class.isInstance(type)) {
            return Class.class.cast(type);
        }
        if (ParameterizedType.class.isInstance(type)) {
            final ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
            return getRawClass(parameterizedType.getRawType());
        }
        if (WildcardType.class.isInstance(type)) {
            final WildcardType wildcardType = WildcardType.class.cast(type);
            final Type[] types = wildcardType.getUpperBounds();
            return getRawClass(types[0]);
        }
        if (GenericArrayType.class.isInstance(type)) {
            final GenericArrayType genericArrayType = GenericArrayType.class.cast(type);
            final Class<?> rawClass = getRawClass(genericArrayType.getGenericComponentType());
            return Array.newInstance(rawClass, 0).getClass();
        }
        return null;
    }

    public static Type getGenericParameterType(final Type type, final int index) {
        if (!ParameterizedType.class.isInstance(type)) {
            return null;
        }
        final Type[] genericParameter = getGenericParameterTypes(type);
        if (genericParameter.length == 0 || genericParameter.length < index) {
            return null;
        }
        return genericParameter[index];
    }

    public static Type[] getGenericParameterTypes(final Type type) {
        if (ParameterizedType.class.isInstance(type)) {
            return ParameterizedType.class.cast(type).getActualTypeArguments();
        }
        if (GenericArrayType.class.isInstance(type)) {
            return getGenericParameterTypes(GenericArrayType.class.cast(type).getGenericComponentType());
        }
        return EMPTY_TYPES;
    }

    public static Type getElementTypeOfArray(final Type type) {
        if (!GenericArrayType.class.isInstance(type)) {
            return null;
        }
        return GenericArrayType.class.cast(type).getGenericComponentType();
    }

    public static Type getElementTypeOfCollection(final Type type) {
        if (!isTypeOf(type, Collection.class)) {
            return null;
        }
        return getGenericParameterType(type, 0);
    }

    public static Type getElementTypeOfList(final Type type) {
        if (!isTypeOf(type, List.class)) {
            return null;
        }
        return getGenericParameterType(type, 0);
    }

    public static Type getElementTypeOfSet(final Type type) {
        if (!isTypeOf(type, Set.class)) {
            return null;
        }
        return getGenericParameterType(type, 0);
    }

    public static Type getKeyTypeOfMap(final Type type) {
        if (!isTypeOf(type, Map.class)) {
            return null;
        }
        return getGenericParameterType(type, 0);
    }

    public static Type getValueTypeOfMap(final Type type) {
        if (!isTypeOf(type, Map.class)) {
            return null;
        }
        return getGenericParameterType(type, 1);
    }

    public static Map<TypeVariable<?>, Type> getTypeVariableMap(final Class<?> clazz) {
        final Map<TypeVariable<?>, Type> map = LdiCollectionsUtil.newLinkedHashMap();

        final TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
        for (TypeVariable<?> typeParameter : typeParameters) {
            map.put(typeParameter, getActualClass(typeParameter.getBounds()[0], map));
        }

        final Class<?> superClass = clazz.getSuperclass();
        final Type superClassType = clazz.getGenericSuperclass();
        if (superClass != null) {
            gatherTypeVariables(superClass, superClassType, map);
        }

        final Class<?>[] interfaces = clazz.getInterfaces();
        final Type[] interfaceTypes = clazz.getGenericInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            gatherTypeVariables(interfaces[i], interfaceTypes[i], map);
        }

        return map;
    }

    protected static void gatherTypeVariables(final Class<?> clazz, final Type type, final Map<TypeVariable<?>, Type> map) {
        if (clazz == null) {
            return;
        }
        gatherTypeVariables(type, map);

        final Class<?> superClass = clazz.getSuperclass();
        final Type superClassType = clazz.getGenericSuperclass();
        if (superClass != null) {
            gatherTypeVariables(superClass, superClassType, map);
        }

        final Class<?>[] interfaces = clazz.getInterfaces();
        final Type[] interfaceTypes = clazz.getGenericInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            gatherTypeVariables(interfaces[i], interfaceTypes[i], map);
        }
    }

    protected static void gatherTypeVariables(final Type type, final Map<TypeVariable<?>, Type> map) {
        if (ParameterizedType.class.isInstance(type)) {
            final ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
            final TypeVariable<?>[] typeVariables = GenericDeclaration.class.cast(parameterizedType.getRawType()).getTypeParameters();
            final Type[] actualTypes = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < actualTypes.length; ++i) {
                map.put(typeVariables[i], actualTypes[i]);
            }
        }
    }

    public static Class<?> getActualClass(final Type type, final Map<TypeVariable<?>, Type> map) {
        if (Class.class.isInstance(type)) {
            return Class.class.cast(type);
        }
        if (ParameterizedType.class.isInstance(type)) {
            return getActualClass(ParameterizedType.class.cast(type).getRawType(), map);
        }
        if (WildcardType.class.isInstance(type)) {
            return getActualClass(WildcardType.class.cast(type).getUpperBounds()[0], map);
        }
        if (TypeVariable.class.isInstance(type)) {
            final TypeVariable<?> typeVariable = TypeVariable.class.cast(type);
            if (map.containsKey(typeVariable)) {
                return getActualClass(map.get(typeVariable), map);
            }
            return getActualClass(typeVariable.getBounds()[0], map);
        }
        if (GenericArrayType.class.isInstance(type)) {
            final GenericArrayType genericArrayType = GenericArrayType.class.cast(type);
            final Class<?> componentClass = getActualClass(genericArrayType.getGenericComponentType(), map);
            return Array.newInstance(componentClass, 0).getClass();
        }
        return null;
    }

    public static Class<?> getActualElementClassOfArray(final Type type, final Map<TypeVariable<?>, Type> map) {
        if (!GenericArrayType.class.isInstance(type)) {
            return null;
        }
        return getActualClass(GenericArrayType.class.cast(type).getGenericComponentType(), map);
    }

    public static Class<?> getActualElementClassOfCollection(final Type type, final Map<TypeVariable<?>, Type> map) {
        if (!isTypeOf(type, Collection.class)) {
            return null;
        }
        return getActualClass(getGenericParameterType(type, 0), map);
    }

    public static Class<?> getActualElementClassOfList(final Type type, final Map<TypeVariable<?>, Type> map) {
        if (!isTypeOf(type, List.class)) {
            return null;
        }
        return getActualClass(getGenericParameterType(type, 0), map);
    }

    public static Class<?> getActualElementClassOfSet(final Type type, final Map<TypeVariable<?>, Type> map) {
        if (!isTypeOf(type, Set.class)) {
            return null;
        }
        return getActualClass(getGenericParameterType(type, 0), map);
    }

    public static Class<?> getActualKeyClassOfMap(final Type type, final Map<TypeVariable<?>, Type> map) {
        if (!isTypeOf(type, Map.class)) {
            return null;
        }
        return getActualClass(getGenericParameterType(type, 0), map);
    }

    public static Class<?> getActualValueClassOfMap(final Type type, final Map<TypeVariable<?>, Type> map) {
        if (!isTypeOf(type, Map.class)) {
            return null;
        }
        return getActualClass(getGenericParameterType(type, 1), map);
    }
}
