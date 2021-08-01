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
package org.lastaflute.di.core.factory.defbuilder.impl;

import javax.annotation.Resource;

import org.lastaflute.di.core.j2ee.JndiResourceLocator;
import org.lastaflute.di.core.meta.AccessTypeDef;
import org.lastaflute.di.core.meta.PropertyDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ResourcePropertyDefBuilder extends AbstractPropertyDefBuilder<Resource> {

    public ResourcePropertyDefBuilder() {
    }

    @Override
    protected Class<Resource> getAnnotationType() {
        return Resource.class;
    }

    @Override
    protected PropertyDef createPropertyDef(String propertyName, AccessTypeDef accessTypeDef, Resource resource) {
        return createPropertyDef(propertyName, accessTypeDef, JndiResourceLocator.resolveName(resource.name()));
    }
}
