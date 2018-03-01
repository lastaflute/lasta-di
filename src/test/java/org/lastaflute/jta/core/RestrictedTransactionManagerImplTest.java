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
package org.lastaflute.jta.core;

import javax.transaction.Status;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.lastaflute.jta.core.RestrictedTransactionManager;
import org.lastaflute.jta.core.LaTransactionManager;
import org.lastaflute.jta.core.LaTransactionSynchronizationRegistry;
import org.lastaflute.jta.core.LaUserTransaction;
import org.lastaflute.jta.unit.UnitLastaJtaTest;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class RestrictedTransactionManagerImplTest extends UnitLastaJtaTest {

    TransactionManager underlyingTm;

    UserTransaction userTransaction;

    TransactionSynchronizationRegistry synchronizationRegistry;

    RestrictedTransactionManager tm;

    protected void setUp() throws Exception {
        super.setUp();
        underlyingTm = new LaTransactionManager();
        userTransaction = new LaUserTransaction(underlyingTm);
        synchronizationRegistry = new LaTransactionSynchronizationRegistry(underlyingTm);
        tm = new RestrictedTransactionManager(userTransaction, synchronizationRegistry);
    }

    /**
     * @throws Exception
     */
    public void testCommit() throws Exception {
        tm.begin();
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        tm.commit();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
    }

    /**
     * @throws Exception
     */
    public void testRollback() throws Exception {
        tm.begin();
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        tm.rollback();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
    }

    /**
     * @throws Exception
     */
    public void testResume() throws Exception {
        tm.begin();
        try {
            tm.resume(null);
            fail();
        } catch (UnsupportedOperationException expected) {}
    }

    /**
     * @throws Exception
     */
    public void testSuspend() throws Exception {
        tm.begin();
        try {
            tm.suspend();
            fail();
        } catch (UnsupportedOperationException expected) {}
    }

}
