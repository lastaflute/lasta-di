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

import javax.xml.parsers.DocumentBuilder;

import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.di.exception.SAXRuntimeException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * {@link DocumentBuilder}用のユーティリティクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class LdiDocumentBuilderUtil {

    /**
     * インスタンスを構築します。
     */
    protected LdiDocumentBuilderUtil() {
    }

    /**
     * XMLを解析します。
     * 
     * @param builder
     * @param is
     * @return {@link Document}
     */
    public static Document parse(DocumentBuilder builder, InputStream is) {
        try {
            return builder.parse(is);
        } catch (SAXException e) {
            throw new SAXRuntimeException(e);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}