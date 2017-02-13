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
package org.lastaflute.di.core;

import org.lastaflute.di.core.exception.CyclicReferenceComponentException;
import org.lastaflute.di.core.exception.TooManyRegistrationComponentException;
import org.lastaflute.di.core.expression.Expression;
import org.lastaflute.di.core.meta.ArgDefAware;
import org.lastaflute.di.core.meta.AspectDefAware;
import org.lastaflute.di.core.meta.AutoBindingDef;
import org.lastaflute.di.core.meta.DestroyMethodDefAware;
import org.lastaflute.di.core.meta.InitMethodDefAware;
import org.lastaflute.di.core.meta.InstanceDef;
import org.lastaflute.di.core.meta.InterTypeDefAware;
import org.lastaflute.di.core.meta.MetaDefAware;
import org.lastaflute.di.core.meta.PropertyDefAware;

/**
 * @author modified by jflute (originated in Seasar)
 */
public interface ComponentDef
        extends ArgDefAware, InterTypeDefAware, PropertyDefAware, InitMethodDefAware, DestroyMethodDefAware, AspectDefAware, MetaDefAware {

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    void init();

    // ===================================================================================
    //                                                                           Injection
    //                                                                           =========
    Object getComponent() throws TooManyRegistrationComponentException, CyclicReferenceComponentException;

    void injectDependency(Object outerComponent);

    // ===================================================================================
    //                                                                             Destroy
    //                                                                             =======
    void destroy();

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    Class<?> getComponentClass();

    void setComponentClass(Class<?> componentClass); // e.g. called by expression handling

    String getComponentName(); // defined type (no enhanced)

    void setComponentName(String componentName);

    Class<?> getConcreteClass();

    LaContainer getContainer();

    void setContainer(LaContainer container);

    Expression getExpression();

    void setExpression(Expression expression);

    AutoBindingDef getAutoBindingDef();

    void setAutoBindingDef(AutoBindingDef autoBindingDef);

    InstanceDef getInstanceDef();

    void setInstanceDef(InstanceDef instanceDef);

    boolean isExternalBinding();

    void setExternalBinding(boolean externalBinding);
}
