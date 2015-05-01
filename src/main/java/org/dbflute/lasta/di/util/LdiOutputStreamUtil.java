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
import java.io.OutputStream;

import org.dbflute.lasta.di.exception.IORuntimeException;

/**
 * {@link OutputStream}用のユーティリティクラスです。
 * 
 * @author shot
 */
public class LdiOutputStreamUtil {

    /**
     * {@link OutputStream}を閉じます。
     * 
     * @param out
     * @throws IORuntimeException
     *             {@link IOException}が発生した場合
     */
    public static void close(OutputStream out) throws IORuntimeException {
        if (out == null) {
            return;
        }
        try {
            out.close();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * {@link OutputStream}をflushします。
     * 
     * @param out
     */
    public static void flush(OutputStream out) {
        if (out == null) {
            return;
        }
        try {
            out.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
