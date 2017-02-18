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
package org.lastaflute.di.core.customizer;

import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.aop.LaMethodInvocation;
import org.lastaflute.di.core.aop.Pointcut;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.aop.frame.MethodInvocation;
import org.lastaflute.di.core.aop.impl.NestedMethodInvocation;
import org.lastaflute.di.core.aop.interceptors.AbstractInterceptor;
import org.lastaflute.di.core.factory.AspectDefFactory;
import org.lastaflute.di.core.meta.AspectDef;
import org.lastaflute.di.core.meta.impl.SimpleComponentDef;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AspectCustomizer extends AbstractCustomizer {

    public static final String interceptorName_BINDING = "bindingType=may";
    public static final String pointcut_BINDING = "bindingType=may";
    public static final String useLookupAdapter_BINDING = "bindingType=may";

    private final List<String> interceptorNames = new ArrayList<String>();
    private String pointcut;
    private boolean useLookupAdapter;

    public void setInterceptorName(final String interceptorName) {
        interceptorNames.clear();
        interceptorNames.add(interceptorName);
    }

    public void addInterceptorName(final String interceptorName) {
        interceptorNames.add(interceptorName);
    }

    public void setPointcut(final String pointcut) {
        this.pointcut = pointcut;
    }

    public void setUseLookupAdapter(final boolean useLookupAdapter) {
        this.useLookupAdapter = useLookupAdapter;
    }

    @Override
    protected void doCustomize(final ComponentDef componentDef) {
        if (useLookupAdapter) {
            final MethodInterceptor adaptor =
                    new LookupAdaptorInterceptor((String[]) interceptorNames.toArray(new String[interceptorNames.size()]));
            final AspectDef aspectDef = AspectDefFactory.createAspectDef(new SimpleComponentDef(adaptor), createPointcut());
            componentDef.addAspectDef(aspectDef);
        } else {
            for (int i = 0; i < interceptorNames.size(); ++i) {
                final AspectDef aspectDef = AspectDefFactory.createAspectDef((String) interceptorNames.get(i), createPointcut());
                componentDef.addAspectDef(aspectDef);
            }
        }
    }

    protected Pointcut createPointcut() {
        if (!LdiStringUtil.isEmpty(pointcut)) {
            return AspectDefFactory.createPointcut(pointcut);
        }
        if (targetInterface != null) {
            return AspectDefFactory.createPointcut(targetInterface);
        }
        return null;
    }

    /**
     * @author modified by jflute (originated in Seasar)
     */
    public static class LookupAdaptorInterceptor extends AbstractInterceptor {

        private static final long serialVersionUID = 1L;

        protected String[] interceptorNames;

        public LookupAdaptorInterceptor(final String[] interceptorNames) {
            this.interceptorNames = interceptorNames;
        }

        public Object invoke(final MethodInvocation invocation) throws Throwable {
            final LaContainer container = getComponentDef(invocation).getContainer().getRoot();
            final MethodInterceptor[] interceptors = new MethodInterceptor[interceptorNames.length];
            for (int i = 0; i < interceptors.length; ++i) {
                interceptors[i] = (MethodInterceptor) container.getComponent(interceptorNames[i]);
            }
            final MethodInvocation nestInvocation = new NestedMethodInvocation((LaMethodInvocation) invocation, interceptors);
            return nestInvocation.proceed();
        }
    }
}
