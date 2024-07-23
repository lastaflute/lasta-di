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
package org.lastaflute.di.core.meta;

import java.lang.reflect.Method;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.expression.Expression;

/**
 * @author modified by jflute (originated in Seasar)
 * @author azusa
 * 
 */
public interface MethodDef extends ArgDefAware {

    /**
     * @return 
     */
    public Method getMethod();

    /**
     * @return 
     */
    public String getMethodName();

    /**
     * @return 
     */
    public Object[] getArgs();

    /**
     * @return 
     */
    public LaContainer getContainer();

    /**
     * @param container
     */
    public void setContainer(LaContainer container);

    /**
     * @return 
     */
    public Expression getExpression();

    /**
     * @param expression
     */
    public void setExpression(Expression expression);
}
