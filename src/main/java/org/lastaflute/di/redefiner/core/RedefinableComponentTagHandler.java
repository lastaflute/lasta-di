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
package org.lastaflute.di.redefiner.core;

import static org.lastaflute.di.redefiner.core.RedefinableXmlLaContainerBuilder.DELIMITER;
import static org.lastaflute.di.redefiner.core.RedefinableXmlLaContainerBuilder.PARAMETER_BASEPATH;
import static org.lastaflute.di.redefiner.core.RedefinableXmlLaContainerBuilder.PARAMETER_BUILDER;

import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.LaContainerFactory;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandler;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandlerFactory;
import org.lastaflute.di.core.factory.dixml.exception.TagAttributeNotDefinedRuntimeException;
import org.lastaflute.di.core.factory.dixml.taghandler.ComponentTagHandler;
import org.lastaflute.di.core.meta.ArgDef;
import org.lastaflute.di.core.meta.impl.InstanceDefFactory;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.lastaflute.di.redefiner.util.LaContainerBuilderUtils;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class RedefinableComponentTagHandler extends ComponentTagHandler { // for e.g. jta+userTransaction.xml

    private static final long serialVersionUID = 2513809305883784501L;

    @Override
    public void end(TagHandlerContext context, String body) {
        final ComponentDef componentDef = (ComponentDef) context.pop();
        final AnnotationHandler annoHandler = AnnotationHandlerFactory.getAnnotationHandler();
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
            final LaContainer container = (LaContainer) context.peek();
            if (componentDef.getComponentName() != null) {
                final String basePath = (String) context.getParameter(PARAMETER_BASEPATH);
                final RedefinableXmlLaContainerBuilder builder = (RedefinableXmlLaContainerBuilder) context.getParameter(PARAMETER_BUILDER);
                final LaContainer redefined = redefine(componentDef, basePath, builder);
                if (redefined != null) { // switch the component by name e.g. jta+userTransaction.xml
                    LaContainerBuilderUtils.mergeContainer(container, redefined);
                } else { // normally here
                    container.register(componentDef);
                }
            } else { // no-name component
                container.register(componentDef);
            }
        } else {
            final ArgDef argDef = (ArgDef) context.peek();
            argDef.setChildComponentDef(componentDef);
        }
    }

    protected LaContainer redefine(ComponentDef componentDef, String path, RedefinableXmlLaContainerBuilder builder) {
        int delimiter = path.lastIndexOf(DELIMITER);
        int slash = path.lastIndexOf('/');
        if (delimiter >= 0 && delimiter > slash) { // e.g. jta+userTransaction.xml, jta++.xml
            return null; // no handling if the resource name contains '+'
        }

        final String name = componentDef.getComponentName();
        final String[] diconPaths = constructRedifinitionDiconPaths(path, name);
        String diconPath = null;
        for (int i = 0; i < diconPaths.length; i++) {
            if (LaContainerBuilderUtils.resourceExists(diconPaths[i], builder)) {
                diconPath = diconPaths[i];
                break;
            }
        }
        if (diconPath == null) {
            return null;
        }

        return LaContainerFactory.create(diconPath);
    }

    protected String[] constructRedifinitionDiconPaths(String path, String name) {
        final List<String> pathList = new ArrayList<String>();
        final String body;
        final String suffix;
        int dot = path.lastIndexOf('.');
        if (dot < 0) {
            body = path;
            suffix = "";
        } else {
            body = path.substring(0, dot);
            suffix = path.substring(dot);
        }
        final String resourcePath = LaContainerBuilderUtils.fromURLToResourcePath(body + DELIMITER + name + suffix);
        if (resourcePath != null) {
            pathList.add(resourcePath);
        }
        pathList.add(body + DELIMITER + name + suffix); // e.g. jta+userTransaction.xml
        return pathList.toArray(new String[0]);
    }
}
