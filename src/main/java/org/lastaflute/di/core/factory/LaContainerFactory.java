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
package org.lastaflute.di.core.factory;

import java.util.Set;

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
    protected static boolean initialized;
    protected static boolean configuring = false;
    protected static LaContainer configurationContainer;
    protected static LaContainerProvider provider;
    protected static LaContainerBuilder defaultBuilder;
    protected static final ThreadLocal<Set<String>> processingPaths = new ThreadLocal<Set<String>>();

    static {
        configure();
    }

    // ===================================================================================
    //                                                                              Create
    //                                                                              ======
    public static synchronized LaContainer create(String path) {
        if (LdiStringUtil.isEmpty(path)) {
            throw new EmptyRuntimeException("path");
        }
        return doCreate(path);
    }

    protected static LaContainer doCreate(String path) {
        if (!initialized) {
            configure();
        }
        return getProvider().create(path);
    }

    // ===================================================================================
    //                                                                             Include
    //                                                                             =======
    public static LaContainer include(final LaContainer parent, final String path) {
        if (!initialized) {
            configure();
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
        show("...Reading {}", configFile);
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
        return new DiXmlLaContainerBuilder(); // TODO jflute lastaflute: [F] improvement: ConfigurationContainer, redefine here?
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
    public interface Configurator {

        void configure(LaContainer configurationContainer);
    }

    public static class DefaultConfigurator implements Configurator {

        public void configure(final LaContainer configurationContainer) {
            provider = createProvider(configurationContainer);
            defaultBuilder = createDefaultBuilder(configurationContainer);
            setupBehavior(configurationContainer);
            setupDeployer(configurationContainer);
            setupAssembler(configurationContainer);
        }

        protected LaContainerProvider createProvider(final LaContainer configurationContainer) {
            if (configurationContainer.hasComponentDef(LaContainerProvider.class)) {
                return (LaContainerProvider) configurationContainer.getComponent(LaContainerProvider.class);
            } else if (provider instanceof LaContainerDefaultProvider) {
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
            if (configurationContainer.hasComponentDef(DEFAULT_BUILDER_NAME)) {
                return (LaContainerBuilder) configurationContainer.getComponent(DEFAULT_BUILDER_NAME);
            }
            if (configurationContainer.hasComponentDef(ResourceResolver.class) && defaultBuilder instanceof AbstractLaContainerBuilder) {
                final ResourceResolver resolver = (ResourceResolver) configurationContainer.getComponent(ResourceResolver.class);
                ((AbstractLaContainerBuilder) defaultBuilder).setResourceResolver(resolver);
            }
            return defaultBuilder;
        }

        protected void setupBehavior(final LaContainer configurationContainer) {
            if (configurationContainer.hasComponentDef(LaContainerBehavior.Provider.class)) {
                LaContainerBehavior.setProvider((LaContainerBehavior.Provider) configurationContainer
                        .getComponent(LaContainerBehavior.Provider.class));
            }
        }

        protected void setupDeployer(final LaContainer configurationContainer) {
            if (configurationContainer.hasComponentDef(ComponentDeployerFactory.Provider.class)) {
                ComponentDeployerFactory.setProvider((ComponentDeployerFactory.Provider) configurationContainer
                        .getComponent(ComponentDeployerFactory.Provider.class));
            }
        }

        protected void setupAssembler(final LaContainer configurationContainer) {
            if (configurationContainer.hasComponentDef(AssemblerFactory.Provider.class)) {
                AssemblerFactory.setProvider((AssemblerFactory.Provider) configurationContainer
                        .getComponent(AssemblerFactory.Provider.class));
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

    protected static void setProvider(LaContainerProvider vider) {
        provider = vider;
    }

    public static LaContainerBuilder getDefaultBuilder() {
        return defaultBuilder;
    }

    protected static void setDefaultBuilder(LaContainerBuilder builder) {
        defaultBuilder = builder;
    }
}
