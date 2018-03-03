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
package org.lastaflute.di.core.autoregister;

import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.customizer.ComponentCustomizer;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandler;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandlerFactory;
import org.lastaflute.di.core.meta.AutoBindingDef;
import org.lastaflute.di.core.meta.InstanceDef;
import org.lastaflute.di.util.ClassTraversal.ClassHandler;
import org.lastaflute.di.util.LdiClassUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractComponentAutoRegister extends AbstractAutoRegister implements ClassHandler {

    protected static final String CLASS_SUFFIX = ".class";

    public static final String autoNaming_BINDING = "bindingType=may";

    private AutoNaming autoNaming = new DefaultAutoNaming();

    public static final String instanceDef_BINDING = "bindingType=may";

    private InstanceDef instanceDef;

    public static final String autoBindingDef_BINDING = "bindingType=may";

    private AutoBindingDef autoBindingDef;

    private boolean externalBinding = false;

    public static final String customizer_BINDING = "bindingType=may";

    private ComponentCustomizer customizer;

    public AutoNaming getAutoNaming() {
        return autoNaming;
    }

    public void setAutoNaming(AutoNaming autoNaming) {
        this.autoNaming = autoNaming;
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

    public ComponentCustomizer getCustomizer() {
        return customizer;
    }

    public void setCustomizer(ComponentCustomizer customizer) {
        this.customizer = customizer;
    }

    public void processClass(final String packageName, final String shortClassName) {
        if (isIgnore(packageName, shortClassName)) {
            return;
        }

        for (int i = 0; i < getClassPatternSize(); ++i) {
            final ClassPattern cp = getClassPattern(i);
            if (cp.isAppliedPackageName(packageName) && cp.isAppliedShortClassName(shortClassName)) {
                register(LdiClassUtil.concatName(packageName, shortClassName));
                return;
            }
        }
    }

    protected void register(final String className) {
        final AnnotationHandler annoHandler = AnnotationHandlerFactory.getAnnotationHandler();
        final ComponentDef cd = annoHandler.createComponentDef(className, instanceDef, autoBindingDef, externalBinding);
        if (cd.getComponentName() == null) {
            String[] names = LdiClassUtil.splitPackageAndShortClassName(className);
            cd.setComponentName(autoNaming.defineName(names[0], names[1]));
        }
        annoHandler.appendDI(cd);
        customize(cd);
        annoHandler.appendInitMethod(cd);
        annoHandler.appendDestroyMethod(cd);
        annoHandler.appendAspect(cd);
        annoHandler.appendInterType(cd);
        getContainer().register(cd);
    }

    protected void customize(ComponentDef componentDef) {
        if (customizer != null) {
            customizer.customize(componentDef);
        }
    }

    protected String[] getTargetPackages() {
        final List<String> result = new ArrayList<String>();
        for (int i = 0; i < getClassPatternSize(); ++i) {
            final String packageName = getClassPattern(i).getPackageName();
            boolean append = true;
            for (int j = 0; j < result.size(); ++j) {
                final String root = (String) result.get(j);
                if (packageName.equals(root)) {
                    append = false;
                    break;
                } else if (packageName.startsWith(root)) {
                    append = false;
                    break;
                } else if (root.startsWith(packageName)) {
                    result.set(j, packageName);
                    append = false;
                    break;
                }
            }
            if (append) {
                result.add(packageName);
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }
}
