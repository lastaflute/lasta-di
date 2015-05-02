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
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.factory.AspectDefFactory;
import org.lastaflute.di.core.meta.AspectDef;

/**
 * アスペクトを自動登録するためのクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class AspectAutoRegister extends AbstractComponentTargetAutoRegister {

    private MethodInterceptor interceptor;

    private String pointcut;

    /**
     * インターセプタを設定します。
     * 
     * @param interceptor
     */
    public void setInterceptor(MethodInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * ポイントカットを設定します。
     * 
     * @param pointcut
     */
    public void setPointcut(String pointcut) {
        this.pointcut = pointcut;
    }

    protected void register(ComponentDef componentDef) {
        AspectDef aspectDef = AspectDefFactory.createAspectDef(interceptor, pointcut);
        componentDef.addAspectDef(aspectDef);
    }
}