/*
 * Copyright 2015-2018 the original author or authors.
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
package org.lastaflute.di.core.meta.impl;

import java.lang.reflect.Method;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.expression.Expression;
import org.lastaflute.di.core.meta.ArgDef;
import org.lastaflute.di.core.meta.MethodDef;
import org.lastaflute.di.core.util.ArgDefSupport;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class MethodDefImpl implements MethodDef {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Method method;
    private String methodName;
    private final ArgDefSupport argDefSupport = new ArgDefSupport();
    private LaContainer container;
    private Expression expression;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public MethodDefImpl(Method method) {
        this.method = method;
    }

    public MethodDefImpl(String methodName) { // e.g. postConstruct name="addSea"
        this.methodName = methodName;
    }

    // ===================================================================================
    //                                                                           Arguments
    //                                                                           =========
    public void addArgDef(ArgDef argDef) {
        argDefSupport.addArgDef(argDef);
    }

    public int getArgDefSize() {
        return argDefSupport.getArgDefSize();
    }

    public ArgDef getArgDef(int index) {
        return argDefSupport.getArgDef(index);
    }

    public Object[] getArgs() {
        final int argDefSize = getArgDefSize();
        final Object[] args = new Object[argDefSize];
        final Class<?>[] parameterTypes = extractParameterTypes(argDefSize); // null allowed
        for (int i = 0; i < argDefSize; ++i) {
            final Class<?> conversionType = parameterTypes != null ? parameterTypes[i] : Object.class;
            args[i] = getArgDef(i).getValue(conversionType);
        }
        return args;
    }

    protected Class<?>[] extractParameterTypes(final int argDefSize) {
        Class<?>[] parameterTypes = method != null ? method.getParameterTypes() : null;
        if (parameterTypes != null && parameterTypes.length != argDefSize) { // no way, just in case
            parameterTypes = null;
        }
        return parameterTypes;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Method getMethod() {
        return method;
    }

    public String getMethodName() {
        return methodName;
    }

    public LaContainer getContainer() {
        return container;
    }

    public void setContainer(LaContainer container) {
        this.container = container;
        argDefSupport.setContainer(container);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
