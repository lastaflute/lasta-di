/*
 * Copyright 2015-2017 the original author or authors.
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
package org.lastaflute.di.core.factory.defbuilder;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.annotation.AutoBindingType;
import org.lastaflute.di.core.annotation.Component;
import org.lastaflute.di.core.annotation.InstanceType;
import org.lastaflute.di.core.assembler.AutoBindingDefFactory;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandler;
import org.lastaflute.di.core.meta.AutoBindingDef;
import org.lastaflute.di.core.meta.InstanceDef;
import org.lastaflute.di.core.meta.impl.ComponentDefImpl;
import org.lastaflute.di.core.meta.impl.InstanceDefFactory;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class PojoComponentDefBuilder implements ComponentDefBuilder {

    public PojoComponentDefBuilder() {
    }

    public ComponentDef createComponentDef(final AnnotationHandler annotationHandler, final Class<?> componentClass,
            final InstanceDef defaultInstanceDef, final AutoBindingDef defaultAutoBindingDef, final boolean defaultExternalBinding) {
        final Component component = componentClass.getAnnotation(Component.class);
        if (component == null) {
            return null;
        }
        final ComponentDef componentDef = new ComponentDefImpl(componentClass);
        if (!LdiStringUtil.isEmpty(component.name())) {
            componentDef.setComponentName(component.name());
        }
        componentDef.setInstanceDef(getInstanceDef(component, defaultInstanceDef));
        componentDef.setAutoBindingDef(getAutoBindingDef(component, defaultAutoBindingDef));
        componentDef.setExternalBinding(component.externalBinding());
        return componentDef;
    }

    protected InstanceDef getInstanceDef(final Component component, final InstanceDef defaultInstanceDef) {
        final InstanceType instanceType = component.instance();
        if (instanceType == null || LdiStringUtil.isEmpty(instanceType.getName())) {
            return defaultInstanceDef;
        }
        return InstanceDefFactory.getInstanceDef(instanceType.getName());
    }

    protected AutoBindingDef getAutoBindingDef(final Component component, final AutoBindingDef defaultAutoBindingDef) {
        final AutoBindingType autoBindingType = component.autoBinding();
        if (autoBindingType == null || LdiStringUtil.isEmpty(autoBindingType.getName())) {
            return defaultAutoBindingDef;
        }
        return AutoBindingDefFactory.getAutoBindingDef(autoBindingType.getName());
    }
}
