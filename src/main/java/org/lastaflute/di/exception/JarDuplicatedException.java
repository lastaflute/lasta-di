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
 * Seasar2のJarファイルが複数存在している場合にスローされる例外です。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class JarDuplicatedException extends SRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param name
     *            Jarの名前
     * @param versions
     *            バージョン
     */
    public JarDuplicatedException(String name, Object versions) {
        super("ESSR0102", new Object[] { name, versions });
    }

}
