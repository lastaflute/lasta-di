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
package org.lastaflute.di.core.smart.hot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.lastaflute.di.core.meta.impl.LaContainerBehavior;
import org.lastaflute.di.core.meta.impl.LaContainerBehavior.Provider;
import org.lastaflute.di.util.LdiClassLoaderUtil;
import org.lastaflute.di.util.LdiClassUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class HotdeployUtil {

    public static final String REBUILDER_CLASS_NAME = HotdeployUtil.class.getName() + "$RebuilderImpl";

    private static Boolean hotdeploy;

    protected HotdeployUtil() {
    }

    public static void setHotdeploy(boolean hotdeploy) {
        HotdeployUtil.hotdeploy = Boolean.valueOf(hotdeploy);
    }

    public static void clearHotdeploy() {
        hotdeploy = null;
    }

    public static boolean isHotdeploy() {
        if (hotdeploy != null) {
            return hotdeploy.booleanValue();
        }
        Provider provider = LaContainerBehavior.getProvider();
        return provider instanceof HotdeployBehavior;
    }

    public static void start() {
        if (isHotdeploy()) {
            ((HotdeployBehavior) LaContainerBehavior.getProvider()).start();
        }
    }

    public static void stop() {
        if (isHotdeploy()) {
            ((HotdeployBehavior) LaContainerBehavior.getProvider()).stop();
        }
    }

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

    public static Object deserializeInternal(final byte[] bytes) throws Exception {
        if (bytes == null) {
            return null;
        }
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class<?> rebuilderClass = LdiClassLoaderUtil.loadClass(loader, REBUILDER_CLASS_NAME);
        final Rebuilder rebuilder = (Rebuilder) LdiClassUtil.newInstance(rebuilderClass);
        return rebuilder.deserialize(bytes);
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
