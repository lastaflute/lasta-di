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
package org.lastaflute.di.redefiner.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.annotation.Aspect;
import org.lastaflute.di.core.assembler.AutoBindingDefFactory;
import org.lastaflute.di.core.factory.AspectDefFactory;
import org.lastaflute.di.core.factory.LaContainerFactory;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandler;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandlerFactory;
import org.lastaflute.di.core.factory.conbuilder.impl.AbstractLaContainerBuilder;
import org.lastaflute.di.core.meta.ArgDef;
import org.lastaflute.di.core.meta.AspectDef;
import org.lastaflute.di.core.meta.DestroyMethodDef;
import org.lastaflute.di.core.meta.InitMethodDef;
import org.lastaflute.di.core.meta.impl.ArgDefImpl;
import org.lastaflute.di.core.meta.impl.DestroyMethodDefImpl;
import org.lastaflute.di.core.meta.impl.InitMethodDefImpl;
import org.lastaflute.di.core.meta.impl.InstanceDefFactory;
import org.lastaflute.di.core.meta.impl.LaContainerImpl;
import org.lastaflute.di.redefiner.LaContainerPreparer;
import org.lastaflute.di.redefiner.annotation.Component;
import org.lastaflute.di.redefiner.annotation.Dicon;
import org.lastaflute.di.redefiner.util.ClassBuilderUtils;
import org.lastaflute.di.redefiner.util.CompositeClassLoader;
import org.lastaflute.di.redefiner.util.LaContainerBuilderUtils;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class ClassS2ContainerBuilder extends AbstractLaContainerBuilder {

    public static final String METHODPREFIX_DEFINE = "define";

    public static final String METHODPREFIX_NEW = "new";

    public static final String METHODPREFIX_DESTROY = "destroy";

    public static final String SUFFIX = ".class";

    private static final String JAR_SUFFIX = ".jar!/";

    private static final String DELIMITER = "_";

    public LaContainer build(String path) {
        return build(null, path);
    }

    LaContainer build(LaContainer parent, String path) {
        LaContainer container = createContainer(parent, path);
        Class<? extends LaContainerPreparer> preparerClass = getPreparerClass(path);

        LaContainerPreparer preparer;
        try {
            preparer = preparerClass.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException("Can't instanciate Preparer: " + path, ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Can't instanciate Preparer: " + path, ex);
        }
        container.register(preparer);
        preparer.setContainer(container);

        Dicon dicon = preparerClass.getAnnotation(Dicon.class);
        if (dicon != null && dicon.namespace().length() > 0) {
            container.setNamespace(dicon.namespace());
        }

        preparer.include();
        registerComponentDefs(container, preparer);

        String additionalDiconPath = constructAdditionalDiconPath(path);
        if (LaContainerBuilderUtils.resourceExists(additionalDiconPath, this)) {
            LaContainerBuilderUtils.mergeContainer(container, LaContainerFactory.create(additionalDiconPath));
        }

        return container;
    }

    protected ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    protected String constructAdditionalDiconPath(String path) {
        return constructRedifinitionDiconPath(path, null);
    }

    LaContainer createContainer(LaContainer parent, String path) {
        LaContainer container = new LaContainerImpl();
        container.setPath(path);
        if (parent != null) {
            container.setRoot(parent.getRoot());
        }
        return container;
    }

    void registerComponentDefs(LaContainer container, LaContainerPreparer preparer) {
        Method[] methods = preparer.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            String name = methods[i].getName();
            if (name.startsWith(METHODPREFIX_DEFINE)) {
                registerComponentDef(container, methods[i], preparer);
            }
        }
    }

    void registerComponentDef(LaContainer container, Method method, LaContainerPreparer preparer) {
        ComponentDef componentDef = constructComponentDef(method, preparer);

        if (componentDef.getComponentName() != null) {
            componentDef = redefine(componentDef, container.getPath());
        }

        container.register(componentDef);
    }

    ComponentDef redefine(ComponentDef componentDef, String path) {
        String name = componentDef.getComponentName();
        String diconPath = constructRedifinitionDiconPath(path, name);
        if (!LaContainerBuilderUtils.resourceExists(diconPath, this)) {
            return componentDef;
        }

        LaContainer container = LaContainerFactory.create(diconPath);
        if (!container.hasComponentDef(name)) {
            throw new RuntimeException("Can't find component definition named '" + name + "' in " + diconPath);
        }

        return container.getComponentDef(name);
    }

    protected String constructRedifinitionDiconPath(String path, String name) {
        String body;
        String suffix;
        int dot = path.lastIndexOf('.');
        if (dot < 0) {
            body = path;
            suffix = "";
        } else {
            body = path.substring(0, dot);
            suffix = path.substring(dot);
        }
        if (name == null) {
            name = "";
        }
        return body + DELIMITER + name + suffix;
    }

    ComponentDef constructComponentDef(Method method, LaContainerPreparer preparer) {
        AnnotationHandler annoHandler = AnnotationHandlerFactory.getAnnotationHandler();

        String componentName = ClassBuilderUtils.toComponentName(method.getName().substring(METHODPREFIX_DEFINE.length()));
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new RuntimeException(
                    "Definition method must have only one parameter but " + parameterTypes.length + ": " + method.getName());
        }
        Class<?> componentClass = parameterTypes[0];
        Class<?>[] constructorParameterTypes = new Class[parameterTypes.length - 1];
        System.arraycopy(parameterTypes, 1, constructorParameterTypes, 0, constructorParameterTypes.length);

        ComponentDef componentDef = annoHandler.createComponentDef(componentClass, null);
        componentDef.setComponentName(componentName);
        annoHandler.appendDI(componentDef);
        annoHandler.appendAspect(componentDef);
        annoHandler.appendInterType(componentDef);

        Component componentAnnotation = method.getAnnotation(Component.class);
        if (componentAnnotation != null) {
            componentDef.setInstanceDef(InstanceDefFactory.getInstanceDef(componentAnnotation.instance().getName()));
            componentDef.setAutoBindingDef(AutoBindingDefFactory.getAutoBindingDef(componentAnnotation.autoBinding().getName()));
            componentDef.setExternalBinding(componentAnnotation.externalBinding());
        }

        Aspect aspectAnnotation = method.getAnnotation(Aspect.class);
        if (aspectAnnotation != null) {
            AspectDef aspectDef = AspectDefFactory.createAspectDef(aspectAnnotation.value(), aspectAnnotation.pointcut());
            componentDef.addAspectDef(aspectDef);
        }

        annoHandler.appendInitMethod(componentDef);
        annoHandler.appendDestroyMethod(componentDef);

        InitMethodDef initMethodDef = new InitMethodDefImpl(method);
        ArgDef argDef = new ArgDefImpl(preparer);
        initMethodDef.addArgDef(argDef);
        componentDef.addInitMethodDef(initMethodDef);

        Method destroyMethod = ClassBuilderUtils.findMethod(preparer.getClass(), componentName, METHODPREFIX_DESTROY);
        if (destroyMethod != null) {
            DestroyMethodDef destroyMethodDef = new DestroyMethodDefImpl(destroyMethod);
            argDef = new ArgDefImpl(preparer);
            destroyMethodDef.addArgDef(argDef);
            componentDef.addDestroyMethodDef(destroyMethodDef);
        }

        return componentDef;
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends LaContainerPreparer> getPreparerClass(String path) {
        ClassLoader classLoader = getClassLoaderForLoadingPreparer();
        Class<?> clazz;
        if (path.indexOf(':') < 0) {
            clazz = getClassFromClassName(path, classLoader);
        } else {
            clazz = getClassFromURL(path, classLoader);
        }
        if (clazz == null) {
            throw new RuntimeException("Class not found: " + path);
        }
        if (LaContainerPreparer.class.isAssignableFrom(clazz)) {
            return (Class<? extends LaContainerPreparer>) clazz;
        } else {
            throw new RuntimeException("Not Preparer: " + path);
        }
    }

    protected Class<?> getClassFromURL(String path, ClassLoader classLoader) {
        String[] classNames;
        int jarSuffix = path.indexOf(JAR_SUFFIX);
        if (jarSuffix >= 0) {
            
            classNames =
                    new String[] { path.substring(jarSuffix + JAR_SUFFIX.length(), path.length() - SUFFIX.length()).replace('/', '.') };
        } else {
            path = path.substring(path.indexOf(':') + 1, path.length() - SUFFIX.length()).replace('/', '.');
            List<String> classNameList = new ArrayList<String>();
            int len = path.length();
            for (int i = len - 1; i >= 0; i--) {
                if (path.charAt(i) == '.') {
                    classNameList.add(path.substring(i + 1));
                }
            }
            classNames = classNameList.toArray(new String[0]);
        }
        for (int i = 0; i < classNames.length; i++) {
            try {
                return Class.forName(classNames[i], true, classLoader);
            } catch (ClassNotFoundException ignore) {}
        }
        return null;
    }

    Class<?> getClassFromClassName(String path, ClassLoader classLoader) {
        String className = path.substring(0, path.length() - SUFFIX.length()).replace('/', '.');
        try {
            return Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    ClassLoader getClassLoaderForLoadingPreparer() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        return new CompositeClassLoader(new ClassLoader[] { classLoader, getClass().getClassLoader() });
    }

    public LaContainer include(LaContainer parent, String path) {
        LaContainer child = build(parent, path);
        parent.include(child);
        return child;
    }
}
