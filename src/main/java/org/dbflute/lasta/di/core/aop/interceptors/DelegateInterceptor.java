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
package org.dbflute.lasta.di.core.aop.interceptors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.dbflute.lasta.di.core.aop.frame.MethodInterceptor;
import org.dbflute.lasta.di.core.aop.frame.MethodInvocation;
import org.dbflute.lasta.di.exception.EmptyRuntimeException;
import org.dbflute.lasta.di.helper.beans.BeanDesc;
import org.dbflute.lasta.di.helper.beans.exception.MethodNotFoundRuntimeException;
import org.dbflute.lasta.di.helper.beans.factory.BeanDescFactory;
import org.dbflute.lasta.di.util.LdiMethodUtil;

/**
 * あるオブジェクトへの呼び出しを別のオブジェクトに転送する{@link MethodInterceptor}です。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class DelegateInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 3613140488663554089L;

    private Object target;

    private BeanDesc beanDesc;

    private Map methodNameMap = new HashMap();

    /**
     * {@link DelegateInterceptor}を作成します。
     */
    public DelegateInterceptor() {
    }

    /**
     * {@link DelegateInterceptor}を作成します。
     * 
     * @param target
     */
    public DelegateInterceptor(Object target) {
        setTarget(target);
    }

    /**
     * ターゲットのオブジェクトを返します。
     * 
     * @return target
     */
    public Object getTarget() {
        return target;
    }

    /**
     * ターゲットのオブジェクトを設定します。
     * 
     * @param target
     */
    public void setTarget(Object target) {
        this.target = target;
        beanDesc = BeanDescFactory.getBeanDesc(target.getClass());
    }

    /**
     * 転送するメソッドの組を追加します。
     * 
     * @param methodName
     * @param targetMethodName
     */
    public void addMethodNameMap(String methodName, String targetMethodName) {
        methodNameMap.put(methodName, targetMethodName);
    }

    /**
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (target == null) {
            throw new EmptyRuntimeException("target");
        }
        Method method = invocation.getMethod();
        String methodName = method.getName();
        if (methodNameMap.containsKey(methodName)) {
            methodName = (String) methodNameMap.get(methodName);
        }
        if (!LdiMethodUtil.isAbstract(method)) {
            return invocation.proceed();
        } else if (beanDesc.hasMethod(methodName)) {
            return beanDesc.invoke(target, methodName, invocation.getArguments());
        } else {
            throw new MethodNotFoundRuntimeException(getTargetClass(invocation), methodName, invocation.getArguments());
        }
    }
}