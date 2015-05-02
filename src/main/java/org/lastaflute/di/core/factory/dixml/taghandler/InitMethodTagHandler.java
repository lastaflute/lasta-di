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
package org.lastaflute.di.core.factory.dixml.taghandler;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.meta.InitMethodDef;
import org.lastaflute.di.core.meta.impl.InitMethodDefImpl;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.xml.sax.Attributes;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class InitMethodTagHandler extends MethodTagHandler {
    private static final long serialVersionUID = 514017929221501933L;

    @Override
    public void start(TagHandlerContext context, Attributes attributes) {
        String name = attributes.getValue("name");
        context.push(createInitMethodDef(name));
    }

    @Override
    public void end(TagHandlerContext context, String body) {
        InitMethodDef methodDef = (InitMethodDef) context.pop();
        ComponentDef componentDef = (ComponentDef) context.peek();
        processExpression(methodDef, body, "postConstruct", context);
        componentDef.addInitMethodDef(methodDef);
    }

    protected InitMethodDefImpl createInitMethodDef(String name) {
        return new InitMethodDefImpl(name);
    }
}
