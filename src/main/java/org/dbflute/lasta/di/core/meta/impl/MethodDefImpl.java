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
package org.dbflute.lasta.di.core.meta.impl;

import java.lang.reflect.Method;

import org.dbflute.lasta.di.core.LaContainer;
import org.dbflute.lasta.di.core.expression.Expression;
import org.dbflute.lasta.di.core.meta.ArgDef;
import org.dbflute.lasta.di.core.meta.MethodDef;
import org.dbflute.lasta.di.core.util.ArgDefSupport;

/**
 * {@link MethodDef}の抽象クラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public abstract class MethodDefImpl implements MethodDef {

    private Method method;

    private String methodName;

    private ArgDefSupport argDefSupport = new ArgDefSupport();

    private LaContainer container;

    private Expression expression;

    /**
     * {@link MethodDefImpl}を作成します。
     */
    public MethodDefImpl() {
    }

    /**
     * {@link MethodDefImpl}を作成します。
     * 
     * @param method
     */
    public MethodDefImpl(Method method) {
        this.method = method;
    }

    /**
     * {@link MethodDefImpl}を作成します。
     * 
     * @param methodName
     */
    public MethodDefImpl(String methodName) {
        this.methodName = methodName;
    }

    public Method getMethod() {
        return method;
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MethodDef#getMethodName()
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MethodDef#addArgDef(org.dbflute.lasta.di.core.meta.ArgDef)
     */
    public void addArgDef(ArgDef argDef) {
        argDefSupport.addArgDef(argDef);
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.ArgDefAware#getArgDefSize()
     */
    public int getArgDefSize() {
        return argDefSupport.getArgDefSize();
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.ArgDefAware#getArgDef(int)
     */
    public ArgDef getArgDef(int index) {
        return argDefSupport.getArgDef(index);
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MethodDef#getArgs()
     */
    public Object[] getArgs() {
        Object[] args = new Object[getArgDefSize()];
        for (int i = 0; i < getArgDefSize(); ++i) {
            args[i] = getArgDef(i).getValue();
        }
        return args;
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MethodDef#getContainer()
     */
    public LaContainer getContainer() {
        return container;
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MethodDef#setContainer(org.dbflute.lasta.di.core.LaContainer)
     */
    public void setContainer(LaContainer container) {
        this.container = container;
        argDefSupport.setContainer(container);
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MethodDef#getExpression()
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MethodDef#setExpression(Expression)
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
