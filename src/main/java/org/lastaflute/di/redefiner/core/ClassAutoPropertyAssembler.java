/*
 * Copyright 2015-2022 the original author or authors.
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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.assembler.AbstractPropertyAssembler;
import org.lastaflute.di.core.assembler.BindingTypeDefFactory;
import org.lastaflute.di.core.meta.AccessTypeDef;
import org.lastaflute.di.core.meta.PropertyDef;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.beans.exception.BeanIllegalPropertyException;
import org.lastaflute.di.redefiner.LaContainerPreparer;
import org.lastaflute.di.redefiner.annotation.ManualBindingProperties;
import org.lastaflute.di.redefiner.util.ClassBuilderUtils;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class ClassAutoPropertyAssembler extends AbstractPropertyAssembler {

    public ClassAutoPropertyAssembler(ComponentDef componentDef) {
        super(componentDef);
    }

    public void assemble(Object component) throws BeanIllegalPropertyException {
        if (component == null) {
            return;
        }

        final BeanDesc beanDesc = getBeanDesc(component);
        final ComponentDef cd = getComponentDef();
        final int defSize = cd.getPropertyDefSize();
        final Set<String> names = new HashSet<String>();
        for (int i = 0; i < defSize; ++i) {
            final PropertyDef propDef = cd.getPropertyDef(i);
            final AccessTypeDef accessTypeDef = propDef.getAccessTypeDef();
            accessTypeDef.bind(cd, propDef, component);
            final String propName = propDef.getPropertyName();
            names.add(propName);
        }

        final LaContainerPreparer preparer = ClassBuilderUtils.getPreparer(getComponentDef());
        if (preparer != null) {
            names.addAll(Arrays.asList(getManualBindingProperties(preparer, getComponentDef().getComponentName())));
        }

        if (cd.isExternalBinding()) {
            bindExternally(beanDesc, cd, component, names);
        }
        final int descSize = beanDesc.getPropertyDescSize();
        for (int i = 0; i < descSize; ++i) {
            final PropertyDesc propDesc = beanDesc.getPropertyDesc(i);
            final String propName = propDesc.getPropertyName();
            if (!names.contains(propName)) {
                BindingTypeDefFactory.SHOULD.bind(getComponentDef(), null, propDesc, component);
            }
        }
    }

    protected String[] getManualBindingProperties(LaContainerPreparer preparer, String componentName) {
        final Method method = ClassBuilderUtils.findMethod(preparer.getClass(), componentName, ClassS2ContainerBuilder.METHODPREFIX_DEFINE);
        if (method != null) {
            final ManualBindingProperties annotation = method.getAnnotation(ManualBindingProperties.class);
            if (annotation != null) {
                return annotation.value();
            }
        }
        return new String[0];
    }
}
