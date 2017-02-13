/*
 * Copyright 2015-2017 the original author or authors.
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
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.assembler.AutoBindingDefFactory;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandler;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandlerFactory;
import org.lastaflute.di.core.factory.dixml.exception.TagAttributeNotDefinedRuntimeException;
import org.lastaflute.di.core.meta.ArgDef;
import org.lastaflute.di.core.meta.impl.ComponentDefImpl;
import org.lastaflute.di.core.meta.impl.InstanceDefFactory;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiStringUtil;
import org.xml.sax.Attributes;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ComponentTagHandler extends AbstractTagHandler {

    private static final long serialVersionUID = 1L;

    @Override
    public void start(TagHandlerContext context, Attributes attributes) {
        final AnnotationHandler annoHandler = AnnotationHandlerFactory.getAnnotationHandler();
        final String className = attributes.getValue("class");
        Class<?> componentClass = null;
        if (className != null) {
            componentClass = LdiClassUtil.forName(className);
        }
        String name = attributes.getValue("name");
        ComponentDef componentDef = null;
        if (componentClass != null) {
            componentDef = annoHandler.createComponentDef(componentClass, null);
            if (name != null) {
                componentDef.setComponentName(name);
            }
            annoHandler.appendDI(componentDef);
        } else {
            componentDef = createComponentDef(null, name); // no type here, may be set later (e.g. in assembly)
        }
        String instanceMode = attributes.getValue("instance");
        if (instanceMode != null) {
            componentDef.setInstanceDef(InstanceDefFactory.getInstanceDef(instanceMode));
        }
        String autoBindingName = attributes.getValue("autoBinding");
        if (autoBindingName != null) {
            componentDef.setAutoBindingDef(AutoBindingDefFactory.getAutoBindingDef(autoBindingName));
        }
        String externalBindingStr = attributes.getValue("externalBinding");
        if (externalBindingStr != null) {
            componentDef.setExternalBinding(Boolean.valueOf(externalBindingStr).booleanValue());
        }
        context.push(componentDef);
    }

    public void end(TagHandlerContext context, String body) {
        ComponentDef componentDef = (ComponentDef) context.pop();
        AnnotationHandler annoHandler = AnnotationHandlerFactory.getAnnotationHandler();
        annoHandler.appendInitMethod(componentDef);
        annoHandler.appendDestroyMethod(componentDef);
        annoHandler.appendAspect(componentDef);
        annoHandler.appendInterType(componentDef);
        String expression = null;
        if (body != null) {
            expression = body.trim();
            if (!LdiStringUtil.isEmpty(expression)) {
                componentDef.setExpression(createExpression(context, expression));
            } else {
                expression = null;
            }
        }
        if (componentDef.getComponentClass() == null && !InstanceDefFactory.OUTER.equals(componentDef.getInstanceDef())
                && expression == null) {
            throw new TagAttributeNotDefinedRuntimeException("component", "class");
        }
        if (context.peek() instanceof LaContainer) {
            LaContainer container = (LaContainer) context.peek();
            container.register(componentDef);
        } else {
            ArgDef argDef = (ArgDef) context.peek();
            argDef.setChildComponentDef(componentDef);
        }
    }

    protected ComponentDef createComponentDef(Class<?> componentClass, String name) {
        return new ComponentDefImpl(componentClass, name);
    }
}
