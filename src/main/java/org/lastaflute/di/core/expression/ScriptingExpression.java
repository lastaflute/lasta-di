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
package org.lastaflute.di.core.expression;

import java.util.Map;
import java.util.function.Function;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.LastaDiProperties;
import org.lastaflute.di.core.expression.dwarf.ExpressionPlainHook;
import org.lastaflute.di.core.expression.dwarf.SimpleExpressionPlainHook;
import org.lastaflute.di.core.expression.engine.ExpressionEngine;
import org.lastaflute.di.core.expression.engine.JavaScriptExpressionEngine;
import org.lastaflute.di.util.LdiClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ScriptingExpression implements Expression {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LoggerFactory.getLogger(ScriptingExpression.class);
    protected static final SimpleExpressionPlainHook defaultPlainHook = new SimpleExpressionPlainHook();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ExpressionEngine engine;
    protected final Object parsed;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ScriptingExpression(String source) {
        this.engine = prepareEngine();
        this.parsed = this.engine.parseExpression(source);
    }

    public ScriptingExpression(Function<ScriptingExpression, String> sourceProvider) {
        this.engine = prepareEngine();
        this.parsed = this.engine.parseExpression(sourceProvider.apply(this));
    }

    protected ExpressionEngine prepareEngine() {
        final Class<?> engineType = LastaDiProperties.getInstance().getDiXmlScriptExpressionEngineType();
        final ExpressionEngine engine;
        if (engineType != null) {
            try {
                engine = (ExpressionEngine) LdiClassUtil.newInstance(engineType);
            } catch (RuntimeException e) {
                throw new IllegalStateException("Failed to create instance of the engine: " + engineType, e);
            }
        } else { // mainly here
            engine = createDefaultEngine();
        }
        return engine;
    }

    protected ExpressionEngine createDefaultEngine() {
        return new JavaScriptExpressionEngine();
    }

    // ===================================================================================
    //                                                                            Evaluate
    //                                                                            ========
    @Override
    public Object evaluate(Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
        if (parsed instanceof String) {
            final Object hooked = hookPlainly((String) parsed, contextMap, container, resultType);
            if (hooked != null) {
                final Object result = ExpressionPlainHook.resolveHookedReturn(hooked); // needed for null return
                if (isInternalDebug()) {
                    logger.debug("#fw_debug Parsed as simple expression by plain hook: {} => {}", parsed, result);
                }
                return result;
            }
        }
        // moved to engine internal process for more rich information
        //if (isInternalDebug()) {
        //    logger.debug("#fw_debug ...Evaluating the script by expression engine: script={}", parsed);
        //}
        return engine.evaluate(parsed, contextMap, container, resultType);
    }

    protected Object hookPlainly(String expression, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
        final ExpressionPlainHook plainHook = preparePlainHook();
        return plainHook != null ? plainHook.hookPlainly(expression, contextMap, container, resultType) : null;
    }

    public ExpressionPlainHook preparePlainHook() {
        return defaultPlainHook;
    }

    // ===================================================================================
    //                                                                       Static Method
    //                                                                       =============
    public String resolveStaticMethodReference(Class<?> refType, String methodName) {
        return engine.resolveStaticMethodReference(refType, methodName);
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected boolean isInternalDebug() {
        return LastaDiProperties.getInstance().isInternalDebug();
    }
}
