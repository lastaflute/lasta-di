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
package org.dbflute.lasta.di.core.expression.hook;

import java.util.Map;

import org.dbflute.lasta.di.core.LaContainer;
import org.dbflute.lasta.di.helper.beans.BeanDesc;
import org.dbflute.lasta.di.helper.beans.PropertyDesc;
import org.dbflute.lasta.di.helper.beans.factory.BeanDescFactory;
import org.dbflute.lasta.di.util.LdiResourceUtil;
import org.dbflute.lasta.di.util.LdiStringUtil;

/**
 * @author jflute
 */
public class SimpleExpressionPlainHook implements ExpressionPlainHook {

    // ===================================================================================
    //                                                                        Hook Plainly
    //                                                                        ============
    @Override
    public Object hookPlainly(String exp, Map<String, ? extends Object> contextMap, LaContainer container) {
        Object resovled = resolveSimpleString(exp, contextMap, container);
        if (resovled != null) {
            return resovled;
        }
        resovled = resolveSimpleNumber(exp, contextMap, container);
        if (resovled != null) {
            return resovled;
        }
        resovled = resolveExistsResource(exp, contextMap, container);
        if (resovled != null) {
            return resovled;
        }
        resovled = resolveSimpleEqualEqual(exp, contextMap, container);
        if (resovled != null) {
            return resovled;
        }
        resovled = resolveProviderConfig(exp, contextMap, container);
        if (resovled != null) {
            return resovled;
        }
        return null;
    }

    protected Object resolveSimpleString(String exp, Map<String, ? extends Object> contextMap, LaContainer container) {
        if (exp.startsWith(DQ) && exp.endsWith(DQ) && exp.length() > DQ.length()) {
            final String unquoted = exp.substring(DQ.length(), exp.length() - DQ.length());
            if (!unquoted.contains(DQ)) { // simple string e.g. "tx_aop.requiredTx"
                return unquoted;
            }
        }
        return null;
    }

    protected Object resolveSimpleNumber(String exp, Map<String, ? extends Object> contextMap, LaContainer container) {
        if (LdiStringUtil.isNumber(exp)) {
            if (exp.length() > 9) {
                return Integer.valueOf(exp);
            } else {
                return Long.valueOf(exp);
            }
        }
        return null;
    }

    protected Object resolveExistsResource(String exp, Map<String, ? extends Object> contextMap, LaContainer container) {
        if (exp.startsWith(EXISTS_BEGIN) && exp.endsWith(EXISTS_END)) {
            final String path = exp.substring(EXISTS_BEGIN.length(), exp.lastIndexOf(EXISTS_END));
            if (!path.contains(SQ)) {
                return LdiResourceUtil.exists(path);
            }
        }
        return null;
    }

    protected Object resolveSimpleEqualEqual(String exp, Map<String, ? extends Object> contextMap, LaContainer container) {
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

    protected Object resolveProviderConfig(String exp, Map<String, ? extends Object> contextMap, LaContainer container) {
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
