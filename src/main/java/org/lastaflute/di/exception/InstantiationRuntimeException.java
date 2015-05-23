/*
 * Copyright 2014-2015 the original author or authors.
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
 * {@link InstantiationException}をラップする例外です。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class InstantiationRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 5220902071756706607L;

    private Class targetClass;

    /**
     * {@link InstantiationRuntimeException}を作成します。
     * 
     * @param targetClass
     * @param cause
     */
    public InstantiationRuntimeException(Class targetClass, InstantiationException cause) {

        super("ESSR0041", new Object[] { targetClass.getName(), cause }, cause);
        this.targetClass = targetClass;
    }

    /**
     * ターゲットのクラスを返します。
     * 
     * @return
     */
    public Class getTargetClass() {
        return targetClass;
    }
}
