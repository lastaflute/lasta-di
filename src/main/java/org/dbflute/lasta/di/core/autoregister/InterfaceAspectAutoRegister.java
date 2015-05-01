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
package org.dbflute.lasta.di.core.autoregister;

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.LaContainer;
import org.dbflute.lasta.di.core.aop.Pointcut;
import org.dbflute.lasta.di.core.aop.frame.MethodInterceptor;
import org.dbflute.lasta.di.core.factory.AspectDefFactory;
import org.dbflute.lasta.di.core.meta.AspectDef;

/**
 * 特定のインターフェースを実装しているクラスに対してアスペクトを自動登録するクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class InterfaceAspectAutoRegister {

    /**
     * INIT_METHODアノテーションの定義です。
     */
    public static final String INIT_METHOD = "registerAll";

    private LaContainer container;

    private MethodInterceptor interceptor;

    private Class targetInterface;

    private Pointcut pointcut;

    /**
     * コンテナを設定します。
     * 
     * @param container
     */
    public void setContainer(LaContainer container) {
        this.container = container;
    }

    /**
     * インタセプタを設定します。
     * 
     * @param interceptor
     */
    public void setInterceptor(MethodInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * ターゲットインターフェースを設定します。このインターフェースを実装したクラスにアスペクトが設定されます。
     * 
     * @param targetInterface
     */
    public void setTargetInterface(Class targetInterface) {
        if (!targetInterface.isInterface()) {
            throw new IllegalArgumentException(targetInterface.getName());
        }
        this.targetInterface = targetInterface;
        this.pointcut = AspectDefFactory.createPointcut(targetInterface);
    }

    /**
     * 自動登録を行います。
     */
    public void registerAll() {
        for (int i = 0; i < container.getComponentDefSize(); ++i) {
            ComponentDef cd = container.getComponentDef(i);
            register(cd);
        }
    }

    /**
     * コンポーネントを登録します。
     * 
     * @param componentDef
     */
    protected void register(ComponentDef componentDef) {
        Class componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }
        if (!targetInterface.isAssignableFrom(componentClass)) {
            return;
        }
        registerInterceptor(componentDef);
    }

    /**
     * インターセプタを登録します。
     * 
     * @param componentDef
     */
    protected void registerInterceptor(ComponentDef componentDef) {
        AspectDef aspectDef = AspectDefFactory.createAspectDef(interceptor, pointcut);
        componentDef.addAspectDef(aspectDef);
    }
}