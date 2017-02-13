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
package org.lastaflute.di.core.exception;

import org.lastaflute.di.exception.SRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class IllegalConstructorRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 1454032979718620824L;

    private Class<?> componentClass_;

    public IllegalConstructorRuntimeException(Class<?> componentClass, Throwable cause) {
        super("ESSR0058", new Object[] { componentClass.getName(), cause }, cause);
        componentClass_ = componentClass;
    }

    public Class<?> getComponentClass() {
        return componentClass_;
    }
}