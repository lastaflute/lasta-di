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
package org.lastaflute.di.core.smart.cool;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.provider.LaContainerDefaultProvider;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LaContainerFactoryCoolProvider extends LaContainerDefaultProvider {

    protected static final String DICON_PATH = "smart/cooldeploy-autoregister.xml";

    @Override
    public LaContainer create(final String path) {
        final LaContainer container = super.create(path);
        include(container, DICON_PATH);
        return container;
    }
}
