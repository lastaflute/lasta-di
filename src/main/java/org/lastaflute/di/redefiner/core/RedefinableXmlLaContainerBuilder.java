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
package org.lastaflute.di.redefiner.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.LaContainerFactory;
import org.lastaflute.di.core.factory.dixml.DiXmlLaContainerBuilder;
import org.lastaflute.di.helper.xml.SaxHandlerParser;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.lastaflute.di.redefiner.util.LaContainerBuilderUtils;

/**
 * used in DefaultProvider#getBuilder()
 * @author modified by jflute (originated in Ymir)
 */
public class RedefinableXmlLaContainerBuilder extends DiXmlLaContainerBuilder { // for e.g. ++jta.xml, ++jta.xml

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    // e.g.
    //  ++jta.xml => [NAME_ADDITIONAL][DELIMITER]jta.xml
    //  jta++.xml => jta.xml[DELIMITER][NAME_ADDITIONAL]
    public static final String DELIMITER = "+";
    protected static final String NAME_ADDITIONAL = "+";

    protected static final String PARAMETER_BUILDER = "builder";
    protected static final String PARAMETER_BASEPATH = "originalPath";

    protected static final ThreadLocal<LinkedList<String>> basePathStack_ = new ThreadLocal<LinkedList<String>>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public RedefinableXmlLaContainerBuilder() {
        rule.addTagHandler("/components", new RedefinableComponentsTagHandler());
        rule.addTagHandler("component", new RedefinableComponentTagHandler());
    }

    // ===================================================================================
    //                                                                     Parser Override
    //                                                                     ===============
    @Override
    protected SaxHandlerParser createSaxHandlerParser(LaContainer parent, String path) {
        final SaxHandlerParser parser = super.createSaxHandlerParser(parent, path);
        final TagHandlerContext context = parser.getSaxHandler().getTagHandlerContext();
        context.addParameter(PARAMETER_BUILDER, this);
        context.addParameter(PARAMETER_BASEPATH, getCurrentBasePath());
        return parser;
    }

    @Override
    protected LaContainer parse(LaContainer parent, String path) {
        pushPath(path);
        try {
            final LaContainer container = super.parse(parent, path); // contains e.g. jta+.xml, jta+userTransaction.xml handling
            mergeContainers(container, path, true); // fixedly tail e.g. jta++.xml
            return container;
        } finally {
            popPath(path);
        }
    }

    // ===================================================================================
    //                                                                          Path Stack
    //                                                                          ==========
    protected void popPath(String path) {
        if (path.equals(getCurrentBasePath())) {
            final LinkedList<String> pathStack = basePathStack_.get();
            pathStack.removeFirst();
            if (pathStack.isEmpty()) {
                basePathStack_.set(null);
            }
        }
    }

    protected String getCurrentBasePath() {
        final LinkedList<String> pathStack = basePathStack_.get();
        if (pathStack == null || pathStack.isEmpty()) {
            return null;
        }
        return pathStack.peek();
    }

    protected void pushPath(String path) {
        LinkedList<String> pathStack = basePathStack_.get();
        if (pathStack == null) {
            pathStack = new LinkedList<String>();
            basePathStack_.set(pathStack);
        }
        if (isBasePath(path)) {
            pathStack.addFirst(path);
        }
    }

    protected boolean isBasePath(String path) {
        if (path == null) {
            return true;
        }
        return path.indexOf(DELIMITER + NAME_ADDITIONAL) < 0 && path.indexOf(NAME_ADDITIONAL + DELIMITER) < 0;
    }

    // ===================================================================================
    //                                                                     Merge Container
    //                                                                     ===============
    protected void mergeContainers(LaContainer container, String path, boolean addToTail) {
        final Set<URL> additionalURLSet = gatherAdditionalDiconURLs(path, addToTail);
        for (Iterator<URL> itr = additionalURLSet.iterator(); itr.hasNext();) {
            final String url = itr.next().toExternalForm();
            if (LaContainerBuilderUtils.resourceExists(url, this)) {
                LaContainerBuilderUtils.mergeContainer(container, LaContainerFactory.create(url));
            }
        }
    }

    protected Set<URL> gatherAdditionalDiconURLs(String path, boolean addToTail) {
        final String[] additionalDiconPaths = constructAdditionalDiconPaths(path, addToTail);
        final Set<URL> urlSet = new LinkedHashSet<URL>();
        for (int i = 0; i < additionalDiconPaths.length; i++) {
            final URL[] urls = LaContainerBuilderUtils.getResourceURLs(additionalDiconPaths[i]);
            for (int j = 0; j < urls.length; j++) {
                urlSet.add(urls[j]);
            }
        }
        return urlSet;
    }

    protected String[] constructAdditionalDiconPaths(String path, boolean addToTail) { // e.g. ++jta.xml, jta++.xml
        final int delimiter = path.lastIndexOf(DELIMITER);
        final int slash = path.lastIndexOf('/');
        if (delimiter >= 0 && delimiter > slash) {
            return new String[0];
        }

        final List<String> pathList = new ArrayList<String>();

        final String dir;
        String name;
        if (slash < 0) {
            dir = "";
            name = path;
        } else {
            dir = path.substring(0, slash + 1);
            name = path.substring(slash + 1);
        }
        final String suffix;
        int dot = name.lastIndexOf('.');
        if (dot < 0) {
            suffix = "";
        } else {
            suffix = name.substring(dot);
            name = name.substring(0, dot);
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(dir);
        if (!addToTail) {
            sb.append(NAME_ADDITIONAL).append(DELIMITER);
        }
        sb.append(name);
        if (addToTail) { // fixedly here e.g. jta++.xml
            sb.append(DELIMITER).append(NAME_ADDITIONAL);
        }
        sb.append(suffix);
        final String additionalPath = sb.toString();
        final String additionalResourcePath = LaContainerBuilderUtils.fromURLToResourcePath(additionalPath);
        if (additionalResourcePath != null) {
            pathList.add(additionalResourcePath);
        }
        pathList.add(additionalPath);

        return pathList.toArray(new String[0]);
    }
}
