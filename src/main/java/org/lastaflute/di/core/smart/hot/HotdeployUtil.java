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
package org.lastaflute.di.core.smart.hot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.di.core.meta.impl.LaContainerBehavior;
import org.lastaflute.di.util.LdiClassLoaderUtil;
import org.lastaflute.di.util.LdiClassUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class HotdeployUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String REBUILDER_CLASS_NAME = HotdeployUtil.class.getName() + "$RebuilderImpl";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected HotdeployUtil() {
    }

    // ===================================================================================
    //                                                                         Flg Control
    //                                                                         ===========
    // *anyone uses
    //public static void setHotdeploy(boolean hotdeploy) {
    //    HotdeployUtil.hotdeploy = Boolean.valueOf(hotdeploy);
    //}
    //
    //public static void clearHotdeploy() {
    //    hotdeploy = null;
    //}

    // ===================================================================================
    //                                                                  HotDeploy Resource
    //                                                                  ==================
    public static HotdeployBehavior getHotdeployBehavior() { // null allowed when non-HotDeploy
        if (isHotdeploy()) {
            return (HotdeployBehavior) LaContainerBehavior.getProvider();
        } else {
            return null;
        }
    }

    public static ClassLoader getLaContainerClassLoader() {
        return SingletonLaContainerFactory.getContainer().getClassLoader();
    }

    public static ClassLoader getThreadContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static void setThreadContextClassLoader(ClassLoader classLoader) {
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public static boolean isHotdeploy() {
        return LaContainerBehavior.getProvider() instanceof HotdeployBehavior;
    }

    public static boolean isLaContainerHotdeploy() {
        return isHotdeploy() && getHotdeployBehavior().isLaContainerHotdeploy();
    }

    public static boolean isThreadContextHotdeploy() {
        return isHotdeploy() && getHotdeployBehavior().isThreadContextHotdeploy();
    }

    // ===================================================================================
    //                                                                          Start/Stop
    //                                                                          ==========
    public static void start() { // if needs
        if (isHotdeploy()) {
            getHotdeployBehavior().start();
        }
    }

    public static void stop() { // if needs
        if (isHotdeploy()) {
            getHotdeployBehavior().stop();
        }
    }

    // *quit because of checked exception headache and also method return in middle process
    //public static void runAsHot(Runnable runnable) {
    //    synchronized (HotdeployLock.class) {
    //        try {
    //            start();
    //            runnable.run();
    //        } finally {
    //            stop();
    //        }
    //    }
    //}

    // ===================================================================================
    //                                                                         Deserialize
    //                                                                         ===========
    public static Object deserializeInternal(final byte[] bytes) throws Exception {
        if (bytes == null) {
            return null;
        }
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class<?> rebuilderClass = LdiClassLoaderUtil.loadClass(loader, REBUILDER_CLASS_NAME);
        final Rebuilder rebuilder = (Rebuilder) LdiClassUtil.newInstance(rebuilderClass);
        return rebuilder.deserialize(bytes);
    }

    // ===================================================================================
    //                                                                            Re-Build
    //                                                                            ========
    public static Object rebuildValue(Object value) {
        if (isHotdeploy()) {
            return rebuildValueInternal(value);
        }
        return value;
    }

    protected static Object rebuildValueInternal(Object value) {
        if (value == null) {
            return null;
        }
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class<?> rebuilderClass = LdiClassLoaderUtil.loadClass(loader, REBUILDER_CLASS_NAME);
        final Rebuilder rebuilder = (Rebuilder) LdiClassUtil.newInstance(rebuilderClass);
        return rebuilder.rebuild(value);
    }

    public interface Rebuilder {

        Object rebuild(Object value);

        Object deserialize(byte[] bytes) throws Exception;
    }

    public static class RebuilderImpl implements Rebuilder {

        public Object rebuild(Object value) {
            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                final ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(value);
                oos.close();

                final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                final ObjectInputStream ois = new ObjectInputStream(bais);
                return ois.readObject();
            } catch (final Throwable t) {
                return value;
            }
        }

        public Object deserialize(final byte[] bytes) throws Exception {
            final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            final ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }
    }
}
