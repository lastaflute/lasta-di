/*
 * Copyright 2015-2020 the original author or authors.
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
package org.lastaflute.di.core.customizer;

import java.lang.reflect.Method;
import java.util.Map;

import javax.transaction.Transactional;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.factory.AspectDefFactory;
import org.lastaflute.di.util.LdiStringUtil;
import org.lastaflute.di.util.tiger.LdiCollectionsUtil;

/**
 * @author jflute
 */
public abstract class PointTxAttributeCustomizer extends AbstractCustomizer {

    protected static final Map<Transactional.TxType, String> txInterceptors = LdiCollectionsUtil.newHashMap();

    static {
        txInterceptors.put(Transactional.TxType.MANDATORY, "tx_aop.mandatoryTx");
        txInterceptors.put(Transactional.TxType.REQUIRED, "tx_aop.requiredTx");
        txInterceptors.put(Transactional.TxType.REQUIRES_NEW, "tx_aop.requiresNewTx");
        txInterceptors.put(Transactional.TxType.NOT_SUPPORTED, "tx_aop.notSupportedTx");
        txInterceptors.put(Transactional.TxType.NEVER, "tx_aop.neverTx");
    }

    @Override
    protected void doCustomize(final ComponentDef componentDef) {
        final Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass.getAnnotation(Transactional.class) != null) {
            String msg = "Cannot use the transactional annotation for class, use for method: " + componentDef;
            throw new IllegalStateException(msg);
        }
        for (final Method method : componentClass.getMethods()) {
            if (method.isSynthetic() || method.isBridge()) {
                continue;
            }
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            final Transactional methodAttr = method.getAnnotation(Transactional.class);
            if (isOutOfTxMethod(methodAttr, method)) {
                continue;
            }
            // action execute or has transaction attribute either class or method here
            final Transactional.TxType methodAttrType = methodAttr.value();
            final String interceptorName = txInterceptors.get(methodAttrType);
            if (!LdiStringUtil.isEmpty(interceptorName)) {
                componentDef.addAspectDef(AspectDefFactory.createAspectDef(interceptorName, method));
            }
        }
    }

    protected boolean isOutOfTxMethod(Transactional methodAttr, Method method) {
        return methodAttr == null || isImplicitTxSupportedMethod(method);
    }

    protected abstract boolean isImplicitTxSupportedMethod(Method method);
}
