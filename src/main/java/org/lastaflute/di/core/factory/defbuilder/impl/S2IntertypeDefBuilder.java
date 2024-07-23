/*
 * Copyright 2015-2024 the original author or authors.
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
package org.lastaflute.di.core.factory.defbuilder.impl;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.annotation.InterType;
import org.lastaflute.di.core.expression.ScriptingExpression;
import org.lastaflute.di.core.factory.annohandler.AnnotationHandler;
import org.lastaflute.di.core.factory.defbuilder.IntertypeDefBuilder;
import org.lastaflute.di.core.meta.InterTypeDef;
import org.lastaflute.di.core.meta.impl.InterTypeDefImpl;

/**
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
