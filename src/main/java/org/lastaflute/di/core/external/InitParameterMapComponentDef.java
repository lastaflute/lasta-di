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
package org.lastaflute.di.core.external;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ContainerConstants;
import org.lastaflute.di.core.meta.impl.SimpleComponentDef;

/**
 * InitParameter用の {@link ComponentDef}です。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class InitParameterMapComponentDef extends SimpleComponentDef {

    /**
     * {@link InitParameterMapComponentDef}を作成します。
     */
    public InitParameterMapComponentDef() {
        super(null, null, ContainerConstants.INIT_PARAM);
    }

    /**
     * @see org.lastaflute.di.core.ComponentDef#getComponent()
     */
    public Object getComponent() {
        return getContainer().getRoot().getExternalContext().getInitParameterMap();
    }
}