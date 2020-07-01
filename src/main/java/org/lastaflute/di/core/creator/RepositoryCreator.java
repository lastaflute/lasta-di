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
package org.lastaflute.di.core.creator;

import org.lastaflute.di.core.customizer.ComponentCustomizer;
import org.lastaflute.di.core.meta.impl.InstanceDefFactory;
import org.lastaflute.di.naming.NamingConvention;

/**
 * @author jflute
 * @since 0.8.3 (2020/07/01)
 */
public class RepositoryCreator extends ComponentCreatorImpl {

    public RepositoryCreator(NamingConvention namingConvention) {
        super(namingConvention);
        setNameSuffix(namingConvention.getRepositorySuffix());
        setInstanceDef(InstanceDefFactory.PROTOTYPE);
        setEnableInterface(true);
        setEnableAbstract(true);
    }

    public ComponentCustomizer getRepositoryCustomizer() {
        return getCustomizer();
    }

    public void setRepositoryCustomizer(ComponentCustomizer customizer) {
        setCustomizer(customizer);
    }
}