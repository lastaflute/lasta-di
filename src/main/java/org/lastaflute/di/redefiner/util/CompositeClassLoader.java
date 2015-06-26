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
package org.lastaflute.di.redefiner.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class CompositeClassLoader extends ClassLoader {

    private final ClassLoader[] classLoaders;

    public CompositeClassLoader(ClassLoader[] classLoaders) {
        super(getSystemClassLoader().getParent());
        this.classLoaders = normalizeClassLoaders(classLoaders);
    }

    public CompositeClassLoader(ClassLoader[] classLoaders, ClassLoader parent) {
        super(parent);
        this.classLoaders = normalizeClassLoaders(classLoaders);
    }

    protected ClassLoader[] normalizeClassLoaders(ClassLoader[] classLoaders) {
        final Set<ClassLoader> set = new LinkedHashSet<ClassLoader>();
        for (int i = 0; i < classLoaders.length; i++) {
            if (set.contains(classLoaders[i])) {
                continue;
            }
            set.add(classLoaders[i]);
        }
        return set.toArray(new ClassLoader[0]);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("C{ ");
        for (int i = 0; i < classLoaders.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(classLoaders[i]);
        }
        sb.append(" }(parent=");
        sb.append(getParent());
        sb.append(")");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                ClassLoader Override
    //                                                                ====================
    public URL getResource(String name) {
        for (int i = 0; i < classLoaders.length; i++) {
            URL url = classLoaders[i].getResource(name);
            if (url != null) {
                return url;
            }
        }
        if (getParent() != null) {
            return super.getResource(name);
        } else {
            return null;
        }
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        final Set<URL> set = new LinkedHashSet<URL>();
        for (int i = 0; i < classLoaders.length; i++) {
            for (Enumeration<URL> enm = classLoaders[i].getResources(name); enm.hasMoreElements();) {
                set.add(enm.nextElement());
            }
        }
        if (getParent() != null) {
            for (Enumeration<URL> enm = super.getResources(name); enm.hasMoreElements();) {
                set.add(enm.nextElement());
            }
        }
        return Collections.enumeration(set);
    }

    @Override
    public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
        for (int i = 0; i < classLoaders.length; i++) {
            try {
                return classLoaders[i].loadClass(name);
            } catch (ClassNotFoundException ignored) {}
        }
        if (getParent() != null) {
            return super.loadClass(name);
        } else {
            throw new ClassNotFoundException("Class doesn't exist: " + name);
        }
    }
}
