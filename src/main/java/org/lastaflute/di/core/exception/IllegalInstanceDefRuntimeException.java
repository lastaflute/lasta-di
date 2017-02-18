/*
 * Copyright 2015-2017 the original author or authors.
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

import org.lastaflute.di.core.meta.InstanceDef;
import org.lastaflute.di.exception.SRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 * @author modified by jflute (originated in Seasar)
 * 
 * @see InstanceDef
 * @see org.lastaflute.di.core.meta.impl.InstanceDefFactory
 */
public class IllegalInstanceDefRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = -3505265879782048528L;

    private String instanceName;

    /**
     * @param instanceName
     */
    public IllegalInstanceDefRuntimeException(String instanceName) {
        super("ESSR0078", new Object[] { instanceName });
        this.instanceName = instanceName;
    }

    /**
     * @return 
     */
    public String getInstanceName() {
        return instanceName;
    }
}