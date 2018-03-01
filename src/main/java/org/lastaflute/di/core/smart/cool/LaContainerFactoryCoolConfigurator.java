/*
 * Copyright 2015-2018 the original author or authors.
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
package org.lastaflute.di.core.smart.cool;

import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.external.ExternalContextComponentDefRegister;
import org.lastaflute.di.core.factory.LaContainerFactory.DefaultConfigurator;
import org.lastaflute.di.core.factory.pathresolver.PathResolver;
import org.lastaflute.di.core.factory.provider.LaContainerProvider;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LaContainerFactoryCoolConfigurator extends DefaultConfigurator {

    @Override
    protected LaContainerProvider createProvider(LaContainer configurationContainer) {
        final LaContainerFactoryCoolProvider provider = newLaContainerFactoryCoolProvider();
        if (configurationContainer.hasComponentDef(PathResolver.class)) {
            provider.setPathResolver((PathResolver) configurationContainer.getComponent(PathResolver.class));
        }
        if (configurationContainer.hasComponentDef(ExternalContext.class)) {
            provider.setExternalContext((ExternalContext) configurationContainer.getComponent(ExternalContext.class));
        }
        if (configurationContainer.hasComponentDef(ExternalContextComponentDefRegister.class)) {
            provider.setExternalContextComponentDefRegister(
                    (ExternalContextComponentDefRegister) configurationContainer.getComponent(ExternalContextComponentDefRegister.class));
        }
        return provider;
    }

    protected LaContainerFactoryCoolProvider newLaContainerFactoryCoolProvider() {
        return new LaContainerFactoryCoolProvider();
    }
}
