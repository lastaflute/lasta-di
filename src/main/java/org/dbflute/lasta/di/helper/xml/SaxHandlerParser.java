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
package org.dbflute.lasta.di.helper.xml;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;

import org.dbflute.lasta.di.util.LdiResourceUtil;
import org.dbflute.lasta.di.util.LdiSAXParserFactoryUtil;
import org.dbflute.lasta.di.util.LdiSAXParserUtil;
import org.xml.sax.InputSource;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SaxHandlerParser {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final SaxHandler saxHandler;
    protected final SAXParser saxParser;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SaxHandlerParser(SaxHandler saxHandler) {
        this(saxHandler, LdiSAXParserFactoryUtil.newSAXParser());
    }

    public SaxHandlerParser(SaxHandler saxHandler, SAXParser saxParser) {
        this.saxHandler = saxHandler;
        this.saxParser = saxParser;
    }

    // ===================================================================================
    //                                                                               Parse
    //                                                                               =====
    public Object parse(String path) {
        return parse(LdiResourceUtil.getResourceAsStream(path), path);
    }

    public Object parse(InputStream inputStream) {
        return parse(new InputSource(inputStream));
    }

    public Object parse(InputStream inputStream, String path) {
        InputSource is = new InputSource(inputStream);
        is.setSystemId(path);
        return parse(is);
    }

    public Object parse(InputSource inputSource) {
        LdiSAXParserUtil.parse(saxParser, inputSource, saxHandler);
        return saxHandler.getResult();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public SaxHandler getSaxHandler() {
        return saxHandler;
    }

    public SAXParser getSAXParser() {
        return saxParser;
    }
}
