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
package org.lastaflute.di.core.exception;

import org.lastaflute.di.core.meta.AutoBindingDef;
import org.lastaflute.di.exception.SRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 * @author modified by jflute (originated in Seasar)
 * 
 * @see AutoBindingDef
 * @see org.lastaflute.di.core.assembler.AutoBindingDefFactory#getAutoBindingDef(String)
 */
public class IllegalAutoBindingDefRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 3640106715772309404L;

    private String autoBindingName;

    /**
     * @param autoBindingName
     */
    public IllegalAutoBindingDefRuntimeException(String autoBindingName) {
        super("ESSR0077", new Object[] { autoBindingName });
        this.autoBindingName = autoBindingName;
    }

    /**
     * @return 
     */
    public String getAutoBindingName() {
        return autoBindingName;
    }
}