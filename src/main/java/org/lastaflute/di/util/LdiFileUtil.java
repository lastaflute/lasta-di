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
package org.lastaflute.di.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.lastaflute.di.exception.IORuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiFileUtil {

    protected LdiFileUtil() {
    }

    /**
     * @param file
     * @return 
     */
    public static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * @param file
     * @return 
     */
    @SuppressWarnings("deprecation")
    public static URL toURL(final File file) {
        try {
            return file.toURL();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * @param file
     * @return 
     */
    public static byte[] getBytes(File file) {
        return LdiInputStreamUtil.getBytes(LdiFileInputStreamUtil.create(file));
    }

    /**
     * @param src
     * @param dest
     */
    public static void copy(File src, File dest) {
        if (dest.exists() && !dest.canWrite()) {
            return;
        }
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(LdiFileInputStreamUtil.create(src));
            out = new BufferedOutputStream(LdiFileOutputStreamUtil.create(dest));
            byte[] buf = new byte[1024];
            int length;
            while (-1 < (length = in.read(buf))) {
                out.write(buf, 0, length);
                out.flush();
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            LdiInputStreamUtil.close(in);
            LdiOutputStreamUtil.close(out);
        }
    }

    /**
     * @param path
     * @param data
     * @throws NullPointerException
     */
    public static void write(String path, byte[] data) {
        if (path == null) {
            throw new NullPointerException("path");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }
        write(path, data, 0, data.length);
    }

    /**
     * @param path
     * @param data
     * @param offset
     * @param length
     * @throws NullPointerException
     */
    public static void write(String path, byte[] data, int offset, int length) {
        if (path == null) {
            throw new NullPointerException("path");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
            try {
                out.write(data, offset, length);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
