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
package org.lastaflute.di.tx;

import org.lastaflute.di.exception.SIllegalArgumentException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TxRule {

    protected final Class<?> exceptionClass;
    protected final boolean commit;

    public TxRule(final Class<?> exceptionClass, final boolean commit) {
        if (!Throwable.class.isAssignableFrom(exceptionClass)) {
            throw new SIllegalArgumentException("ESSR0365", new Object[] { exceptionClass.getName() });
        }
        this.exceptionClass = exceptionClass;
        this.commit = commit;
    }

    public boolean isAssignableFrom(Throwable cause) {
        return exceptionClass.isAssignableFrom(cause.getClass());
    }

    public void complete(TransactionManagerAdapter adapter) {
        if (!commit) {
            adapter.setRollbackOnly();
        }
    }
}
