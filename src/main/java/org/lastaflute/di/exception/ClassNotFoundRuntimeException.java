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

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ClassNotFoundRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = -9022468864937761059L;

    private String className;

    public ClassNotFoundRuntimeException(ClassNotFoundException cause) {
        this(null, cause);
    }

    public ClassNotFoundRuntimeException(String className, ClassNotFoundException cause) {
        super("ESSR0044", new Object[] { cause }, cause);
        setClassName(className);
    }

    public String getClassName() {
        return className;
    }

    protected void setClassName(String className) {
        this.className = className;
    }
}
