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
package org.lastaflute.di.core.expression.dwarf;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.exception.ExpressionClassCreateFailureException;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;
import org.lastaflute.di.util.LdiSrl;

/**
 * @author jflute
 * @since 0.6.3 (2015/09/29 Tuesday)
 */
public class ExpressionCastResolver {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String CAST_INT_ARRAY = "(int[])";
    public static final String CAST_STRING_ARRAY = "(String[])";
    public static final String CAST_SET = "(Set)";

    public static class CastResolved { // marker class

        protected final String filteredExp;
        protected final Class<?> resolvedType;

        public CastResolved(String filteredExp, Class<?> resolvedType) {
            this.filteredExp = filteredExp;
            this.resolvedType = resolvedType;
        }

        public String getFilteredExp() {
            return filteredExp;
        }

        public Class<?> getResolvedType() {
            return resolvedType;
        }
    }

    // ===================================================================================
    //                                                                        Resolve Cast
    //                                                                        ============
    public CastResolved resolveCast(String exp, Class<?> conversionType) { // null allowed
        return doResolveCast(exp.trim());
    }

    protected CastResolved doResolveCast(String exp) {
        final String filteredExp;
        final Class<?> resolvedType;
        if (exp.startsWith(CAST_INT_ARRAY)) {
            filteredExp = exp.substring(CAST_INT_ARRAY.length()).trim();
            resolvedType = int[].class;
        } else if (exp.startsWith(CAST_STRING_ARRAY)) {
            filteredExp = exp.substring(CAST_STRING_ARRAY.length()).trim();
            resolvedType = String[].class;
        } else if (exp.startsWith(CAST_SET)) {
            filteredExp = exp.substring(CAST_SET.length()).trim();
            resolvedType = Set.class;
            // migrated to "var result" way (DBFlute Engine uses) by jflute (2021/08/31)
            //} else if (exp.startsWith("{") && exp.endsWith("}")) {
            //    filteredExp = "[" + exp + "]";
            //    resolvedType = MarkedMap.class;
        } else { // for a little performance, as no instance
            return null;
        }
        return new CastResolved(filteredExp, resolvedType);
    }

    // ===================================================================================
    //                                                                        Convert List
    //                                                                        ============
    public Object convertListTo(String exp, LaContainer container, Class<?> resultType, List<Object> challengeList) { // not null
        if (int[].class.isAssignableFrom(resultType)) { // e.g. (int[])[1,2]
            final int[] intAry = new int[challengeList.size()];
            int index = 0;
            try {
                for (Object element : challengeList) {
                    if (element == null) {
                        throw new IllegalStateException("Cannot handle null element in array: index=" + index);
                    }
                    // if rhino, [1,2] then [1.0, 2] (why?), so remove it if exists
                    final String numExp = LdiSrl.substringLastFront(element.toString(), ".0");
                    intAry[index] = Integer.parseInt(numExp);
                    ++index;
                }
            } catch (RuntimeException e) {
                throwExpressionCannotConvertException(exp, container, resultType, index, e);
            }
            return intAry;
        } else if (String[].class.isAssignableFrom(resultType)) { // e.g. (String[])["sea","land"]
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
                throwExpressionCannotConvertException(exp, container, resultType, index, e);
            }
            return strAry;
        } else if (Set.class.isAssignableFrom(resultType)) { // e.g. (Set)["sea","land"]
            return new LinkedHashSet<Object>(challengeList);
            // also here, migrated to "var result" way (DBFlute Engine uses) by jflute (2021/08/31)
            //} else if (MarkedMap.class.isAssignableFrom(resultType)) { // e.g. {"sea":"land"} as [{"sea":"land"}]
            //    @SuppressWarnings("unchecked")
            //    final Map<Object, Object> wrappedMap = (Map<Object, Object>) challengeList.get(0);
            //    return new LinkedHashMap<Object, Object>(wrappedMap); // convert to normal map
        } else { // e.g. [1,2]
            return challengeList;
        }
    }

    protected void throwExpressionCannotConvertException(String exp, LaContainer container, Class<?> conversionType, Integer index,
            RuntimeException cause) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to convert the value to the type in the expression.");
        br.addItem("Di XML");
        br.addElement(container.getPath());
        br.addItem("Expression");
        br.addElement(exp);
        br.addItem("Conversion Type");
        br.addElement(conversionType);
        if (index != null) {
            br.addItem("Array Index");
            br.addElement(index);
        }
        final String msg = br.buildExceptionMessage();
        throw new ExpressionClassCreateFailureException(msg, cause);
    }
}
