/*
 * Copyright 2015-2020 the original author or authors.
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

import javax.xml.parsers.SAXParser;

import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.di.exception.SAXRuntimeException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiSAXParserUtil {

    protected LdiSAXParserUtil() {
    }

    public static void parse(SAXParser parser, InputSource inputSource, DefaultHandler handler) {
        try {
            parser.parse(inputSource, handler);
        } catch (SAXException e) {
            throw new SAXRuntimeException(e);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static void setProperty(final SAXParser parser, final String name, final String value) {
        try {
            parser.setProperty(name, value);
        } catch (final SAXException e) {
            throw new SAXRuntimeException(e);
        }
    }

}
