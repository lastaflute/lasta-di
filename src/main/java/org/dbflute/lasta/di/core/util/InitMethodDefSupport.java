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
package org.dbflute.lasta.di.core.util;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.lasta.di.core.LaContainer;
import org.dbflute.lasta.di.core.meta.InitMethodDef;

/**
 * {@link InitMethodDef}を補助するクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class InitMethodDefSupport {

    private List methodDefs = new ArrayList();

    private LaContainer container;

    /**
     * {@link InitMethodDefSupport}を作成します。
     */
    public InitMethodDefSupport() {
    }

    /**
     * {@link InitMethodDef}を追加します。
     * 
     * @param methodDef
     */
    public void addInitMethodDef(InitMethodDef methodDef) {
        if (container != null) {
            methodDef.setContainer(container);
        }
        methodDefs.add(methodDef);
    }

    /**
     * {@link InitMethodDef}の数を返します。
     * 
     * @return {@link InitMethodDef}の数
     */
    public int getInitMethodDefSize() {
        return methodDefs.size();
    }

    /**
     * {@link InitMethodDef}を返します。
     * 
     * @param index
     * @return {@link InitMethodDef}
     */
    public InitMethodDef getInitMethodDef(int index) {
        return (InitMethodDef) methodDefs.get(index);
    }

    /**
     * {@link LaContainer}を返します。
     * 
     * @param container
     */
    public void setContainer(LaContainer container) {
        this.container = container;
        for (int i = 0; i < getInitMethodDefSize(); ++i) {
            getInitMethodDef(i).setContainer(container);
        }
    }
}