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
package org.dbflute.lasta.di.core.factory.dixml.taghandler;

import java.util.HashMap;
import java.util.Map;

import org.dbflute.lasta.di.core.LaContainer;
import org.dbflute.lasta.di.core.expression.Expression;
import org.dbflute.lasta.di.core.expression.ScriptingExpression;
import org.dbflute.lasta.di.core.factory.LaContainerFactory;
import org.dbflute.lasta.di.core.factory.dixml.exception.TagAttributeNotDefinedRuntimeException;
import org.dbflute.lasta.di.core.smart.SmartDeployMode;
import org.dbflute.lasta.di.helper.xml.TagHandlerContext;
import org.dbflute.lasta.di.util.LdiResourceUtil;
import org.dbflute.lasta.di.util.LdiStringUtil;
import org.xml.sax.Attributes;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class IncludeTagHandler extends AbstractTagHandler {

    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                      Start Handling
    //                                                                      ==============
    @Override
    public void start(TagHandlerContext context, Attributes attributes) {
        final String path = attributes.getValue("path");
        if (path == null) {
            throw new TagAttributeNotDefinedRuntimeException("include", "path");
        }
        final LaContainer container = (LaContainer) context.peek();
        final String condition = attributes.getValue("condition");
        if (!LdiStringUtil.isEmpty(condition)) {
            final Map<String, String> contextMap = prepareContextMap();
            final Expression expression = createExpression(context, condition, () -> {
                return new ScriptingExpression(exp -> {
                    return resolveVariableIfNeeds(condition, path, exp);
                });
            });
            final Object evaluated = expression.evaluate(contextMap, container);
            if (!(evaluated instanceof Boolean)) {
                throw new IllegalStateException("condition:" + condition);
            }
            if (!((Boolean) evaluated).booleanValue()) {
                return;
            }
        }
        LaContainerFactory.include(container, path);
    }

    protected Map<String, String> prepareContextMap() {
        final Map<String, String> map = new HashMap<String, String>(1);
        map.put("SMART", SmartDeployMode.getValue().code()); // e.g. #SMART == '...'
        return map;
    }

    // ===================================================================================
    //                                                                    Resolve Variable
    //                                                                    ================
    protected String resolveVariableIfNeeds(String expr, String path, ScriptingExpression expression) {
        if (expr == null) {
            return null;
        }
        return doResolveShortExistsIfNeeds(doResolvePathExpIfNeeds((String) expr, path, expression), expression);
    }

    protected String doResolvePathExpIfNeeds(String expr, String path, ScriptingExpression expression) {
        final String pathExp = "#path";
        return expr.contains(pathExp) ? expr.replace(pathExp, path) : expr;
    }

    protected String doResolveShortExistsIfNeeds(String expr, ScriptingExpression expression) {
        final String shortExists = "#exists(";
        if (expr.contains(shortExists)) {
            final String reference = expression.resolveStaticMethodReference(LdiResourceUtil.class, "exists");
            return expr.replace(shortExists, reference + "(");
        } else {
            return expr;
        }
    }
}