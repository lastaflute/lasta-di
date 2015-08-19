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
import java.io.InputStreamReader;

import org.lastaflute.di.exception.IORuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiInputStreamReaderUtil {

    protected LdiInputStreamReaderUtil() {
    }

    /**
     * @param is
     * @return {@link InputStreamReader}
     * @see #create(InputStream, String)
     */
    public static InputStreamReader create(InputStream is) {
        return create(is, "JISAutoDetect");
    }

    /**
     * @param is
     * @param encoding
     * @return {@link InputStreamReader}
     * @throws IORuntimeException
     * @see InputStreamReader#InputStreamReader(InputStream, String)
     */
    public static InputStreamReader create(InputStream is, String encoding) throws IORuntimeException {
        try {
            return new InputStreamReader(is, encoding);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
