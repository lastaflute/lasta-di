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
package org.dbflute.lasta.di.core.exception;

import org.dbflute.lasta.di.core.meta.AutoBindingDef;
import org.dbflute.lasta.di.exception.SRuntimeException;

/**
 * 不正な自動バインディング定義が指定された場合にスローされます。
 * 
 * @author modified by jflute (originated in Seasar)
 * @author modified by jflute (originated in Seasar)
 * 
 * @see AutoBindingDef
 * @see org.dbflute.lasta.di.core.assembler.AutoBindingDefFactory#getAutoBindingDef(String)
 */
public class IllegalAutoBindingDefRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 3640106715772309404L;

    private String autoBindingName;

    /**
     * <code>IllegalAutoBindingDefRuntimeException</code>を構築します。
     * 
     * @param autoBindingName
     *            指定された不正な自動バインディング定義名
     */
    public IllegalAutoBindingDefRuntimeException(String autoBindingName) {
        super("ESSR0077", new Object[] { autoBindingName });
        this.autoBindingName = autoBindingName;
    }

    /**
     * 例外の原因となった不正な自動バインディング定義名を返します。
     * 
     * @return 自動バインディング定義名
     */
    public String getAutoBindingName() {
        return autoBindingName;
    }
}