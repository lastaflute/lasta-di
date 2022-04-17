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
package org.lastaflute.di.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import org.lastaflute.di.exception.ClassNotFoundRuntimeException;
import org.lastaflute.di.exception.IORuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class LdiClassLoaderUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Method findLoadedClassMethod = getFindLoadedClassMethod();
    private static final Method defineClassMethod = getDefineClassMethod();
    private static final Method definePackageMethod = getDefinePackageMethod();

    private static Method getFindLoadedClassMethod() {
        final Method method = LdiClassUtil.getDeclaredMethod(ClassLoader.class, "findLoadedClass", new Class[] { String.class });
        // lazy to avoid Java11 warning in cool deploy
        //method.setAccessible(true);
        return method;
    }

    private static Method getDefineClassMethod() {
        final Method method = LdiClassUtil.getDeclaredMethod(ClassLoader.class, "defineClass",
                new Class[] { String.class, byte[].class, int.class, int.class });
        // lazy to avoid Java11 warning in cool deploy
        //method.setAccessible(true);
        return method;
    }

    private static Method getDefinePackageMethod() {
        final Method method = LdiClassUtil.getDeclaredMethod(ClassLoader.class, "definePackage", new Class[] { String.class, String.class,
                String.class, String.class, String.class, String.class, String.class, URL.class });
        // lazy to avoid Java11 warning in cool deploy
        //method.setAccessible(true);
        return method;
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected LdiClassLoaderUtil() {
    }

    // ===================================================================================
    //                                                                       Loader Search
    //                                                                       =============
    public static ClassLoader getClassLoader(final Class<?> targetClass) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader;
        }

        final ClassLoader targetClassLoader = targetClass.getClassLoader();
        final ClassLoader thisClassLoader = LdiClassLoaderUtil.class.getClassLoader();
        if (targetClassLoader != null && thisClassLoader != null) {
            if (isAncestor(thisClassLoader, targetClassLoader)) {
                return thisClassLoader;
            }
            return targetClassLoader;
        }
        if (targetClassLoader != null) {
            return targetClassLoader;
        }
        if (thisClassLoader != null) {
            return thisClassLoader;
        }

        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        if (systemClassLoader != null) {
            return systemClassLoader;
        }

        throw new IllegalStateException("Not found the class loader: " + targetClass);
    }

    // ===================================================================================
    //                                                                     Resource Search
    //                                                                     ===============
    public static Iterator<URL> getResources(final String name) {
        return getResources(Thread.currentThread().getContextClassLoader(), name);
    }

    public static Iterator<URL> getResources(final Class<?> targetClass, final String name) {
        return getResources(getClassLoader(targetClass), name);
    }

    public static Iterator<URL> getResources(final ClassLoader loader, final String name) {
        try {
            final Enumeration<URL> e = loader.getResources(name);
            return new EnumerationIterator<URL>(e);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    protected static boolean isAncestor(ClassLoader cl, final ClassLoader other) {
        while (cl != null) {
            if (cl == other) {
                return true;
            }
            cl = cl.getParent();
        }
        return false;
    }

    // ===================================================================================
    //                                                                       Loader Deeply
    //                                                                       =============
    // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
    // #for_now jflute illegal-access cannot be allowed in Java17, so deprecate them here (2021/07/30)
    // _/_/_/_/_/_/_/_/_/_/
    @Deprecated // unused in Lasta Di since 0.8.4
    public static Class<?> findLoadedClass(final ClassLoader classLoader, final String className) {
        final Method targetMethod = findLoadedClassMethod;
        adjustAccessibleIfNeeds(targetMethod);
        for (ClassLoader loader = classLoader; loader != null; loader = loader.getParent()) {
            final Class<?> clazz = (Class<?>) LdiMethodUtil.invoke(targetMethod, loader, new Object[] { className });
            if (clazz != null) {
                return clazz;
            }
        }
        return null;
    }

    @Deprecated // unused in Lasta Di originally
    public static Class<?> defineClass(final ClassLoader classLoader, final String className, final byte[] bytes, final int offset,
            final int length) {
        final Method targetMethod = defineClassMethod;
        adjustAccessibleIfNeeds(targetMethod);
        return (Class<?>) LdiMethodUtil.invoke(targetMethod, classLoader,
                new Object[] { className, bytes, new Integer(offset), new Integer(length) });
    }

    @Deprecated // unused in Lasta Di originally
    public static Package definePackage(final ClassLoader classLoader, final String name, final String specTitle, final String specVersion,
            final String specVendor, final String implTitle, final String implVersion, final String implVendor, final URL sealBase) {
        final Method targetMethod = definePackageMethod;
        adjustAccessibleIfNeeds(targetMethod);
        return (Package) LdiMethodUtil.invoke(targetMethod, classLoader,
                new Object[] { name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase });
    }

    private static void adjustAccessibleIfNeeds(Method targetMethod) {
        if (!targetMethod.isAccessible()) {
            synchronized (targetMethod) {
                if (!targetMethod.isAccessible()) {
                    targetMethod.setAccessible(true);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                     Simple Delegate
    //                                                                     ===============
    public static Class<?> loadClass(final ClassLoader loader, final String className) {
        try {
            return loader.loadClass(className);
        } catch (final ClassNotFoundException e) {
            throw new ClassNotFoundRuntimeException(e);
        }
    }
}
