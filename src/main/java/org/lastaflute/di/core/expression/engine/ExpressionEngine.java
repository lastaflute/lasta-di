/*
 * Copyright 2015-2020 the original author or authors.
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

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author jflute
 */
public interface ExpressionEngine {

    Object parseExpression(String source);

    Object evaluate(Object exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType);

    String resolveStaticMethodReference(Class<?> refType, String methodName);

    // for engine that doesn't have context handling e.g. JavaScript
    static String resolveExpressionVariableSimply(String exp, Map<String, ? extends Object> contextMap) {
        final String variableMark = "#";
        if (!exp.contains(variableMark)) {
            return exp; // almost here
        }
        String filteredExp = exp; // has at least one '#' here
        for (Entry<String, ? extends Object> entry : contextMap.entrySet()) { // e.g. #SMART => 'cool'
            filteredExp = LdiStringUtil.replace(filteredExp, variableMark + entry.getKey(), "'" + entry.getValue() + "'");
        }
        return filteredExp;
    }
}
