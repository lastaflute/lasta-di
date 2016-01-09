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
package org.lastaflute.di.helper.xml;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.util.LdiResourceUtil;
import org.lastaflute.di.util.LdiURLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SaxHandler extends DefaultHandler {

    private TagHandlerRule tagHandlerRule;
    private TagHandlerContext context = new TagHandlerContext();
    private Map<String, String> dtdPaths = new HashMap<String, String>();
    private Locator locator;

    public SaxHandler(TagHandlerRule tagHandlerRule) {
        this.tagHandlerRule = tagHandlerRule;
    }

    public TagHandlerContext getTagHandlerContext() {
        return context;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void startDocument() throws SAXException {
        context.setLocator(locator);
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) {
        appendBody();
        context.startElement(qName);
        start(attributes);
    }

    @Override
    public void characters(char[] buffer, int start, int length) {
        int begin = start;
        int end = start + length;
        for (int i = begin; i < end; ++i) {
            if (buffer[i] == '\n') {
                context.characters(buffer, begin, i - begin + 1);
                appendBody();
                begin = i + 1;
            }
        }
        if (begin < end) {
            context.characters(buffer, begin, end - begin);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) {
        appendBody();
        end();
        context.endElement();
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        if (publicId != null) {
            String dtdPath = (String) dtdPaths.get(publicId);
            if (dtdPath != null) {
                return createInputSource(LdiResourceUtil.getResourceAsStream(dtdPath), systemId);
            }
        }

        if (systemId == null) {
            return null;
        }

        if (systemId.startsWith("file:")) {
            URL url = LdiURLUtil.create(systemId);
            String path = url.getFile();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return createInputSource(LdiResourceUtil.getResourceAsStream(path), systemId);
        }

        return new InputSource(LdiResourceUtil.getResourceAsStream(systemId));
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        System.err.println(e);
    }

    public void registerDtdPath(String publicId, String dtdPath) {
        dtdPaths.put(publicId, dtdPath);
    }

    public Object getResult() {
        return context.getResult();
    }

    private InputSource createInputSource(InputStream stream, String systemId) {
        InputSource is = new InputSource(stream);
        is.setSystemId(systemId);
        return is;
    }

    private TagHandler getTagHandlerByPath() {
        return tagHandlerRule.getTagHandler(context.getPath());
    }

    private TagHandler getTagHandlerByQName() {
        return tagHandlerRule.getTagHandler(context.getQName());
    }

    private void start(Attributes attributes) {
        TagHandler th = getTagHandlerByPath();
        start(th, attributes);
        th = getTagHandlerByQName();
        start(th, attributes);
    }

    private void start(TagHandler handler, Attributes attributes) {
        if (handler != null) {
            try {
                handler.start(context, attributes);
            } catch (RuntimeException ex) {
                reportDetailPath();
                ex.printStackTrace();
                throw ex;
            }

        }
    }

    private void appendBody() {
        String characters = context.getCharacters();
        if (characters.length() > 0) {
            TagHandler th = getTagHandlerByPath();
            appendBody(th, characters);
            th = getTagHandlerByQName();
            appendBody(th, characters);
            context.clearCharacters();
        }
    }

    private void appendBody(TagHandler handler, String characters) {
        if (handler != null) {
            try {
                handler.appendBody(context, characters);
            } catch (RuntimeException ex) {
                reportDetailPath();
                ex.printStackTrace();
                throw ex;
            }

        }
    }

    private void end() {
        String body = context.getBody();
        TagHandler th = getTagHandlerByPath();
        end(th, body);
        th = getTagHandlerByQName();
        end(th, body);
    }

    private void end(TagHandler handler, String body) {
        if (handler != null) {
            try {
                handler.end(context, body);
            } catch (RuntimeException ex) {
                reportDetailPath();
                ex.printStackTrace();
                throw ex;
            }

        }
    }

    private void reportDetailPath() {
        System.err.println("Exception occured at " + context.getDetailPath());
    }
}