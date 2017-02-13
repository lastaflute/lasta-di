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
package org.lastaflute.di.core.util;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.lastaflute.di.Disposable;
import org.lastaflute.di.DisposableUtil;
import org.lastaflute.di.exception.NotFoundRuntimeException;
import org.lastaflute.di.util.LdiClassLoaderUtil;
import org.lastaflute.di.util.LdiClassUtil;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ClassPoolUtil {

    protected static final Map<ClassLoader, ClassPool> classPoolMap =
            Collections.synchronizedMap(new WeakHashMap<ClassLoader, ClassPool>());

    protected static boolean initialized;

    public static synchronized void initialize() {
        if (!initialized) {
            DisposableUtil.add(new Disposable() {
                public void dispose() {
                    synchronized (ClassPoolUtil.class) {
                        classPoolMap.clear();
                        initialized = false;
                    }
                }
            });
            initialized = true;
        }
    }

    public static ClassPool getClassPool(final Class<?> targetClass) {
        return getClassPool(LdiClassLoaderUtil.getClassLoader(targetClass));
    }

    public static ClassPool getClassPool(final ClassLoader classLoader) {
        initialize();
        ClassPool classPool = (ClassPool) classPoolMap.get(classLoader);
        if (classPool == null) {
            if (classLoader == null) {
                return ClassPool.getDefault();
            }
            classPool = new ClassPool();
            classPool.appendClassPath(new LoaderClassPath(classLoader));
            classPoolMap.put(classLoader, classPool);
        }
        return classPool;
    }

    public static CtClass toCtClass(final ClassPool classPool, final Class<?> clazz) {
        return toCtClass(classPool, LdiClassUtil.getSimpleClassName(clazz));
    }

    public static CtClass toCtClass(final ClassPool classPool, final String className) {
        try {
            return classPool.get(className);
        } catch (final NotFoundException e) {
            throw new NotFoundRuntimeException(e);
        }
    }

    public static CtClass[] toCtClassArray(final ClassPool classPool, final String[] classNames) {
        if (classNames == null) {
            return null;
        }
        final CtClass[] result = new CtClass[classNames.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = toCtClass(classPool, classNames[i]);
        }
        return result;
    }

    public static CtClass[] toCtClassArray(final ClassPool classPool, final Class<?>[] classes) {
        if (classes == null) {
            return null;
        }
        final CtClass[] result = new CtClass[classes.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = toCtClass(classPool, classes[i]);
        }
        return result;
    }

    public static CtClass createCtClass(final ClassPool classPool, final String name) {
        return createCtClass(classPool, name, Object.class);
    }

    public static CtClass createCtClass(final ClassPool classPool, final String name, final Class<?> superClass) {
        return createCtClass(classPool, name, toCtClass(classPool, superClass));
    }

    public static CtClass createCtClass(final ClassPool classPool, final String name, final CtClass superClass) {
        return classPool.makeClass(name, superClass);
    }
}