/*
 * Copyright 2015-2022 the original author or authors.
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
import org.lastaflute.di.core.LaContainer;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractAutoRegister {

    public static final String INIT_METHOD = "registerAll";

    private LaContainer container;
    private final List<ClassPattern> classPatterns = new ArrayList<ClassPattern>();
    private final List<ClassPattern> ignoreClassPatterns = new ArrayList<ClassPattern>();

    public LaContainer getContainer() {
        return container;
    }

    public void setContainer(LaContainer container) {
        this.container = container;
    }

    public int getClassPatternSize() {
        return classPatterns.size();
    }

    public ClassPattern getClassPattern(int index) {
        return (ClassPattern) classPatterns.get(index);
    }

    public void addClassPattern(String packageName, String shortClassNames) {
        addClassPattern(new ClassPattern(packageName, shortClassNames));
    }

    public void addClassPattern(ClassPattern classPattern) {
        classPatterns.add(classPattern);
    }

    public void addIgnoreClassPattern(String packageName, String shortClassNames) {
        addIgnoreClassPattern(new ClassPattern(packageName, shortClassNames));
    }

    public void addIgnoreClassPattern(ClassPattern classPattern) {
        ignoreClassPatterns.add(classPattern);
    }

    public abstract void registerAll();

    protected boolean hasComponentDef(String name) {
        return findComponentDef(name) != null;
    }

    protected ComponentDef findComponentDef(String name) {
        if (name == null) {
            return null;
        }
        LaContainer container = getContainer();
        for (int i = 0; i < container.getComponentDefSize(); ++i) {
            ComponentDef cd = container.getComponentDef(i);
            if (name.equals(cd.getComponentName())) {
                return cd;
            }
        }
        return null;
    }

    protected boolean isIgnore(String packageName, String shortClassName) {
        if (ignoreClassPatterns.isEmpty()) {
            return false;
        }
        for (int i = 0; i < ignoreClassPatterns.size(); ++i) {
            ClassPattern cp = (ClassPattern) ignoreClassPatterns.get(i);
            if (!cp.isAppliedPackageName(packageName)) {
                continue;
            }
            if (cp.isAppliedShortClassName(shortClassName)) {
                return true;
            }
        }
        return false;
    }
}