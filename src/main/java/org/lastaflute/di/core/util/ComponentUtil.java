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
package org.lastaflute.di.core.util;

import java.io.Externalizable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ContainerConstants;
import org.lastaflute.di.helper.log.LaLogger;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ComponentUtil implements ContainerConstants {

    private static final LaLogger logger = LaLogger.getLogger(ComponentUtil.class);
    private static final Class<?>[] EMPTY_CLASSES = new Class<?>[0];
    private static final Set<Class<?>> notAssignableClasses = new HashSet<Class<?>>();

    static {
        notAssignableClasses.add(Cloneable.class);
        notAssignableClasses.add(Comparable.class);
        notAssignableClasses.add(Serializable.class);
        notAssignableClasses.add(Externalizable.class);
        notAssignableClasses.add(ContainerConstants.class);
    }

    private ComponentUtil() {
    }

    public static Class<?>[] getAssignableClasses(Class<?> componentClass) {
        if (componentClass == null) {
            return EMPTY_CLASSES;
        }
        final Set<Class<?>> classes = new HashSet<Class<?>>(4);
        for (Class<?> clazz = componentClass; clazz != Object.class && clazz != null; clazz = clazz.getSuperclass()) {
            addAssignableClasses(classes, clazz);
        }
        return (Class[]) classes.toArray(new Class[classes.size()]);
    }

    private static void addAssignableClasses(Set<Class<?>> classes, Class<?> clazz) {
        if (notAssignableClasses.contains(clazz)) {
            return;
        }
        classes.add(clazz);
        final Class<?>[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            addAssignableClasses(classes, interfaces[i]);
        }
    }

    public static void putRegisterLog(final ComponentDef cd) {
        if (logger.isDebugEnabled()) {
            final StringBuffer buf = new StringBuffer(100);
            final Class<?> componentClass = cd.getComponentClass();
            if (componentClass != null) {
                buf.append(componentClass.getName());
            }
            final String componentName = cd.getComponentName();
            if (!LdiStringUtil.isEmpty(componentName)) {
                buf.append("[").append(componentName).append("]");
            }
            logger.log("DSSR0105", new Object[] { new String(buf) });
        }
    }
}