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
package org.lastaflute.di.core.factory.dixml.taghandler;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.meta.DestroyMethodDef;
import org.lastaflute.di.core.meta.impl.DestroyMethodDefImpl;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.xml.sax.Attributes;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class DestroyMethodTagHandler extends MethodTagHandler {

    private static final long serialVersionUID = 1L;

    @Override
    public void start(TagHandlerContext context, Attributes attributes) {
        String name = attributes.getValue("name");
        context.push(createDestroyMethodDef(name));
    }

    @Override
    public void end(TagHandlerContext context, String body) {
        DestroyMethodDef methodDef = (DestroyMethodDef) context.pop();
        processExpression(methodDef, body, "preDestroy", context);
        ComponentDef componentDef = (ComponentDef) context.peek();
        componentDef.addDestroyMethodDef(methodDef);
    }

    protected DestroyMethodDefImpl createDestroyMethodDef(String name) {
        return new DestroyMethodDefImpl(name);
    }
}
