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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.exception.ExpressionClassCreateFailureException;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;
import org.lastaflute.di.util.LdiClassUtil;
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
    protected static final String CAST_INT_ARRAY = "(int[])";
    protected static final String CAST_STRING_ARRAY = "(String[])";
    protected static final String CAST_SET = "(Set)";

    // thread-safe without e.g. put, register
    protected static final ScriptEngineManager defaultManager = new ScriptEngineManager();

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
    public Object evaluate(Object exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> conversionType) {
        return viaVariableResolvedEvaluate((String) exp, contextMap, container, conversionType);
    }

    protected Object viaVariableResolvedEvaluate(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> conversionType) {
        String filteredExp = exp;
        for (Entry<String, ? extends Object> entry : contextMap.entrySet()) { // e.g. #SMART => 'cool'
            filteredExp = LdiStringUtil.replace(filteredExp, "#" + entry.getKey(), SQ + entry.getValue() + SQ);
        }
        return viaCastResolvedEvaluate(filteredExp, contextMap, container, conversionType);
    }

    protected Object viaCastResolvedEvaluate(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> conversionType) {
        final String filteredExp;
        final Class<?> resolvedType;
        if (exp.startsWith(CAST_INT_ARRAY)) {
            filteredExp = exp.substring(CAST_INT_ARRAY.length());
            resolvedType = int[].class;
        } else if (exp.startsWith(CAST_STRING_ARRAY)) {
            filteredExp = exp.substring(CAST_STRING_ARRAY.length());
            resolvedType = String[].class;
        } else if (exp.startsWith(CAST_SET)) {
            filteredExp = exp.substring(CAST_SET.length());
            resolvedType = Set.class;
        } else if (exp.startsWith("{") && exp.endsWith("}")) {
            filteredExp = "[" + exp + "]";
            resolvedType = MarkedMap.class;
        } else {
            filteredExp = exp;
            resolvedType = conversionType;
        }
        return viaFirstNameResolvedEvaluate(filteredExp, contextMap, container, resolvedType);
    }

    protected static class MarkedMap { // marker class
    }

    protected Object viaFirstNameResolvedEvaluate(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> conversionType) {
        final String filteredExp;
        String firstName = null;
        Object firstComponent = null;
        if (!exp.startsWith(DQ) && !exp.startsWith("[") && !exp.startsWith("{") && exp.contains(".")) {
            final String componentName = exp.substring(0, exp.indexOf("."));
            final LaContainer namedContainer = container.getRoot().findChild(componentName); // in all container
            if (namedContainer != null) { // first element is named container
                final String rear = exp.substring(exp.indexOf(".") + ".".length());
                if (rear.contains(".")) { // has more chain
                    final String nextName = rear.substring(0, rear.indexOf("."));
                    if (namedContainer.hasComponentDef(nextName)) { // in named container
                        filteredExp = rear;
                        firstName = nextName;
                        firstComponent = namedContainer.getComponent(nextName);
                    } else { // may be JavaScript expression (but basically mistake...)
                        filteredExp = exp;
                        firstName = componentName;
                        firstComponent = namedContainer;
                    }
                } else {
                    if (namedContainer.hasComponentDef(rear)) { // in named container
                        return namedContainer.getComponent(rear); // resolved without evaluation
                    } else { // may be JavaScript expression (but basically mistake...)
                        filteredExp = exp;
                        firstName = componentName;
                        firstComponent = namedContainer;
                    }
                }
            } else { // first element may be component
                filteredExp = exp;
                if (container.hasComponentDef(componentName)) { // in current container only
                    firstName = componentName;
                    firstComponent = container.getComponent(componentName);
                }
            }
        } else {
            filteredExp = exp;
        }
        final Object evaluated = actuallyEvaluate(filteredExp, contextMap, container, firstName, firstComponent);
        return filterEvaluated(filteredExp, contextMap, container, evaluated, conversionType);
    }

    // ===================================================================================
    //                                                                   Actually Evaluate
    //                                                                   =================
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
    //                                                                           Filtering
    //                                                                           =========
    protected Object filterEvaluated(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Object evaluated,
            Class<?> conversionType) {
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
        if (evaluated instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) evaluated;
            return handleMap(exp, contextMap, container, map, conversionType);
        }
        return evaluated;
    }

    protected void throwExpressionClassCreateFailureException(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            String className, RuntimeException cause) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to create the class in the expression.");
        br.addItem("Di XML");
        br.addElement(container.getPath());
        br.addItem("Expression");
        br.addElement(exp);
        br.addItem("Context Map");
        br.addElement(contextMap);
        br.addItem("Class Name");
        br.addElement(className);
        final String msg = br.buildExceptionMessage();
        throw new ExpressionClassCreateFailureException(msg, cause);
    }

    protected Object handleMap(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Map<String, Object> map,
            Class<?> conversionType) {
        final List<Object> challengeList = challengeList(map);
        if (challengeList != null) { // e.g. [1,2] or ...
            if (int[].class.isAssignableFrom(conversionType)) { // e.g. (int[])[1,2]
                final int[] intAry = new int[challengeList.size()];
                int index = 0;
                try {
                    for (Object element : challengeList) {
                        if (element == null) {
                            throw new IllegalStateException("Cannot handle null element in array: index=" + index);
                        }
                        intAry[index] = Integer.parseInt(element.toString());
                        ++index;
                    }
                } catch (RuntimeException e) {
                    throwExpressionCannotConvertException(exp, contextMap, container, conversionType, index, e);
                }
                return intAry;
            } else if (String[].class.isAssignableFrom(conversionType)) { // e.g. (String[])["sea","land"]
                final String[] strAry = new String[challengeList.size()];
                int index = 0;
                try {
                    for (Object element : challengeList) {
                        if (element == null) {
                            throw new IllegalStateException("Cannot handle null element in array: index=" + index);
                        }
                        if (!(element instanceof String)) {
                            throw new IllegalStateException("Non-string element in array: index=" + index);
                        }
                        strAry[index] = (String) element;
                        ++index;
                    }
                } catch (RuntimeException e) {
                    throwExpressionCannotConvertException(exp, contextMap, container, conversionType, index, e);
                }
                return strAry;
            } else if (Set.class.isAssignableFrom(conversionType)) { // e.g. (Set)["sea","land"]
                return new LinkedHashSet<Object>(challengeList);
            } else if (MarkedMap.class.isAssignableFrom(conversionType)) { // e.g. {"sea":"land"} as [{"sea":"land"}]
                @SuppressWarnings("unchecked")
                final Map<Object, Object> wrappedMap = (Map<Object, Object>) challengeList.get(0);
                return new LinkedHashMap<Object, Object>(wrappedMap); // convert to normal map
            } else { // e.g. [1,2]
                return challengeList;
            }
        } else {
            return map;
        }
    }

    protected List<Object> challengeList(Map<String, Object> map) {
        int index = 0;
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            if (LdiStringUtil.isNumber(key) && Integer.parseInt(key) == index) {
                ++index;
                continue;
            }
            return null;
        }
        return new ArrayList<Object>(map.values());
    }

    protected void throwExpressionCannotConvertException(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> conversionType, Integer index, RuntimeException cause) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to convert the value to the type in the expression.");
        br.addItem("Di XML");
        br.addElement(container.getPath());
        br.addItem("Expression");
        br.addElement(exp);
        br.addItem("Context Map");
        br.addElement(contextMap);
        br.addItem("Conversion Type");
        br.addElement(conversionType);
        if (index != null) {
            br.addItem("Array Index");
            br.addElement(index);
        }
        final String msg = br.buildExceptionMessage();
        throw new ExpressionClassCreateFailureException(msg, cause);
    }

    // ===================================================================================
    //                                                                       Static Method
    //                                                                       =============
    @Override
    public String resolveStaticMethodReference(Class<?> refType, String methodName) {
        return refType.getName() + "." + methodName; // e.g. org.lastaflute.di.util.LdiResourceUtil.exists
    }
}
