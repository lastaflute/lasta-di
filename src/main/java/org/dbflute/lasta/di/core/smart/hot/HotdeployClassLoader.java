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
package org.dbflute.lasta.di.core.smart.hot;

import java.io.InputStream;

import org.dbflute.lasta.di.helper.log.SLogger;
import org.dbflute.lasta.di.naming.NamingConvention;
import org.dbflute.lasta.di.util.LdiClassLoaderUtil;
import org.dbflute.lasta.di.util.LdiClassUtil;
import org.dbflute.lasta.di.util.LdiInputStreamUtil;
import org.dbflute.lasta.di.util.LdiResourceUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class HotdeployClassLoader extends ClassLoader {

    private static final SLogger logger = SLogger.getLogger(HotdeployClassLoader.class);

    protected final NamingConvention namingConvention;

    public HotdeployClassLoader(ClassLoader classLoader, NamingConvention namingConvention) {
        super(classLoader);
        this.namingConvention = namingConvention;
    }

    @Override
    public Class loadClass(String className, boolean resolve) throws ClassNotFoundException {
        if (HotdeployUtil.REBUILDER_CLASS_NAME.equals(className)) {
            final Class clazz = findLoadedClass(className);
            if (clazz != null) {
                return clazz;
            }
            return defineClass(className, resolve);
        }
        if (isTargetClass(className)) {
            Class clazz = findLoadedClass(className);
            if (clazz != null) {
                return clazz;
            }
            clazz = LdiClassLoaderUtil.findLoadedClass(getParent(), className);
            if (clazz != null) {
                logger.log("WSSR0015", new Object[] { className });
                return clazz;
            }
            clazz = defineClass(className, resolve);
            if (clazz != null) {
                return clazz;
            }
        }
        return super.loadClass(className, resolve);
    }

    protected Class defineClass(String className, boolean resolve) {
        final String path = LdiClassUtil.getResourcePath(className);
        final InputStream is = LdiResourceUtil.getResourceAsStreamNoException(path);
        if (is != null) {
            final Class clazz = defineClass(className, is);
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
        return null;
    }

    protected Class defineClass(String className, InputStream classFile) {
        return defineClass(className, LdiInputStreamUtil.getBytes(classFile));
    }

    protected Class defineClass(String className, byte[] bytes) {
        return defineClass(className, bytes, 0, bytes.length);
    }

    protected boolean isTargetClass(String className) {
        return namingConvention.isHotdeployTargetClassName(className);
    }
}