/*
 * Copyright 2015-2018 the original author or authors.
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
package org.lastaflute.di.exception;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class IllegalAccessRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = -3649900343028907465L;

    private Class<?> targetClass;

    public IllegalAccessRuntimeException(Class<?> targetClass, IllegalAccessException cause) {
        super("ESSR0042", new Object[] { targetClass.getName(), cause }, cause);
        this.targetClass = targetClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
