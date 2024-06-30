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
package org.lastaflute.di.core.factory.annohandler.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lastaflute.di.Disposable;
import org.lastaflute.di.DisposableUtil;
import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.aop.annotation.Interceptor;
import org.lastaflute.di.core.factory.defbuilder.AspectDefBuilder;
import org.lastaflute.di.core.factory.defbuilder.ComponentDefBuilder;
import org.lastaflute.di.core.factory.defbuilder.DestroyMethodDefBuilder;
import org.lastaflute.di.core.factory.defbuilder.InitMethodDefBuilder;
import org.lastaflute.di.core.factory.defbuilder.IntertypeDefBuilder;
import org.lastaflute.di.core.factory.defbuilder.PojoComponentDefBuilder;
import org.lastaflute.di.core.factory.defbuilder.PropertyDefBuilder;
import org.lastaflute.di.core.factory.defbuilder.impl.AspectAnnotationAspectDefBuilder;
import org.lastaflute.di.core.factory.defbuilder.impl.DestroyMethodDefBuilderImpl;
import org.lastaflute.di.core.factory.defbuilder.impl.InitMethodDefBuilderImpl;
import org.lastaflute.di.core.factory.defbuilder.impl.MetaAnnotationAspectDefBuilder;
import org.lastaflute.di.core.factory.defbuilder.impl.ResourcePropertyDefBuilder;
import org.lastaflute.di.core.factory.defbuilder.impl.S2IntertypeDefBuilder;
import org.lastaflute.di.core.meta.AutoBindingDef;
import org.lastaflute.di.core.meta.InstanceDef;
import org.lastaflute.di.core.meta.PropertyDef;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TigerAnnotationHandler extends ConstantAnnotationHandler {

    protected static boolean initialized;

    // #deleted
    //protected static final boolean enableJPA;
    //static {
    //    boolean enable = false;
    //    try {
    //        Class.forName("javax.persistence.PersistenceContext"); // geronimo-jpa_3.0_spec-1.0.jar
    //        enable = true;
    //    } catch (final Throwable ignore) {}
    //    enableJPA = enable;
    //}

    protected static final boolean enableCommonAnnotations;

    static {
        boolean enable = false;
        try {
            Class.forName("javax.annotation.Resource"); // geronimo-annotation_1.0_spec-1.0.jar
            enable = true;
        } catch (final Throwable ignore) {}
        enableCommonAnnotations = enable;
    }

    protected static final List<ComponentDefBuilder> componentDefBuilders =
            Collections.synchronizedList(new ArrayList<ComponentDefBuilder>());

    protected static final List<PropertyDefBuilder> propertyDefBuilders = Collections.synchronizedList(new ArrayList<PropertyDefBuilder>());

    protected static final List<AspectDefBuilder> aspectDefBuilders = Collections.synchronizedList(new ArrayList<AspectDefBuilder>());

    protected static final List<IntertypeDefBuilder> intertypeDefBuilders =
            Collections.synchronizedList(new ArrayList<IntertypeDefBuilder>());

    protected static final List<InitMethodDefBuilder> initMethodDefBuilders =
            Collections.synchronizedList(new ArrayList<InitMethodDefBuilder>());

    protected static final List<DestroyMethodDefBuilder> destroyMethodDefBuilders =
            Collections.synchronizedList(new ArrayList<DestroyMethodDefBuilder>());

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

    public static void dispose() {
        clearComponentDefBuilder();
        clearPropertyDefBuilder();
        clearAspectDefBuilder();
        clearIntertypeDefBuilder();
        clearInitMethodDefBuilder();
        clearDestroyMethodDefBuilder();
        initialized = false;
    }

    public static void loadDefaultComponentDefBuilder() {
        componentDefBuilders.add(new PojoComponentDefBuilder());
    }

    /**
     * @param builder
     */
    public static void addComponentDefBuilder(final ComponentDefBuilder builder) {
        componentDefBuilders.add(builder);
    }

    /**
     * @param builder
     */
    public static void removeComponentDefBuilder(final ComponentDefBuilder builder) {
        componentDefBuilders.remove(builder);
    }

    public static void clearComponentDefBuilder() {
        componentDefBuilders.clear();
    }

    public static void loadDefaultPropertyDefBuilder() {
        clearPropertyDefBuilder();
        // #lasta_di remove Binding annotation
        //propertyDefBuilders.add(new BindingPropertyDefBuilder());
        if (enableCommonAnnotations) {
            propertyDefBuilders.add(new ResourcePropertyDefBuilder());
        }
    }

    public static void addPropertyDefBuilder(final PropertyDefBuilder builder) {
        propertyDefBuilders.add(builder);
    }

    public static void removePropertyDefBuilder(final PropertyDefBuilder builder) {
        propertyDefBuilders.remove(builder);
    }

    public static void clearPropertyDefBuilder() {
        propertyDefBuilders.clear();
    }

    public static void loadDefaultAspectDefBuilder() {
        aspectDefBuilders.add(new AspectAnnotationAspectDefBuilder());
        aspectDefBuilders.add(new MetaAnnotationAspectDefBuilder(Interceptor.class, "Interceptor"));
    }

    public static void addAspectDefBuilder(final AspectDefBuilder builder) {
        aspectDefBuilders.add(builder);
    }

    public static void removeAspectDefBuilder(final AspectDefBuilder builder) {
        aspectDefBuilders.remove(builder);
    }

    public static void clearAspectDefBuilder() {
        aspectDefBuilders.clear();
    }

    public static void loadDefaultIntertypeDefBuilder() {
        intertypeDefBuilders.add(new S2IntertypeDefBuilder());
    }

    public static void addIntertypeDefBuilder(final IntertypeDefBuilder builder) {
        intertypeDefBuilders.add(builder);
    }

    public static void removeIntertypeDefBuilder(final IntertypeDefBuilder builder) {
        intertypeDefBuilders.remove(builder);
    }

    public static void clearIntertypeDefBuilder() {
        intertypeDefBuilders.clear();
    }

    public static void loadDefaultInitMethodDefBuilder() {
        initMethodDefBuilders.add(new InitMethodDefBuilderImpl());
    }

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
