/*
 * Copyright 2015-2020 the original author or authors.
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
package org.lastaflute.di.core.assembler;

import org.lastaflute.di.core.meta.AutoBindingDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractAutoBindingDef implements AutoBindingDef {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String name;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected AbstractAutoBindingDef(String name) {
        this.name = name;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AutoBindingDef)) {
            return false;
        }
        final AutoBindingDef other = (AutoBindingDef) o;
        return name == null ? other.getName() == null : name.equals(other.getName());
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getName() {
        return name;
    }
}