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
package org.lastaflute.di.util.tiger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.lastaflute.di.exception.ClassNotFoundRuntimeException;
import org.lastaflute.di.exception.IllegalAccessRuntimeException;
import org.lastaflute.di.exception.InstantiationRuntimeException;
import org.lastaflute.di.exception.InvocationTargetRuntimeException;
import org.lastaflute.di.exception.NoSuchConstructorRuntimeException;
import org.lastaflute.di.exception.NoSuchFieldRuntimeException;
import org.lastaflute.di.exception.NoSuchMethodRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class LdiReflectionUtil {

    protected LdiReflectionUtil() {
    }

    public static <T> Class<T> forName(final String className) throws ClassNotFoundRuntimeException {
        return forName(className, Thread.currentThread().getContextClassLoader());
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> forName(final String className, final ClassLoader loader) throws ClassNotFoundRuntimeException {
        try {
            return (Class<T>) Class.forName(className, true, loader);
        } catch (final ClassNotFoundException e) {
            throw new ClassNotFoundRuntimeException(e);
        }
    }

    public static <T> Class<T> forNameNoException(final String className) {
        return forNameNoException(className, Thread.currentThread().getContextClassLoader());
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> forNameNoException(final String className, final ClassLoader loader) {
        try {
            return (Class<T>) Class.forName(className, true, loader);
        } catch (final Throwable ignore) {
            return null;
        }
    }

    public static <T> Constructor<T> getConstructor(final Class<T> clazz, final Class<?>... argTypes)
            throws NoSuchConstructorRuntimeException {
        try {
            return clazz.getConstructor(argTypes);
        } catch (final NoSuchMethodException e) {
            throw new NoSuchConstructorRuntimeException(clazz, argTypes, e);
        }
    }

    public static <T> Constructor<T> getDeclaredConstructor(final Class<T> clazz, final Class<?>... argTypes)
            throws NoSuchConstructorRuntimeException {
        try {
            return clazz.getDeclaredConstructor(argTypes);
        } catch (final NoSuchMethodException e) {
            throw new NoSuchConstructorRuntimeException(clazz, argTypes, e);
        }
    }

    public static Field getField(final Class<?> clazz, final String name) throws NoSuchFieldRuntimeException {
        try {
            return clazz.getField(name);
        } catch (final NoSuchFieldException e) {
            throw new NoSuchFieldRuntimeException(clazz, name, e);
        }
    }

    public static Field getDeclaredField(final Class<?> clazz, final String name) throws NoSuchFieldRuntimeException {
        try {
            return clazz.getDeclaredField(name);
        } catch (final NoSuchFieldException e) {
            throw new NoSuchFieldRuntimeException(clazz, name, e);
        }
    }

    public static Method getMethod(final Class<?> clazz, final String name, final Class<?>... argTypes)
            throws NoSuchMethodRuntimeException {
        try {
            return clazz.getMethod(name, argTypes);
        } catch (final NoSuchMethodException e) {
            throw new NoSuchMethodRuntimeException(clazz, name, argTypes, e);
        }
    }

    public static Method getDeclaredMethod(final Class<?> clazz, final String name, final Class<?>... argTypes)
            throws NoSuchMethodRuntimeException {
        try {
            return clazz.getDeclaredMethod(name, argTypes);
        } catch (final NoSuchMethodException e) {
            throw new NoSuchMethodRuntimeException(clazz, name, argTypes, e);
        }
    }

    public static <T> T newInstance(final Class<T> clazz) throws InstantiationRuntimeException, IllegalAccessRuntimeException {
        try {
            return clazz.newInstance();
        } catch (final InstantiationException e) {
            throw new InstantiationRuntimeException(clazz, e);
        } catch (final IllegalAccessException e) {
            throw new IllegalAccessRuntimeException(clazz, e);
        }
    }

    public static <T> T newInstance(final Constructor<T> constructor, final Object... args)
            throws InstantiationRuntimeException, IllegalAccessRuntimeException {
        try {
            return constructor.newInstance(args);
        } catch (final InstantiationException e) {
            throw new InstantiationRuntimeException(constructor.getDeclaringClass(), e);
        } catch (final IllegalAccessException e) {
            throw new IllegalAccessRuntimeException(constructor.getDeclaringClass(), e);
        } catch (final InvocationTargetException e) {
            throw new InvocationTargetRuntimeException(constructor.getDeclaringClass(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(final Field field, final Object target) throws IllegalAccessRuntimeException {
        try {
            return (T) field.get(target);
        } catch (final IllegalAccessException e) {
            throw new IllegalAccessRuntimeException(field.getDeclaringClass(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getStaticValue(final Field field) throws IllegalAccessRuntimeException {
        return (T) getValue(field, null);
    }

    public static void setValue(final Field field, final Object target, final Object value) throws IllegalAccessRuntimeException {
        try {
            field.set(target, value);
        } catch (final IllegalAccessException e) {
            throw new IllegalAccessRuntimeException(field.getDeclaringClass(), e);
        }
    }

    public static void setStaticValue(final Field field, final Object value) throws IllegalAccessRuntimeException {
        setValue(field, null, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invoke(final Method method, final Object target, final Object... args)
            throws IllegalAccessRuntimeException, InvocationTargetRuntimeException {
        try {
            return (T) method.invoke(target, args);
        } catch (final IllegalAccessException e) {
            throw new IllegalAccessRuntimeException(method.getDeclaringClass(), e);
        } catch (final InvocationTargetException e) {
            throw new InvocationTargetRuntimeException(method.getDeclaringClass(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeStatic(final Method method, final Object... args)
            throws IllegalAccessRuntimeException, InvocationTargetRuntimeException {
        return (T) invoke(method, null, args);
    }

    public static Class<?> getElementTypeOfCollection(final Type parameterizedCollection) {
        return LdiGenericUtil.getRawClass(LdiGenericUtil.getElementTypeOfCollection(parameterizedCollection));
    }

    public static Class<?> getElementTypeOfCollectionFromFieldType(final Field field) {
        final Type type = field.getGenericType();
        return getElementTypeOfCollection(type);
    }

    public static Class<?> getElementTypeOfCollectionFromParameterType(final Method method, final int parameterPosition) {
        final Type[] parameterTypes = method.getGenericParameterTypes();
        return getElementTypeOfCollection(parameterTypes[parameterPosition]);
    }

    public static Class<?> getElementTypeOfCollectionFromReturnType(final Method method) {
        return getElementTypeOfCollection(method.getGenericReturnType());
    }

    public static Class<?> getElementTypeOfList(final Type parameterizedList) {
        return LdiGenericUtil.getRawClass(LdiGenericUtil.getElementTypeOfList(parameterizedList));
    }

    public static Class<?> getElementTypeOfListFromFieldType(final Field field) {
        final Type type = field.getGenericType();
        return getElementTypeOfList(type);
    }

    public static Class<?> getElementTypeOfListFromParameterType(final Method method, final int parameterPosition) {
        final Type[] parameterTypes = method.getGenericParameterTypes();
        return getElementTypeOfList(parameterTypes[parameterPosition]);
    }

    public static Class<?> getElementTypeOfListFromReturnType(final Method method) {
        return getElementTypeOfList(method.getGenericReturnType());
    }

    public static Class<?> getElementTypeOfSet(final Type parameterizedSet) {
        return LdiGenericUtil.getRawClass(LdiGenericUtil.getElementTypeOfSet(parameterizedSet));
    }

    public static Class<?> getElementTypeOfSetFromFieldType(final Field field) {
        final Type type = field.getGenericType();
        return getElementTypeOfSet(type);
    }

    public static Class<?> getElementTypeOfSetFromParameterType(final Method method, final int parameterPosition) {
        final Type[] parameterTypes = method.getGenericParameterTypes();
        return getElementTypeOfSet(parameterTypes[parameterPosition]);
    }

    public static Class<?> getElementTypeOfSetFromReturnType(final Method method) {
        return getElementTypeOfSet(method.getGenericReturnType());
    }
}
