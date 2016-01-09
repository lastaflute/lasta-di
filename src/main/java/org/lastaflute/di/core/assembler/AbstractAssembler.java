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

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.exception.ComponentNotFoundException;
import org.lastaflute.di.core.util.BindingUtil;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.log.LaLogger;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractAssembler {

    private static LaLogger logger = LaLogger.getLogger(AbstractAssembler.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final ComponentDef componentDef;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractAssembler(ComponentDef componentDef) {
        this.componentDef = componentDef;
    }

    // ===================================================================================
    //                                                                   Assembly Resource
    //                                                                   =================
    protected BeanDesc getBeanDesc(Object component) {
        return BindingUtil.getBeanDesc(getComponentDef(), component);
    }

    protected Class<?> getComponentClass(Object component) {
        return BindingUtil.getComponentClass(getComponentDef(), component);
    }

    protected Object[] getArgs(Class<?>[] argTypes) {
        final Object[] args = new Object[argTypes.length];
        for (int i = 0; i < argTypes.length; ++i) {
            try {
                args[i] = getComponentDef().getContainer().getComponent(argTypes[i]);
            } catch (ComponentNotFoundException e) {
                logger.log("WSSR0007", new Object[] { getComponentDef().getComponentClass().getName(), e.getComponentKey() });
                args[i] = null;
            }
        }
        return args;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected final ComponentDef getComponentDef() {
        return componentDef;
    }
}