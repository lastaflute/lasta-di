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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.dbflute.lasta.di.exception.IllegalAccessRuntimeException;
import org.dbflute.lasta.di.exception.SIllegalArgumentException;
import org.dbflute.lasta.di.util.tiger.LdiReflectionUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiFieldUtil {

    /**
     * ReflectUtilのクラス名です。
     */
    protected static final String REFLECTION_UTIL_CLASS_NAME = LdiReflectionUtil.class.getName();

    /**
     * {@link #getElementTypeOfCollectionFromFieldType(Field)}への定数参照です
     */
    protected static final Method GET_ELEMENT_TYPE_OF_COLLECTION_FROM_FIELD_TYPE_METHOD = getElementTypeFromFieldTypeMethod("Collection");

    /**
     * {@link #getElementTypeOfListFromFieldType(Field)}への定数参照です
     */
    protected static final Method GET_ELEMENT_TYPE_OF_LIST_FROM_FIELD_TYPE_METHOD = getElementTypeFromFieldTypeMethod("List");

    /**
     * {@link #getElementTypeOfSetFromFieldType(Field)}への定数参照です
     */
    protected static final Method GET_ELEMENT_TYPE_OF_SET_FROM_FIELD_TYPE_METHOD = getElementTypeFromFieldTypeMethod("Set");

    protected LdiFieldUtil() {
    }

    public static Object get(Field field, Object target) throws IllegalAccessRuntimeException {
        assertArgumentNotNull("field", field);
        try {
            return field.get(target);
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessRuntimeException(field.getDeclaringClass(), ex);
        }
    }

    public static int getInt(Field field) throws IllegalAccessRuntimeException {
        return getInt(field, null);
    }

    public static int getInt(Field field, Object target) throws IllegalAccessRuntimeException {
        try {
            return field.getInt(target);
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessRuntimeException(field.getDeclaringClass(), ex);
        }
    }

    public static String getString(Field field) throws IllegalAccessRuntimeException {
        return getString(field, null);
    }

    public static String getString(Field field, Object target) throws IllegalAccessRuntimeException {
        try {
            return (String) field.get(target);
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessRuntimeException(field.getDeclaringClass(), ex);
        }
    }

    /**
     * {@link Field}に値を設定します。
     * 
     * @param field
     * @param target
     * @param value
     * @throws IllegalAccessRuntimeException
     *             {@link IllegalAccessException}が発生した場合
     * @see Field#set(Object, Object)
     */
    public static void set(Field field, Object target, Object value) throws IllegalAccessRuntimeException {

        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessRuntimeException(field.getDeclaringClass(), e);
        } catch (IllegalArgumentException e) {
            Class clazz = field.getDeclaringClass();
            Class fieldClass = field.getType();
            Class valueClass = value == null ? null : value.getClass();
            Class targetClass = target == null ? null : target.getClass();
            throw new SIllegalArgumentException("ESSR0094", new Object[] { clazz.getName(), clazz.getClassLoader(), fieldClass.getName(),
                    fieldClass.getClassLoader(), field.getName(), valueClass == null ? null : valueClass.getName(),
                    valueClass == null ? null : valueClass.getClassLoader(), value, targetClass == null ? null : targetClass.getName(),
                    targetClass == null ? null : targetClass.getClassLoader() }, e);
        }

    }

    public static boolean isInstanceField(Field field) {
        int mod = field.getModifiers();
        // treat final fields as read-only property in lasta_di  
        //return !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
        return !Modifier.isStatic(mod);
    }

    /**
     * パブリックフィールドかどうか返します。
     * 
     * @param field
     * @return パブリックフィールドかどうか
     */
    public static boolean isPublicField(Field field) {
        int mod = field.getModifiers();
        return Modifier.isPublic(mod);
    }

    /**
     * Java5以上の場合は、指定されたフィールドのパラメタ化されたコレクションの要素型を返します。
     * 
     * @param field
     *            フィールド
     * @return フィールドのパラメタ化されたコレクションの要素型
     */
    public static Class getElementTypeOfCollectionFromFieldType(final Field field) {
        if (GET_ELEMENT_TYPE_OF_COLLECTION_FROM_FIELD_TYPE_METHOD == null) {
            return null;
        }
        return (Class) LdiMethodUtil.invoke(GET_ELEMENT_TYPE_OF_COLLECTION_FROM_FIELD_TYPE_METHOD, null, new Object[] { field });
    }

    /**
     * Java5以上の場合は、指定されたフィールドのパラメタ化されたリストの要素型を返します。
     * 
     * @param field
     *            フィールド
     * @return フィールドのパラメタ化されたリストの要素型
     */
    public static Class getElementTypeOfListFromFieldType(final Field field) {
        if (GET_ELEMENT_TYPE_OF_LIST_FROM_FIELD_TYPE_METHOD == null) {
            return null;
        }
        return (Class) LdiMethodUtil.invoke(GET_ELEMENT_TYPE_OF_LIST_FROM_FIELD_TYPE_METHOD, null, new Object[] { field });
    }

    /**
     * Java5以上の場合は、指定されたフィールドのパラメタ化されたセットの要素型を返します。
     * 
     * @param field
     *            フィールド
     * @return フィールドのパラメタ化されたセットの要素型
     */
    public static Class getElementTypeOfSetFromFieldType(final Field field) {
        if (GET_ELEMENT_TYPE_OF_SET_FROM_FIELD_TYPE_METHOD == null) {
            return null;
        }
        return (Class) LdiMethodUtil.invoke(GET_ELEMENT_TYPE_OF_SET_FROM_FIELD_TYPE_METHOD, null, new Object[] { field });
    }

    /**
     * <code>ReflectionUtil#getElementTypeOf<var>Xxx</var>FromFieldType()</code>
     * の {@link Method}を返します。
     * 
     * @param type
     *            取得するメソッドが対象とする型名
     * @return
     *         <code>ReflectionUtil#getElementTypeOf<var>Xxx</var>FromFieldType()</code>
     *         の{@link Method}
     */
    protected static Method getElementTypeFromFieldTypeMethod(final String type) {
        try {
            final Class reflectionUtilClass = Class.forName(REFLECTION_UTIL_CLASS_NAME);
            return reflectionUtilClass.getMethod("getElementTypeOf" + type + "FromFieldType", new Class[] { Field.class });
        } catch (final Throwable ignore) {}
        return null;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected static void assertArgumentNotNull(String variableName, Object value) {
        if (variableName == null) {
            throw new IllegalArgumentException("The variableName should not be null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("The argument '" + variableName + "' should not be null.");
        }
    }
}
