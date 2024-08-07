/*
 * Copyright 2015-2024 the original author or authors.
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
import org.lastaflute.di.helper.log.LaLogger;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiConstructorUtil;
import org.lastaflute.di.util.LdiMethodUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AopProxy implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 0L;
    private static final LaLogger logger = LaLogger.getLogger(AopProxy.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Class<?> targetClass; // not null
    protected final Class<?> enhancedClass; // not null
    protected final Pointcut defaultPointcut; // not null
    protected final AspectWeaver aspectWeaver; // not null, has state

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AopProxy(final Class<?> targetClass, final Aspect[] aspects) {
        this(targetClass, aspects, null, null);
    }

    public AopProxy(final Class<?> targetClass, final Aspect[] aspects, final InterType[] interTypes) {
        this(targetClass, aspects, interTypes, null);
    }

    public AopProxy(final Class<?> targetClass, final Aspect[] aspects, final Map<?, ?> parameters) {
        this(targetClass, aspects, null, parameters);
    }

    /**
     * @param targetClass (NotNull)
     * @param aspects (NullAllowed)
     * @param interTypes (NullAllowed)
     * @param parameters (NullAllowed)
     * @throws EmptyRuntimeException When both the aspects and interTypes are null or empty.
     */
    public AopProxy(final Class<?> targetClass, final Aspect[] aspects, final InterType[] interTypes, final Map<?, ?> parameters) {
        // also targetClass is null-checked at pointcut implementation
        if (isBothAspectInterTypeEmpty(aspects, interTypes)) {
            throw new EmptyRuntimeException("aspects and interTypes");
        }

        this.targetClass = targetClass;
        defaultPointcut = createDefaultPointcut(targetClass);

        aspectWeaver = createAspectWeaver(targetClass, parameters);
        setupAspects(aspects);
        aspectWeaver.setInterTypes(interTypes);
        enhancedClass = aspectWeaver.generateClass();
    }

    protected boolean isBothAspectInterTypeEmpty(final Aspect[] aspects, final InterType[] interTypes) {
        return (aspects == null || aspects.length == 0) && (interTypes == null || interTypes.length == 0);
    }

    protected Pointcut createDefaultPointcut(final Class<?> targetClass) {
        return new PointcutImpl(targetClass);
    }

    // -----------------------------------------------------
    //                                                Aspect
    //                                                ------
    protected AspectWeaver createAspectWeaver(final Class<?> targetClass, final Map<?, ?> parameters) {
        return newAspectWeaver(targetClass, parameters);
    }

    protected AspectWeaver newAspectWeaver(final Class<?> targetClass, final Map<?, ?> parameters) {
        return new AspectWeaver(targetClass, parameters);
    }

    protected void setupAspects(Aspect[] aspects) {
        if (aspects == null || aspects.length == 0) {
            return;
        }

        for (int i = 0; i < aspects.length; ++i) {
            Aspect aspect = aspects[i];
            if (aspect.getPointcut() == null) {
                aspect.setPointcut(defaultPointcut);
            }
        }

        final Method[] methods = targetClass.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            if (LdiMethodUtil.isBridgeMethod(method) || LdiMethodUtil.isSyntheticMethod(method)) {
                continue;
            }

            List<MethodInterceptor> interceptorList = new ArrayList<MethodInterceptor>();
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
            aspectWeaver.setInterceptors(method,
                    (MethodInterceptor[]) interceptorList.toArray(new MethodInterceptor[interceptorList.size()]));
        }
    }

    protected boolean isApplicableAspect(Method method) {
        int mod = method.getModifiers();
        return !Modifier.isFinal(mod) && !Modifier.isStatic(mod);
    }

    // ===================================================================================
    //                                                                      Enhanced Class
    //                                                                      ==============
    public Object create() {
        return LdiClassUtil.newInstance(enhancedClass);
    }

    public Object create(Class<?>[] argTypes, Object[] args) {
        final Constructor<?> constructor = LdiClassUtil.getConstructor(enhancedClass, argTypes);
        return LdiConstructorUtil.newInstance(constructor, args);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Class<?> getEnhancedClass() {
        return enhancedClass;
    }

    public Pointcut getDefaultPointcut() {
        return defaultPointcut;
    }

    public AspectWeaver getAspectWeaver() {
        return aspectWeaver;
    }
}