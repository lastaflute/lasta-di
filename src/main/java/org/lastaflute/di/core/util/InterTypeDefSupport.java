/*
 * Copyright 2015-2017 the original author or authors.
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
import org.lastaflute.di.core.meta.InterTypeDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class InterTypeDefSupport {

    private final List<InterTypeDef> interTypeDefs = new ArrayList<InterTypeDef>();
    private LaContainer container;

    public InterTypeDefSupport() {
    }

    public void addInterTypeDef(InterTypeDef interTypeDef) {
        if (container != null) {
            interTypeDef.setContainer(container);
        }
        interTypeDefs.add(interTypeDef);
    }

    public int getInterTypeDefSize() {
        return interTypeDefs.size();
    }

    public InterTypeDef getInterTypeDef(int index) {
        return (InterTypeDef) interTypeDefs.get(index);
    }

    public void setContainer(LaContainer container) {
        this.container = container;
        for (int i = 0; i < getInterTypeDefSize(); ++i) {
            getInterTypeDef(i).setContainer(container);
        }
    }
}
