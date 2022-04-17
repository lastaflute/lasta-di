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
package org.lastaflute.di.redefiner.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.core.factory.resresolver.impl.ClassPathResourceResolver;
import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.di.redefiner.util.LaContainerBuilderUtils;
import org.lastaflute.di.util.LdiResourceUtil;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class RedefinableResourceResolver extends ClassPathResourceResolver { // for e.g. jta+.xml

    private static final char COLON = ':';

    public InputStream getInputStream(String path) {
        final String[] paths = constructRedefinedDiconPaths(path);
        for (int i = 0; i < paths.length; i++) {
            try {
                InputStream is = super.getInputStream(paths[i]);
                if (is != null) {
                    // #hope logging but difficult for beautiful style by jflute (2016/09/05)
                    return is;
                }
            } catch (IORuntimeException ignored) {
                // to return null
            }
        }
        return super.getInputStream(path);
    }

    @Override
    protected URL getURL(String path) {
        return toURL_corrected(path);
    }

    URL toURL_corrected(final String path) {
        if (path.indexOf(COLON) >= 0) {
            try {
                URL url = new URL(path);
                InputStream is = null;
                try {
                    is = url.openStream();
                    return url;
                } catch (IOException ex) {
                    return null;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ignore) {}
                    }
                }
            } catch (MalformedURLException ignore) {}
        }
        return LdiResourceUtil.getResourceNoException(path);
    }

    protected String[] constructRedefinedDiconPaths(String path) {
        final int delimiter = path.lastIndexOf(RedefinableXmlLaContainerBuilder.DELIMITER);
        final int slash = path.lastIndexOf('/');
        if (delimiter >= 0 && delimiter > slash) {
            return new String[0]; // no handling if the resource name contains '+'
        }
        final List<String> pathList = new ArrayList<String>();
        final String body;
        final String suffix;
        final int dot = path.lastIndexOf('.');
        if (dot < 0) {
            body = path;
            suffix = "";
        } else {
            body = path.substring(0, dot);
            suffix = path.substring(dot);
        }
        final String resourceBody =
                LaContainerBuilderUtils.fromURLToResourcePath(body + RedefinableXmlLaContainerBuilder.DELIMITER + suffix);
        if (resourceBody != null) {
            pathList.add(resourceBody);
        }
        pathList.add(body + RedefinableXmlLaContainerBuilder.DELIMITER + suffix); // e.g. jta+.xml
        return pathList.toArray(new String[0]);
    }
}
