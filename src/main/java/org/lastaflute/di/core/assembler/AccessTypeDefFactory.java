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
package org.lastaflute.di.core.assembler;

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.core.exception.IllegalAccessTypeDefRuntimeException;
import org.lastaflute.di.core.meta.AccessTypeDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AccessTypeDefFactory {

    public static final AccessTypeDef PROPERTY = new AccessTypePropertyDef();
    public static final AccessTypeDef FIELD = new AccessTypeFieldDef();

    protected AccessTypeDefFactory() {
    }

    private static Map<String, AccessTypeDef> accessTypeDefs = new HashMap<String, AccessTypeDef>();

    static {
        addAccessTypeDef(PROPERTY);
        addAccessTypeDef(FIELD);
    }

    public static void addAccessTypeDef(final AccessTypeDef accessTypeDef) {
        accessTypeDefs.put(accessTypeDef.getName(), accessTypeDef);
    }

    public static boolean existAccessTypeDef(String name) {
        return accessTypeDefs.containsKey(name);
    }

    public static AccessTypeDef getAccessTypeDef(String name) {
        if (!existAccessTypeDef(name)) {
            throw new IllegalAccessTypeDefRuntimeException(name);
        }
        return (AccessTypeDef) accessTypeDefs.get(name);
    }
}
