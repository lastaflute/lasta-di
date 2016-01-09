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
import org.lastaflute.di.core.meta.ArgDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ArgDefSupport {

    private final List<ArgDef> argDefs = new ArrayList<ArgDef>();
    private LaContainer container;

    public ArgDefSupport() {
    }

    public void addArgDef(ArgDef argDef) {
        if (container != null) {
            argDef.setContainer(container);
        }
        argDefs.add(argDef);
    }

    public int getArgDefSize() {
        return argDefs.size();
    }

    public ArgDef getArgDef(int index) {
        return (ArgDef) argDefs.get(index);
    }

    public void setContainer(LaContainer container) {
        this.container = container;
        for (int i = 0; i < getArgDefSize(); ++i) {
            getArgDef(i).setContainer(container);
        }
    }
}
