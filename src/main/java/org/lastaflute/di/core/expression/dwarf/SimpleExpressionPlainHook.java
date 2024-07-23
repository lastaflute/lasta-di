/*
 * Copyright 2015-2024 the original author or authors.
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
package org.lastaflute.di.core.expression.dwarf;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.expression.dwarf.ExpressionCastResolver.CastResolved;
import org.lastaflute.di.core.expression.engine.ExpressionEngine;
import org.lastaflute.di.exception.ClassNotFoundRuntimeException;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.beans.factory.BeanDescFactory;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiResourceUtil;
import org.lastaflute.di.util.LdiSrl;
import org.lastaflute.di.util.LdiStringUtil;
import org.lastaflute.di.util.tiger.LdiReflectionUtil;

/**
 * @author jflute
 */
public class SimpleExpressionPlainHook implements ExpressionPlainHook {

    protected static final ExpressionCastResolver castResolver = new ExpressionCastResolver();

    // ===================================================================================
    //                                                                        Hook Plainly
    //                                                                        ============
    @Override
    public Object hookPlainly(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
        // same filter in JavaScript engine however cannot commonize easily because of OGNL-embedded
        // no fix as small cost for now by jflute (2020/09/30)
        final String resolvedExp = ExpressionEngine.resolveExpressionVariableSimply(exp, contextMap);
        return doHookPlainly(resolvedExp, contextMap, container, resultType);
    }

    protected Object doHookPlainly(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
        final CastResolved resolved = castResolver.resolveCast(exp, resultType);
        final String realExp;
        final Class<?> realType;
        if (resolved != null) {
            realExp = resolved.getFilteredExp();
            realType = resolved.getResolvedType();
        } else {
            realExp = exp.trim();
            realType = resultType;
        }
        return actuallyHookPlainly(realExp, container, realType);
    }

    protected Object actuallyHookPlainly(String exp, LaContainer container, Class<?> resultType) {
        Object resovled = resolveSimpleString(exp, container, resultType); // "sea"
        if (isReallyResolved(resovled)) {
            return resovled;
        }
        resovled = resolveSimpleNumber(exp, container, resultType); // e.g. 7
        if (isReallyResolved(resovled)) {
            return resovled;
        }
        resovled = resolveSimpleEqualEqual(exp, container, resultType); // e.g. 'hot' == 'cool'
        if (isReallyResolved(resovled)) {
            return resovled;
        }
        resovled = resolveSimpleTypeExp(exp, container, resultType); // e.g. @org.docksidestage.Sea@class
        if (isReallyResolved(resovled)) {
            return resovled;
        }
        resovled = resolveSimpleComponent(exp, container, resultType); // e.g. sea
        if (isReallyResolved(resovled)) {
            return resovled;
        }
        resovled = resolveExistsResource(exp, container, resultType); // e.g. .exists()
        if (isReallyResolved(resovled)) {
            return resovled;
        }
        resovled = resolveProviderConfig(exp, container, resultType); // e.g. provider.config...
        if (isReallyResolved(resovled)) {
            return resovled;
        }
        resovled = resolveComponentList(exp, container, resultType); // e.g. [sea, land]
        if (isReallyResolved(resovled)) {
            return resovled;
        }
        return null;
    }

    protected boolean isReallyResolved(Object resovled) {
        return resovled != null; // includes null return object
    }

    // ===================================================================================
    //                                                                      Basic Handling
    //                                                                      ==============
    protected Object resolveSimpleString(String exp, LaContainer container, Class<?> resultType) {
        if (exp.startsWith(DQ) && exp.endsWith(DQ) && exp.length() > DQ.length()) {
            final String unquoted = exp.substring(DQ.length(), exp.length() - DQ.length());
            if (!unquoted.contains(DQ)) { // simple string e.g. "sea"
                return unquoted;
            }
        }
        return null;
    }

    protected Object resolveSimpleNumber(String exp, LaContainer container, Class<?> resultType) {
        if (LdiStringUtil.isNumber(exp)) {
            if (exp.length() > 9) {
                return Long.valueOf(exp);
            } else {
                return Integer.valueOf(exp);
            }
        }
        return null;
    }

    protected Object resolveSimpleEqualEqual(String exp, LaContainer container, Class<?> resultType) {
        if (exp.contains("==")) {
            final String[] split = exp.split("==");
            if (split.length == 2) { // may be e.g. 'hot' == 'cool'
                final String left = split[0].trim();
                final String right = split[1].trim();
                if (left.startsWith(SQ) && left.endsWith(SQ) && left.length() > SQ.length() // left
                        && right.startsWith(SQ) && right.endsWith(SQ) && right.length() > SQ.length()) { // right
                    final String unquotedLeft = left.substring(SQ.length(), left.length() - SQ.length());
                    final String unquotedRight = right.substring(SQ.length(), right.length() - SQ.length());
                    if (!unquotedLeft.contains(SQ) && !unquotedRight.contains(SQ)) { // yes, e.g. 'hot' == 'cool'
                        return unquotedLeft.equals(unquotedRight);
                    }
                }
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                              Simple Type Expression
    //                                                              ======================
    protected Object resolveSimpleTypeExp(String exp, LaContainer container, Class<?> resultType) {
        if (exp.startsWith(TYPE_BEGIN) && exp.endsWith(TYPE_END_CLASS)) { // @org.docksidestage.Sea@class
            // mainly for OGNL compatibility
            final String className = exp.substring(TYPE_BEGIN.length(), exp.lastIndexOf(TYPE_END_CLASS));
            return LdiClassUtil.forName(className);
        }
        if (exp.startsWith(TYPE_BEGIN) && exp.contains(TYPE_END)) { // @org.docksidestage.Sea@call()
            // minor domain, e.g. jp, cannot be parsed by Nashon so original logic here
            final String className = exp.substring(TYPE_BEGIN.length(), exp.lastIndexOf(TYPE_END));
            final String rear = exp.substring(exp.lastIndexOf(TYPE_END) + TYPE_END.length());
            final Class<?> clazz = LdiClassUtil.forName(className);
            if (rear.endsWith(METHOD_MARK)) {
                final String methodName = rear.substring(0, rear.lastIndexOf(METHOD_MARK));
                final Method method = LdiReflectionUtil.getMethod(clazz, methodName, (Class<?>[]) null);
                return LdiReflectionUtil.invoke(method, null, (Object[]) null);
            } else {
                final Field field = LdiReflectionUtil.getField(clazz, rear);
                return LdiReflectionUtil.getValue(field, null);
            }
        }
        // added for rhino, which cannot resolve this expression by jflute (2021/08/31)
        if (LdiSrl.count(exp, ".") >= 2 && exp.endsWith(".class")) { // org...Sea.class
            final String pureFqcn = LdiSrl.substringLastFront(exp, ".class");
            final String pureClassName = LdiSrl.substringLastRear(pureFqcn, ".");
            if (LdiSrl.isInitLowerCase(pureFqcn) && LdiSrl.isInitUpperCase(pureClassName)) { // more strict
                try {
                    return LdiClassUtil.forName(pureFqcn);
                } catch (ClassNotFoundRuntimeException ignored) {
                    // because of best effort logic (may be non-class expression)
                }
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                                    Simple Component
    //                                                                    ================
    protected Object resolveSimpleComponent(String exp, LaContainer container, Class<?> resultType) {
        if (!LdiSrl.containsAny(exp, ".", ",", SQ, DQ, "@", "#")) { // main except, just in case
            if (container.hasComponentDef(exp)) {
                return container.getComponent(exp);
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                                        Simple Array
    //                                                                        ============
    // hard to parse String[] so convert evaluated value to resultType later
    //protected Object resolveSimpleArray(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
    //    Object resolved = doResolveSimpleIntArray(exp, contextMap, container);
    //    if (resolved != null) {
    //        return resolved;
    //    }
    //    resolved = doResolveSimpleStringArray(exp, contextMap, container);
    //    if (resolved != null) {
    //        return resolved;
    //    }
    //    return null;
    //}
    //
    //protected Object doResolveSimpleIntArray(String exp, Map<String, ? extends Object> contextMap, LaContainer container) {
    //    final String intAryMark = "new int[]";
    //    if (exp.startsWith(intAryMark)) {
    //        final String rear = exp.substring(intAryMark.length()).trim();
    //        if (rear.startsWith("{") && rear.endsWith("}") && !rear.contains(DQ) && !rear.contains(SQ)) {
    //            final List<String> splitList = LdiSrl.splitListTrimmed(LdiSrl.unquoteAnything(rear, "{", "}"), ",");
    //            final int[] intAry = new int[splitList.size()];
    //            int index = 0;
    //            for (String element : splitList) {
    //                if (!LdiStringUtil.isNumber(element)) {
    //                    return null;
    //                }
    //                intAry[index] = Integer.parseInt(element);
    //                ++index;
    //            }
    //            return intAry;
    //        }
    //    }
    //    return null;
    //}

    // ===================================================================================
    //                                                                     Exists Resource
    //                                                                     ===============
    protected Object resolveExistsResource(String exp, LaContainer container, Class<?> resultType) {
        if (exp.startsWith(EXISTS_BEGIN) && exp.endsWith(EXISTS_END)) {
            final String path = exp.substring(EXISTS_BEGIN.length(), exp.lastIndexOf(EXISTS_END));
            if (!path.contains(SQ)) {
                return LdiResourceUtil.exists(path);
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                              Provider Configuration
    //                                                              ======================
    protected Object resolveProviderConfig(String exp, LaContainer container, Class<?> resultType) {
        final boolean noArgMethod = isProviderConfigNoArgMethod(exp);
        final boolean orDefaultMethod = isProviderConfigOrDefaultMethod(exp);
        if (noArgMethod || orDefaultMethod) {
            if (orDefaultMethod && LdiSrl.count(exp, "\"") == 2) {
                final List<String> splitList = LdiSrl.splitList(exp, "\""); // always three elements
                final String savedDotKey = LdiSrl.replace(splitList.get(1), ".", "$$dot$$");
                exp = splitList.get(0) + "\"" + savedDotKey + "\"" + splitList.get(2);
            }
            final String[] tokens = exp.split("\\.");
            if (tokens.length > 1) {
                Object component = null;
                BeanDesc beanDesc = null;
                for (String prop : tokens) {
                    if (prop.endsWith(METHOD_MARK)) { // no-argument method e.g. config(), getJdbcUrl()
                        if (component == null) { // no way? no-component method call
                            break;
                        }
                        final String methodName = prop.substring(0, prop.length() - METHOD_MARK.length());
                        component = beanDesc.invoke(component, methodName, (Object[]) null);
                        if (component == null) { // empty property value, sometimes possible
                            component = NULL_RETURN;
                            break;
                        }
                        beanDesc = BeanDescFactory.getBeanDesc(component.getClass());
                    } else if (prop.startsWith(ORDEFAULT_BEGIN) && prop.endsWith(ORDEFAULT_END)) { // getOrDefault(...)
                        if (component == null) { // no way? no-component method call
                            break;
                        }
                        final String key = LdiSrl.extractScopeFirst(prop, ORDEFAULT_BEGIN, ORDEFAULT_END).getContent();
                        final String plainDotKey = LdiSrl.replace(key, "$$dot$$", ".");
                        component = beanDesc.invoke(component, ORDEFAULT_METHOD_NAME, new Object[] { plainDotKey, null });
                        if (component == null) { // not found (default null), enough possible
                            component = NULL_RETURN;
                            break;
                        }
                        beanDesc = BeanDescFactory.getBeanDesc(component.getClass());
                    } else { // component or property
                        if (beanDesc == null) { // first element
                            if (container.hasComponentDef(prop)) { // component
                                component = container.getComponent(prop);
                                beanDesc = BeanDescFactory.getBeanDesc(component.getClass());
                            } else {
                                break;
                            }
                        } else { // next elements, property
                            final PropertyDesc propertyDesc = beanDesc.getPropertyDesc(prop);
                            if (propertyDesc != null) {
                                component = propertyDesc.getValue(component);
                                beanDesc = BeanDescFactory.getBeanDesc(component.getClass());
                            } else {
                                break;
                            }
                        }
                    }
                }
                if (component != null) {
                    return component;
                }
            }
        }
        return null;
    }

    protected boolean isProviderConfigNoArgMethod(String exp) { // LastaFlute uses
        // e.g. provider.config().getJdbcUrl()
        return exp.startsWith(PROVIDER_GET) && exp.endsWith(METHOD_MARK) && !exp.contains("\"");
    }

    protected boolean isProviderConfigOrDefaultMethod(String exp) { // LastaFlute uses
        // e.g. provider.config().getOrDefault("jdbc.connection.pooling.min.size", null)
        return exp.startsWith(ORDEFAULT_PROVIDER_GET) && exp.endsWith(ORDEFAULT_END);
    }

    // ===================================================================================
    //                                                                      Component List
    //                                                                      ==============
    protected Object resolveComponentList(String exp, LaContainer container, Class<?> resultType) {
        if (!exp.contains(DQ) && !exp.contains(SQ) && exp.startsWith("[") && exp.endsWith("]")) {
            final String listContents = exp.substring(1, exp.length() - 1);
            final String[] elements = LdiStringUtil.split(listContents, ",");
            boolean compAry = false;
            for (String comp : elements) {
                if (!LdiStringUtil.isNumber(comp)) {
                    compAry = true;
                    break;
                }
            }
            if (compAry) {
                final List<Object> resultList = new ArrayList<Object>();
                for (String comp : elements) {
                    final Object component = container.getComponent(comp.trim()); // in same or child container
                    resultList.add(component);
                }
                return castResolver.convertListTo(exp, container, resultType, resultList);
            }
        }
        return null;
    }
}
