/*
 * Copyright 2015-2021 the original author or authors.
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
package org.lastaflute.di.core.factory.dixml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.conbuilder.impl.AbstractLaContainerBuilder;
import org.lastaflute.di.core.factory.dixml.exception.DiXmlParseFailureException;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;
import org.lastaflute.di.helper.xml.SaxHandler;
import org.lastaflute.di.helper.xml.SaxHandlerParser;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.lastaflute.di.util.LdiInputStreamUtil;
import org.lastaflute.di.util.LdiSAXParserFactoryUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class DiXmlLaContainerBuilder extends AbstractLaContainerBuilder {

    public static final String PUBLIC_ID10 = "-//DBFLUTE//DTD LastaDi 1.0//EN";
    public static final String DTD_PATH10 = "org/lastaflute/di/lastadi10.dtd";

    protected DiXmlTagHandlerRule rule = new DiXmlTagHandlerRule();
    protected final Map<String, String> dtdMap = new HashMap<String, String>();

    public DiXmlLaContainerBuilder() {
        dtdMap.put(PUBLIC_ID10, DTD_PATH10);
    }

    public void addDtd(final String publicId, final String systemId) {
        dtdMap.put(publicId, systemId);
    }

    public void clearDtd() {
        dtdMap.clear();
    }

    public LaContainer build(String path) {
        return parse(null, path);
    }

    public LaContainer include(LaContainer parent, final String path) {
        final LaContainer child = parse(parent, path);
        parent.include(child);
        return child;
    }

    protected LaContainer parse(LaContainer parent, String path) {
        final SaxHandlerParser parser = createSaxHandlerParser(parent, path);
        final InputStream is = findDiXmlInputStream(parent, path);
        try {
            return (LaContainer) parser.parse(is, path);
        } catch (Throwable cause) { // contains e.g. NoSuchMethodError
            if (cause instanceof DiXmlParseFailureException) {
                throw (DiXmlParseFailureException) cause;
            }
            throwDependencyXmlParseFailureException(parent, path, cause);
            return null; // unreachable
        } finally {
            LdiInputStreamUtil.close(is);
        }
    }

    protected void throwDependencyXmlParseFailureException(LaContainer parent, String path, Throwable cause) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to parse the dependency XML.");
        br.addItem("Dependency XML");
        br.addElement(path + (parent != null ? " included by " + parent.getPath() : ""));
        final String msg = br.buildExceptionMessage();
        throw new DiXmlParseFailureException(msg, cause);
    }

    protected SaxHandlerParser createSaxHandlerParser(final LaContainer parent, final String path) {
        final SAXParserFactory factory = LdiSAXParserFactoryUtil.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        // to avoid warning of JDK-internal access at Java11 by jflute (2019/04/21)
        // Lasta Di does not need xinclude because of Di xml redefiner
        //LdiSAXParserFactoryUtil.setXIncludeAware(factory, true);

        final SAXParser saxParser = LdiSAXParserFactoryUtil.newSAXParser(factory);

        final SaxHandler handler = new SaxHandler(rule);
        for (final Iterator<Entry<String, String>> it = dtdMap.entrySet().iterator(); it.hasNext();) {
            final Entry<String, String> entry = (Entry<String, String>) it.next();
            final String publicId = entry.getKey();
            final String systemId = entry.getValue();
            handler.registerDtdPath(publicId, systemId);
        }

        final TagHandlerContext ctx = handler.getTagHandlerContext();
        ctx.addParameter("parent", parent);
        ctx.addParameter("path", path);

        return new SaxHandlerParser(handler, saxParser);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DiXmlTagHandlerRule getRule() {
        return rule;
    }

    public void setRule(final DiXmlTagHandlerRule rule) {
        this.rule = rule;
    }
}
