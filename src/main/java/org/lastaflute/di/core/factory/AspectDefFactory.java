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
package org.lastaflute.di.core.factory;

import java.lang.reflect.Method;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.aop.Pointcut;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.aop.impl.PointcutImpl;
import org.lastaflute.di.core.expression.ScriptingExpression;
import org.lastaflute.di.core.meta.AspectDef;
import org.lastaflute.di.core.meta.impl.AspectDefImpl;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AspectDefFactory {

    protected AspectDefFactory() {
    }

    public static AspectDef createAspectDef(MethodInterceptor interceptor, Pointcut pointcut) {
        AspectDef aspectDef = new AspectDefImpl(pointcut);
        aspectDef.setValue(interceptor);
        return aspectDef;
    }

    public static AspectDef createAspectDef(String interceptorName, Pointcut pointcut) {
        AspectDef aspectDef = new AspectDefImpl(pointcut);
        aspectDef.setExpression(new ScriptingExpression(interceptorName));
        return aspectDef;
    }

    public static AspectDef createAspectDef(ComponentDef cd, Pointcut pointcut) {
        AspectDef aspectDef = new AspectDefImpl(pointcut);
        aspectDef.setChildComponentDef(cd);
        return aspectDef;
    }

    public static AspectDef createAspectDef(String interceptorName, String pointcutStr) {
        Pointcut pointcut = createPointcut(pointcutStr);
        return createAspectDef(interceptorName, pointcut);
    }

    public static AspectDef createAspectDef(MethodInterceptor interceptor, String pointcutStr) {
        Pointcut pointcut = createPointcut(pointcutStr);
        return createAspectDef(interceptor, pointcut);
    }

    public static AspectDef createAspectDef(ComponentDef cd, String pointcutStr) {
        Pointcut pointcut = createPointcut(pointcutStr);
        return createAspectDef(cd, pointcut);
    }

    public static AspectDef createAspectDef(String interceptorName, Method method) {
        Pointcut pointcut = createPointcut(method);
        return createAspectDef(interceptorName, pointcut);
    }

    public static AspectDef createAspectDef(MethodInterceptor interceptor, Method method) {
        Pointcut pointcut = createPointcut(method);
        return createAspectDef(interceptor, pointcut);
    }

    public static AspectDef createAspectDef(ComponentDef cd, Method method) {
        Pointcut pointcut = createPointcut(method);
        return createAspectDef(cd, pointcut);
    }

    public static Pointcut createPointcut(String pointcutStr) {
        if (!LdiStringUtil.isEmpty(pointcutStr)) {
            String[] methodNames = LdiStringUtil.split(pointcutStr, ", \n");
            return new PointcutImpl(methodNames);
        }
        return null;
    }

    public static Pointcut createPointcut(Class<?> clazz) {
        return new PointcutImpl(clazz);
    }

    public static Pointcut createPointcut(Method method) {
        if (method != null) {
            return new PointcutImpl(method);
        }
        return null;
    }
}
