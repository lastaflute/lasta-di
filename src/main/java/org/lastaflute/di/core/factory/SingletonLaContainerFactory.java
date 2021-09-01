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
package org.lastaflute.di.core.factory;

import org.lastaflute.di.DisposableUtil;
import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.LastaDiProperties;
import org.lastaflute.di.core.expression.engine.JavaScriptExpressionEngine;
import org.lastaflute.di.core.external.ExternalContextComponentDefRegister;
import org.lastaflute.di.core.smart.SmartDeployMode;
import org.lastaflute.di.core.util.SmartDeployUtil;
import org.lastaflute.di.naming.NamingConvention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SingletonLaContainerFactory {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LoggerFactory.getLogger(SingletonLaContainerFactory.class);
    private static String configPath = "app.xml"; // as default

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private static ExternalContext externalContext;
    private static ExternalContextComponentDefRegister externalContextComponentDefRegister;
    private static LaContainer container;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    private SingletonLaContainerFactory() {
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    public static void init() {
        if (container != null) {
            return;
        }
        setupScriptEngine();
        setupSmartDeployMode();
        container = createContainer();
        setupExternalContext();
        container.init();
        showBoot();
    }

    // -----------------------------------------------------
    //                                         Script Engine
    //                                         -------------
    protected static void setupScriptEngine() {
        new Thread(() -> {
            /* first getEngineByName() costs about 0.5 seconds so initialize it background */
            final Class<?> engineType = LastaDiProperties.getInstance().getDiXmlScriptExpressionEngineType();
            if (engineType == null) { /* use default */
                /* initialize e.g. static resources */
                new JavaScriptExpressionEngine().initializeManagedEngine();
            }
        }).start();
    }

    // -----------------------------------------------------
    //                                      SmartDeploy Mode
    //                                      ----------------
    protected static void setupSmartDeployMode() {
        final String smartDeployMode = LastaDiProperties.getInstance().getSmartDeployMode();
        if (smartDeployMode != null) {
            SmartDeployMode.setValue(SmartDeployMode.codeOf(smartDeployMode)); // with logging
        } else {
            logger.info("*Not found smart deploy mode property so use default mode: {}", SmartDeployMode.getValue());
        }
    }

    // -----------------------------------------------------
    //                                             Container
    //                                             ---------
    protected static LaContainer createContainer() {
        return LaContainerFactory.create(configPath);
    }

    protected static void setupExternalContext() {
        if (container.getExternalContext() == null) {
            if (externalContext != null) {
                container.setExternalContext(externalContext);
            }
        } else if (container.getExternalContext().getApplication() == null && externalContext != null) {
            container.getExternalContext().setApplication(externalContext.getApplication());
        }
        if (container.getExternalContextComponentDefRegister() == null && externalContextComponentDefRegister != null) {
            container.setExternalContextComponentDefRegister(externalContextComponentDefRegister);
        }
    }

    // -----------------------------------------------------
    //                                             Show Boot
    //                                             ---------
    protected static void showBoot() {
        logger.info("Lasta Di boot successfully.");
        logger.info("  SmartDeploy Mode: {}", SmartDeployUtil.getDeployMode(container));
        if (getContainer().hasComponentDef(NamingConvention.class)) { // just in case
            final NamingConvention convention = getContainer().getComponent(NamingConvention.class);
            final StringBuilder sb = new StringBuilder();
            for (String rootPkg : convention.getRootPackageNames()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(rootPkg);
            }
            logger.info("  Smart Package: {}", sb.toString());
        }
    }

    // ===================================================================================
    //                                                                             Destroy
    //                                                                             =======
    public static void destroy() {
        if (container == null) {
            return;
        }
        container.destroy();
        container = null;
        DisposableUtil.dispose();
    }

    // ===================================================================================
    //                                                                           Container
    //                                                                           =========
    public static boolean hasContainer() {
        return container != null;
    }

    public static LaContainer getContainer() {
        if (container == null) {
            throw new IllegalStateException("Not initialized the container when getContainer().");
        }
        return container;
    }

    public static void setContainer(LaContainer ner) {
        container = ner;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public static String getConfigPath() {
        return configPath;
    }

    public static void setConfigPath(String path) {
        configPath = path;
    }

    public static ExternalContext getExternalContext() {
        return externalContext;
    }

    public static void setExternalContext(ExternalContext extCtx) {
        externalContext = extCtx;
    }

    public static ExternalContextComponentDefRegister getExternalContextComponentDefRegister() {
        return externalContextComponentDefRegister;
    }

    public static void setExternalContextComponentDefRegister(ExternalContextComponentDefRegister extCtxComponentDefRegister) {
        externalContextComponentDefRegister = extCtxComponentDefRegister;
    }
}
