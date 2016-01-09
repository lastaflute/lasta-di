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
package org.lastaflute.di.core.meta.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ContainerConstants;
import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.exception.ContainerNotRegisteredRuntimeException;
import org.lastaflute.di.core.exception.CyclicReferenceComponentException;
import org.lastaflute.di.core.external.ExternalContextComponentDefRegister;
import org.lastaflute.di.core.meta.MetaDef;
import org.lastaflute.di.core.meta.TooManyRegistrationComponentDef;
import org.lastaflute.di.core.util.ComponentUtil;
import org.lastaflute.di.core.util.MetaDefSupport;
import org.lastaflute.di.core.util.Traversal;
import org.lastaflute.di.exception.ContainerInitFailureException;
import org.lastaflute.di.helper.log.LaLogger;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;
import org.lastaflute.di.util.CaseInsensitiveMap;
import org.lastaflute.di.util.LdiStringUtil;

import javassist.runtime.Desc;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyFactory.ClassLoaderProvider;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LaContainerImpl implements LaContainer, ContainerConstants {

    private static final LaLogger logger = LaLogger.getLogger(LaContainerImpl.class);

    protected Map<Object, ComponentDefHolder> componentDefMap = new HashMap<Object, ComponentDefHolder>();
    protected List<ComponentDef> componentDefList = new ArrayList<ComponentDef>();
    protected String namespace;
    protected String path;
    protected boolean initializeOnCreate;
    protected List<LaContainer> children = new ArrayList<LaContainer>();
    protected Map<LaContainer, Integer> childPositions = new HashMap<LaContainer, Integer>();
    protected List<LaContainer> parents = new ArrayList<LaContainer>();
    protected CaseInsensitiveMap descendants = new CaseInsensitiveMap();
    protected LaContainer root;
    protected ExternalContext externalContext;
    protected ExternalContextComponentDefRegister externalContextComponentDefRegister;
    protected MetaDefSupport metaDefSupport = new MetaDefSupport(this);
    protected boolean inited = false;
    protected ClassLoader classLoader = null;

    static {
        Desc.useContextClassLoader = true;
        ProxyFactory.classLoaderProvider = new ClassLoaderProvider() {
            public ClassLoader get(ProxyFactory proxyFactory) {
                return Thread.currentThread().getContextClassLoader();
            }
        };
    }

    public LaContainerImpl() {
        root = this;
        register0(new SimpleComponentDef(this, CONTAINER_NAME));
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    public LaContainer getRoot() {
        return root;
    }

    public void setRoot(LaContainer root) {
        this.root = root != null ? root : this;
    }

    @SuppressWarnings("unchecked")
    public <COMPONENT> COMPONENT getComponent(Object componentKey) {
        assertParameterIsNotNull(componentKey, "componentKey");
        final ComponentDef cd = LaContainerBehavior.acquireFromGetComponent(this, componentKey);
        if (cd == null) { // maybe unneeded, exception when not found but keep just in case by jflute
            return null;
        }
        return (COMPONENT) cd.getComponent();
    }

    public Object[] findComponents(Object componentKey) {
        assertParameterIsNotNull(componentKey, "componentKey");
        ComponentDef[] componentDefs = findComponentDefs(componentKey);
        return toComponentArray(componentKey, componentDefs);
    }

    public Object[] findAllComponents(Object componentKey) throws CyclicReferenceComponentException {
        assertParameterIsNotNull(componentKey, "componentKey");
        ComponentDef[] componentDefs = findAllComponentDefs(componentKey);
        return toComponentArray(componentKey, componentDefs);
    }

    public Object[] findLocalComponents(Object componentKey) throws CyclicReferenceComponentException {
        assertParameterIsNotNull(componentKey, "componentKey");
        ComponentDef[] componentDefs = findLocalComponentDefs(componentKey);
        return toComponentArray(componentKey, componentDefs);
    }

    protected Object[] toComponentArray(Object componentKey, ComponentDef[] componentDefs) {
        int length = componentDefs.length;
        Object[] components =
                (componentKey instanceof Class) ? (Object[]) Array.newInstance((Class<?>) componentKey, length) : new Object[length];
        for (int i = 0; i < length; ++i) {
            components[i] = componentDefs[i].getComponent();
        }
        return components;
    }

    public void injectDependency(Object outerComponent) {
        injectDependency(outerComponent, outerComponent.getClass());
    }

    public void injectDependency(Object outerComponent, Class<?> componentClass) {
        assertParameterIsNotNull(outerComponent, "outerComponent");
        assertParameterIsNotNull(componentClass, "componentClass");
        ComponentDef cd = LaContainerBehavior.acquireFromInjectDependency(this, componentClass);
        if (cd != null) {
            cd.injectDependency(outerComponent);
        }
    }

    public void injectDependency(Object outerComponent, String componentName) {
        assertParameterIsNotNull(outerComponent, "outerComponent");
        assertParameterIsNotEmpty(componentName, "componentName");
        ComponentDef cd = LaContainerBehavior.acquireFromInjectDependency(this, componentName);
        if (cd != null) {
            cd.injectDependency(outerComponent);
        }
    }

    @Override
    public void register(Object component) {
        assertParameterIsNotNull(component, "component");
        register(new SimpleComponentDef(component));
    }

    @Override
    public void register(Object component, String componentName) {
        assertParameterIsNotNull(component, "component");
        assertParameterIsNotEmpty(componentName, "componentName");
        register(new SimpleComponentDef(component, componentName));
    }

    @Override
    public void register(Class<?> componentClass) {
        assertParameterIsNotNull(componentClass, "componentClass");
        register(new ComponentDefImpl(componentClass));
    }

    @Override
    public void register(Class<?> componentClass, String componentName) {
        assertParameterIsNotNull(componentClass, "componentClass");
        assertParameterIsNotEmpty(componentName, "componentName");
        register(new ComponentDefImpl(componentClass, componentName));
    }

    @Override
    public void register(ComponentDef componentDef) {
        assertParameterIsNotNull(componentDef, "componentDef");
        register0(componentDef);
        componentDefList.add(componentDef);
    }

    public void register0(ComponentDef componentDef) { // #component_point
        if (componentDef.getContainer() == null) {
            componentDef.setContainer(this);
        }
        registerByClass(componentDef);
        registerByName(componentDef);
    }

    @Override
    public void registerByClass(ComponentDef componentDef) {
        final Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass != null) { // null allowed when e.g. expression, then registered later
            final Class<?>[] classes = ComponentUtil.getAssignableClasses(componentClass);
            for (int i = 0; i < classes.length; ++i) {
                registerMap(classes[i], componentDef);
            }
        }
    }

    protected void registerByName(ComponentDef componentDef) {
        final String componentName = componentDef.getComponentName();
        if (componentName != null) {
            registerMap(componentName, componentDef);
        }
    }

    protected void registerMap(Object key, ComponentDef componentDef) {
        registerMap(key, componentDef, this);
    }

    public void registerMap(Object key, ComponentDef componentDef, LaContainer container) {
        int position = getContainerPosition(container);
        ComponentDefHolder holder = (ComponentDefHolder) componentDefMap.get(key);
        if (holder == null) {
            holder = new ComponentDefHolder(position, componentDef);
            componentDefMap.put(key, holder);
        } else if (position > holder.getPosition()) {
            return;
        } else if (position < holder.getPosition()) {
            holder.setPosition(position);
            holder.setComponentDef(componentDef);
        } else if (container != this) {
            holder.setComponentDef(componentDef);
        } else {
            holder.setComponentDef(createTooManyRegistration(key, holder.getComponentDef(), componentDef));
        }

        registerParent(key, holder.getComponentDef());
    }

    protected void registerParent(Object key, ComponentDef componentDef) { // for performacen when deep nest
        for (int i = 0; i < getParentSize(); i++) {
            LaContainer parent = (LaContainer) getParent(i);
            parent.registerMap(key, componentDef, this);
            if (isNeedNS(key, componentDef)) {
                parent.registerMap(namespace + NS_SEP + key, componentDef, this);
            }
        }
    }

    public int getComponentDefSize() {
        return componentDefList.size();
    }

    public ComponentDef getComponentDef(int index) {
        return (ComponentDef) componentDefList.get(index);
    }

    public ComponentDef getComponentDef(Object key) {
        assertParameterIsNotNull(key, "key");
        return LaContainerBehavior.acquireFromGetComponentDef(this, key);
    }

    public ComponentDef[] findComponentDefs(Object key) {
        assertParameterIsNotNull(key, "key");
        ComponentDef cd = internalGetComponentDef(key);
        return toComponentDefArray(cd);
    }

    public ComponentDef[] findAllComponentDefs(final Object componentKey) {
        assertParameterIsNotNull(componentKey, "componentKey");
        final List<ComponentDef> componentDefs = new ArrayList<ComponentDef>();
        Traversal.forEachContainer(this, new Traversal.S2ContainerHandler() {
            public Object processContainer(LaContainer container) {
                componentDefs.addAll(Arrays.asList(container.findLocalComponentDefs(componentKey)));
                return null;
            }
        });
        return (ComponentDef[]) componentDefs.toArray(new ComponentDef[componentDefs.size()]);
    }

    public ComponentDef[] findLocalComponentDefs(Object componentKey) {
        ComponentDefHolder holder = (ComponentDefHolder) componentDefMap.get(componentKey);
        if (holder == null || holder.getPosition() > 0) {
            return new ComponentDef[0];
        }
        return toComponentDefArray(holder.getComponentDef());
    }

    protected ComponentDef[] toComponentDefArray(ComponentDef cd) {
        if (cd == null) {
            return new ComponentDef[0];
        } else if (cd instanceof TooManyRegistrationComponentDefImpl) {
            return ((TooManyRegistrationComponentDefImpl) cd).getComponentDefs();
        }
        return new ComponentDef[] { cd };
    }

    protected ComponentDef internalGetComponentDef(Object key) {
        ComponentDefHolder holder = (ComponentDefHolder) componentDefMap.get(key);
        if (holder != null) {
            return holder.getComponentDef();
        }
        if (key instanceof String) {
            String name = (String) key;
            final int index = name.indexOf(NS_SEP);
            if (index > 0) {
                String ns = name.substring(0, index);
                name = name.substring(index + 1);
                if (ns.equals(namespace)) {
                    return internalGetComponentDef(name);
                }
            }
        }
        return null;
    }

    public boolean hasComponentDef(Object componentKey) {
        assertParameterIsNotNull(componentKey, "componentKey");
        return LaContainerBehavior.acquireFromHasComponentDef(this, componentKey) != null;
    }

    public boolean hasDescendant(String path) {
        assertParameterIsNotEmpty(path, "path");
        return descendants.containsKey(path);
    }

    public LaContainer getDescendant(String path) {
        LaContainer descendant = (LaContainer) descendants.get(path);
        if (descendant != null) {
            return descendant;
        }
        throw new ContainerNotRegisteredRuntimeException(path);
    }

    public void registerDescendant(LaContainer descendant) {
        assertParameterIsNotNull(descendant, "descendant");
        descendants.put(descendant.getPath(), descendant);
    }

    public void include(LaContainer child) {
        assertParameterIsNotNull(child, "child");
        children.add(child);
        childPositions.put(child, new Integer(children.size()));
        child.setRoot(getRoot());
        child.addParent(this);
    }

    protected int getContainerPosition(LaContainer container) {
        if (container == this) {
            return 0;
        }
        return ((Integer) childPositions.get(container)).intValue();
    }

    protected boolean isNeedNS(Object key, ComponentDef cd) {
        return key instanceof String && namespace != null;
    }

    // ===================================================================================
    //                                                                     Child Container
    //                                                                     ===============
    @Override
    public LaContainer findChild(String namespace) {
        for (LaContainer child : children) {
            if (namespace.equals(child.getNamespace())) {
                return child;
            }
        }
        for (LaContainer child : children) {
            final LaContainer nestedFound = child.findChild(namespace);
            if (nestedFound != null) {
                return nestedFound;
            }
        }
        return null;
    }

    @Override
    public int getChildSize() {
        return children.size();
    }

    @Override
    public LaContainer getChild(int index) {
        return children.get(index);
    }

    public int getParentSize() {
        return parents.size();
    }

    public LaContainer getParent(int index) {
        return (LaContainer) parents.get(index);
    }

    public void addParent(LaContainer parent) {
        parents.add(parent);
        for (Iterator<Entry<Object, ComponentDefHolder>> it = componentDefMap.entrySet().iterator(); it.hasNext();) {
            final Entry<Object, ComponentDefHolder> entry = (Entry<Object, ComponentDefHolder>) it.next();
            final Object key = entry.getKey();
            final ComponentDefHolder holder = (ComponentDefHolder) entry.getValue();
            final ComponentDef cd = holder.getComponentDef();
            parent.registerMap(key, cd, this);
            if (isNeedNS(key, cd)) {
                parent.registerMap(namespace + NS_SEP + key, cd, this);
            }
        }
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    public void init() {
        try {
            doInit();
        } catch (Throwable e) {
            throwContainerInitFailureException(e);
        }
    }

    protected void doInit() {
        if (inited) {
            return;
        }
        final ExternalContextComponentDefRegister register = getRoot().getExternalContextComponentDefRegister();
        if (register != null) {
            register.registerComponentDefs(this);
        }
        final ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            for (int i = 0; i < getChildSize(); ++i) {
                getChild(i).init();
            }
            for (int i = 0; i < getComponentDefSize(); ++i) {
                getComponentDef(i).init();
            }
            inited = true;
        } finally {
            Thread.currentThread().setContextClassLoader(currentLoader);
        }
    }

    protected void throwContainerInitFailureException(Throwable cause) {
        if (cause instanceof ContainerInitFailureException) { // means nested exception
            throw (ContainerInitFailureException) cause;
        }
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to initialize the container.");
        br.addItem("Path");
        br.addElement(getPath());
        br.addItem("Namespace");
        br.addElement(getNamespace());
        int parentSize = getParentSize();
        if (parentSize > 0) {
            br.addItem("Included by");
            for (int i = 0; i < parentSize; i++) {
                final LaContainer parent = getParent(i);
                if (parent != null) { // just in case
                    br.addElement(parent.getPath());
                }
            }
        }
        final String msg = br.buildExceptionMessage();
        throw new ContainerInitFailureException(msg, cause);
    }

    // ===================================================================================
    //                                                                             Destroy
    //                                                                             =======
    public void destroy() {
        if (!inited) {
            return;
        }
        final ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            for (int i = getComponentDefSize() - 1; 0 <= i; --i) {
                try {
                    getComponentDef(i).destroy();
                } catch (Throwable t) {
                    logger.error("ESSR0017", t);
                }
            }
            for (int i = getChildSize() - 1; 0 <= i; --i) {
                getChild(i).destroy();
            }

            componentDefMap = null;
            componentDefList = null;
            namespace = null;
            path = null;
            children = null;
            childPositions = null;
            parents = null;
            descendants = null;
            externalContext = null;
            externalContextComponentDefRegister = null;
            metaDefSupport = null;
            classLoader = null;
            root = this;
            inited = false;
        } finally {
            Thread.currentThread().setContextClassLoader(currentLoader);
        }
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    public static ComponentDef createTooManyRegistration(Object key, ComponentDef currentComponentDef, ComponentDef newComponentDef) {
        if (currentComponentDef instanceof TooManyRegistrationComponentDef) {
            ((TooManyRegistrationComponentDef) currentComponentDef).addComponentDef(newComponentDef);
            return currentComponentDef;
        } else {
            TooManyRegistrationComponentDef tmrcf = new TooManyRegistrationComponentDefImpl(key);
            tmrcf.addComponentDef(currentComponentDef);
            tmrcf.addComponentDef(newComponentDef);
            return tmrcf;
        }
    }

    /**
     * @param parameter
     * @param name
     */
    protected void assertParameterIsNotNull(Object parameter, String name) {
        if (parameter == null) {
            throw new IllegalArgumentException(name);
        }
    }

    /**
     * @param parameter
     * @param name
     */
    protected void assertParameterIsNotEmpty(String parameter, String name) {
        if (LdiStringUtil.isEmpty(parameter)) {
            throw new IllegalArgumentException(name);
        }
    }

    static class ComponentDefHolder {

        private int position;
        private ComponentDef componentDef;

        public ComponentDefHolder(int position, ComponentDef componentDef) {
            this.position = position;
            this.componentDef = componentDef;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public ComponentDef getComponentDef() {
            return componentDef;
        }

        public void setComponentDef(ComponentDef componentDef) {
            this.componentDef = componentDef;
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final Integer defCount = (componentDefMap != null ? componentDefMap.size() : null);
        return "container:{path=" + path + ", def_count=" + defCount + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        componentDefMap.remove(namespace);
        this.namespace = namespace;
        registerMap(namespace, new LaContainerComponentDef(this, namespace));
    }

    public boolean isInitializeOnCreate() {
        return initializeOnCreate;
    }

    public void setInitializeOnCreate(boolean initializeOnCreate) {
        this.initializeOnCreate = initializeOnCreate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public void setExternalContextComponentDefRegister(ExternalContextComponentDefRegister register) {
        this.externalContextComponentDefRegister = register;
    }

    public void addMetaDef(MetaDef metaDef) {
        metaDefSupport.addMetaDef(metaDef);
    }

    public MetaDef getMetaDef(int index) {
        return metaDefSupport.getMetaDef(index);
    }

    public MetaDef getMetaDef(String name) {
        return metaDefSupport.getMetaDef(name);
    }

    public MetaDef[] getMetaDefs(String name) {
        return metaDefSupport.getMetaDefs(name);
    }

    public int getMetaDefSize() {
        return metaDefSupport.getMetaDefSize();
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}