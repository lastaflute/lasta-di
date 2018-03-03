/*
 * Copyright 2015-2018 the original author or authors.
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
package org.lastaflute.di.core.factory.conbuilder.impl;

import java.io.InputStream;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.conbuilder.LaContainerBuilder;
import org.lastaflute.di.core.factory.resresolver.ResourceResolver;
import org.lastaflute.di.core.factory.resresolver.impl.ClassPathResourceResolver;
import org.lastaflute.di.exception.DiXmlNotFoundException;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractLaContainerBuilder implements LaContainerBuilder {

    public static final String resourceResolver_BINDING = "bindingType=may";
    protected ResourceResolver resourceResolver = new ClassPathResourceResolver(); // as default

    public LaContainer build(String path, ClassLoader classLoader) {
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(classLoader);
            }
            return build(path);
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    protected InputStream findDiXmlInputStream(LaContainer parent, String path) {
        final InputStream is = resourceResolver.getInputStream(path);
        if (is == null) {
            throwDiXmlNotFoundException(parent, path);
        }
        return is;
    }

    protected void throwDiXmlNotFoundException(LaContainer parent, String path) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Not found the Di xml by the path.");
        br.addItem("Specified Path");
        br.addElement(path);
        if (parent != null) {
            br.addItem("Included By");
            br.addElement(parent.getPath());
        }
        final String msg = br.buildExceptionMessage();
        throw new DiXmlNotFoundException(msg);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    public void setResourceResolver(final ResourceResolver resolver) {
        resourceResolver = resolver;
    }
}
