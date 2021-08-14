/*
 * Copyright 2015-2021 the original author or authors.
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

import java.util.Collections;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.expression.Expression;
import org.lastaflute.di.core.meta.ArgDef;
import org.lastaflute.di.core.meta.MetaDef;
import org.lastaflute.di.core.util.MetaDefSupport;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ArgDefImpl implements ArgDef {

    private Object value;
    private LaContainer container;
    private Expression expression;
    private ComponentDef childComponentDef;
    private final MetaDefSupport metaDefSupport = new MetaDefSupport();

    public ArgDefImpl() {
    }

    public ArgDefImpl(Object value) {
        setValue(value);
    }

    public Object getValue(Class<?> conversionType) {
        if (expression != null) {
            return expression.evaluate(Collections.emptyMap(), container, conversionType);
        }
        if (childComponentDef != null) {
            return childComponentDef.getComponent();
        }
        return value;
    }

    public final void setValue(Object value) {
        this.value = value;
    }

    public boolean isValueGettable() {
        return value != null || childComponentDef != null || expression != null;
    }

    /**
     * @see org.lastaflute.di.core.meta.ArgDef#getContainer()
     */
    public final LaContainer getContainer() {
        return container;
    }

    /**
     * @see org.lastaflute.di.core.meta.ArgDef#setContainer(org.lastaflute.di.core.LaContainer)
     */
    public final void setContainer(LaContainer container) {
        this.container = container;
        if (childComponentDef != null) {
            childComponentDef.setContainer(container);
        }
        metaDefSupport.setContainer(container);
    }

    /**
     * @see org.lastaflute.di.core.meta.ArgDef#getExpression()
     */
    public final Expression getExpression() {
        return expression;
    }

    /**
     * @see org.lastaflute.di.core.meta.ArgDef#setExpression(Expression)
     */
    public final void setExpression(Expression expression) {
        this.expression = expression;
    }

    /**
     * @see org.lastaflute.di.core.meta.ArgDef#setChildComponentDef(org.lastaflute.di.core.ComponentDef)
     */
    public final void setChildComponentDef(ComponentDef componentDef) {
        if (container != null) {
            componentDef.setContainer(container);
        }
        childComponentDef = componentDef;
    }

    public void addMetaDef(MetaDef metaDef) {
        metaDefSupport.addMetaDef(metaDef);
    }

    public MetaDef getMetaDef(int index) {
        return metaDefSupport.getMetaDef(index);
    }

    public MetaDef getMetaDef(String name) {
        return metaDefSupport.getMetaDef(name);
    }

    public MetaDef[] getMetaDefs(String name) {
        return metaDefSupport.getMetaDefs(name);
    }

    public int getMetaDefSize() {
        return metaDefSupport.getMetaDefSize();
    }
}
