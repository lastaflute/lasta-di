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
package org.dbflute.lasta.di.core.factory.defbuilder;

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.factory.annohandler.AnnotationHandler;
import org.dbflute.lasta.di.core.meta.AspectDef;

/**
 * Tigerアノテーションを読み取り{@link AspectDef}を作成するインターフェースです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public interface AspectDefBuilder {

    /**
     * コンポーネントからTigerアノテーションを読み取り{@link AspectDef}を作成し、コンポーネント定義に追加します。
     * 
     * @param annotationHandler
     *            このメソッドを呼び出しているアノテーションハンドラ
     * @param componentDef
     *            コンポーネント定義
     */
    void appendAspectDef(AnnotationHandler annotationHandler, ComponentDef componentDef);

}
