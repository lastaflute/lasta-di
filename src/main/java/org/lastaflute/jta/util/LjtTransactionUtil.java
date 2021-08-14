/*
 * Copyright 2015-2021 the original author or authors.
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

import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

import org.lastaflute.jta.exception.LjtRollbackRuntimeException;
import org.lastaflute.jta.exception.LjtSystemRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LjtTransactionUtil {

    protected LjtTransactionUtil() {
    }

    public static int getStatus(final Transaction tx) {
        try {
            return tx.getStatus();
        } catch (final SystemException e) {
            throw new LjtSystemRuntimeException(e);
        }
    }

    public static void enlistResource(Transaction tx, XAResource xaResource) {
        try {
            tx.enlistResource(xaResource);
        } catch (SystemException e) {
            throw new LjtSystemRuntimeException(e);
        } catch (RollbackException e) {
            throw new LjtRollbackRuntimeException(e);
        }
    }

    public static void registerSynchronization(Transaction tx, Synchronization sync) {
        try {
            tx.registerSynchronization(sync);
        } catch (SystemException e) {
            throw new LjtSystemRuntimeException(e);
        } catch (RollbackException e) {
            throw new LjtRollbackRuntimeException(e);
        }
    }
}
