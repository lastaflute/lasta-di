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
package org.dbflute.lasta.di.core.factory.defbuilder.impl;

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.annotation.InterType;
import org.dbflute.lasta.di.core.expression.ScriptingExpression;
import org.dbflute.lasta.di.core.factory.annohandler.AnnotationHandler;
import org.dbflute.lasta.di.core.factory.defbuilder.IntertypeDefBuilder;
import org.dbflute.lasta.di.core.meta.InterTypeDef;
import org.dbflute.lasta.di.core.meta.impl.InterTypeDefImpl;

/**
 * {@link InterType}アノテーションを読み取り{@link InterTypeDef}を作成するコンポーネントの実装クラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class S2IntertypeDefBuilder implements IntertypeDefBuilder {

    public void appendIntertypeDef(final AnnotationHandler annotationHandler, final ComponentDef componentDef) {
        final Class<?> componentClass = componentDef.getComponentClass();
        if (componentClass == null) {
            return;
        }

        final InterType interType = componentClass.getAnnotation(InterType.class);
        if (interType != null) {
            for (String interTypeName : interType.value()) {
                final InterTypeDef interTypeDef = new InterTypeDefImpl();
                interTypeDef.setExpression(new ScriptingExpression(interTypeName));
                componentDef.addInterTypeDef(interTypeDef);
            }
        }
    }

}
