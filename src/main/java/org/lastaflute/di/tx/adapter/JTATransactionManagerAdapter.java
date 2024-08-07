/*
 * Copyright 2015-2024 the original author or authors.
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
package org.lastaflute.di.tx.adapter;

import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.lastaflute.di.exception.SIllegalStateException;
import org.lastaflute.di.helper.log.LaLogger;
import org.lastaflute.di.tx.TransactionCallback;
import org.lastaflute.di.tx.TransactionManagerAdapter;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class JTATransactionManagerAdapter implements TransactionManagerAdapter, Status {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final LaLogger logger = LaLogger.getLogger(JTATransactionManagerAdapter.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final TransactionManager transactionManager;
    protected final UserTransaction userTransaction;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public JTATransactionManagerAdapter(TransactionManager transactionManager, UserTransaction userTransaction) {
        this.transactionManager = transactionManager;
        this.userTransaction = userTransaction;
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public Object required(TransactionCallback callback) throws Throwable {
        final boolean began = begin();
        try {
            return callback.execute(this);
        } finally {
            if (began) {
                end();
            }
        }
    }

    public Object requiresNew(TransactionCallback callback) throws Throwable {
        final Transaction tx = suspend();
        try {
            begin();
            try {
                return callback.execute(this);
            } finally {
                end();
            }
        } finally {
            if (tx != null) {
                resume(tx);
            }
        }
    }

    public Object mandatory(TransactionCallback callback) throws Throwable {
        if (!hasTransaction()) {
            throw new SIllegalStateException("ESSR0311", null);
        }
        return callback.execute(this);
    }

    public Object notSupported(TransactionCallback callback) throws Throwable {
        final Transaction tx = suspend();
        try {
            return callback.execute(this);
        } finally {
            if (tx != null) {
                resume(tx);
            }
        }
    }

    public Object never(TransactionCallback callback) throws Throwable {
        if (hasTransaction()) {
            throw new SIllegalStateException("ESSR0317", null);
        }
        return callback.execute(this);
    }

    public void setRollbackOnly() {
        try {
            if (userTransaction.getStatus() == STATUS_ACTIVE) {
                userTransaction.setRollbackOnly();
            }
        } catch (final Exception e) {
            logger.log("ESSR0017", new Object[] { e.getMessage() }, e);
        }
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected boolean hasTransaction() throws SystemException {
        final int status = userTransaction.getStatus();
        return status != STATUS_NO_TRANSACTION && status != STATUS_UNKNOWN;
    }

    protected boolean begin() throws NotSupportedException, SystemException {
        if (hasTransaction()) {
            return false;
        }
        doBegin();
        return true;
    }

    protected void doBegin() throws NotSupportedException, SystemException {
        userTransaction.begin();
    }

    protected void end() throws Exception {
        if (userTransaction.getStatus() == STATUS_ACTIVE) {
            userTransaction.commit();
        } else {
            userTransaction.rollback();
        }
    }

    protected Transaction suspend() throws Exception {
        return hasTransaction() ? transactionManager.suspend() : null;
    }

    protected void resume(Transaction transaction) throws Exception {
        transactionManager.resume(transaction);
    }
}
