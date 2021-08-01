/*
 * Copyright 2015-2021 the original author or authors.
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

import org.lastaflute.di.core.factory.dixml.exception.TagAttributeNotDefinedRuntimeException;
import org.lastaflute.di.core.meta.MethodDef;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class MethodTagHandler extends AbstractTagHandler {

    private static final long serialVersionUID = 1L;

    /**
     * @param methodDef
     * @param expression
     * @param tagName
     * @param context
     * @throws TagAttributeNotDefinedRuntimeException
     */
    protected void processExpression(MethodDef methodDef, String expression, String tagName, TagHandlerContext context) {
        String expr = expression;
        if (expr != null) {
            expr = expr.trim();
            if (!LdiStringUtil.isEmpty(expr)) {
                methodDef.setExpression(createExpression(context, expr));
            } else {
                expr = null;
            }
        }
        if (methodDef.getMethodName() == null && expr == null) {
            throw new TagAttributeNotDefinedRuntimeException(tagName, "name");
        }
    }
}
