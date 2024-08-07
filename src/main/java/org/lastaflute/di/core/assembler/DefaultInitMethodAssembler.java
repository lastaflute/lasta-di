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
package org.lastaflute.di.core.assembler;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.exception.IllegalMethodRuntimeException;
import org.lastaflute.di.core.meta.MethodDef;
import org.lastaflute.di.helper.beans.BeanDesc;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class DefaultInitMethodAssembler extends AbstractMethodAssembler {

    public DefaultInitMethodAssembler(ComponentDef componentDef) {
        super(componentDef);
    }

    public void assemble(Object component) throws IllegalMethodRuntimeException {
        if (component == null) {
            return;
        }
        final BeanDesc beanDesc = getBeanDesc(component);
        final int size = getComponentDef().getInitMethodDefSize();
        for (int i = 0; i < size; ++i) {
            final MethodDef methodDef = getComponentDef().getInitMethodDef(i);
            invoke(beanDesc, component, methodDef);
        }
    }
}
