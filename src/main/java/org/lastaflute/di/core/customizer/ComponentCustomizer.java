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
package org.lastaflute.di.core.customizer;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.creator.ComponentCreator;

/**
 * コンポーネント定義をカスタマイズします。
 * <p>
 * SMART deployを使用すると、 規約に基づいて{@link ComponentDef コンポーネント定義}が作成されます。
 * これらのコンポーネント定義をカスタマイズしたい場合に、 このインターフェースを実装したクラスを使用します。
 * </p>
 * 
 * @author modified by jflute (originated in Seasar)
 * @author belltree
 * 
 * @see ComponentCreator
 * @see org.lastaflute.di.core.customizer.AspectCustomizer
 */
public interface ComponentCustomizer {

    /**
     * 指定されたコンポーネント定義をカスタマイズします。
     * 
     * @param componentDef
     *            コンポーネント定義
     */
    void customize(ComponentDef componentDef);
}
