/*
 * Copyright 2015-2024 the original author or authors.
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.lastaflute.di.exception.ParserConfigurationRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiDocumentBuilderFactoryUtil {

    protected LdiDocumentBuilderFactoryUtil() {
    }

    public static DocumentBuilderFactory newInstance() {
        return DocumentBuilderFactory.newInstance();
    }

    public static DocumentBuilder newDocumentBuilder() {
        try {
            return newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ParserConfigurationRuntimeException(e);
        }
    }
}
