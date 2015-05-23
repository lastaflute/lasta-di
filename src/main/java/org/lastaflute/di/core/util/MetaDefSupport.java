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
import org.lastaflute.di.core.meta.MetaDef;

/**
 * {@link MetaDef}を補助するクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class MetaDefSupport {

    private List metaDefs = new ArrayList();

    private LaContainer container;

    /**
     * {@link MetaDefSupport}を作成します。
     */
    public MetaDefSupport() {
    }

    /**
     * {@link MetaDefSupport}を作成します。
     * 
     * @param container
     */
    public MetaDefSupport(LaContainer container) {
        setContainer(container);
    }

    /**
     * {@link MetaDef}を追加します。
     * 
     * @param metaDef
     */
    public void addMetaDef(MetaDef metaDef) {
        if (container != null) {
            metaDef.setContainer(container);
        }
        metaDefs.add(metaDef);
    }

    /**
     * {@link MetaDef}の数を返します。
     * 
     * @return {@link MetaDef}の数
     */
    public int getMetaDefSize() {
        return metaDefs.size();
    }

    /**
     * {@link MetaDef}を返します。
     * 
     * @param index
     * @return {@link MetaDef}
     */
    public MetaDef getMetaDef(int index) {
        return (MetaDef) metaDefs.get(index);
    }

    /**
     * {@link MetaDef}を返します。
     * 
     * @param name
     * @return {@link MetaDef}
     */
    public MetaDef getMetaDef(String name) {
        for (int i = 0; i < getMetaDefSize(); ++i) {
            MetaDef metaDef = getMetaDef(i);
            if (name == null && metaDef.getName() == null || name != null && name.equalsIgnoreCase(metaDef.getName())) {
                return metaDef;
            }
        }
        return null;
    }

    /**
     * {@link MetaDef}の配列を返します。
     * 
     * @param name
     * @return {@link MetaDef}の配列
     */
    public MetaDef[] getMetaDefs(String name) {
        List defs = new ArrayList();
        for (int i = 0; i < getMetaDefSize(); ++i) {
            MetaDef metaDef = getMetaDef(i);
            if (name == null && metaDef.getName() == null || name != null && name.equalsIgnoreCase(metaDef.getName())) {
                defs.add(metaDef);
            }
        }
        return (MetaDef[]) defs.toArray(new MetaDef[defs.size()]);
    }

    /**
     * {@link LaContainer}を返します。
     * 
     * @param container
     */
    public void setContainer(LaContainer container) {
        this.container = container;
        for (int i = 0; i < getMetaDefSize(); ++i) {
            getMetaDef(i).setContainer(container);
        }
    }
}