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
package org.dbflute.lasta.di.core.external;

import org.dbflute.lasta.di.core.ExternalContext;
import org.dbflute.lasta.di.core.LaContainer;
import org.dbflute.lasta.di.core.deployer.ComponentDeployerFactory;
import org.dbflute.lasta.di.core.deployer.ExternalComponentDeployerProvider;
import org.dbflute.lasta.di.core.factory.LaContainerFactory;
import org.dbflute.lasta.di.core.factory.SingletonLaContainerFactory;
import org.dbflute.lasta.di.util.LdiStringUtil;

/**
 * Genericな {@link LaContainer}の初期化クラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class GenericS2ContainerInitializer {

    /**
     * {@link LaContainer}用の設定パスです。
     */
    protected String containerConfigPath;

    /**
     * アプリケーション用の設定パスです。
     */
    protected String configPath;

    /**
     * {@link ExternalContext}です。
     */
    protected ExternalContext externalContext;

    /**
     * {@link ExternalContextComponentDefRegister}です。
     */
    protected ExternalContextComponentDefRegister externalContextComponentDefRegister;

    /**
     * {@link GenericS2ContainerInitializer}を作成します。
     */
    public GenericS2ContainerInitializer() {
        this(new GenericExternalContext(), new GenericExternalContextComponentDefRegister());
    }

    /**
     * {@link GenericS2ContainerInitializer}を作成します。
     * 
     * @param externalContext
     * @param externalContextComponentDefRegister
     */
    public GenericS2ContainerInitializer(ExternalContext externalContext,
            ExternalContextComponentDefRegister externalContextComponentDefRegister) {
        this.externalContext = externalContext;
        this.externalContextComponentDefRegister = externalContextComponentDefRegister;
    }

    /**
     * 初期化します。
     * 
     * @return {@link LaContainer}
     */
    public LaContainer initialize() {
        if (isAlreadyInitialized()) {
            return SingletonLaContainerFactory.getContainer();
        }
        if (!LdiStringUtil.isEmpty(containerConfigPath)) {
            LaContainerFactory.configure(containerConfigPath);
        }
        if (!LdiStringUtil.isEmpty(configPath)) {
            SingletonLaContainerFactory.setConfigPath(configPath);
        }
        if (ComponentDeployerFactory.getProvider() instanceof ComponentDeployerFactory.DefaultProvider) {
            ComponentDeployerFactory.setProvider(new ExternalComponentDeployerProvider());
        }
        SingletonLaContainerFactory.setExternalContext(externalContext);
        SingletonLaContainerFactory.setExternalContextComponentDefRegister(externalContextComponentDefRegister);
        SingletonLaContainerFactory.init();

        return SingletonLaContainerFactory.getContainer();
    }

    /**
     * 初期化されているかどうか返します。
     * 
     * @return 初期化されているかどうか
     */
    protected boolean isAlreadyInitialized() {
        return SingletonLaContainerFactory.hasContainer();
    }

    /**
     * 設定パスを設定します。
     * 
     * @param configPath
     */
    public void setConfigPath(final String configPath) {
        this.configPath = configPath;
    }

}
