/*
 * Copyright 2015-2022 the original author or authors.
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
package org.lastaflute.di.core.factory.annohandler;

import org.lastaflute.di.core.factory.annohandler.impl.TigerAnnotationHandler;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AnnotationHandlerFactory {

    private static AnnotationHandler annotationHandler;

    static {
        initialize();
    }

    protected static void initialize() {
        if (annotationHandler != null) {
            return;
        }
        annotationHandler = new TigerAnnotationHandler();
    }

    public static AnnotationHandler getAnnotationHandler() {
        initialize();
        return annotationHandler;
    }

    public static void setAnnotationHandler(AnnotationHandler handler) {
        annotationHandler = handler;
    }
}
