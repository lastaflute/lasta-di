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
package org.lastaflute.di;

import java.beans.Introspector;
import java.util.LinkedList;

import org.lastaflute.di.helper.log.LaLogger;
import org.lastaflute.di.util.LdiDriverManagerUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class DisposableUtil {

    protected static final LinkedList<Disposable> disposables = new LinkedList<Disposable>();

    public static synchronized void add(Disposable disposable) {
        disposables.add(disposable);
    }

    public static synchronized void remove(Disposable disposable) {
        disposables.remove(disposable);
    }

    public static synchronized void dispose() {
        while (!disposables.isEmpty()) {
            final Disposable disposable = disposables.removeLast();
            try {
                disposable.dispose();
            } catch (final Throwable t) {
                t.printStackTrace(); // must not use Logger.
            }
        }
        disposables.clear();
        Introspector.flushCaches();
        LaLogger.dispose();
    }

    public static void deregisterAllDrivers() {
        LdiDriverManagerUtil.deregisterAllDrivers();
    }
}
