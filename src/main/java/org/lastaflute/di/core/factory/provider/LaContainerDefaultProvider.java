/*
 * Copyright 2015-2016 the original author or authors.
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
package org.lastaflute.di.core.factory.provider;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.exception.DiXmlExtensionNotFoundException;
import org.lastaflute.di.core.external.ExternalContextComponentDefRegister;
import org.lastaflute.di.core.factory.LaContainerFactory;
import org.lastaflute.di.core.factory.conbuilder.LaContainerBuilder;
import org.lastaflute.di.core.factory.exception.CircularIncludeRuntimeException;
import org.lastaflute.di.core.factory.pathresolver.PathResolver;
import org.lastaflute.di.core.factory.pathresolver.impl.SimplePathResolver;
import org.lastaflute.di.core.meta.impl.LaContainerImpl;
import org.lastaflute.di.util.LdiResourceUtil;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LaContainerDefaultProvider implements LaContainerProvider {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String pathResolver_BINDING = "bindingType=may";
    public static final String externalContext_BINDING = "bindingType=may";
    public static final String externalContextComponentDefRegister_BINDING = "bindingType=may";
    protected static final ThreadLocal<Set<String>> processingPaths = new ThreadLocal<Set<String>>();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected PathResolver pathResolver = new SimplePathResolver();
    protected ExternalContext externalContext;
    protected ExternalContextComponentDefRegister externalContextComponentDefRegister;

    // ===================================================================================
    //                                                                              Create
    //                                                                              ======
    public LaContainer create(String path) {
        final LaContainer configurationContainer = LaContainerFactory.getConfigurationContainer();
        final ClassLoader classLoader;
        if (configurationContainer != null && configurationContainer.hasComponentDef(ClassLoader.class)) {
            classLoader = (ClassLoader) configurationContainer.getComponent(ClassLoader.class);
        } else {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        final LaContainer container = LdiStringUtil.isEmpty(path) ? new LaContainerImpl() : build(path, classLoader);
        if (container.isInitializeOnCreate()) {
            container.init();
        }
        return container;
    }

    // ===================================================================================
    //                                                                             Include
    //                                                                             =======
    public LaContainer include(LaContainer parent, String path) {
        final String realPath = pathResolver.resolvePath(parent.getPath(), path);
        assertCircularInclude(parent, realPath);
        enter(realPath);
        try {
            final LaContainer root = parent.getRoot();
            final LaContainer child;
            synchronized (root) {
                if (root.hasDescendant(realPath)) {
                    showReadingLog(parent, path, realPath, true);
                    child = root.getDescendant(realPath);
                    parent.include(child);
                } else {
                    showReadingLog(parent, path, realPath, false);
                    final String ext = getExtension(realPath);
                    final LaContainerBuilder builder = getBuilder(ext);
                    child = builder.include(parent, realPath);
                    root.registerDescendant(child);
                    if (child.isInitializeOnCreate()) {
                        child.init();
                    }
                }
            }
            return child;
        } finally {
            leave(realPath);
        }
    }

    protected LaContainer build(String path, ClassLoader classLoader) {
        final String realPath = pathResolver.resolvePath(null, path);
        showReadingLog(null, path, realPath, false);
        enter(realPath);
        try {
            final String ext = getExtension(realPath);
            final LaContainer container = getBuilder(ext).build(realPath, classLoader);
            container.setExternalContext(externalContext);
            container.setExternalContextComponentDefRegister(externalContextComponentDefRegister);
            return container;
        } finally {
            leave(realPath);
        }
    }

    protected String getExtension(String path) {
        final String ext = LdiResourceUtil.getExtension(path);
        if (ext == null) { // translated by outer process with parent info so simple here
            throw new DiXmlExtensionNotFoundException("Not found the extension in the Di xml path: " + path, path);
        }
        return ext;
    }

    protected LaContainerBuilder getBuilder(String ext) {
        final String componentName = ext + "Redefined";
        final LaContainer configurationContainer = LaContainerFactory.getConfigurationContainer();
        if (configurationContainer != null && configurationContainer.hasComponentDef(componentName)) {
            return (LaContainerBuilder) configurationContainer.getComponent(componentName);
        }
        return LaContainerFactory.getDefaultBuilder();
    }

    // ===================================================================================
    //                                                                     Circular Detect
    //                                                                     ===============
    protected static void assertCircularInclude(final LaContainer container, final String path) {
        assertCircularInclude(container, path, new LinkedList<String>());
    }

    protected static void assertCircularInclude(final LaContainer container, final String path, LinkedList<String> paths) {
        paths.addFirst(container.getPath());
        try {
            if (path.equals(container.getPath())) {
                throw new CircularIncludeRuntimeException(path, new ArrayList<String>(paths));
            }
            for (int i = 0; i < container.getParentSize(); ++i) {
                assertCircularInclude(container.getParent(i), path, paths);
            }
        } finally {
            paths.removeFirst();
        }
    }

    protected static void enter(final String path) {
        final Set<String> paths = getProcessingPaths();
        if (paths.contains(path)) {
            throw new CircularIncludeRuntimeException(path, paths);
        }
        paths.add(path);
    }

    protected static void leave(final String path) {
        final Set<String> paths = getProcessingPaths();
        paths.remove(path);
    }

    protected static Set<String> getProcessingPaths() {
        Set<String> paths = (Set<String>) processingPaths.get();
        if (paths == null) {
            paths = new LinkedHashSet<String>();
            processingPaths.set(paths);
        }
        return paths;
    }

    // ===================================================================================
    //                                                                             Logging
    //                                                                             =======
    protected void showReadingLog(LaContainer parent, String path, String realPath, boolean descendant) {
        if (LaContainerFactory.isShowEnabled()) {
            final String indent = prepareReadingLogHierarchalIndent(parent);
            final String pathExp = path + (descendant ? " (recycle)" : "");
            if (path.equals(realPath)) {
                LaContainerFactory.show("...Reading {}{}", indent, pathExp);
            } else {
                LaContainerFactory.show("...Reading {}{} realPath={}", indent, pathExp, realPath);
            }
        }
    }

    protected String prepareReadingLogHierarchalIndent(LaContainer parent) {
        final StringBuilder indentSb = new StringBuilder();
        int indentSize = getProcessingPaths().size();
        if (parent != null && isOriginatedInConfigurationContainer(parent)) {
            --indentSize;
        }
        for (int i = 0; i < indentSize; i++) {
            indentSb.append("  ");
        }
        return indentSb.toString();
    }

    protected boolean isOriginatedInConfigurationContainer(LaContainer container) {
        final LaContainer root = container.getRoot();
        final LaContainer configurationContainer = LaContainerFactory.getConfigurationContainer();
        return root != null && configurationContainer != null && root != configurationContainer;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ======== 
    public PathResolver getPathResolver() {
        return pathResolver;
    }

    public void setPathResolver(final PathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    public ExternalContext getExternalContext() {
        return externalContext;
    }

    public void setExternalContext(ExternalContext externalContext) {
        this.externalContext = externalContext;
    }

    public ExternalContextComponentDefRegister getExternalContextComponentDefRegister() {
        return externalContextComponentDefRegister;
    }

    public void setExternalContextComponentDefRegister(ExternalContextComponentDefRegister externalContextComponentDefRegister) {
        this.externalContextComponentDefRegister = externalContextComponentDefRegister;
    }
}
