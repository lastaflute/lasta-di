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
package org.lastaflute.di.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.lastaflute.di.exception.IORuntimeException;

/**
 * {@link JarInputStream}用のユーティリティクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class LdiJarInputStreamUtil {

    /**
     * インスタンスを構築します。
     */
    protected LdiJarInputStreamUtil() {
    }

    /**
     * {@link JarInputStream}を作成します。
     * 
     * @param is
     * @return {@link JarInputStream}
     * @throws IORuntimeException
     *             {@link IOException}が発生した場合
     * @see JarInputStream#JarInputStream(InputStream)
     */
    public static JarInputStream create(final InputStream is) throws IORuntimeException {
        try {
            return new JarInputStream(is);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * {@link JarInputStream#getNextJarEntry()}の例外処理をラップするメソッドです。
     * 
     * @param is
     * @return {@link JarEntry}
     * @throws IORuntimeException
     *             {@link IOException}が発生した場合
     * @see JarInputStream#getNextJarEntry()
     */
    public static JarEntry getNextJarEntry(final JarInputStream is) throws IORuntimeException {
        try {
            return is.getNextJarEntry();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
