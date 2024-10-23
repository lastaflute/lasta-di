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

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.creator.ComponentCreator;
import org.lastaflute.di.core.util.ComponentUtil;
import org.lastaflute.di.naming.NamingConvention;
import org.lastaflute.di.util.ClassTraversal.ClassHandler;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiModifierUtil;
import org.lastaflute.di.util.LdiResourcesUtil;
import org.lastaflute.di.util.LdiResourcesUtil.Resources;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class CoolComponentAutoRegister implements ClassHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    // see ConstantAnnotationHandler
    public static final String INIT_METHOD = "registerAll";
    public static final String container_BINDING = "bindingType=must";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private LaContainer container;
    @Resource
    private ComponentCreator[] creators;
    @Resource
    private NamingConvention namingConvention;

    // to avoid duplicate registration
    protected final Set<Class<?>> registeredClasses = new HashSet<Class<?>>();

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    public void registerAll() { // called as constant annotation
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

    // ===================================================================================
    //                                                                  Class Registration
    //                                                                  ==================
    // callback from resources structure
    public void processClass(final String packageName, final String shortClassName) {
        if (shortClassName.indexOf('$') != -1) { // inner class
            return;
        }
        final String className = LdiClassUtil.concatName(packageName, shortClassName);
        if (!namingConvention.isTargetClassName(className)) { // non quick target
            return;
        }
        final Class<?> clazz = LdiClassUtil.forName(className);
        if (namingConvention.isSkipClass(clazz)) { // special skip
            return;
        }
        if (container.getRoot().hasComponentDef(clazz)) { // already exists
            if (clazz.isInterface() || LdiModifierUtil.isAbstract(clazz)) {
                return;
            }
            final ComponentDef cd = container.getRoot().getComponentDef(clazz);
            if (clazz == cd.getComponentClass()) { // same as registered
                return;
            }
        }
        final ComponentDef cd = createComponentDef(clazz);
        if (cd == null) { // not found creator for the type
            return;
        }
        if (registeredClasses.contains(cd.getComponentClass())) { // already registered
            return;
        }

        // cool components are always registered to root container
        container.getRoot().register(cd);

        registeredClasses.add(cd.getComponentClass()); // goal
        ComponentUtil.putRegisterLog(cd);
    }

    protected ComponentDef createComponentDef(final Class<?> componentClass) {
        // chain of responsibility?
        for (int i = 0; i < creators.length; ++i) {
            final ComponentCreator creator = creators[i];
            final ComponentDef cd = creator.createComponentDef(componentClass);
            if (cd != null) {
                return cd;
            }
        }
        return null;
    }
}