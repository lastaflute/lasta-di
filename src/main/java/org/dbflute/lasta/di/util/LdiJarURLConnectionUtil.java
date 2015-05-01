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

import java.io.IOException;
import java.net.JarURLConnection;
import java.util.jar.JarFile;

import org.dbflute.lasta.di.exception.IORuntimeException;

/**
 * {@link JarURLConnection}用のユーティリティクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class LdiJarURLConnectionUtil {

    /**
     * インスタンスを構築します。
     */
    protected LdiJarURLConnectionUtil() {
    }

    /**
     * {@link JarURLConnection#getJarFile()}の例外処理をラップするメソッドです。
     * 
     * @param conn
     * @return {@link JarFile}
     * @throws IORuntimeException
     *             {@link IOException}が発生した場合
     */
    public static JarFile getJarFile(JarURLConnection conn) throws IORuntimeException {
        try {
            return conn.getJarFile();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
