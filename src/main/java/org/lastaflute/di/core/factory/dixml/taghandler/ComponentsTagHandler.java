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
package org.lastaflute.di.core.factory.dixml.taghandler;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.meta.impl.LaContainerImpl;
import org.lastaflute.di.helper.xml.TagHandler;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiStringUtil;
import org.xml.sax.Attributes;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ComponentsTagHandler extends TagHandler {

    private static final long serialVersionUID = 3182865184697069169L;

    protected Class<?> containerImplClass = LaContainerImpl.class;

    public Class<?> getContainerImplClass() {
        return containerImplClass;
    }

    public void setContainerImplClass(Class<?> containerImplClass) {
        this.containerImplClass = containerImplClass;
    }

    public void start(TagHandlerContext context, Attributes attributes) {
        final LaContainer container = createContainer();
        final String path = (String) context.getParameter("path");
        container.setPath(path);
        final String namespace = attributes.getValue("namespace");
        if (!LdiStringUtil.isEmpty(namespace)) {
            container.setNamespace(namespace);
        }
        final String initializeOnCreate = attributes.getValue("initializeOnCreate");
        if (!LdiStringUtil.isEmpty(initializeOnCreate)) {
            container.setInitializeOnCreate(Boolean.valueOf(initializeOnCreate).booleanValue());
        }

        LaContainer parent = (LaContainer) context.getParameter("parent");
        if (parent != null) {
            container.setRoot(parent.getRoot());
        }
        context.push(container);
    }

    protected LaContainer createContainer() {
        return (LaContainer) LdiClassUtil.newInstance(containerImplClass);
    }
}
