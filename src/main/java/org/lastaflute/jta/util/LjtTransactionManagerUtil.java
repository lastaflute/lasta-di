/*
 * Copyright 2015-2022 the original author or authors.
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
package org.lastaflute.jta.util;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.lastaflute.jta.exception.LjtSystemRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LjtTransactionManagerUtil {

    protected LjtTransactionManagerUtil() {
    }

    public static Transaction getTransaction(TransactionManager tm) throws LjtSystemRuntimeException {
        try {
            return tm.getTransaction();
        } catch (SystemException e) {
            throw new LjtSystemRuntimeException(e);
        }
    }

    public static boolean isActive(TransactionManager tm) {
        return getStatus(tm) != Status.STATUS_NO_TRANSACTION;
    }

    public static int getStatus(TransactionManager tm) throws LjtSystemRuntimeException {
        try {
            return tm.getStatus();
        } catch (SystemException e) {
            throw new LjtSystemRuntimeException(e);
        }
    }

    public static void setRollbackOnly(TransactionManager tm) throws LjtSystemRuntimeException {
        try {
            tm.setRollbackOnly();
        } catch (SystemException e) {
            throw new LjtSystemRuntimeException(e);
        }
    }
}
