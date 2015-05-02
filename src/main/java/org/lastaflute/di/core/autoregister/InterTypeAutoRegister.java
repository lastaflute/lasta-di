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
package org.lastaflute.di.core.autoregister;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.aop.InterType;
import org.lastaflute.di.core.meta.impl.InterTypeDefImpl;

/**
 * インタータイプの自動登録を行うクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class InterTypeAutoRegister extends AbstractComponentTargetAutoRegister {

    /**
     * {@link InterType}です。
     */
    protected InterType interType;

    /**
     * インタータイプを設定します。
     * 
     * @param interType
     */
    public void setInterType(final InterType interType) {
        this.interType = interType;
    }

    protected void register(final ComponentDef componentDef) {
        componentDef.addInterTypeDef(new InterTypeDefImpl(interType));
    }
}