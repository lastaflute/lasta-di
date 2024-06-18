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
package org.lastaflute.di.core.meta.impl;

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.core.exception.IllegalInstanceDefRuntimeException;
import org.lastaflute.di.core.meta.InstanceDef;

/**
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class InstanceDefFactory {

    public static final InstanceDef SINGLETON = new InstanceSingletonDef(InstanceDef.SINGLETON_NAME);
    public static final InstanceDef PROTOTYPE = new InstancePrototypeDef(InstanceDef.PROTOTYPE_NAME);
    public static final InstanceDef APPLICATION = new InstanceApplicationDef(InstanceDef.APPLICATION_NAME);
    public static final InstanceDef SESSION = new InstanceSessionDef(InstanceDef.SESSION_NAME);
    public static final InstanceDef REQUEST = new InstanceRequestDef(InstanceDef.REQUEST_NAME);
    public static final InstanceDef OUTER = new InstanceOuterDef(InstanceDef.OUTER_NAME);

    private static Map<String, InstanceDef> instanceDefs = new HashMap<String, InstanceDef>();

    static {
        addInstanceDef(SINGLETON);
        addInstanceDef(PROTOTYPE);
        addInstanceDef(APPLICATION);
        addInstanceDef(SESSION);
        addInstanceDef(REQUEST);
        addInstanceDef(OUTER);
    }

    protected InstanceDefFactory() {
    }

    public static void addInstanceDef(InstanceDef instanceDef) {
        instanceDefs.put(instanceDef.getName(), instanceDef);
    }

    public static boolean existInstanceDef(String name) {
        return instanceDefs.containsKey(name);
    }

    public static InstanceDef getInstanceDef(String name) {
        if (!instanceDefs.containsKey(name)) {
            throw new IllegalInstanceDefRuntimeException(name);
        }
        return instanceDefs.get(name);
    }
}
