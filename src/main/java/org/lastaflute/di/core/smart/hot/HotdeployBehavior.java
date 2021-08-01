/*
 * Copyright 2015-2021 the original author or authors.
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
package org.lastaflute.di.core.smart.hot;

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.DisposableUtil;
import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.creator.ComponentCreator;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.di.core.meta.impl.LaContainerBehavior.DefaultProvider;
import org.lastaflute.di.core.meta.impl.LaContainerImpl;
import org.lastaflute.di.core.util.ComponentUtil;
import org.lastaflute.di.helper.log.LaLogger;
import org.lastaflute.di.naming.NamingConvention;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class HotdeployBehavior extends DefaultProvider {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final LaLogger logger = LaLogger.getLogger(HotdeployBehavior.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private ClassLoader originalClassLoader;
    private HotdeployClassLoader hotdeployClassLoader;
    private Map<Object, ComponentDef> componentDefCache = new HashMap<Object, ComponentDef>();
    private NamingConvention namingConvention;
    private ComponentCreator[] creators = new ComponentCreator[0];

    public static final String keep_BINDING = "bindingType=may";
    private boolean keep;

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public void setKeep(boolean keep) {
        this.keep = keep;
        if (hotdeployClassLoader != null) {
            finish();
        }
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isLaContainerHotdeploy() {
        return SingletonLaContainerFactory.getContainer().getClassLoader() instanceof HotdeployClassLoader;
    }

    public boolean isThreadContextHotdeploy() {
        return Thread.currentThread().getContextClassLoader() instanceof HotdeployClassLoader;
    }

    // ===================================================================================
    //                                                                      Start and Stop
    //                                                                      ==============
    public void start() {
        originalClassLoader = Thread.currentThread().getContextClassLoader();
        if (!keep || hotdeployClassLoader == null) {
            hotdeployClassLoader = new HotdeployClassLoader(originalClassLoader, namingConvention);
        }
        Thread.currentThread().setContextClassLoader(hotdeployClassLoader);
        LaContainer container = SingletonLaContainerFactory.getContainer();
        container.setClassLoader(hotdeployClassLoader);
    }

    public void stop() {
        if (!keep) {
            finish();
        }
        LaContainer container = SingletonLaContainerFactory.getContainer();
        container.setClassLoader(originalClassLoader);
        Thread.currentThread().setContextClassLoader(originalClassLoader);
        originalClassLoader = null;
    }

    public void finish() {
        componentDefCache.clear();
        hotdeployClassLoader = null;
        DisposableUtil.dispose();
    }

    // ===================================================================================
    //                                                                       Core Override
    //                                                                       =============
    @Override
    protected ComponentDef getComponentDef(LaContainer container, Object key) {
        synchronized (this) { // to avoid too-many registration of async hot-deploy (basically job)
            return doGetComponentDef(container, key);
        }
    }

    protected ComponentDef doGetComponentDef(LaContainer container, Object key) {
        ComponentDef cd = super.getComponentDef(container, key);
        if (cd != null) {
            return cd;
        }
        if (container != container.getRoot()) {
            return null;
        }
        cd = getComponentDefFromCache(key);
        if (cd != null) {
            return cd;
        }
        if (key instanceof Class<?>) {
            cd = createComponentDef((Class<?>) key);
        } else if (key instanceof String) {
            cd = createComponentDef((String) key);
            if (cd != null && !key.equals(cd.getComponentName())) {
                logger.log("WSSR0011", new Object[] { key, cd.getComponentClass().getName(), cd.getComponentName() });
                cd = null;
            }
        } else {
            throw new IllegalArgumentException("Illegal component key: " + key);
        }
        if (cd != null) {
            register(cd);
            ComponentUtil.putRegisterLog(cd);
            cd.init();
        }
        return cd;
    }

    protected ComponentDef getComponentDefFromCache(Object key) {
        return (ComponentDef) componentDefCache.get(key);
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

    protected void register(ComponentDef componentDef) {
        componentDef.setContainer(SingletonLaContainerFactory.getContainer());
        registerByClass(componentDef);
        registerByName(componentDef);
    }

    protected void registerByClass(ComponentDef componentDef) {
        Class<?>[] classes = ComponentUtil.getAssignableClasses(componentDef.getComponentClass());
        for (int i = 0; i < classes.length; ++i) {
            registerMap(classes[i], componentDef);
        }
    }

    protected void registerByName(ComponentDef componentDef) {
        String componentName = componentDef.getComponentName();
        if (componentName != null) {
            registerMap(componentName, componentDef);
        }
    }

    protected void registerMap(Object key, ComponentDef componentDef) {
        ComponentDef previousCd = (ComponentDef) componentDefCache.get(key);
        if (previousCd == null) {
            componentDefCache.put(key, componentDef);
        } else {
            ComponentDef tmrcd = LaContainerImpl.createTooManyRegistration(key, previousCd, componentDef);
            componentDefCache.put(key, tmrcd);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
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
}