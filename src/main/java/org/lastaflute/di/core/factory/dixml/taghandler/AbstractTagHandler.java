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

import java.util.function.Supplier;

import org.lastaflute.di.core.expression.Expression;
import org.lastaflute.di.core.expression.ScriptingExpression;
import org.lastaflute.di.core.meta.impl.ComponentNameExpression;
import org.lastaflute.di.core.meta.impl.LiteralExpression;
import org.lastaflute.di.helper.xml.TagHandler;
import org.lastaflute.di.helper.xml.TagHandlerContext;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AbstractTagHandler extends TagHandler {

    private static final long serialVersionUID = 1L;

    protected Expression createExpression(TagHandlerContext context, String body) {
        return doCreateExpression(body, null);
    }

    protected Expression createExpression(TagHandlerContext context, String body, Supplier<ScriptingExpression> scriptingSupplier) {
        return doCreateExpression(body, scriptingSupplier);
    }

    protected Expression doCreateExpression(String body, Supplier<ScriptingExpression> scriptingSupplier) {
        final String expr = body.trim();
        // TODO jflute lastaflute: [E] fitting: DI :: expression error handling
        if ("null".equals(expr)) {
            return new LiteralExpression(expr, null);
        }
        if ("true".equals(expr)) {
            return new LiteralExpression(expr, Boolean.TRUE);
        }
        if ("false".equals(expr)) {
            return new LiteralExpression(expr, Boolean.FALSE);
        }
        if (isComponentName(expr)) {
            return new ComponentNameExpression(expr);
        }
        return createScriptingExpression(expr, scriptingSupplier);
    }

    protected ScriptingExpression createScriptingExpression(String expr, Supplier<ScriptingExpression> scriptingSupplier) {
        if (scriptingSupplier != null) {
            return scriptingSupplier.get();
        } else {
            return new ScriptingExpression(expr);
        }
    }

    protected boolean isComponentName(String expr) {
        if (!Character.isJavaIdentifierStart(expr.charAt(0))) {
            return false;
        }
        for (int i = 1; i < expr.length(); ++i) {
            if (!Character.isJavaIdentifierPart(expr.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
