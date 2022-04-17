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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.lastaflute.di.exception.IORuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiInputStreamUtil {

    /**
     * @param is
     * @throws IORuntimeException
     * @see InputStream#close()
     */
    public static void close(InputStream is) throws IORuntimeException {
        if (is == null) {
            return;
        }
        try {
            is.close();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * @param is
     * @throws IORuntimeException
     * @see InputStream#close()
     */
    public static void closeSilently(InputStream is) throws IORuntimeException {
        if (is == null) {
            return;
        }
        try {
            is.close();
        } catch (IOException e) {}
    }

    public static final byte[] getBytes(InputStream is) throws IORuntimeException {
        byte[] bytes = null;
        byte[] buf = new byte[8192];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int n = 0;
            while ((n = is.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, n);
            }
            bytes = baos.toByteArray();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (is != null) {
                close(is);
            }
        }
        return bytes;
    }

    public static final void copy(InputStream is, OutputStream os) throws IORuntimeException {
        byte[] buf = new byte[8192];
        try {
            int n = 0;
            while ((n = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, n);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static int available(InputStream is) throws IORuntimeException {
        try {
            return is.available();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * @param is
     * @throws IORuntimeException
     * @see InputStream#reset()
     */
    public static void reset(InputStream is) throws IORuntimeException {
        try {
            is.reset();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

}
