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

import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.util.LdiResourcesUtil;
import org.lastaflute.di.util.ClassTraversal.ClassHandler;
import org.lastaflute.di.util.LdiResourcesUtil.Resources;

/**
 * jarファイルに含まれているあるいはファイルシステム上(WEBINF/classesとか)にあるコンポーネントを自動登録するためのクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class ComponentAutoRegister extends AbstractComponentAutoRegister implements ClassHandler {

    /**
     * 参照するクラスのリストです。
     */
    protected List referenceClasses = new ArrayList();

    /**
     * デフォルトのコンストラクタです。
     */
    public ComponentAutoRegister() {
    }

    /**
     * jarファイルに含まれているクラスを追加します。jarファイルに含まれているならどのクラスでもOKです。
     * このクラスを参照してjarファイルの物理的な位置を特定します。
     * 
     * @param referenceClass
     */
    public void addReferenceClass(final Class referenceClass) {
        referenceClasses.add(referenceClass);
    }

    public void registerAll() {
        for (int i = 0; i < referenceClasses.size(); ++i) {
            final Class referenceClass = (Class) referenceClasses.get(i);
            final Resources resources = LdiResourcesUtil.getResourcesType(referenceClass);
            try {
                resources.forEach(this);
            } finally {
                resources.close();
            }
        }
    }

}
