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
package org.dbflute.lasta.jta.core;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.dbflute.lasta.jta.exception.LjtIllegalStateException;
import org.dbflute.lasta.jta.exception.LjtNotSupportedException;
import org.dbflute.lasta.jta.util.LjtTransactionUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractTransactionManagerImpl implements TransactionManager {

    protected final ThreadLocal<ExtendedTransaction> threadAttachTx = new ThreadLocal<ExtendedTransaction>();

    public AbstractTransactionManagerImpl() {
    }

    public void begin() throws NotSupportedException, SystemException {
        ExtendedTransaction tx = getCurrent();
        if (tx != null) {
            throw new LjtNotSupportedException("Unsupported nested transaction: current=" + tx);
        }
        tx = attachNewTransaction();
        tx.begin();
    }

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException {
        final ExtendedTransaction tx = getCurrent();
        if (tx == null) {
            throw new LjtIllegalStateException("Not begun transaction for commit().");
        }
        try {
            tx.commit();
        } finally {
            setCurrent(null);
        }
    }

    public Transaction suspend() throws SystemException {
        final ExtendedTransaction tx = getCurrent();
        if (tx == null) {
            throw new LjtIllegalStateException("Not begun transaction for suspend().");
        }
        try {
            tx.suspend();
        } finally {
            setCurrent(null);
        }
        return tx;
    }

    public void resume(final Transaction resumeTx) throws InvalidTransactionException, IllegalStateException, SystemException {
        final ExtendedTransaction tx = getCurrent();
        if (tx != null) {
            throw new LjtIllegalStateException("Already assigned the other transaction for resume().");
        }
        ((ExtendedTransaction) resumeTx).resume();
        setCurrent((ExtendedTransaction) resumeTx);
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        final ExtendedTransaction tx = getCurrent();
        if (tx == null) {
            throw new LjtIllegalStateException("Not begun transaction for rollback().");
        }
        try {
            tx.rollback();
        } finally {
            setCurrent(null);
        }
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        final ExtendedTransaction tx = getCurrent();
        if (tx == null) {
            throw new LjtIllegalStateException("Not begun transaction for setRollbackOnly().");
        }
        tx.setRollbackOnly();
    }

    public void setTransactionTimeout(final int timeout) throws SystemException {
    }

    public int getStatus() {
        final ExtendedTransaction tx = getCurrent();
        if (tx != null) {
            return LjtTransactionUtil.getStatus(tx);
        }
        return Status.STATUS_NO_TRANSACTION;
    }

    public Transaction getTransaction() {
        return getCurrent();
    }

    protected ExtendedTransaction getCurrent() {
        final ExtendedTransaction tx = (ExtendedTransaction) threadAttachTx.get();
        if (tx != null && LjtTransactionUtil.getStatus(tx) == Status.STATUS_NO_TRANSACTION) {
            setCurrent(null);
            return null;
        }
        return tx;
    }

    protected void setCurrent(ExtendedTransaction current) {
        threadAttachTx.set(current);
    }

    protected ExtendedTransaction attachNewTransaction() {
        ExtendedTransaction tx = (ExtendedTransaction) threadAttachTx.get();
        if (tx == null) {
            tx = createTransaction();
            setCurrent(tx);
        }
        return tx;
    }

    protected abstract ExtendedTransaction createTransaction();
}