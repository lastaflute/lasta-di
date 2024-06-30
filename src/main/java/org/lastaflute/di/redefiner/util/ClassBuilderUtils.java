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
package org.lastaflute.di.redefiner.util;

import java.lang.reflect.Method;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.redefiner.LaContainerPreparer;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class ClassBuilderUtils {

    protected ClassBuilderUtils() {
    }

    public static String toComponentName(String name) {
        if (name.length() > 0) {
            // #future easy logic here so want to rewrite real logic
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        return name;
    }

    public static LaContainerPreparer getPreparer(ComponentDef componentDef) {
        final LaContainer container = componentDef.getContainer();
        final ComponentDef[] componentDefs = container.findLocalComponentDefs(LaContainerPreparer.class);
        if (componentDefs.length == 0) {
            return null;
        }
        return (LaContainerPreparer) componentDefs[0].getComponent();
    }

    public static Method findMethod(Class<?> clazz, String componentName, String prefix) {
        final Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            final String methodName = methods[i].getName();
            if (methodName.startsWith(prefix) && componentName.equals(toComponentName(methodName.substring(prefix.length())))) {
                return methods[i];
            }
        }
        return null;
    }
}
