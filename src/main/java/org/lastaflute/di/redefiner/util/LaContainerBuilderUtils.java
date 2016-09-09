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
package org.lastaflute.di.redefiner.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.conbuilder.impl.AbstractLaContainerBuilder;
import org.lastaflute.di.core.meta.MetaDef;
import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.di.util.LdiResourceUtil;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class LaContainerBuilderUtils {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String PREFIX_JAR = "jar:";
    protected static final String SUFFIX_JAR = "!/";
    protected static final String PREFIX_FILE = "file:";
    protected static final String PREFIX_ZIP = "zip:";
    public static final String SUFFIX_ZIP = "!/";
    public static final String PREFIX_CODE_SOURCE = "code-source:";
    public static final String SUFFIX_CODE_SOURCE = "!/";
    protected static final char COLON = ':';

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected LaContainerBuilderUtils() {
    }

    // ===================================================================================
    //                                                                           Utilities
    //                                                                           =========
    public static boolean resourceExists(String path, AbstractLaContainerBuilder builder) {
        InputStream is;
        try {
            is = builder.getResourceResolver().getInputStream(path);
        } catch (IORuntimeException ex) {
            if (ex.getCause() instanceof FileNotFoundException) {
                return false;
            } else {
                throw ex;
            }
        }
        if (is == null) {
            return false;
        } else {
            try {
                is.close();
            } catch (IOException ignore) {}
            return true;
        }
    }

    public static void mergeContainer(LaContainer container, LaContainer merged) {
        int size = merged.getChildSize();
        for (int i = 0; i < size; i++) {
            container.include(merged.getChild(i));
        }

        size = merged.getMetaDefSize();
        for (int i = 0; i < size; i++) {
            MetaDef metaDef = merged.getMetaDef(i);
            metaDef.setContainer(container);
            container.addMetaDef(metaDef);
        }

        // cannot override, always add
        size = merged.getComponentDefSize();
        for (int i = 0; i < size; i++) {
            ComponentDef componentDef = merged.getComponentDef(i);
            componentDef.setContainer(container);
            container.register(componentDef);
        }
    }

    public static String fromURLToResourcePath(String path) {
        if (path == null) {
            return null;
        }
        if (path.startsWith(PREFIX_JAR)) {
            int idx = path.indexOf(SUFFIX_JAR);
            if (idx >= 0) {
                return path.substring(idx + SUFFIX_JAR.length());
            } else {
                return null;
            }
        } else if (path.startsWith(PREFIX_FILE)) {
            final String filePath;
            try {
                filePath = LdiResourceUtil.getFileName(new URL(encodeURL(path)));
            } catch (MalformedURLException ex) {
                return null;
            }
            final ClassLoader cl = getClassLoader();
            int pre = 0;
            int idx;
            while ((idx = filePath.indexOf('/', pre)) >= 0) {
                pre = idx + 1;
                String resourcePath = filePath.substring(pre);
                try {
                    if (cl.getResource(resourcePath) != null) {
                        return resourcePath;
                    }
                } catch (RuntimeException ignored) {
                    // ClassLoader#getResource() throws exceptions when resourcePath is invalid
                }
            }
            if (pre < filePath.length()) {
                String resourcePath = filePath.substring(pre);
                if (cl.getResource(resourcePath) != null) {
                    return resourcePath;
                }
            }
        } else if (path.startsWith(PREFIX_ZIP)) {
            final int idx = path.indexOf(SUFFIX_ZIP);
            if (idx >= 0) {
                return path.substring(idx + SUFFIX_ZIP.length());
            } else {
                return null;
            }
        } else if (path.startsWith(PREFIX_CODE_SOURCE)) {
            final int idx = path.indexOf(SUFFIX_CODE_SOURCE);
            if (idx >= 0) {
                return path.substring(idx + SUFFIX_CODE_SOURCE.length());
            } else {
                return null;
            }
        }
        return null;
    }

    public static URL[] getResourceURLs(String path) {
        return getResourceURLs(path, Thread.currentThread().getContextClassLoader());
    }

    public static URL[] getResourceURLs(String path, ClassLoader classLoader) {
        if (path.indexOf(COLON) >= 0) {
            try {
                return new URL[] { new URL(path) };
            } catch (MalformedURLException ignored) {}
        }
        final Enumeration<URL> enm;
        try {
            enm = classLoader.getResources(path);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
        final Set<URL> urlSet = new LinkedHashSet<URL>();
        for (; enm.hasMoreElements();) {
            urlSet.add(enm.nextElement());
        }
        return urlSet.toArray(new URL[0]);
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected static String encodeURL(String path) {
        if (path == null) {
            return null;
        }
        return path.replace("+", "%2B");
    }

    protected static ClassLoader getClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = LaContainerBuilderUtils.class.getClassLoader();
        }
        return cl;
    }
}
