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
package org.lastaflute.di.core.smart.cool;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.creator.ComponentCreator;
import org.lastaflute.di.core.util.ComponentUtil;
import org.lastaflute.di.naming.NamingConvention;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiModifierUtil;
import org.lastaflute.di.util.LdiResourcesUtil;
import org.lastaflute.di.util.ClassTraversal.ClassHandler;
import org.lastaflute.di.util.LdiResourcesUtil.Resources;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class CoolComponentAutoRegister implements ClassHandler {

    public static final String INIT_METHOD = "registerAll";
    public static final String container_BINDING = "bindingType=must";

    @Resource
    private LaContainer container;
    @Resource
    private ComponentCreator[] creators;
    @Resource
    private NamingConvention namingConvention;

    protected Set<Class<?>> registeredClasses = new HashSet<Class<?>>();

    public void registerAll() {
        try {
            final String[] rootPackageNames = namingConvention.getRootPackageNames();
            if (rootPackageNames != null) {
                for (int i = 0; i < rootPackageNames.length; ++i) {
                    final Resources[] resourcesArray = LdiResourcesUtil.getResourcesTypes(rootPackageNames[i]);
                    for (int j = 0; j < resourcesArray.length; ++j) {
                        final Resources resources = resourcesArray[j];
                        try {
                            resources.forEach(this);
                        } finally {
                            resources.close();
                        }
                    }
                }
            }
        } finally {
            registeredClasses.clear();
        }
    }

    public void processClass(final String packageName, final String shortClassName) {
        if (shortClassName.indexOf('$') != -1) {
            return;
        }
        final String className = LdiClassUtil.concatName(packageName, shortClassName);
        if (!namingConvention.isTargetClassName(className)) {
            return;
        }
        final Class<?> clazz = LdiClassUtil.forName(className);
        if (namingConvention.isSkipClass(clazz)) {
            return;
        }
        if (container.getRoot().hasComponentDef(clazz)) {
            if (clazz.isInterface() || LdiModifierUtil.isAbstract(clazz)) {
                return;
            }
            final ComponentDef cd = container.getRoot().getComponentDef(clazz);
            if (clazz == cd.getComponentClass()) {
                return;
            }
        }
        ComponentDef cd = createComponentDef(clazz);
        if (cd == null) {
            return;
        }
        if (registeredClasses.contains(cd.getComponentClass())) {
            return;
        }
        container.getRoot().register(cd);
        registeredClasses.add(cd.getComponentClass());
        ComponentUtil.putRegisterLog(cd);
    }

    protected ComponentDef createComponentDef(final Class<?> componentClass) {
        for (int i = 0; i < creators.length; ++i) {
            ComponentCreator creator = creators[i];
            ComponentDef cd = creator.createComponentDef(componentClass);
            if (cd != null) {
                return cd;
            }
        }
        return null;
    }
}