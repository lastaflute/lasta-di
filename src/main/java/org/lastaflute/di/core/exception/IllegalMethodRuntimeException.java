/*
 * Copyright 2015-2021 the original author or authors.
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
public class IllegalMethodRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = -9114586009590848186L;

    private Class<?> componentClass_;
    private String methodName_;

    public IllegalMethodRuntimeException(Class<?> componentClass, String methodName, Throwable cause) {
        super("ESSR0060", new Object[] { componentClass.getName(), methodName, cause }, cause);
        componentClass_ = componentClass;
        methodName_ = methodName;
    }

    public Class<?> getComponentClass() {
        return componentClass_;
    }

    public String getMethodName() {
        return methodName_;
    }
}