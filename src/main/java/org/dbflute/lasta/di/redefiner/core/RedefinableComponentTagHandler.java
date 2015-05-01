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
package org.dbflute.lasta.di.redefiner.core;

import static org.dbflute.lasta.di.redefiner.core.RedefinableXmlLaContainerBuilder.DELIMITER;
import static org.dbflute.lasta.di.redefiner.core.RedefinableXmlLaContainerBuilder.PARAMETER_BASEPATH;
import static org.dbflute.lasta.di.redefiner.core.RedefinableXmlLaContainerBuilder.PARAMETER_BUILDER;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.LaContainer;
import org.dbflute.lasta.di.core.factory.LaContainerFactory;
import org.dbflute.lasta.di.core.factory.annohandler.AnnotationHandler;
import org.dbflute.lasta.di.core.factory.annohandler.AnnotationHandlerFactory;
import org.dbflute.lasta.di.core.factory.dixml.exception.TagAttributeNotDefinedRuntimeException;
import org.dbflute.lasta.di.core.factory.dixml.taghandler.ComponentTagHandler;
import org.dbflute.lasta.di.core.meta.ArgDef;
import org.dbflute.lasta.di.core.meta.impl.InstanceDefFactory;
import org.dbflute.lasta.di.helper.xml.TagHandlerContext;
import org.dbflute.lasta.di.redefiner.util.LaContainerBuilderUtils;
import org.dbflute.lasta.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class RedefinableComponentTagHandler extends ComponentTagHandler {

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
                if (redefined != null) {
                    LaContainerBuilderUtils.mergeContainer(container, redefined);
                } else {
                    container.register(componentDef);
                }
            } else {
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
        if (delimiter >= 0 && delimiter > slash) {
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
            // パスがJarのURLの場合はURLをリソースパスに変換した上で作成したパスを候補に含める。
            pathList.add(resourcePath);
        }
        pathList.add(body + DELIMITER + name + suffix);
        return pathList.toArray(new String[0]);
    }
}
