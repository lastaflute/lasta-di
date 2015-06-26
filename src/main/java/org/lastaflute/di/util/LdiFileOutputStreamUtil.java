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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.lastaflute.di.exception.IORuntimeException;

/**
 * {@link FileOutputStream}用のユーティリティクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class LdiFileOutputStreamUtil {

    /**
     * インスタンスを構築します。
     */
    protected LdiFileOutputStreamUtil() {
    }

    /**
     * {@link FileOutputStream}を作成します。
     * 
     * @param file
     * @return {@link FileOutputStream}
     * @throws IORuntimeException
     *             {@link IOException}が発生した場合
     * @see FileOutputStream#FileOutputStream(File)
     */
    public static FileOutputStream create(File file) throws IORuntimeException {
        try {
            return new FileOutputStream(file);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
