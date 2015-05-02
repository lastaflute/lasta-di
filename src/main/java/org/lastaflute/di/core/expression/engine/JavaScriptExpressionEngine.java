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
package org.lastaflute.di.core.expression.engine;

import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.exception.ExpressionClassCreateFailureException;
import org.lastaflute.di.core.expression.hook.ExpressionPlainHook;
import org.lastaflute.di.core.expression.hook.SimpleExpressionPlainHook;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiResourceUtil;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author jflute
 */
public class JavaScriptExpressionEngine implements ExpressionEngine {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String SQ = "'";
    protected static final String DQ = "\"";
    protected static final String NUM = "0123456789";
    protected static final String EXISTS_BEGIN = LdiResourceUtil.class.getName() + ".exists('";
    protected static final String EXISTS_END = "')";
    protected static final String PROVIDER_GET = "provider.config().get";
    protected static final ScriptEngineManager defaultManager = new ScriptEngineManager();
    protected static final SimpleExpressionPlainHook defaultPlainHook = new SimpleExpressionPlainHook();

    // ===================================================================================
    //                                                                    Parse Expression
    //                                                                    ================
    @Override
    public Object parseExpression(String source) {
        return source.trim();
    }

    // ===================================================================================
    //                                                                            Evaluate
    //                                                                            ========
    @Override
    public Object evaluate(Object exp, Map<String, ? extends Object> contextMap, LaContainer container) {
        return doEvaluate(resolveVariableOnExpression((String) exp, contextMap), contextMap, container);
    }

    protected String resolveVariableOnExpression(String exp, Map<String, ? extends Object> contextMap) {
        String filtered = exp;
        for (Entry<String, ? extends Object> entry : contextMap.entrySet()) { // e.g. #SMART => 'cool'
            filtered = LdiStringUtil.replace(filtered, "#" + entry.getKey(), SQ + entry.getValue() + SQ);
        }
        return filtered;
    }

    protected Object doEvaluate(String exp, Map<String, ? extends Object> contextMap, LaContainer container) {
        final Object plainly = hookPlainly(exp, contextMap, container);
        if (plainly != null) {
            return plainly;
        }
        String firstName = null;
        Object firstComponent = null;
        if (!exp.startsWith(DQ) && exp.contains(".")) {
            final String componentName = exp.substring(0, exp.indexOf("."));
            if (container.hasComponentDef(componentName)) {
                firstName = componentName;
                firstComponent = container.getComponent(componentName);
            }
        }
        final Object evaluated = actuallyEvaluate(exp, contextMap, container, firstName, firstComponent);
        return filterEvaluated(exp, contextMap, container, evaluated);
    }

    // -----------------------------------------------------
    //                                             Filtering
    //                                             ---------
    protected Object hookPlainly(String exp, Map<String, ? extends Object> contextMap, LaContainer container) {
        final ExpressionPlainHook plainHook = preparePlainHook();
        return plainHook != null ? plainHook.hookPlainly(exp, contextMap, container) : null;
    }

    public ExpressionPlainHook preparePlainHook() {
        return defaultPlainHook;
    }

    protected Object filterEvaluated(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Object evaluated) {
        if (evaluated instanceof String) {
            // e.g. jp. cannot create the instance with this error,
            // ReferenceError: "jp" is not defined in <eval> at line number 1
            // (com. and org. can do it)
            // so you can create by quoted string expression: "new jp.dbflute.SeaLogic()"
            final String str = ((String) evaluated).trim();
            final String prefix = "new ";
            final String suffix = "()";
            if (str.startsWith(prefix) && str.endsWith(suffix)) {
                final String className = str.substring(prefix.length(), str.length() - suffix.length());
                try {
                    return LdiClassUtil.newInstance(className);
                } catch (RuntimeException e) {
                    throwExpressionClassCreateFailureException(exp, contextMap, container, className, e);
                }
            }
        }
        return evaluated;
    }

    protected void throwExpressionClassCreateFailureException(String expression, Map<String, ? extends Object> contextMap,
            LaContainer container, String className, RuntimeException cause) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to create the class in the expression.");
        br.addItem("Di XML");
        br.addElement(container.getPath());
        br.addItem("Expression");
        br.addElement(expression);
        br.addItem("Context Map");
        br.addElement(contextMap);
        br.addItem("Class Name");
        br.addElement(className);
        final String msg = br.buildExceptionMessage();
        throw new ExpressionClassCreateFailureException(msg, cause);
    }

    // -----------------------------------------------------
    //                                     Actually Evaluate
    //                                     -----------------
    protected Object actuallyEvaluate(String exp, Map<String, ? extends Object> contextMap, LaContainer container, String firstName,
            Object firstComponent) {
        final ScriptEngine engine = prepareScriptEngineManager().getEngineByName("javascript");
        if (firstName != null) {
            engine.put(firstName, firstComponent);
        }
        try {
            return engine.eval(exp);
        } catch (ScriptException | RuntimeException e) {
            throwJavaScriptExpressionException(exp, contextMap, container, e);
            return null; // unreachable
        }
    }

    protected ScriptEngineManager prepareScriptEngineManager() {
        return defaultManager; // as default
    }

    protected void throwJavaScriptExpressionException(Object exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Exception e) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to evaluate the JavaScript expression.");
        br.addItem("Di XML");
        br.addElement(container.getPath());
        br.addItem("Expression");
        br.addElement(exp);
        br.addItem("Context Map");
        br.addElement(contextMap);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg, e);
    }

    // ===================================================================================
    //                                                                       Static Method
    //                                                                       =============
    @Override
    public String resolveStaticMethodReference(Class<?> refType, String methodName) {
        return refType.getName() + "." + methodName; // e.g. org.lastaflute.di.util.LdiResourceUtil.exists
    }
}
