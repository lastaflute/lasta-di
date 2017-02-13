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
package org.lastaflute.di.helper.beans.factory;

import java.util.Map;

import org.lastaflute.di.Disposable;
import org.lastaflute.di.DisposableUtil;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.impl.BeanDescImpl;
import org.lastaflute.di.util.LdiMapUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class BeanDescFactory {

    private static volatile boolean initialized;

    private static Map<Class<?>, BeanDesc> beanDescCache = LdiMapUtil.createHashMap(1024);

    static {
        initialize();
    }

    protected BeanDescFactory() {
    }

    public static BeanDesc getBeanDesc(Class<?> clazz) {
        if (!initialized) {
            initialize();
        }
        BeanDesc beanDesc = beanDescCache.get(clazz);
        if (beanDesc == null) {
            beanDesc = new BeanDescImpl(clazz);
            beanDescCache.put(clazz, beanDesc);
        }
        return beanDesc;
    }

    public static void initialize() {
        DisposableUtil.add(new Disposable() {
            public void dispose() {
                clear();
            }
        });
        initialized = true;
    }

    public static void clear() {
        beanDescCache.clear();
        initialized = false;
    }
}
