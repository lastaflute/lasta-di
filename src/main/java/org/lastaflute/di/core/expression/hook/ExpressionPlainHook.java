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
import org.lastaflute.di.util.LdiResourceUtil;

/**
 * @author jflute
 */
public interface ExpressionPlainHook {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    String DQ = "\"";
    String SQ = "'";
    String NUM = "0123456789";
    String EXISTS_BEGIN = LdiResourceUtil.class.getName() + ".exists('";
    String EXISTS_END = "')";
    String TYPE_BEGIN = "@"; // compatible with OGNL e.g. @org.dbflute.Entity@class
    String TYPE_END = "@class"; // me too
    String METHOD_MARK = "()";
    String PROVIDER_GET = "provider.config().get";

    Object hookPlainly(String exp, Map<String, ? extends Object> contextMap, LaContainer container);
}
