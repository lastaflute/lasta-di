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
package org.lastaflute.jta.core;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class RestrictedTransactionImplTest extends TestCase {

    TransactionManager underlyingTm;

    UserTransaction userTransaction;

    TransactionSynchronizationRegistry synchronizationRegistry;

    RestrictedTransaction tx;

    protected void setUp() throws Exception {
        super.setUp();
        underlyingTm = new LaTransactionManager();
        userTransaction = new LaUserTransaction(underlyingTm);
        synchronizationRegistry = new LaTransactionSynchronizationRegistry(underlyingTm);
        tx = new RestrictedTransaction(userTransaction, synchronizationRegistry);
    }

    /**
     * @throws Exception
     */
    public void testBegin() throws Exception {
        tx.begin();
        assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
    }

    /**
     * @throws Exception
     */
    public void testCommit() throws Exception {
        tx.begin();
        assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
        tx.commit();
        assertEquals(Status.STATUS_NO_TRANSACTION, tx.getStatus());
    }

    /**
     * @throws Exception
     */
    public void testRollback() throws Exception {
        tx.begin();
        assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
        tx.rollback();
        assertEquals(Status.STATUS_NO_TRANSACTION, tx.getStatus());
    }

    /**
     * @throws Exception
     */
    public void testSetRollbackOnly() throws Exception {
        tx.begin();
        assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
        tx.setRollbackOnly();
        assertEquals(Status.STATUS_MARKED_ROLLBACK, tx.getStatus());
    }

    /**
     * @throws Exception
     */
    public void testRegisterSynchronization() throws Exception {
        SynchronizationImpl sync = new SynchronizationImpl();
        tx.begin();
        tx.registerSynchronization(sync);
        tx.commit();
        assertEquals("BeforeAfter", sync.toString());
    }

    /**
     * @throws Exception
     */
    public void testUnregisterSynchronization() throws Exception {
        SynchronizationImpl sync = new SynchronizationImpl();
        tx.begin();
        tx.registerSynchronization(sync);
        tx.commit();
        assertEquals("BeforeAfter", sync.toString());
    }

    private static class SynchronizationImpl implements Synchronization {
        private StringBuffer buf = new StringBuffer();

        private SynchronizationImpl() {
        }

        public void afterCompletion(int status) {
            buf.append("After");
        }

        public void beforeCompletion() {
            buf.append("Before");
        }

        public String toString() {
            return buf.toString();
        }
    }

}
