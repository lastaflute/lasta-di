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
import java.io.InputStream;
import java.io.Reader;

/**
 * テキスト用のユーティリティクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class LdiTextUtil {

    private static final String UTF8 = "UTF-8";

    /**
     * インスタンスを構築します。
     */
    protected LdiTextUtil() {
    }

    /**
     * テキストを読み込みます。
     * 
     * @param path
     *            パス
     * @return 読み込んだテキスト
     */
    public static String readText(String path) {
        InputStream is = LdiResourceUtil.getResourceAsStream(path);
        Reader reader = LdiInputStreamReaderUtil.create(is);
        return LdiReaderUtil.readText(reader);
    }

    /**
     * テキストを読み込みます。
     * 
     * @param file
     *            ファイル
     * @return 読み込んだテキスト
     */
    public static String readText(File file) {
        InputStream is = LdiFileInputStreamUtil.create(file);
        Reader reader = LdiInputStreamReaderUtil.create(is);
        return LdiReaderUtil.readText(reader);
    }

    /**
     * UTF8でテキストを読み込みます。
     * 
     * @param path
     *            パス
     * @return 読み込んだテキスト
     */
    public static String readUTF8(String path) {
        InputStream is = LdiResourceUtil.getResourceAsStream(path);
        Reader reader = LdiInputStreamReaderUtil.create(is, UTF8);
        return LdiReaderUtil.readText(reader);
    }

    /**
     * UTF8でテキストを読み込みます。
     * 
     * @param file
     *            ファイル
     * @return 読み込んだテキスト
     */
    public static String readUTF8(File file) {
        InputStream is = LdiFileInputStreamUtil.create(file);
        Reader reader = LdiInputStreamReaderUtil.create(is, UTF8);
        return LdiReaderUtil.readText(reader);
    }
}
