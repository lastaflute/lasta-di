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
package org.lastaflute.di.core.meta.impl;

import java.util.List;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.smart.hot.HotdeployUtil;

/**
 * @author jflute
 * @since 0.7.5 (2017/06/25 Sunday at bay maihama)
 */
public class ComponentCreatorAgent {

    public static String prepareCreatorThreadCode() { // static for performance
        return isUseCreatorThreadCode() ? generateCurrentCode() : null;
    }

    public static Object findComponentByCreatorThreadCode(List<ComponentDef> componentDefs) {
        if (isUseCreatorThreadCode()) {
            final String currentCode = generateCurrentCode();
            for (ComponentDef componentDef : componentDefs) {
                final String creatorThreadCode = componentDef.getCreatorThreadCode(); // null allowed
                if (currentCode.equals(creatorThreadCode)) {
                    return componentDef.getComponent(); // first found
                }
            }
        }
        return null;
    }

    protected static boolean isUseCreatorThreadCode() {
        return HotdeployUtil.isThreadContextHotdeploy(); // only for hot-deploy (too-many registration) for now
    }

    protected static String generateCurrentCode() { // static for performance
        final Thread thread = Thread.currentThread();
        final String name = thread.getName(); // to identify even if thread recycle
        return name + "_" + Integer.toHexString(thread.hashCode());
    }
}