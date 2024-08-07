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
package org.lastaflute.di.exception;

import javassist.CannotCompileException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class CannotCompileRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1329201462786753994L;

    public CannotCompileRuntimeException(String msg, CannotCompileException cause) {
        super(msg, cause); // "ESSR0017" is used in old days
    }
}
