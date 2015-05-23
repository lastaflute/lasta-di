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
package org.lastaflute.di.redefiner.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.assembler.DefaultDestroyMethodAssembler;
import org.lastaflute.di.core.exception.IllegalMethodRuntimeException;
import org.lastaflute.di.core.meta.MethodDef;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.redefiner.LaContainerPreparer;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class ClassDestroyMethodAssembler extends DefaultDestroyMethodAssembler {

    public ClassDestroyMethodAssembler(ComponentDef componentDef) {
        super(componentDef);
    }

    @Override
    public void assemble(Object component) throws IllegalMethodRuntimeException {
        if (component == null) {
            return;
        }
        final BeanDesc beanDesc = getBeanDesc(component);
        final int size = getComponentDef().getDestroyMethodDefSize();
        for (int i = 0; i < size; ++i) {
            final MethodDef methodDef = getComponentDef().getDestroyMethodDef(i);
            final Method method = methodDef.getMethod();
            if (method != null && LaContainerPreparer.class.isAssignableFrom(method.getDeclaringClass())) {
                invokePreparerMethod(component, methodDef);
            } else {
                invoke(beanDesc, component, methodDef);
            }
        }
    }

    protected void invokePreparerMethod(Object component, MethodDef methodDef) {
        try {
            methodDef.getMethod().invoke(methodDef.getArgs()[0], new Object[] { component });
        } catch (IllegalArgumentException ex) {
            throw new IllegalMethodRuntimeException(getComponentClass(component), getComponentDef().getComponentName(), ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalMethodRuntimeException(getComponentClass(component), getComponentDef().getComponentName(), ex);
        } catch (InvocationTargetException ex) {
            throw new IllegalMethodRuntimeException(getComponentClass(component), getComponentDef().getComponentName(), ex);
        }
    }
}
