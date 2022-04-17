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

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.dixml.taghandler.ComponentsTagHandler;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.xml.sax.Attributes;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class RedefinableComponentsTagHandler extends ComponentsTagHandler {

    private static final long serialVersionUID = 1L;

    @Override
    public void start(TagHandlerContext context, Attributes attributes) {
        super.start(context, attributes);

        final String name = RedefinableXmlLaContainerBuilder.PARAMETER_BUILDER;
        final RedefinableXmlLaContainerBuilder builder = (RedefinableXmlLaContainerBuilder) context.getParameter(name);
        final LaContainer container = (LaContainer) context.peek();
        final String path = (String) context.getParameter("path");

        builder.mergeContainers(container, path, false);
    }
}
