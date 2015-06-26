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
package org.lastaflute.di.core.expression.hook;

import java.util.Map;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.beans.factory.BeanDescFactory;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiResourceUtil;
import org.lastaflute.di.util.LdiSrl;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author jflute
 */
public class SimpleExpressionPlainHook implements ExpressionPlainHook {

    // ===================================================================================
    //                                                                        Hook Plainly
    //                                                                        ============
    @Override
    public Object hookPlainly(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
        return doHookPlainly(exp.trim(), contextMap, container, resultType);
    }

    protected Object doHookPlainly(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
        Object resovled = resolveSimpleString(exp, contextMap, container, resultType);
        if (resovled != null) {
            return resovled;
        }
        resovled = resolveSimpleNumber(exp, contextMap, container, resultType);
        if (resovled != null) {
            return resovled;
        }
        resovled = resolveSimpleEqualEqual(exp, contextMap, container, resultType);
        if (resovled != null) {
            return resovled;
        }
        resovled = resolveSimpleType(exp, contextMap, container, resultType);
        if (resovled != null) {
            return resovled;
        }
        resovled = resolveSimpleComponent(exp, contextMap, container, resultType);
        if (resovled != null) {
            return resovled;
        }
        resovled = resolveExistsResource(exp, contextMap, container, resultType);
        if (resovled != null) {
            return resovled;
        }
        resovled = resolveProviderConfig(exp, contextMap, container, resultType);
        if (resovled != null) {
            return resovled;
        }
        return null;
    }

    // ===================================================================================
    //                                                                      Basic Handling
    //                                                                      ==============
    protected Object resolveSimpleString(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
        if (exp.startsWith(DQ) && exp.endsWith(DQ) && exp.length() > DQ.length()) {
            final String unquoted = exp.substring(DQ.length(), exp.length() - DQ.length());
            if (!unquoted.contains(DQ)) { // simple string e.g. "sea"
                return unquoted;
            }
        }
        return null;
    }

    protected Object resolveSimpleNumber(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
        if (LdiStringUtil.isNumber(exp)) {
            if (exp.length() > 9) {
                return Integer.valueOf(exp);
            } else {
                return Long.valueOf(exp);
            }
        }
        return null;
    }

    protected Object resolveSimpleEqualEqual(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> resultType) {
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

    protected Object resolveSimpleType(String exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
        if (exp.startsWith(TYPE_BEGIN) && exp.endsWith(TYPE_END)) {
            final String className = exp.substring(TYPE_BEGIN.length(), exp.lastIndexOf(TYPE_END));
            return LdiClassUtil.forName(className);
        }
        return null;
    }

    protected Object resolveSimpleComponent(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> resultType) {
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
    protected Object resolveExistsResource(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> resultType) {
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
    protected Object resolveProviderConfig(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> resultType) {
        // TODO jflute lastaflute: [E] fitting: DI :: JavaScript performance tuning, e.g. ? :
        if (exp.startsWith(PROVIDER_GET) && exp.endsWith(METHOD_MARK) && exp.contains(".") && !exp.contains("\"")) {
            final String[] tokens = exp.split("\\.");
            if (tokens.length > 1) {
                Object component = null;
                BeanDesc beanDesc = null;
                for (String prop : tokens) {
                    if (prop.endsWith(METHOD_MARK)) { // method
                        if (component == null) { // e.g. getJdbcUrl() only
                            break;
                        }
                        final String methodName = prop.substring(0, prop.length() - METHOD_MARK.length());
                        component = beanDesc.invoke(component, methodName, (Object[]) null);
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
}
