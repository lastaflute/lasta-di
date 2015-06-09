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
package org.lastaflute.di.core.exception;

import org.lastaflute.di.core.meta.BindingTypeDef;
import org.lastaflute.di.exception.SRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 * @author modified by jflute (originated in Seasar)
 * 
 * @see BindingTypeDef
 * @see org.lastaflute.di.core.assembler.BindingTypeDefFactory
 */
public class IllegalBindingTypeDefRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 9557906947127294L;

    private String bindingTypeName;

    /**
     * @param bindingTypeName
     */
    public IllegalBindingTypeDefRuntimeException(String bindingTypeName) {
        super("ESSR0079", new Object[] { bindingTypeName });
        this.bindingTypeName = bindingTypeName;
    }

    /**
     * @return 
     */
    public String getBindingTypeName() {
        return bindingTypeName;
    }
}