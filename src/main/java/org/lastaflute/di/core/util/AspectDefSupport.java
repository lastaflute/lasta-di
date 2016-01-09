/*
 * Copyright 2015-2016 the original author or authors.
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
import org.lastaflute.di.core.meta.AspectDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AspectDefSupport {

    private List<AspectDef> aspectDefs = new ArrayList<AspectDef>();

    private LaContainer container;

    public AspectDefSupport() {
    }

    public void addAspectDef(AspectDef aspectDef) {
        if (container != null) {
            aspectDef.setContainer(container);
        }
        aspectDefs.add(aspectDef);
    }

    public void addAspectDef(int index, AspectDef aspectDef) {
        if (container != null) {
            aspectDef.setContainer(container);
        }
        aspectDefs.add(index, aspectDef);
    }

    public int getAspectDefSize() {
        return aspectDefs.size();
    }

    public AspectDef getAspectDef(int index) {
        return aspectDefs.get(index);
    }

    public void setContainer(LaContainer container) {
        this.container = container;
        for (int i = 0; i < getAspectDefSize(); ++i) {
            getAspectDef(i).setContainer(container);
        }
    }
}