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
package org.lastaflute.di.core.aop.proxy;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lastaflute.di.core.aop.Aspect;
import org.lastaflute.di.core.aop.InterType;
import org.lastaflute.di.core.aop.Pointcut;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.aop.impl.PointcutImpl;
import org.lastaflute.di.core.aop.javassist.AspectWeaver;
import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.helper.log.SLogger;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiConstructorUtil;
import org.lastaflute.di.util.LdiMethodUtil;

/**
 * {@link Aspect}を織り込んだクラスを作成するクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class AopProxy implements Serializable {

    static final long serialVersionUID = 0L;

    private static SLogger logger = SLogger.getLogger(AopProxy.class);

    private final Class targetClass;

    private final Class enhancedClass;

    private final Pointcut defaultPointcut;

    private final AspectWeaver weaver;

    /**
     * {@link AopProxy}を作成します。
     * 
     * @param targetClass
     * @param aspects
     */
    public AopProxy(final Class targetClass, final Aspect[] aspects) {
        this(targetClass, aspects, null, null);
    }

    /**
     * {@link AopProxy}を作成します。
     * 
     * @param targetClass
     * @param aspects
     * @param interTypes
     */
    public AopProxy(final Class targetClass, final Aspect[] aspects, final InterType[] interTypes) {
        this(targetClass, aspects, interTypes, null);
    }

    /**
     * {@link AopProxy}を作成します。
     * 
     * @param targetClass
     * @param aspects
     * @param parameters
     */
    public AopProxy(final Class targetClass, final Aspect[] aspects, final Map parameters) {
        this(targetClass, aspects, null, parameters);
    }

    /**
     * {@link AopProxy}を作成します。
     * 
     * @param targetClass
     * @param aspects
     * @param interTypes
     * @param parameters
     */
    public AopProxy(final Class targetClass, final Aspect[] aspects, final InterType[] interTypes, final Map parameters) {
        if ((aspects == null || aspects.length == 0) && (interTypes == null || interTypes.length == 0)) {
            throw new EmptyRuntimeException("aspects and interTypes");
        }

        this.targetClass = targetClass;
        defaultPointcut = new PointcutImpl(targetClass);

        weaver = new AspectWeaver(targetClass, parameters);
        setupAspects(aspects);
        weaver.setInterTypes(interTypes);
        enhancedClass = weaver.generateClass();
    }

    private void setupAspects(Aspect[] aspects) {
        if (aspects == null || aspects.length == 0) {
            return;
        }

        for (int i = 0; i < aspects.length; ++i) {
            Aspect aspect = aspects[i];
            if (aspect.getPointcut() == null) {
                aspect.setPointcut(defaultPointcut);
            }
        }

        Method[] methods = targetClass.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if (LdiMethodUtil.isBridgeMethod(method) || LdiMethodUtil.isSyntheticMethod(method)) {
                continue;
            }

            List interceptorList = new ArrayList();
            for (int j = 0; j < aspects.length; ++j) {
                Aspect aspect = aspects[j];
                if (aspect.getPointcut().isApplied(method)) {
                    interceptorList.add(aspect.getMethodInterceptor());
                }
            }
            if (interceptorList.size() == 0) {
                continue;
            }
            if (!isApplicableAspect(method)) {
                logger.log("WSSR0009", new Object[] { targetClass.getName(), method.getName() });
                continue;
            }
            weaver.setInterceptors(method, (MethodInterceptor[]) interceptorList.toArray(new MethodInterceptor[interceptorList.size()]));
        }
    }

    /**
     * エンハンスされたクラスを返します。
     * 
     * @return
     */
    public Class getEnhancedClass() {
        return enhancedClass;
    }

    /**
     * エンハンスされたインスタンスを作成します。
     * 
     * @return エンハンスされたインスタンス
     */
    public Object create() {
        return LdiClassUtil.newInstance(enhancedClass);
    }

    /**
     * エンハンスされたインスタンスを作成します。
     * 
     * @param argTypes
     * @param args
     * @return エンハンスされたインスタンス
     */
    public Object create(Class[] argTypes, Object[] args) {
        final Constructor constructor = LdiClassUtil.getConstructor(enhancedClass, argTypes);
        return LdiConstructorUtil.newInstance(constructor, args);
    }

    private boolean isApplicableAspect(Method method) {
        int mod = method.getModifiers();
        return !Modifier.isFinal(mod) && !Modifier.isStatic(mod);
    }

}