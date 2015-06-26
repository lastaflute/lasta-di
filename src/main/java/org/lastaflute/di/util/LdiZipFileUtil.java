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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.lastaflute.di.exception.IORuntimeException;

/**
 * {@link java.util.zip.ZipFile}を扱うユーティリティクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class LdiZipFileUtil {

    /**
     * インスタンスを構築します。
     */
    protected LdiZipFileUtil() {
    }

    /**
     * 指定されたZipファイルを読み取るための<code>ZipFile</code>を作成して返します。
     * 
     * @param file
     *            ファイルパス
     * @return 指定されたZipファイルを読み取るための<code>ZipFile</code>
     * @throws IORuntimeException
     *             入出力エラーが発生した場合にスローされます
     */
    public static ZipFile create(final String file) {
        try {
            return new ZipFile(file);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 指定されたZipファイルを読み取るための<code>ZipFile</code>を作成して返します。
     * 
     * @param file
     *            ファイル
     * @return 指定されたZipファイルを読み取るための<code>ZipFile</code>
     * @throws IORuntimeException
     *             入出力エラーが発生した場合にスローされます
     */
    public static ZipFile create(final File file) {
        try {
            return new ZipFile(file);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 指定されたZipファイルエントリの内容を読み込むための入力ストリームを返します。
     * 
     * @param file
     *            Zipファイル
     * @param entry
     *            Zipファイルエントリ
     * @return 指定されたZipファイルエントリの内容を読み込むための入力ストリーム
     * @throws IORuntimeException
     *             入出力エラーが発生した場合にスローされます
     */
    public static InputStream getInputStream(final ZipFile file, final ZipEntry entry) {
        try {
            return file.getInputStream(entry);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * URLで指定されたZipファイルを読み取るための<code>ZipFile</code>を作成して返します。
     * 
     * @param zipUrl
     *            Zipファイルを示すURL
     * @return 指定されたZipファイルを読み取るための<code>ZipFile</code>
     * @throws IORuntimeException
     *             入出力エラーが発生した場合にスローされます
     */
    public static ZipFile toZipFile(final URL zipUrl) {
        return create(new File(toZipFilePath(zipUrl)));
    }

    /**
     * URLで指定されたZipファイルのパスを返します。
     * 
     * @param zipUrl
     *            Zipファイルを示すURL
     * @return URLで指定されたZipファイルのパス
     * @throws IORuntimeException
     *             入出力エラーが発生した場合にスローされます
     */
    public static String toZipFilePath(final URL zipUrl) {
        final String urlString = zipUrl.getPath();
        final int pos = urlString.lastIndexOf('!');
        final String zipFilePath = urlString.substring(0, pos);
        final File zipFile = new File(LdiURLUtil.decode(zipFilePath, "UTF8"));
        return LdiFileUtil.getCanonicalPath(zipFile);
    }

    /**
     * Zipファイルをクローズします。
     * 
     * @param zipFile
     *            Zipファイル
     * @throws IORuntimeException
     *             入出力エラーが発生した場合にスローされます
     */
    public static void close(final ZipFile zipFile) {
        try {
            zipFile.close();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

}
