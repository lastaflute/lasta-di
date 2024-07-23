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
package org.lastaflute.di.core.meta.impl;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.exception.ComponentNotFoundException;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LaContainerBehavior {

    private static Provider provider = new DefaultProvider();

    private LaContainerBehavior() {
    }

    public static Provider getProvider() {
        return provider;
    }

    public static void setProvider(final Provider p) {
        provider = p;
    }

    public static ComponentDef acquireFromGetComponent(LaContainer container, final Object key) {
        return getProvider().acquireFromGetComponent(container, key);
    }

    public static ComponentDef acquireFromGetComponentDef(LaContainer container, final Object key) {
        return getProvider().acquireFromGetComponentDef(container, key);
    }

    public static ComponentDef acquireFromHasComponentDef(LaContainer container, final Object key) {
        return getProvider().acquireFromHasComponentDef(container, key);
    }

    public static ComponentDef acquireFromInjectDependency(LaContainer container, final Object key) {
        return getProvider().acquireFromInjectDependency(container, key);
    }

    public interface Provider {

        ComponentDef acquireFromGetComponent(LaContainer container, final Object key);

        ComponentDef acquireFromGetComponentDef(LaContainer container, final Object key);

        ComponentDef acquireFromHasComponentDef(LaContainer container, final Object key);

        ComponentDef acquireFromInjectDependency(LaContainer container, final Object key);
    }

    public static class DefaultProvider implements Provider {
        public ComponentDef acquireFromGetComponent(final LaContainer container, final Object key) {
            return acquireFromGetComponentDef(container, key);
        }

        public ComponentDef acquireFromGetComponentDef(final LaContainer container, final Object key) {
            final ComponentDef cd = getComponentDef(container, key);
            if (cd == null) {
                throwComponentNotFoundException(container, key);
            }
            return cd;
        }

        protected void throwComponentNotFoundException(LaContainer container, Object key) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Not found the component by the key.");
            br.addItem("Component Key");
            br.addElement(key);
            br.addItem("Definition Path");
            br.addElement(container.getPath());
            final String msg = br.buildExceptionMessage();
            throw new ComponentNotFoundException(msg, key);
        }

        public ComponentDef acquireFromHasComponentDef(final LaContainer container, final Object key) {
            return getComponentDef(container, key);
        }

        public ComponentDef acquireFromInjectDependency(final LaContainer container, final Object key) {
            return acquireFromGetComponentDef(container, key);
        }

        protected ComponentDef getComponentDef(final LaContainer container, final Object key) {
            return ((LaContainerImpl) container).internalGetComponentDef(key);
        }
    }
}