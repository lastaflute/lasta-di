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

import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.meta.DestroyMethodDef;

/**
 * {@link DestroyMethodDef}を補助するクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class DestroyMethodDefSupport {

    private List methodDefs = new ArrayList();

    private LaContainer container;

    /**
     * {@link DestroyMethodDefSupport}を作成します。
     */
    public DestroyMethodDefSupport() {
    }

    /**
     * {@link DestroyMethodDef}を追加します。
     * 
     * @param methodDef
     */
    public void addDestroyMethodDef(DestroyMethodDef methodDef) {
        if (container != null) {
            methodDef.setContainer(container);
        }
        methodDefs.add(methodDef);
    }

    /**
     * {@link DestroyMethodDef}の数を返します。
     * 
     * @return {@link DestroyMethodDef}
     */
    public int getDestroyMethodDefSize() {
        return methodDefs.size();
    }

    /**
     * {@link DestroyMethodDef}を返します。
     * 
     * @param index
     * @return {@link DestroyMethodDef}
     */
    public DestroyMethodDef getDestroyMethodDef(int index) {
        return (DestroyMethodDef) methodDefs.get(index);
    }

    /**
     * {@link LaContainer}を設定します。
     * 
     * @param container
     */
    public void setContainer(LaContainer container) {
        this.container = container;
        for (int i = 0; i < getDestroyMethodDefSize(); ++i) {
            getDestroyMethodDef(i).setContainer(container);
        }
    }
}