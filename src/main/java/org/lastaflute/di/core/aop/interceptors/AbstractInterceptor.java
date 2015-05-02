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
package org.lastaflute.di.core.aop.interceptors;

import java.io.Serializable;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ContainerConstants;
import org.lastaflute.di.core.aop.Aspect;
import org.lastaflute.di.core.aop.LaMethodInvocation;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.aop.frame.MethodInvocation;
import org.lastaflute.di.core.aop.impl.AspectImpl;
import org.lastaflute.di.core.aop.impl.PointcutImpl;
import org.lastaflute.di.core.aop.proxy.AopProxy;

/**
 * {@link MethodInterceptor}を拡張するための抽象クラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public abstract class AbstractInterceptor implements MethodInterceptor, Serializable {

    static final long serialVersionUID = 0L;

    /**
     * {@link AopProxy}を作成します。
     * 
     * @param proxyClass
     * @return
     */
    public Object createProxy(Class proxyClass) {
        Aspect aspect = new AspectImpl(this, new PointcutImpl(new String[] { ".*" }));
        return new AopProxy(proxyClass, new Aspect[] { aspect }).create();
    }

    /**
     * ターゲットクラスを返します。
     * 
     * @param invocation
     *            メソッド呼び出し
     * @return ターゲットクラス
     */
    protected Class getTargetClass(MethodInvocation invocation) {
        return ((LaMethodInvocation) invocation).getTargetClass();
    }

    /**
     * コンポーネント定義を返します。
     * 
     * @param invocation
     *            メソッド呼び出し
     * @return コンポーネント定義
     */
    protected ComponentDef getComponentDef(MethodInvocation invocation) {
        if (invocation instanceof LaMethodInvocation) {
            LaMethodInvocation impl = (LaMethodInvocation) invocation;
            return (ComponentDef) impl.getParameter(ContainerConstants.COMPONENT_DEF_NAME);
        }
        return null;
    }
}