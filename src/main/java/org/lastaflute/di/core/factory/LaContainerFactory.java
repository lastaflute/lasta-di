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
package org.lastaflute.di.core.factory;

import org.lastaflute.di.Disposable;
import org.lastaflute.di.DisposableUtil;
import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.assembler.AssemblerFactory;
import org.lastaflute.di.core.deployer.ComponentDeployerFactory;
import org.lastaflute.di.core.external.ExternalContextComponentDefRegister;
import org.lastaflute.di.core.factory.conbuilder.LaContainerBuilder;
import org.lastaflute.di.core.factory.conbuilder.impl.AbstractLaContainerBuilder;
import org.lastaflute.di.core.factory.dixml.DiXmlLaContainerBuilder;
import org.lastaflute.di.core.factory.pathresolver.PathResolver;
import org.lastaflute.di.core.factory.provider.LaContainerDefaultProvider;
import org.lastaflute.di.core.factory.provider.LaContainerProvider;
import org.lastaflute.di.core.factory.resresolver.ResourceResolver;
import org.lastaflute.di.core.meta.impl.LaContainerBehavior;
import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.util.LdiResourceUtil;
import org.lastaflute.di.util.LdiStringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LaContainerFactory {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger parsingShowLogger = LoggerFactory.getLogger(LaContainerFactory.class);
    public static final String FACTORY_CONFIG_KEY = "org.lastaflute.di.core.factory.config";
    public static final String FACTORY_CONFIG_PATH = "lasta_di.xml";
    public static final String DEFAULT_BUILDER_NAME = "defaultBuilder";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // static singleton pattern here so these are attributes
    protected static boolean initialized;
    protected static boolean configuring = false;

    // immediately configured in static initializer so basially not null 
    protected static LaContainer configurationContainer; // for e.g. lasta_di.xml
    protected static LaContainerProvider provider; // default or cool
    protected static LaContainerBuilder defaultBuilder; // default or redefiner

    static { // immediately
        configure();
    }

    // ===================================================================================
    //                                                                              Create
    //                                                                              ======
    // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
    // called by entry point (as root container) and redefiner (as additional container)
    // #for_now jflute not always root here so cool reading timing is varying (2024/10/22)
    // https://github.com/lastaflute/lasta-di/issues/45
    // _/_/_/_/_/_/_/_/_/_/
    public static synchronized LaContainer create(String path) {
        if (LdiStringUtil.isEmpty(path)) {
            throw new EmptyRuntimeException("path");
        }
        return doCreate(path);
    }

    protected static LaContainer doCreate(String path) {
        if (!initialized) {
            // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
            // at first, reading e.g. lasta_di.xml as configuration container
            // and switching container provider here e.g. LaContainerFactoryCoolProvider (if cool)
            //
            // Configurator
            //  ^
            // DefaultConfigurator // if hot, warm
            //  ^
            // LaContainerFactoryCoolConfigurator // if cool (defined at cooldeploy.xml)
            // _/_/_/_/_/_/_/_/_/_/
            configure();
        }
        // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
        // then, reading e.g. app.xml as application container
        // 
        // LaContainerProvider
        //  ^
        // LaContainerDefaultProvider // if hot, warm
        //  ^
        // LaContainerFactoryCoolProvider // if cool (created by cool configurator)
        // _/_/_/_/_/_/_/_/_/_/
        return getProvider().create(path);
    }

    // ===================================================================================
    //                                                                             Include
    //                                                                             =======
    // also called by include tag
    public static LaContainer include(final LaContainer parent, final String path) {
        if (!initialized) {
            configure(); // same as create()
        }
        return getProvider().include(parent, path);
    }

    // ===================================================================================
    //                                                                           Configure
    //                                                                           =========
    public static void configure() {
        final String configFile = System.getProperty(FACTORY_CONFIG_KEY, FACTORY_CONFIG_PATH);
        configure(configFile);
    }

    public static synchronized void configure(String configFile) {
        if (configuring) {
            return;
        }
        configuring = true;
        initializeDefaultProviderIfNeeds();
        initializeDefaultBuilderIfNeeds();
        if (LdiResourceUtil.isExist(configFile)) {
            doConfigure(configFile);
        }
        DisposableUtil.add(new Disposable() {
            public void dispose() {
                LaContainerFactory.destroy();
            }
        });
        configuring = false;
        initialized = true;
    }

    protected static void initializeDefaultProviderIfNeeds() {
        if (provider == null) {
            provider = newDefaultProvider();
        }
    }

    protected static LaContainerDefaultProvider newDefaultProvider() {
        return new LaContainerDefaultProvider();
    }

    protected static void initializeDefaultBuilderIfNeeds() {
        if (defaultBuilder == null) {
            defaultBuilder = newDefaultContainerBuilder();
        }
    }

    protected static DiXmlLaContainerBuilder newDefaultContainerBuilder() {
        return new DiXmlLaContainerBuilder();
    }

    protected static void doConfigure(String configFile) {
        show("...Reading {}", configFile); // first configuration so no indent (see LaContainerDefaultProvider)
        final LaContainerBuilder builder = newConfigurationContainerBuilder();
        configurationContainer = builder.build(configFile);
        configurationContainer.init();
        final Configurator configurator;
        if (configurationContainer.hasComponentDef(Configurator.class)) {
            configurator = (Configurator) configurationContainer.getComponent(Configurator.class);
        } else {
            configurator = newDefaultConfigurator();
        }
        configurator.configure(configurationContainer);
    }

    public static boolean isShowEnabled() {
        return parsingShowLogger.isInfoEnabled();
    }

    public static void show(String msg, Object... objs) {
        parsingShowLogger.info(msg, objs);
    }

    protected static DiXmlLaContainerBuilder newConfigurationContainerBuilder() {
        // cannot be redefiner, maybe redefiner uses configuration?
        // and unneeded because creator, customizer uses component priority pattern 
        return new DiXmlLaContainerBuilder();
    }

    protected static DefaultConfigurator newDefaultConfigurator() {
        return new DefaultConfigurator();
    }

    // ===================================================================================
    //                                                                             Destroy
    //                                                                             =======
    public static synchronized void destroy() {
        defaultBuilder = null;
        provider = null;
        if (configurationContainer != null) {
            configurationContainer.destroy();
        }
        configurationContainer = null;
        initialized = false;
    }

    // ===================================================================================
    //                                                                       Configuration
    //                                                                       =============
    public interface Configurator { // using e.g. lasta_di.xml container

        void configure(LaContainer configurationContainer);
    }

    public static class DefaultConfigurator implements Configurator {

        public void configure(final LaContainer configurationContainer) {
            provider = createProvider(configurationContainer); // overwrite default (or same)
            defaultBuilder = createDefaultBuilder(configurationContainer); // me too
            setupBehavior(configurationContainer);
            setupDeployer(configurationContainer);
            setupAssembler(configurationContainer);
        }

        protected LaContainerProvider createProvider(final LaContainer configurationContainer) {
            if (configurationContainer.hasComponentDef(LaContainerProvider.class)) { // extension point
                return (LaContainerProvider) configurationContainer.getComponent(LaContainerProvider.class);
            } else if (provider instanceof LaContainerDefaultProvider) { // not found in configuration
                final LaContainerDefaultProvider dp = (LaContainerDefaultProvider) provider;
                if (configurationContainer.hasComponentDef(PathResolver.class)) {
                    dp.setPathResolver((PathResolver) configurationContainer.getComponent(PathResolver.class));
                }
                if (configurationContainer.hasComponentDef(ExternalContext.class)) {
                    dp.setExternalContext((ExternalContext) configurationContainer.getComponent(ExternalContext.class));
                }
                if (configurationContainer.hasComponentDef(ExternalContextComponentDefRegister.class)) {
                    dp.setExternalContextComponentDefRegister((ExternalContextComponentDefRegister) configurationContainer
                            .getComponent(ExternalContextComponentDefRegister.class));
                }
            }
            return provider;
        }

        protected LaContainerBuilder createDefaultBuilder(final LaContainer configurationContainer) {
            if (configurationContainer.hasComponentDef(DEFAULT_BUILDER_NAME)) { // rare case? (2024/10/22)
                return (LaContainerBuilder) configurationContainer.getComponent(DEFAULT_BUILDER_NAME);
            }
            if (configurationContainer.hasComponentDef(ResourceResolver.class) && defaultBuilder instanceof AbstractLaContainerBuilder) {
                final ResourceResolver resolver = (ResourceResolver) configurationContainer.getComponent(ResourceResolver.class);
                ((AbstractLaContainerBuilder) defaultBuilder).setResourceResolver(resolver); // e.g. redefiner
            }
            return defaultBuilder;
        }

        protected void setupBehavior(final LaContainer configurationContainer) {
            if (configurationContainer.hasComponentDef(LaContainerBehavior.Provider.class)) {
                LaContainerBehavior.setProvider(
                        (LaContainerBehavior.Provider) configurationContainer.getComponent(LaContainerBehavior.Provider.class));
            }
        }

        protected void setupDeployer(final LaContainer configurationContainer) {
            if (configurationContainer.hasComponentDef(ComponentDeployerFactory.Provider.class)) {
                ComponentDeployerFactory.setProvider(
                        (ComponentDeployerFactory.Provider) configurationContainer.getComponent(ComponentDeployerFactory.Provider.class));
            }
        }

        protected void setupAssembler(final LaContainer configurationContainer) {
            if (configurationContainer.hasComponentDef(AssemblerFactory.Provider.class)) {
                AssemblerFactory
                        .setProvider((AssemblerFactory.Provider) configurationContainer.getComponent(AssemblerFactory.Provider.class));
            }
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public static synchronized LaContainer getConfigurationContainer() {
        return configurationContainer;
    }

    protected static LaContainerProvider getProvider() {
        return provider;
    }

    protected static void setProvider(LaContainerProvider vider) { // basically unused? (2024/10/22)
        provider = vider;
    }

    public static LaContainerBuilder getDefaultBuilder() {
        return defaultBuilder;
    }

    protected static void setDefaultBuilder(LaContainerBuilder builder) { // basically unused? (2024/10/22)
        defaultBuilder = builder;
    }
}
