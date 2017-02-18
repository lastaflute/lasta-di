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
package org.lastaflute.di.core.util;

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ContainerConstants;
import org.lastaflute.di.core.aop.Aspect;
import org.lastaflute.di.core.aop.InterType;
import org.lastaflute.di.core.aop.proxy.AopProxy;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AopProxyUtil {

    protected AopProxyUtil() {
    }

    public static Class<?> getConcreteClass(final ComponentDef componentDef) {
        if (componentDef.getAspectDefSize() == 0 && componentDef.getInterTypeDefSize() == 0) {
            return componentDef.getComponentClass();
        }
        final Map<String, ComponentDef> parameters = new HashMap<String, ComponentDef>();
        parameters.put(ContainerConstants.COMPONENT_DEF_NAME, componentDef);
        AopProxy proxy = new AopProxy(componentDef.getComponentClass(), getAspects(componentDef), getInterTypes(componentDef), parameters);
        return proxy.getEnhancedClass();
    }

    private static Aspect[] getAspects(final ComponentDef componentDef) {
        final int size = componentDef.getAspectDefSize();
        Aspect[] aspects = new Aspect[size];
        for (int i = 0; i < size; ++i) {
            aspects[i] = componentDef.getAspectDef(i).getAspect();
        }
        return aspects;
    }

    private static InterType[] getInterTypes(final ComponentDef componentDef) {
        final int size = componentDef.getInterTypeDefSize();
        InterType[] interTypes = new InterType[size];
        for (int i = 0; i < size; ++i) {
            interTypes[i] = componentDef.getInterTypeDef(i).getInterType();
        }
        return interTypes;
    }
}