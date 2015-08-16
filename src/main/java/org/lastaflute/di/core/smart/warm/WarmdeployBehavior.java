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
package org.lastaflute.di.core.smart.warm;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.creator.ComponentCreator;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.di.core.meta.impl.LaContainerBehavior.DefaultProvider;
import org.lastaflute.di.core.util.ComponentUtil;
import org.lastaflute.di.helper.log.LaLogger;
import org.lastaflute.di.naming.NamingConvention;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class WarmdeployBehavior extends DefaultProvider {

    private static final LaLogger logger = LaLogger.getLogger(WarmdeployBehavior.class);

    private NamingConvention namingConvention;
    private ComponentCreator[] creators = new ComponentCreator[0];

    public NamingConvention getNamingConvention() {
        return namingConvention;
    }

    public void setNamingConvention(NamingConvention namingConvention) {
        this.namingConvention = namingConvention;
    }

    public ComponentCreator[] getCreators() {
        return creators;
    }

    public void setCreators(ComponentCreator[] creators) {
        this.creators = creators;
    }

    protected ComponentDef getComponentDef(LaContainer container, Object key) {
        synchronized (container.getRoot()) {
            ComponentDef cd = super.getComponentDef(container, key);
            if (cd != null) {
                return cd;
            }
            if (container != container.getRoot()) {
                return null;
            }
            if (key instanceof Class) {
                cd = createComponentDef((Class<?>) key);
            } else if (key instanceof String) {
                cd = createComponentDef((String) key);
                if (cd != null && !key.equals(cd.getComponentName())) {
                    logger.log("WSSR0011", new Object[] { key, cd.getComponentClass().getName(), cd.getComponentName() });
                    cd = null;
                }
            } else {
                throw new IllegalArgumentException("key");
            }
            if (cd != null) {
                SingletonLaContainerFactory.getContainer().register(cd);
                ComponentUtil.putRegisterLog(cd);
                cd.init();
            }
            return cd;
        }
    }

    protected ComponentDef createComponentDef(Class<?> componentClass) {
        for (int i = 0; i < creators.length; ++i) {
            ComponentCreator creator = creators[i];
            ComponentDef cd = creator.createComponentDef(componentClass);
            if (cd != null) {
                return cd;
            }
        }
        return null;
    }

    protected ComponentDef createComponentDef(String componentName) {
        for (int i = 0; i < creators.length; ++i) {
            ComponentCreator creator = creators[i];
            ComponentDef cd = creator.createComponentDef(componentName);
            if (cd != null) {
                return cd;
            }
        }
        return null;
    }
}
