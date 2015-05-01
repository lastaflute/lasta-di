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

import java.util.Collections;

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.LaContainer;
import org.dbflute.lasta.di.core.expression.Expression;
import org.dbflute.lasta.di.core.meta.ArgDef;
import org.dbflute.lasta.di.core.meta.MetaDef;
import org.dbflute.lasta.di.core.util.MetaDefSupport;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ArgDefImpl implements ArgDef {

    private Object value;
    private LaContainer container;
    private Expression expression;
    private ComponentDef childComponentDef;
    private MetaDefSupport metaDefSupport = new MetaDefSupport();

    public ArgDefImpl() {
    }

    public ArgDefImpl(Object value) {
        setValue(value);
    }

    public final Object getValue() {
        if (expression != null) {
            return expression.evaluate(Collections.emptyMap(), container);
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
     * @see org.dbflute.lasta.di.core.meta.ArgDef#getContainer()
     */
    public final LaContainer getContainer() {
        return container;
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.ArgDef#setContainer(org.dbflute.lasta.di.core.LaContainer)
     */
    public final void setContainer(LaContainer container) {
        this.container = container;
        if (childComponentDef != null) {
            childComponentDef.setContainer(container);
        }
        metaDefSupport.setContainer(container);
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.ArgDef#getExpression()
     */
    public final Expression getExpression() {
        return expression;
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.ArgDef#setExpression(Expression)
     */
    public final void setExpression(Expression expression) {
        this.expression = expression;
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.ArgDef#setChildComponentDef(org.dbflute.lasta.di.core.ComponentDef)
     */
    public final void setChildComponentDef(ComponentDef componentDef) {
        if (container != null) {
            componentDef.setContainer(container);
        }
        childComponentDef = componentDef;
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MetaDefAware#addMetaDef(org.dbflute.lasta.di.core.meta.MetaDef)
     */
    public void addMetaDef(MetaDef metaDef) {
        metaDefSupport.addMetaDef(metaDef);
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MetaDefAware#getMetaDef(int)
     */
    public MetaDef getMetaDef(int index) {
        return metaDefSupport.getMetaDef(index);
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MetaDefAware#getMetaDef(java.lang.String)
     */
    public MetaDef getMetaDef(String name) {
        return metaDefSupport.getMetaDef(name);
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MetaDefAware#getMetaDefs(java.lang.String)
     */
    public MetaDef[] getMetaDefs(String name) {
        return metaDefSupport.getMetaDefs(name);
    }

    /**
     * @see org.dbflute.lasta.di.core.meta.MetaDefAware#getMetaDefSize()
     */
    public int getMetaDefSize() {
        return metaDefSupport.getMetaDefSize();
    }
}
