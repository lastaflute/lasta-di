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
package org.lastaflute.di.core.deployer;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.assembler.AssemblerFactory;
import org.lastaflute.di.core.assembler.ConstructorAssembler;
import org.lastaflute.di.core.assembler.MethodAssembler;
import org.lastaflute.di.core.assembler.PropertyAssembler;
import org.lastaflute.di.core.meta.AutoBindingDef;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractComponentDeployer implements ComponentDeployer {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private ComponentDef componentDef;
    private ConstructorAssembler constructorAssembler;
    private PropertyAssembler propertyAssembler;
    private MethodAssembler initMethodAssembler;
    private MethodAssembler destroyMethodAssembler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractComponentDeployer(ComponentDef componentDef) {
        this.componentDef = componentDef;
        setupAssembler();
    }

    protected void setupAssembler() {
        final AutoBindingDef autoBindingDef = componentDef.getAutoBindingDef();
        constructorAssembler = autoBindingDef.createConstructorAssembler(componentDef);
        propertyAssembler = autoBindingDef.createPropertyAssembler(componentDef);
        initMethodAssembler = AssemblerFactory.createInitMethodAssembler(componentDef);
        destroyMethodAssembler = AssemblerFactory.createDestroyMethodAssembler(componentDef);
    }

    // ===================================================================================
    //                                                                      Component Name
    //                                                                      ==============
    protected String getComponentName() {
        String componentName = componentDef.getComponentName();
        if (componentName == null) {
            componentName = LdiClassUtil.getShortClassName(componentDef.getComponentClass());
            componentName = LdiStringUtil.decapitalize(componentName);
        }
        return componentName;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected final ComponentDef getComponentDef() {
        return componentDef;
    }

    protected final ConstructorAssembler getConstructorAssembler() {
        return constructorAssembler;
    }

    protected final PropertyAssembler getPropertyAssembler() {
        return propertyAssembler;
    }

    protected final MethodAssembler getInitMethodAssembler() {
        return initMethodAssembler;
    }

    protected final MethodAssembler getDestroyMethodAssembler() {
        return destroyMethodAssembler;
    }
}
