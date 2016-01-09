/*
 * Copyright 2015-2016 the original author or authors.
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
 * @author modified by jflute (originated in Seasar)
 */
public class LdiZipFileUtil {

    protected LdiZipFileUtil() {
    }

    /**
     * @param file
     * @return 
     * @throws IORuntimeException
     */
    public static ZipFile create(final String file) {
        try {
            return new ZipFile(file);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * @param file
     * @return 
     * @throws IORuntimeException
     */
    public static ZipFile create(final File file) {
        try {
            return new ZipFile(file);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * @param file
     * @param entry
     * @return 
     * @throws IORuntimeException
     */
    public static InputStream getInputStream(final ZipFile file, final ZipEntry entry) {
        try {
            return file.getInputStream(entry);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * @param zipUrl
     * @return 
     * @throws IORuntimeException
     */
    public static ZipFile toZipFile(final URL zipUrl) {
        return create(new File(toZipFilePath(zipUrl)));
    }

    /**
     * @param zipUrl
     * @return 
     * @throws IORuntimeException
     */
    public static String toZipFilePath(final URL zipUrl) {
        final String urlString = zipUrl.getPath();
        final int pos = urlString.lastIndexOf('!');
        final String zipFilePath = urlString.substring(0, pos);
        final File zipFile = new File(LdiURLUtil.decode(zipFilePath, "UTF8"));
        return LdiFileUtil.getCanonicalPath(zipFile);
    }

    /**
     * @param zipFile
     * @throws IORuntimeException
     */
    public static void close(final ZipFile zipFile) {
        try {
            zipFile.close();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

}
