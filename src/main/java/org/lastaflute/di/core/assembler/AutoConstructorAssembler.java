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
package org.lastaflute.di.core.assembler;

import java.lang.reflect.Constructor;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.util.BindingUtil;
import org.lastaflute.di.util.LdiConstructorUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AutoConstructorAssembler extends AbstractConstructorAssembler {

    public AutoConstructorAssembler(ComponentDef componentDef) {
        super(componentDef);
    }

    @Override
    protected Object doAssemble() {
        final Constructor<?> constructor = getSuitableConstructor();
        if (constructor == null) {
            return assembleDefault();
        }
        final Object[] args = getArgs(constructor.getParameterTypes());
        return LdiConstructorUtil.newInstance(constructor, args);
    }

    protected Constructor<?> getSuitableConstructor() {
        final Constructor<?>[] constructors = getComponentDef().getConcreteClass().getConstructors();
        Constructor<?> constructor = null;
        int argSize = -1;
        for (int i = 0; i < constructors.length; ++i) {
            final int tempArgSize = constructors[i].getParameterTypes().length;
            if (tempArgSize == 0) {
                return null;
            }
            if (tempArgSize > argSize && BindingUtil.isPropertyAutoBindable(constructors[i].getParameterTypes())) {
                constructor = constructors[i];
                argSize = tempArgSize;
            }
        }
        return constructor;
    }
}
