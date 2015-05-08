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
package org.lastaflute.di.core.creator;

import java.lang.reflect.Modifier;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.customizer.ComponentCustomizer;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandler;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandlerFactory;
import org.lastaflute.di.core.meta.AutoBindingDef;
import org.lastaflute.di.core.meta.InstanceDef;
import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.naming.NamingConvention;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ComponentCreatorImpl implements ComponentCreator {

    private final NamingConvention namingConvention;

    public static final String instanceDef_BINDING = "bindingType=may";
    private InstanceDef instanceDef;

    public static final String autoBindingDef_BINDING = "bindingType=may";
    private AutoBindingDef autoBindingDef;

    public static final String externalBinding_BINDING = "bindingType=may";
    private boolean externalBinding = false;

    public static final String enableInterface_BINDING = "bindingType=may";
    private boolean enableInterface = false;

    public static final String enableAbstract_BINDING = "bindingType=may";
    private boolean enableAbstract = false;

    private String nameSuffix;
    private ComponentCustomizer customizer;

    public ComponentCreatorImpl(NamingConvention namingConvention) {
        if (namingConvention == null) {
            throw new EmptyRuntimeException("namingConvetion");
        }
        this.namingConvention = namingConvention;
    }

    public NamingConvention getNamingConvention() {
        return namingConvention;
    }

    public InstanceDef getInstanceDef() {
        return instanceDef;
    }

    public void setInstanceDef(InstanceDef instanceDef) {
        this.instanceDef = instanceDef;
    }

    public AutoBindingDef getAutoBindingDef() {
        return autoBindingDef;
    }

    public void setAutoBindingDef(AutoBindingDef autoBindingDef) {
        this.autoBindingDef = autoBindingDef;
    }

    public boolean isExternalBinding() {
        return externalBinding;
    }

    public void setExternalBinding(boolean externalBinding) {
        this.externalBinding = externalBinding;
    }

    public boolean isEnableInterface() {
        return enableInterface;
    }

    public void setEnableInterface(boolean enableInterface) {
        this.enableInterface = enableInterface;
    }

    public boolean isEnableAbstract() {
        return enableAbstract;
    }

    public void setEnableAbstract(boolean enableAbstract) {
        this.enableAbstract = enableAbstract;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    protected ComponentCustomizer getCustomizer() {
        return customizer;
    }

    protected void setCustomizer(ComponentCustomizer customizer) {
        this.customizer = customizer;
    }

    public ComponentDef createComponentDef(Class<?> componentClass) {
        if (!namingConvention.isTargetClassName(componentClass.getName(), nameSuffix)) {
            return null;
        }
        final Class<?> targetClass = namingConvention.toCompleteClass(componentClass);
        if (targetClass.isInterface()) {
            if (!isEnableInterface()) {
                return null;
            }
        } else if (Modifier.isAbstract(targetClass.getModifiers())) {
            if (!isEnableAbstract()) {
                return null;
            }
        }
        final AnnotationHandler handler = AnnotationHandlerFactory.getAnnotationHandler();
        final ComponentDef cd = handler.createComponentDef(targetClass, instanceDef, autoBindingDef, externalBinding);
        if (cd.getComponentName() == null) {
            cd.setComponentName(namingConvention.fromClassNameToComponentName(targetClass.getName()));
        }
        handler.appendDI(cd);
        customize(cd);
        handler.appendInitMethod(cd);
        handler.appendDestroyMethod(cd);
        handler.appendAspect(cd);
        handler.appendInterType(cd);
        return cd;
    }

    public ComponentDef createComponentDef(String componentName) {
        if (!isTargetComponentName(componentName)) {
            return null;
        }
        final Class<?> componentClass = namingConvention.fromComponentNameToClass(componentName);
        if (componentClass == null) {
            return null;
        }
        return createComponentDef(componentClass);
    }

    public boolean isTargetComponentName(String componentName) {
        return componentName.endsWith(nameSuffix);
    }

    protected void customize(ComponentDef componentDef) {
        if (customizer != null) {
            customizer.customize(componentDef);
        }
    }
}