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
package org.dbflute.lasta.di.core.util;

import java.util.Collection;
import java.util.Map;

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.ContainerConstants;
import org.dbflute.lasta.di.helper.beans.BeanDesc;
import org.dbflute.lasta.di.helper.beans.factory.BeanDescFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class BindingUtil implements ContainerConstants {

    protected BindingUtil() {
    }

    public static final boolean isPropertyAutoBindable(Class<?> clazz) {
        return clazz.isInterface() && isAnywayAutoBindable(clazz); // interface only (no change), only for DBFlute package
    }

    public static final boolean isPropertyAutoBindable(Class<?>[] classes) { // for e.g. method, constructor
        for (int i = 0; i < classes.length; ++i) {
            if (!isPropertyAutoBindable(classes[i])) {
                return false;
            }
        }
        return true;
    }

    public static final boolean isFieldAutoBindable(Class<?> clazz) {
        return isAnywayAutoBindable(clazz); // allows injection by non-interface type
    }

    protected static boolean isAnywayAutoBindable(Class<?> clazz) { // added exclusion of Object
        return !Object.class.equals(clazz) && !Collection.class.isAssignableFrom(clazz) && !Map.class.isAssignableFrom(clazz);
    }

    public static final boolean isAutoBindableArray(Class<?> clazz) {
        return clazz.isArray() && clazz.getComponentType().isInterface();
    }

    public static BeanDesc getBeanDesc(ComponentDef componentDef, Object component) {
        return BeanDescFactory.getBeanDesc(getComponentClass(componentDef, component));
    }

    public static Class<?> getComponentClass(ComponentDef componentDef, Object component) {
        final Class<?> clazz = componentDef.getConcreteClass();
        return clazz != null ? clazz : component.getClass(); // before enhanced
    }
}