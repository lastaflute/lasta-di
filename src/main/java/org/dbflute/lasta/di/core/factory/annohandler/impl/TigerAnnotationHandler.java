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
package org.dbflute.lasta.di.core.factory.annohandler.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dbflute.lasta.di.Disposable;
import org.dbflute.lasta.di.DisposableUtil;
import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.aop.annotation.Interceptor;
import org.dbflute.lasta.di.core.factory.defbuilder.AspectDefBuilder;
import org.dbflute.lasta.di.core.factory.defbuilder.ComponentDefBuilder;
import org.dbflute.lasta.di.core.factory.defbuilder.DestroyMethodDefBuilder;
import org.dbflute.lasta.di.core.factory.defbuilder.InitMethodDefBuilder;
import org.dbflute.lasta.di.core.factory.defbuilder.IntertypeDefBuilder;
import org.dbflute.lasta.di.core.factory.defbuilder.PojoComponentDefBuilder;
import org.dbflute.lasta.di.core.factory.defbuilder.PropertyDefBuilder;
import org.dbflute.lasta.di.core.factory.defbuilder.impl.AspectAnnotationAspectDefBuilder;
import org.dbflute.lasta.di.core.factory.defbuilder.impl.MetaAnnotationAspectDefBuilder;
import org.dbflute.lasta.di.core.factory.defbuilder.impl.ResourcePropertyDefBuilder;
import org.dbflute.lasta.di.core.factory.defbuilder.impl.DestroyMethodDefBuilderImpl;
import org.dbflute.lasta.di.core.factory.defbuilder.impl.InitMethodDefBuilderImpl;
import org.dbflute.lasta.di.core.factory.defbuilder.impl.S2IntertypeDefBuilder;
import org.dbflute.lasta.di.core.meta.AutoBindingDef;
import org.dbflute.lasta.di.core.meta.InstanceDef;
import org.dbflute.lasta.di.core.meta.PropertyDef;
import org.dbflute.lasta.di.helper.beans.BeanDesc;
import org.dbflute.lasta.di.helper.beans.PropertyDesc;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TigerAnnotationHandler extends ConstantAnnotationHandler {

    /** イニシャライズ済みなら<code>true</code> */
    protected static boolean initialized;

    // #deleted
    ///** JPAが有効なら<code>true</code> */
    //protected static final boolean enableJPA;
    //static {
    //    boolean enable = false;
    //    try {
    //        Class.forName("javax.persistence.PersistenceContext"); // geronimo-jpa_3.0_spec-1.0.jar
    //        enable = true;
    //    } catch (final Throwable ignore) {}
    //    enableJPA = enable;
    //}

    /** Common Annotationsが有効なら<code>true</code> */
    protected static final boolean enableCommonAnnotations;
    static {
        boolean enable = false;
        try {
            Class.forName("javax.annotation.Resource"); // geronimo-annotation_1.0_spec-1.0.jar
            enable = true;
        } catch (final Throwable ignore) {}
        enableCommonAnnotations = enable;
    }

    protected static final List<ComponentDefBuilder> componentDefBuilders = Collections
            .synchronizedList(new ArrayList<ComponentDefBuilder>());

    protected static final List<PropertyDefBuilder> propertyDefBuilders = Collections.synchronizedList(new ArrayList<PropertyDefBuilder>());

    protected static final List<AspectDefBuilder> aspectDefBuilders = Collections.synchronizedList(new ArrayList<AspectDefBuilder>());

    protected static final List<IntertypeDefBuilder> intertypeDefBuilders = Collections
            .synchronizedList(new ArrayList<IntertypeDefBuilder>());

    protected static final List<InitMethodDefBuilder> initMethodDefBuilders = Collections
            .synchronizedList(new ArrayList<InitMethodDefBuilder>());

    protected static final List<DestroyMethodDefBuilder> destroyMethodDefBuilders = Collections
            .synchronizedList(new ArrayList<DestroyMethodDefBuilder>());

    static {
        initialize();
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        loadDefaultComponentDefBuilder();
        loadDefaultPropertyDefBuilder();
        loadDefaultAspectDefBuilder();
        loadDefaultIntertypeDefBuilder();
        loadDefaultInitMethodDefBuilder();
        loadDefaultDestroyMethodDefBuilder();
        DisposableUtil.add(new Disposable() {

            public void dispose() {
                TigerAnnotationHandler.dispose();
            }
        });
        initialized = true;
    }

    /**
     * インスタンスの状態を破棄します。
     */
    public static void dispose() {
        clearComponentDefBuilder();
        clearPropertyDefBuilder();
        clearAspectDefBuilder();
        clearIntertypeDefBuilder();
        clearInitMethodDefBuilder();
        clearDestroyMethodDefBuilder();
        initialized = false;
    }

    /**
     * デフォルトの{@link ComponentDefBuilder}を追加します。
     */
    public static void loadDefaultComponentDefBuilder() {
        componentDefBuilders.add(new PojoComponentDefBuilder());
    }

    /**
     * {@link ComponentDefBuilder}を追加します。
     * 
     * @param builder
     *            {@link ComponentDefBuilder}
     */
    public static void addComponentDefBuilder(final ComponentDefBuilder builder) {
        componentDefBuilders.add(builder);
    }

    /**
     * {@link ComponentDefBuilder}を削除します。
     * 
     * @param builder
     *            {@link ComponentDefBuilder}
     */
    public static void removeComponentDefBuilder(final ComponentDefBuilder builder) {
        componentDefBuilders.remove(builder);
    }

    /**
     * {@link ComponentDefBuilder}をクリアします。
     */
    public static void clearComponentDefBuilder() {
        componentDefBuilders.clear();
    }

    /**
     * デフォルトの{@link PropertyDefBuilder}を追加します。
     */
    public static void loadDefaultPropertyDefBuilder() {
        clearPropertyDefBuilder();
        // #lasta_di remove Binding annotation
        //propertyDefBuilders.add(new BindingPropertyDefBuilder());
        if (enableCommonAnnotations) {
            propertyDefBuilders.add(new ResourcePropertyDefBuilder());
        }
    }

    /**
     * {@link PropertyDefBuilder}を追加します。
     * 
     * @param builder
     *            {@link PropertyDefBuilder}
     */
    public static void addPropertyDefBuilder(final PropertyDefBuilder builder) {
        propertyDefBuilders.add(builder);
    }

    /**
     * {@link PropertyDefBuilder}を削除します。
     * 
     * @param builder
     *            {@link PropertyDefBuilder}
     */
    public static void removePropertyDefBuilder(final PropertyDefBuilder builder) {
        propertyDefBuilders.remove(builder);
    }

    /**
     * {@link PropertyDefBuilder}をクリアします。
     */
    public static void clearPropertyDefBuilder() {
        propertyDefBuilders.clear();
    }

    /**
     * デフォルトの{@link AspectDefBuilder}を追加します。
     */
    public static void loadDefaultAspectDefBuilder() {
        aspectDefBuilders.add(new AspectAnnotationAspectDefBuilder());
        aspectDefBuilders.add(new MetaAnnotationAspectDefBuilder(Interceptor.class, "Interceptor"));
    }

    /**
     * {@link AspectDefBuilder}を追加します。
     * 
     * @param builder
     *            {@link AspectDefBuilder}
     */
    public static void addAspectDefBuilder(final AspectDefBuilder builder) {
        aspectDefBuilders.add(builder);
    }

    /**
     * {@link AspectDefBuilder}を削除します。
     * 
     * @param builder
     *            {@link AspectDefBuilder}
     */
    public static void removeAspectDefBuilder(final AspectDefBuilder builder) {
        aspectDefBuilders.remove(builder);
    }

    /**
     * {@link AspectDefBuilder}をクリアします。
     */
    public static void clearAspectDefBuilder() {
        aspectDefBuilders.clear();
    }

    /**
     * デフォルトの{@link IntertypeDefBuilder}を追加します。
     */
    public static void loadDefaultIntertypeDefBuilder() {
        intertypeDefBuilders.add(new S2IntertypeDefBuilder());
    }

    /**
     * {@link IntertypeDefBuilder}を追加します。
     * 
     * @param builder
     *            {@link IntertypeDefBuilder}
     */
    public static void addIntertypeDefBuilder(final IntertypeDefBuilder builder) {
        intertypeDefBuilders.add(builder);
    }

    /**
     * {@link IntertypeDefBuilder}を削除します。
     * 
     * @param builder
     *            {@link IntertypeDefBuilder}
     */
    public static void removeIntertypeDefBuilder(final IntertypeDefBuilder builder) {
        intertypeDefBuilders.remove(builder);
    }

    /**
     * {@link IntertypeDefBuilder}をクリアします。
     */
    public static void clearIntertypeDefBuilder() {
        intertypeDefBuilders.clear();
    }

    /**
     * デフォルトの{@link InitMethodDefBuilder}を追加します。
     */
    public static void loadDefaultInitMethodDefBuilder() {
        initMethodDefBuilders.add(new InitMethodDefBuilderImpl());
    }

    /**
     * {@link InitMethodDefBuilder}を追加します。
     * 
     * @param builder
     *            {@link InitMethodDefBuilder}
     */
    public static void addInitMethodDefBuilder(final InitMethodDefBuilder builder) {
        initMethodDefBuilders.add(builder);
    }

    public static void removeInitMethodDefBuilder(final InitMethodDefBuilder builder) {
        initMethodDefBuilders.remove(builder);
    }

    public static void clearInitMethodDefBuilder() {
        initMethodDefBuilders.clear();
    }

    public static void loadDefaultDestroyMethodDefBuilder() {
        destroyMethodDefBuilders.add(new DestroyMethodDefBuilderImpl());
    }

    public static void addDestroyMethodDefBuilder(final DestroyMethodDefBuilder builder) {
        destroyMethodDefBuilders.add(builder);
    }

    public static void removeDestroyMethodDefBuilder(final DestroyMethodDefBuilder builder) {
        destroyMethodDefBuilders.remove(builder);
    }

    public static void clearDestroyMethodDefBuilder() {
        destroyMethodDefBuilders.clear();
    }

    @Override
    public ComponentDef createComponentDef(Class<?> componentClass, InstanceDef defaultInstanceDef,
            final AutoBindingDef defaultAutoBindingDef, final boolean defaultExternalBinding) {
        initialize();
        for (final ComponentDefBuilder builder : componentDefBuilders) {
            final ComponentDef componentDef =
                    builder.createComponentDef(this, componentClass, defaultInstanceDef, defaultAutoBindingDef, defaultExternalBinding);
            if (componentDef != null) {
                return componentDef;
            }
        }
        return super.createComponentDef(componentClass, defaultInstanceDef, defaultAutoBindingDef, defaultExternalBinding);
    }

    @Override
    public PropertyDef createPropertyDef(final BeanDesc beanDesc, PropertyDesc propertyDesc) {
        if (propertyDesc.isWritable()) {
            for (final PropertyDefBuilder builder : propertyDefBuilders) {
                final PropertyDef propertyDef = builder.createPropertyDef(this, beanDesc, propertyDesc);
                if (propertyDef != null) {
                    return propertyDef;
                }
            }
        }
        return super.createPropertyDef(beanDesc, propertyDesc);
    }

    @Override
    public PropertyDef createPropertyDef(BeanDesc beanDesc, Field field) {
        for (PropertyDefBuilder builder : propertyDefBuilders) {
            final PropertyDef propertyDef = builder.createPropertyDef(this, beanDesc, field);
            if (propertyDef != null) {
                return propertyDef;
            }
        }
        return super.createPropertyDef(beanDesc, field);
    }

    @Override
    public void appendAspect(ComponentDef componentDef) {
        for (final AspectDefBuilder builder : aspectDefBuilders) {
            builder.appendAspectDef(this, componentDef);
        }
        super.appendAspect(componentDef);
    }

    @Override
    public void appendInterType(final ComponentDef componentDef) {
        for (final IntertypeDefBuilder builder : intertypeDefBuilders) {
            builder.appendIntertypeDef(this, componentDef);
        }
        super.appendInterType(componentDef);
    }

    @Override
    public void appendInitMethod(final ComponentDef componentDef) {
        Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }
        for (final InitMethodDefBuilder builder : initMethodDefBuilders) {
            builder.appendInitMethodDef(this, componentDef);
        }
        super.appendInitMethod(componentDef);
    }

    @Override
    public void appendDestroyMethod(final ComponentDef componentDef) {
        Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }
        for (final DestroyMethodDefBuilder builder : destroyMethodDefBuilders) {
            builder.appendDestroyMethodDef(this, componentDef);
        }
        super.appendDestroyMethod(componentDef);
    }

}
