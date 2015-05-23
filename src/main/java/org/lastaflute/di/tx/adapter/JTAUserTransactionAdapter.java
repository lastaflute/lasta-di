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
package org.lastaflute.di.tx.adapter;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.lastaflute.di.exception.SIllegalStateException;
import org.lastaflute.di.helper.log.SLogger;
import org.lastaflute.di.tx.TransactionCallback;
import org.lastaflute.di.tx.TransactionManagerAdapter;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class JTAUserTransactionAdapter implements TransactionManagerAdapter, Status {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final SLogger logger = SLogger.getLogger(JTAUserTransactionAdapter.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final UserTransaction userTransaction;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public JTAUserTransactionAdapter(final UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    // ===================================================================================
    //                                                                           Operation
    //                                                                           =========
    public Object required(final TransactionCallback callback) throws Throwable {
        final boolean began = begin();
        try {
            return callback.execute(this);
        } finally {
            if (began) {
                end();
            }
        }
    }

    public Object requiresNew(final TransactionCallback callback) throws Throwable {
        throw new UnsupportedOperationException("REQUIRES_NEW");
    }

    public Object mandatory(final TransactionCallback callback) throws Throwable {
        if (!hasTransaction()) {
            throw new SIllegalStateException("ESSR0311", null);
        }
        return callback.execute(this);
    }

    public Object notSupported(final TransactionCallback callback) throws Throwable {
        throw new UnsupportedOperationException("NOT_SUPPORTED");
    }

    public Object never(final TransactionCallback callback) throws Throwable {
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
    //                                                                         Transaction
    //                                                                         ===========
    protected boolean hasTransaction() throws SystemException {
        final int status = userTransaction.getStatus();
        return status != STATUS_NO_TRANSACTION && status != STATUS_UNKNOWN;
    }

    protected boolean begin() throws Exception {
        if (hasTransaction()) {
            return false;
        }
        userTransaction.begin();
        return true;
    }

    protected void end() throws Exception {
        if (userTransaction.getStatus() == STATUS_ACTIVE) {
            userTransaction.commit();
        } else {
            userTransaction.rollback();
        }
    }
}
