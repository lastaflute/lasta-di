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
package org.lastaflute.di.core.aop.intertype;

import org.lastaflute.di.core.aop.InterType;
import org.lastaflute.di.util.LdiArrayUtil;

import javassist.CtClass;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class InterTypeChain implements InterType {

    protected InterType[] interTypes = new InterType[0];

    public InterTypeChain() {
    }

    public void add(final InterType interType) {
        interTypes = (InterType[]) LdiArrayUtil.add(interTypes, interType);
    }

    public void introduce(final Class<?> targetClass, final CtClass enhancedClass) {
        for (int i = 0; i < interTypes.length; ++i) {
            interTypes[i].introduce(targetClass, enhancedClass);
        }
    }
}
