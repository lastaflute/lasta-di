/*
 * Copyright 2015-2016 the original author or authors.
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
package org.lastaflute.di.core.assembler;

import java.lang.reflect.Constructor;
import java.util.Collections;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.exception.ClassUnmatchRuntimeException;
import org.lastaflute.di.core.exception.ComponentNotFoundException;
import org.lastaflute.di.core.exception.IllegalConstructorRuntimeException;
import org.lastaflute.di.core.expression.Expression;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.factory.BeanDescFactory;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiConstructorUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractConstructorAssembler extends AbstractAssembler implements ConstructorAssembler {

    public AbstractConstructorAssembler(ComponentDef componentDef) {
        super(componentDef);
    }

    @Override
    public Object assemble() {
        if (getComponentDef().getExpression() != null) {
            return assembleExpression();
        }
        if (getComponentDef().getArgDefSize() > 0) {
            return assembleManual();
        }
        return doAssemble();
    }

    protected Object assembleExpression() throws ClassUnmatchRuntimeException {
        final ComponentDef cd = getComponentDef();
        final LaContainer container = cd.getContainer();
        final Expression expression = cd.getExpression();
        final Object component = expression.evaluate(Collections.emptyMap(), container, Object.class); // #expression_point
        final Class<?> componentClass = cd.getComponentClass();
        if (componentClass != null && !componentClass.isInstance(component)) {
            throw new ClassUnmatchRuntimeException(componentClass, component != null ? component.getClass() : null);
        }
        if (componentClass == null) { // expression lazy type
            cd.setComponentClass(component.getClass()); // related to sub-class type
            cd.getContainer().registerByClass(cd); // to get the component by type
        }
        return component;
    }

    protected abstract Object doAssemble();

    protected Object assembleManual() {
        final Object[] args = new Object[getComponentDef().getArgDefSize()];
        for (int i = 0; i < args.length; ++i) {
            try {
                args[i] = getComponentDef().getArgDef(i).getValue(Object.class);
            } catch (ComponentNotFoundException cause) {
                throw new IllegalConstructorRuntimeException(getComponentDef().getComponentClass(), cause);
            }
        }
        final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(getComponentDef().getConcreteClass());
        return beanDesc.newInstance(args);
    }

    protected Object assembleDefault() {
        final Constructor<?> constructor = LdiClassUtil.getConstructor(getComponentDef().getConcreteClass(), null);
        return LdiConstructorUtil.newInstance(constructor, null);
    }
}