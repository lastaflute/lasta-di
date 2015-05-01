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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

import org.dbflute.lasta.di.core.util.ClassPoolUtil;
import org.dbflute.lasta.di.exception.ClassNotFoundRuntimeException;
import org.dbflute.lasta.di.exception.IllegalAccessRuntimeException;
import org.dbflute.lasta.di.exception.InstantiationRuntimeException;
import org.dbflute.lasta.di.exception.NoSuchConstructorRuntimeException;
import org.dbflute.lasta.di.exception.NoSuchFieldRuntimeException;
import org.dbflute.lasta.di.exception.NoSuchMethodRuntimeException;

/**
 * {@link Class}用のユーティリティクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class LdiClassUtil {

    private static Map wrapperToPrimitiveMap = new HashMap();

    private static Map primitiveToWrapperMap = new HashMap();

    private static Map primitiveClassNameMap = new HashMap();

    static {
        wrapperToPrimitiveMap.put(Character.class, Character.TYPE);
        wrapperToPrimitiveMap.put(Byte.class, Byte.TYPE);
        wrapperToPrimitiveMap.put(Short.class, Short.TYPE);
        wrapperToPrimitiveMap.put(Integer.class, Integer.TYPE);
        wrapperToPrimitiveMap.put(Long.class, Long.TYPE);
        wrapperToPrimitiveMap.put(Double.class, Double.TYPE);
        wrapperToPrimitiveMap.put(Float.class, Float.TYPE);
        wrapperToPrimitiveMap.put(Boolean.class, Boolean.TYPE);

        primitiveToWrapperMap.put(Character.TYPE, Character.class);
        primitiveToWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveToWrapperMap.put(Short.TYPE, Short.class);
        primitiveToWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveToWrapperMap.put(Long.TYPE, Long.class);
        primitiveToWrapperMap.put(Double.TYPE, Double.class);
        primitiveToWrapperMap.put(Float.TYPE, Float.class);
        primitiveToWrapperMap.put(Boolean.TYPE, Boolean.class);

        primitiveClassNameMap.put(Character.TYPE.getName(), Character.TYPE);
        primitiveClassNameMap.put(Byte.TYPE.getName(), Byte.TYPE);
        primitiveClassNameMap.put(Short.TYPE.getName(), Short.TYPE);
        primitiveClassNameMap.put(Integer.TYPE.getName(), Integer.TYPE);
        primitiveClassNameMap.put(Long.TYPE.getName(), Long.TYPE);
        primitiveClassNameMap.put(Double.TYPE.getName(), Double.TYPE);
        primitiveClassNameMap.put(Float.TYPE.getName(), Float.TYPE);
        primitiveClassNameMap.put(Boolean.TYPE.getName(), Boolean.TYPE);
    }

    /**
     * 
     */
    protected LdiClassUtil() {
    }

    /**
     * {@link Class}を返します。
     * 
     * @param className
     * @return {@link Class}
     * @throws ClassNotFoundRuntimeException
     *             {@link ClassNotFoundException}がおきた場合
     * @see Class#forName(String)
     */
    public static Class forName(String className) throws ClassNotFoundRuntimeException {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            return Class.forName(className, true, loader);
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundRuntimeException(className, ex);
        }
    }

    /**
     * プリミティブクラスの場合は、ラッパークラスに変換して返します。
     * 
     * @param className
     * @return {@link Class}
     * @throws ClassNotFoundRuntimeException
     *             {@link ClassNotFoundException}がおきた場合
     * @see #forName(String)
     */
    public static Class convertClass(String className) throws ClassNotFoundRuntimeException {
        Class clazz = (Class) primitiveClassNameMap.get(className);
        if (clazz != null) {
            return clazz;
        }
        return forName(className);
    }

    public static Object newInstance(Class<?> clazz) throws InstantiationRuntimeException, IllegalAccessRuntimeException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new InstantiationRuntimeException(clazz, ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessRuntimeException(clazz, ex);
        }
    }

    public static Object newInstance(String className) throws ClassNotFoundRuntimeException, InstantiationRuntimeException,
            IllegalAccessRuntimeException {
        return newInstance(forName(className));
    }

    public static boolean isAssignableFrom(Class<?> toClass, Class<?> fromClass) {
        if (toClass == Object.class && !fromClass.isPrimitive()) {
            return true;
        }
        if (toClass.isPrimitive()) {
            fromClass = getPrimitiveClassIfWrapper(fromClass);
        }
        return toClass.isAssignableFrom(fromClass);
    }

    public static Class getPrimitiveClass(Class clazz) {
        return (Class) wrapperToPrimitiveMap.get(clazz);
    }

    /**
     * ラッパークラスならプリミティブクラスに、 そうでなければそのままクラスを返します。
     * 
     * @param clazz
     * @return {@link Class}
     */
    public static Class getPrimitiveClassIfWrapper(Class clazz) {
        Class ret = getPrimitiveClass(clazz);
        if (ret != null) {
            return ret;
        }
        return clazz;
    }

    /**
     * プリミティブクラスをラッパークラスに変換します。
     * 
     * @param clazz
     * @return {@link Class}
     */
    public static Class getWrapperClass(Class clazz) {
        return (Class) primitiveToWrapperMap.get(clazz);
    }

    /**
     * プリミティブの場合はラッパークラス、そうでない場合はもとのクラスを返します。
     * 
     * @param clazz
     * @return {@link Class}
     */
    public static Class getWrapperClassIfPrimitive(Class clazz) {
        Class ret = getWrapperClass(clazz);
        if (ret != null) {
            return ret;
        }
        return clazz;
    }

    /**
     * {@link Constructor}を返します。
     * 
     * @param clazz
     * @param argTypes
     * @return {@link Constructor}
     * @throws NoSuchConstructorRuntimeException
     *             {@link NoSuchMethodException}がおきた場合
     * @see Class#getConstructor(Class[])
     */
    public static Constructor getConstructor(Class clazz, Class[] argTypes) throws NoSuchConstructorRuntimeException {
        try {
            return clazz.getConstructor(argTypes);
        } catch (NoSuchMethodException ex) {
            throw new NoSuchConstructorRuntimeException(clazz, argTypes, ex);
        }
    }

    /**
     * そのクラスに宣言されている {@link Constructor}を返します。
     * 
     * @param clazz
     * @param argTypes
     * @return {@link Constructor}
     * @throws NoSuchConstructorRuntimeException
     *             {@link NoSuchMethodException}がおきた場合
     * @see Class#getDeclaredConstructor(Class[])
     */
    public static Constructor getDeclaredConstructor(Class clazz, Class[] argTypes) throws NoSuchConstructorRuntimeException {
        try {
            return clazz.getDeclaredConstructor(argTypes);
        } catch (NoSuchMethodException ex) {
            throw new NoSuchConstructorRuntimeException(clazz, argTypes, ex);
        }
    }

    /**
     * {@link Method}を返します。
     * 
     * @param clazz
     * @param methodName
     * @param argTypes
     * @return {@link Method}
     * @throws NoSuchMethodRuntimeException
     *             {@link NoSuchMethodException}がおきた場合
     * @see Class#getMethod(String, Class[])
     */
    public static Method getMethod(Class clazz, String methodName, Class[] argTypes) throws NoSuchMethodRuntimeException {

        try {
            return clazz.getMethod(methodName, argTypes);
        } catch (NoSuchMethodException ex) {
            throw new NoSuchMethodRuntimeException(clazz, methodName, argTypes, ex);
        }
    }

    /**
     * そのクラスに宣言されている {@link Method}を返します。
     * 
     * @param clazz
     * @param methodName
     * @param argTypes
     * @return {@link Method}
     * @throws NoSuchMethodRuntimeException
     *             {@link NoSuchMethodException}がおきた場合
     * @see Class#getDeclaredMethod(String, Class[])
     */
    public static Method getDeclaredMethod(Class clazz, String methodName, Class[] argTypes) throws NoSuchMethodRuntimeException {

        try {
            return clazz.getDeclaredMethod(methodName, argTypes);
        } catch (NoSuchMethodException ex) {
            throw new NoSuchMethodRuntimeException(clazz, methodName, argTypes, ex);
        }
    }

    /**
     * {@link Field}を返します。
     * 
     * @param clazz
     * @param fieldName
     * @return {@link Field}
     * @throws NoSuchFieldRuntimeException
     *             {@link NoSuchFieldException}がおきた場合
     * @see Class#getField(String)
     */
    public static Field getField(Class clazz, String fieldName) throws NoSuchFieldRuntimeException {
        try {
            return clazz.getField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw new NoSuchFieldRuntimeException(clazz, fieldName, ex);
        }
    }

    /**
     * そのクラスに宣言されている {@link Field}を返します。
     * 
     * @param clazz
     * @param fieldName
     * @return {@link Field}
     * @throws NoSuchFieldRuntimeException
     *             {@link NoSuchFieldException}がおきた場合
     * @see Class#getDeclaredField(String)
     */
    public static Field getDeclaredField(Class clazz, String fieldName) throws NoSuchFieldRuntimeException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw new NoSuchFieldRuntimeException(clazz, fieldName, ex);
        }
    }

    /**
     * このクラスに定義された{@link Field フィールド}をクラスファイルに定義された順番で返します。
     * 
     * @param clazz
     *            対象のクラス
     * @return このクラスに定義されたフィールドの配列
     */
    public static Field[] getDeclaredFields(final Class clazz) {
        final ClassPool pool = ClassPoolUtil.getClassPool(clazz);
        final CtClass ctClass = ClassPoolUtil.toCtClass(pool, clazz);
        final CtField[] ctFields;
        synchronized (ctClass) {
            ctFields = ctClass.getDeclaredFields();
        }
        final int size = ctFields.length;
        final Field[] fields = new Field[size];
        for (int i = 0; i < size; ++i) {
            fields[i] = LdiClassUtil.getDeclaredField(clazz, ctFields[i].getName());
        }
        return fields;
    }

    /**
     * パッケージ名を返します。
     * 
     * @param clazz
     * @return パッケージ名
     */
    public static String getPackageName(Class clazz) {
        String fqcn = clazz.getName();
        int pos = fqcn.lastIndexOf('.');
        if (pos > 0) {
            return fqcn.substring(0, pos);
        }
        return null;
    }

    /**
     * FQCNからパッケージ名を除いた名前を返します。
     * 
     * @param clazz
     * @return FQCNからパッケージ名を除いた名前
     * @see #getShortClassName(String)
     */
    public static String getShortClassName(Class clazz) {
        return getShortClassName(clazz.getName());
    }

    /**
     * FQCNからパッケージ名を除いた名前を返します。
     * 
     * @param className
     * @return FQCNからパッケージ名を除いた名前
     */
    public static String getShortClassName(String className) {
        int i = className.lastIndexOf('.');
        if (i > 0) {
            return className.substring(i + 1);
        }
        return className;
    }

    /**
     * FQCNをパッケージ名とFQCNからパッケージ名を除いた名前に分けます。
     * 
     * @param className
     * @return パッケージ名とFQCNからパッケージ名を除いた名前
     */
    public static String[] splitPackageAndShortClassName(String className) {
        String[] ret = new String[2];
        int i = className.lastIndexOf('.');
        if (i > 0) {
            ret[0] = className.substring(0, i);
            ret[1] = className.substring(i + 1);
        } else {
            ret[1] = className;
        }
        return ret;
    }

    /**
     * 配列の場合は要素のクラス名、それ以外はクラス名そのものを返します。
     * 
     * @param clazz
     * @return クラス名
     */
    public static String getSimpleClassName(final Class clazz) {
        if (clazz.isArray()) {
            return getSimpleClassName(clazz.getComponentType()) + "[]";
        }
        return clazz.getName();
    }

    /**
     * クラス名をリソースパスとして表現します。
     * 
     * @param clazz
     * @return リソースパス
     * @see #getResourcePath(String)
     */
    public static String getResourcePath(Class clazz) {
        return getResourcePath(clazz.getName());
    }

    /**
     * クラス名をリソースパスとして表現します。
     * 
     * @param className
     * @return リソースパス
     */
    public static String getResourcePath(String className) {
        return LdiStringUtil.replace(className, ".", "/") + ".class";
    }

    /**
     * クラス名の要素を結合します。
     * 
     * @param s1
     * @param s2
     * @return 結合された名前
     */
    public static String concatName(String s1, String s2) {
        if (LdiStringUtil.isEmpty(s1) && LdiStringUtil.isEmpty(s2)) {
            return null;
        }
        if (!LdiStringUtil.isEmpty(s1) && LdiStringUtil.isEmpty(s2)) {
            return s1;
        }
        if (LdiStringUtil.isEmpty(s1) && !LdiStringUtil.isEmpty(s2)) {
            return s2;
        }
        return s1 + '.' + s2;
    }
}
