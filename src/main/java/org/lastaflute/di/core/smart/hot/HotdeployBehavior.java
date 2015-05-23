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
package org.lastaflute.di.core.smart.hot;

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.DisposableUtil;
import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.creator.ComponentCreator;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.di.core.meta.impl.LaContainerImpl;
import org.lastaflute.di.core.meta.impl.LaContainerBehavior.DefaultProvider;
import org.lastaflute.di.core.util.ComponentUtil;
import org.lastaflute.di.helper.log.SLogger;
import org.lastaflute.di.naming.NamingConvention;

/**
 * HOT deployのための
 * {@link org.lastaflute.di.core.meta.impl.LaContainerBehavior.Provider}です。
 * <p>
 * このクラスをs2container.diconに登録するとHOT deployで動作するようになります。
 * </p>
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class HotdeployBehavior extends DefaultProvider {

    private static final SLogger logger = SLogger.getLogger(HotdeployBehavior.class);

    private ClassLoader originalClassLoader;

    private HotdeployClassLoader hotdeployClassLoader;

    private Map componentDefCache = new HashMap();

    private NamingConvention namingConvention;

    private ComponentCreator[] creators = new ComponentCreator[0];

    /** keepプロパティのバインディングタイプアノテーションです。 */
    public static final String keep_BINDING = "bindingType=may";

    private boolean keep;

    /**
     * {@link NamingConvention}を返します。
     * 
     * @return {@link NamingConvention}
     */
    public NamingConvention getNamingConvention() {
        return namingConvention;
    }

    /**
     * {@link NamingConvention}を設定します。
     * 
     * @param namingConvention
     */
    public void setNamingConvention(NamingConvention namingConvention) {
        this.namingConvention = namingConvention;
    }

    /**
     * {@link ComponentCreator}の配列を返します。
     * 
     * @return {@link ComponentCreator}の配列
     */
    public ComponentCreator[] getCreators() {
        return creators;
    }

    /**
     * {@link ComponentCreator}の配列を設定します。
     * 
     * @param creators
     */
    public void setCreators(ComponentCreator[] creators) {
        this.creators = creators;
    }

    /**
     * {@link #start()}/{@link #stop()}の度にクラスローダをキープするかどうかを設定します。
     * 
     * @param keep
     *            クラスローダをキープする場合<code>true</code>
     */
    public void setKeep(boolean keep) {
        this.keep = keep;
        if (hotdeployClassLoader != null) {
            finish();
        }
    }

    public void start() {
        originalClassLoader = Thread.currentThread().getContextClassLoader();
        if (!keep || hotdeployClassLoader == null) {
            hotdeployClassLoader = new HotdeployClassLoader(originalClassLoader, namingConvention);
        }
        Thread.currentThread().setContextClassLoader(hotdeployClassLoader);
        LaContainerImpl container = (LaContainerImpl) SingletonLaContainerFactory.getContainer();
        container.setClassLoader(hotdeployClassLoader);
    }

    /**
     * HOT deployを終了します。
     * <p>
     * {@link #keep}プロパティが<code>true</code>の場合、HOT deployクラスローダは破棄せず、 次の
     * {@link #start()}～{@link #stop()}でも同じクラスローダが使用されます。
     * </p>
     */
    public void stop() {
        if (!keep) {
            finish();
        }
        LaContainerImpl container = (LaContainerImpl) SingletonLaContainerFactory.getContainer();
        container.setClassLoader(originalClassLoader);
        Thread.currentThread().setContextClassLoader(originalClassLoader);
        originalClassLoader = null;
    }

    public void finish() {
        componentDefCache.clear();
        hotdeployClassLoader = null;
        DisposableUtil.dispose();
    }

    protected ComponentDef getComponentDef(LaContainer container, Object key) {
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
        if (key instanceof Class) {
            cd = createComponentDef((Class) key);
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
            register(cd);
            ComponentUtil.putRegisterLog(cd);
            cd.init();
        }
        return cd;
    }

    /**
     * キャッシュにある {@link ComponentDef}を返します。
     * 
     * @param key
     * @return {@link ComponentDef}
     */
    protected ComponentDef getComponentDefFromCache(Object key) {
        return (ComponentDef) componentDefCache.get(key);
    }

    /**
     * {@link ComponentDef}を作成します。
     * 
     * @param componentClass
     * @return {@link ComponentDef}
     */
    protected ComponentDef createComponentDef(Class componentClass) {
        for (int i = 0; i < creators.length; ++i) {
            ComponentCreator creator = creators[i];
            ComponentDef cd = creator.createComponentDef(componentClass);
            if (cd != null) {
                return cd;
            }
        }
        return null;
    }

    /**
     * {@link ComponentDef}を作成します。
     * 
     * @param componentName
     * @return {@link ComponentDef}
     */
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

    /**
     * {@link ComponentDef}を登録します。
     * 
     * @param componentDef
     */
    protected void register(ComponentDef componentDef) {
        componentDef.setContainer(SingletonLaContainerFactory.getContainer());
        registerByClass(componentDef);
        registerByName(componentDef);
    }

    /**
     * {@link ComponentDef}をクラスをキーにして登録します。
     * 
     * @param componentDef
     */
    protected void registerByClass(ComponentDef componentDef) {
        Class[] classes = ComponentUtil.getAssignableClasses(componentDef.getComponentClass());
        for (int i = 0; i < classes.length; ++i) {
            registerMap(classes[i], componentDef);
        }
    }

    /**
     * {@link ComponentDef}を名前をキーにして登録します。
     * 
     * @param componentDef
     */
    protected void registerByName(ComponentDef componentDef) {
        String componentName = componentDef.getComponentName();
        if (componentName != null) {
            registerMap(componentName, componentDef);
        }
    }

    /**
     * {@link ComponentDef}をキャッシュに登録します。
     * <p>
     * キャッシュは基本的にリクエストごとに破棄されます
     * </p>
     * 
     * @param key
     * @param componentDef
     */
    protected void registerMap(Object key, ComponentDef componentDef) {
        ComponentDef previousCd = (ComponentDef) componentDefCache.get(key);
        if (previousCd == null) {
            componentDefCache.put(key, componentDef);
        } else {
            ComponentDef tmrcd = LaContainerImpl.createTooManyRegistration(key, previousCd, componentDef);
            componentDefCache.put(key, tmrcd);
        }
    }
}