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
package org.lastaflute.di.core.autoregister;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.util.LdiClassUtil;

/**
 * コンポーネントを対象にした自動登録を行うための抽象クラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public abstract class AbstractComponentTargetAutoRegister extends AbstractAutoRegister {

    public void registerAll() {
        LaContainer container = getContainer();
        for (int i = 0; i < container.getComponentDefSize(); ++i) {
            ComponentDef cd = container.getComponentDef(i);
            if (isAppliedComponent(cd)) {
                register(cd);
            }
        }
    }

    /**
     * {@link ComponentDef}を登録します。
     * 
     * @param cd
     */
    protected abstract void register(ComponentDef cd);

    /**
     * 処理対象のコンポーネントかどうか返します。
     * 
     * @param cd
     * @return 処理対象のコンポーネントかどうか
     */
    protected boolean isAppliedComponent(final ComponentDef cd) {
        final Class componentClass = cd.getComponentClass();
        if (componentClass == null) {
            return false;
        }

        final String packageName = LdiClassUtil.getPackageName(componentClass);
        final String shortClassName = LdiClassUtil.getShortClassName(componentClass);
        for (int i = 0; i < getClassPatternSize(); ++i) {
            final ClassPattern cp = getClassPattern(i);
            if (isIgnore(packageName, shortClassName)) {
                return false;
            }
            if (cp.isAppliedPackageName(packageName) && cp.isAppliedShortClassName(shortClassName)) {
                return true;
            }
        }
        return false;
    }
}
