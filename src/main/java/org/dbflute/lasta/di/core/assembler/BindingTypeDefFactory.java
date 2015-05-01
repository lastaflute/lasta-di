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
package org.dbflute.lasta.di.core.assembler;

import java.util.HashMap;
import java.util.Map;

import org.dbflute.lasta.di.core.exception.IllegalBindingTypeDefRuntimeException;
import org.dbflute.lasta.di.core.meta.BindingTypeDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class BindingTypeDefFactory {

    public static final BindingTypeDef MUST = new BindingTypeMustDef(BindingTypeDef.MUST_NAME);
    public static final BindingTypeDef SHOULD = new BindingTypeShouldDef(BindingTypeDef.SHOULD_NAME);
    public static final BindingTypeDef MAY = new BindingTypeMayDef(BindingTypeDef.MAY_NAME);
    public static final BindingTypeDef NONE = new BindingTypeNoneDef(BindingTypeDef.NONE_NAME);

    private static final Map<String, BindingTypeDef> bindingTypeDefs = new HashMap<String, BindingTypeDef>();

    static {
        addBindingTypeDef(MUST);
        addBindingTypeDef(SHOULD);
        addBindingTypeDef(MAY);
        addBindingTypeDef(NONE);
    }

    protected BindingTypeDefFactory() {
    }

    public static void addBindingTypeDef(BindingTypeDef bindingTypeDef) {
        bindingTypeDefs.put(bindingTypeDef.getName(), bindingTypeDef);
    }

    public static boolean existBindingTypeDef(String name) {
        return bindingTypeDefs.containsKey(name);
    }

    public static BindingTypeDef getBindingTypeDef(String name) {
        if (!existBindingTypeDef(name)) {
            throw new IllegalBindingTypeDefRuntimeException(name);
        }
        return bindingTypeDefs.get(name);
    }
}