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
package org.lastaflute.di.exception;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class NoSuchFieldRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 6609175673610180338L;

    private Class<?> targetClass;
    private String fieldName;

    public NoSuchFieldRuntimeException(Class<?> targetClass, String fieldName, NoSuchFieldException cause) {
        super("ESSR0070", new Object[] { targetClass.getName(), fieldName }, cause);
        this.targetClass = targetClass;
        this.fieldName = fieldName;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getFieldName() {
        return fieldName;
    }
}
