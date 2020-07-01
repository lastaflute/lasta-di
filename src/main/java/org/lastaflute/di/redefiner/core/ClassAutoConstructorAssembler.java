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
package org.lastaflute.di.redefiner.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.assembler.AutoConstructorAssembler;
import org.lastaflute.di.redefiner.LaContainerPreparer;
import org.lastaflute.di.redefiner.util.ClassBuilderUtils;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class ClassAutoConstructorAssembler extends AutoConstructorAssembler {

    public ClassAutoConstructorAssembler(ComponentDef componentDef) {
        super(componentDef);
    }

    @Override
    protected Object doAssemble() {
        Object component = null;
        final LaContainerPreparer preparer = ClassBuilderUtils.getPreparer(getComponentDef());
        if (preparer != null) {
            component = newInstance(preparer, getComponentDef());
        }
        if (component == null) {
            component = super.doAssemble();
        }
        return component;
    }

    protected Object newInstance(LaContainerPreparer preparer, ComponentDef componentDef) {
        final Class<? extends LaContainerPreparer> preparerType = preparer.getClass();
        final String componentName = componentDef.getComponentName();
        final String methodprefixNew = ClassS2ContainerBuilder.METHODPREFIX_NEW;
        final Method method = ClassBuilderUtils.findMethod(preparerType, componentName, methodprefixNew);
        if (method != null) {
            try {
                if (method.getParameterTypes().length == 0) {
                    return method.invoke(preparer, new Object[0]);
                } else {
                    // needs to create from concrete class when aspect component
                    // then the method that can accept concrete class is used
                    return method.invoke(preparer, new Object[] { componentDef.getConcreteClass() });
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Can't invoke method for instanciating component: " + method.getName(), e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can't invoke method for instanciating component: " + method.getName(), e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Can't invoke method for instanciating component: " + method.getName(), e);
            }
        } else {
            return null;
        }
    }
}
