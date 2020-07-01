/*
 * Copyright 2015-2020 the original author or authors.
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
import org.lastaflute.di.core.assembler.BindingTypeDefFactory;
import org.lastaflute.di.core.factory.dixml.exception.TagAttributeNotDefinedRuntimeException;
import org.lastaflute.di.core.meta.BindingTypeDef;
import org.lastaflute.di.core.meta.PropertyDef;
import org.lastaflute.di.core.meta.impl.PropertyDefImpl;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.lastaflute.di.util.LdiStringUtil;
import org.xml.sax.Attributes;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class PropertyTagHandler extends AbstractTagHandler {

    private static final long serialVersionUID = 1L;

    @Override
    public void start(TagHandlerContext context, Attributes attributes) {
        final String name = attributes.getValue("name");
        if (name == null) {
            throw new TagAttributeNotDefinedRuntimeException("property", "name");
        }
        final PropertyDef pd = createPropertyDef(name);
        final String bindingTypeName = attributes.getValue("bindingType");
        if (bindingTypeName != null) {
            final BindingTypeDef bindingTypeDef = BindingTypeDefFactory.getBindingTypeDef(bindingTypeName);
            pd.setBindingTypeDef(bindingTypeDef);
        }
        context.push(pd);
    }

    @Override
    public void end(TagHandlerContext context, String body) {
        final PropertyDef propertyDef = (PropertyDef) context.pop();
        if (!LdiStringUtil.isEmpty(body)) {
            propertyDef.setExpression(createExpression(context, body));
        }
        final ComponentDef componentDef = (ComponentDef) context.peek();
        componentDef.addPropertyDef(propertyDef);
    }

    protected PropertyDefImpl createPropertyDef(String name) {
        return new PropertyDefImpl(name);
    }
}
