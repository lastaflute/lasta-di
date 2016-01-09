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
package org.lastaflute.di.core.customizer;

import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.autoregister.ClassPattern;
import org.lastaflute.di.util.LdiClassUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractCustomizer implements ComponentCustomizer {

    public static final String targetInterface_BINDING = "bindingType=may";
    protected final List<ClassPattern> classPatterns = new ArrayList<ClassPattern>();
    protected final List<ClassPattern> ignoreClassPatterns = new ArrayList<ClassPattern>();
    protected Class<?> targetInterface;

    public void addClassPattern(final String packageName, final String shortClassNames) {
        addClassPattern(new ClassPattern(packageName, shortClassNames));
    }

    public void addClassPattern(final ClassPattern classPattern) {
        classPatterns.add(classPattern);
    }

    public void addIgnoreClassPattern(final String packageName, final String shortClassNames) {
        addIgnoreClassPattern(new ClassPattern(packageName, shortClassNames));
    }

    public void addIgnoreClassPattern(final ClassPattern classPattern) {
        ignoreClassPatterns.add(classPattern);
    }

    public void setTargetInterface(Class<?> targetInterface) {
        if (!targetInterface.isInterface()) {
            throw new IllegalArgumentException(targetInterface.getName());
        }
        this.targetInterface = targetInterface;
    }

    public void customize(final ComponentDef componentDef) {
        if (!isMatchClassPattern(componentDef)) {
            return;
        }
        if (!isMatchTargetInterface(componentDef)) {
            return;
        }
        doCustomize(componentDef);
    }

    protected boolean isMatchClassPattern(final ComponentDef componentDef) {
        if (classPatterns.isEmpty() && ignoreClassPatterns.isEmpty()) {
            return true;
        }
        final Class<?> clazz = componentDef.getComponentClass();
        if (clazz == null) {
            return false;
        }
        final String packageName = LdiClassUtil.getPackageName(clazz);
        final String shortClassName = LdiClassUtil.getShortClassName(clazz);
        for (int i = 0; i < ignoreClassPatterns.size(); ++i) {
            final ClassPattern cp = (ClassPattern) ignoreClassPatterns.get(i);
            if (cp.isAppliedPackageName(packageName) && cp.isAppliedShortClassName(shortClassName)) {
                return false;
            }
        }
        if (classPatterns.isEmpty()) {
            return true;
        }
        for (int i = 0; i < classPatterns.size(); ++i) {
            final ClassPattern cp = (ClassPattern) classPatterns.get(i);
            if (cp.isAppliedPackageName(packageName) && cp.isAppliedShortClassName(shortClassName)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isMatchTargetInterface(final ComponentDef componentDef) {
        if (targetInterface == null) {
            return true;
        }
        final Class<?> clazz = componentDef.getComponentClass();
        if (clazz == null) {
            return false;
        }
        return targetInterface.isAssignableFrom(clazz);
    }

    protected abstract void doCustomize(final ComponentDef componentDef);
}
