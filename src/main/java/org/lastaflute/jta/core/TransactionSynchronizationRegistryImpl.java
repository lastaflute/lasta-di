/*
 * Copyright 2015-2016 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import org.lastaflute.jta.exception.LjtIllegalStateException;
import org.lastaflute.jta.util.LjtTransactionManagerUtil;
import org.lastaflute.jta.util.LjtTransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TransactionSynchronizationRegistryImpl implements TransactionSynchronizationRegistry {

    private static final Logger logger = LoggerFactory.getLogger(TransactionSynchronizationRegistryImpl.class);

    private TransactionManager tm;
    private final Map<Transaction, SynchronizationRegisterImpl> transactionContexts =
            Collections.synchronizedMap(new HashMap<Transaction, SynchronizationRegisterImpl>());

    public TransactionSynchronizationRegistryImpl() {
    }

    public TransactionSynchronizationRegistryImpl(TransactionManager tm) {
        this.tm = tm;
    }

    public void putResource(final Object key, final Object value) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        assertActive();
        getContext().putResource(key, value);
    }

    public Object getResource(final Object key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        assertActive();
        return getContext().getResource(key);
    }

    public void setRollbackOnly() {
        assertActive();
        LjtTransactionManagerUtil.setRollbackOnly(tm);
    }

    public boolean getRollbackOnly() {
        assertActive();
        switch (getTransactionStatus()) {
        case Status.STATUS_MARKED_ROLLBACK:
        case Status.STATUS_ROLLING_BACK:
            return true;
        }
        return false;
    }

    public Object getTransactionKey() {
        if (!isActive()) {
            return null;
        }
        return getTransaction();
    }

    public int getTransactionStatus() {
        return LjtTransactionManagerUtil.getStatus(tm);
    }

    public void registerInterposedSynchronization(final Synchronization sync) {
        assertActive();
        getContext().registerInterposedSynchronization(sync);
    }

    protected Transaction getTransaction() {
        return LjtTransactionManagerUtil.getTransaction(tm);
    }

    protected void assertActive() throws IllegalStateException {
        if (!isActive()) {
            throw new LjtIllegalStateException("Not begun transaction: transaction=" + tm);
        }
    }

    protected boolean isActive() {
        return LjtTransactionManagerUtil.isActive(tm);
    }

    protected SynchronizationRegister getContext() {
        final Transaction tx = getTransaction();
        if (tx instanceof SynchronizationRegister) {
            return (SynchronizationRegister) tx;
        }
        SynchronizationRegisterImpl context = (SynchronizationRegisterImpl) transactionContexts.get(tx);
        if (context == null) {
            context = new SynchronizationRegisterImpl(tx);
            LjtTransactionUtil.registerSynchronization(tx, context);
            transactionContexts.put(tx, context);
        }
        return context;
    }

    public class SynchronizationRegisterImpl implements SynchronizationRegister, Synchronization {

        private final Transaction tx;

        private final List<Synchronization> interposedSynchronizations = new ArrayList<Synchronization>();

        private final Map<Object, Object> resourceMap = new HashMap<Object, Object>();

        public SynchronizationRegisterImpl(final Transaction tx) {
            this.tx = tx;
        }

        public void registerInterposedSynchronization(final Synchronization sync) throws IllegalStateException {
            interposedSynchronizations.add(sync);
        }

        public void putResource(final Object key, final Object value) throws IllegalStateException {
            resourceMap.put(key, value);
        }

        public Object getResource(final Object key) throws IllegalStateException {
            return resourceMap.get(key);
        }

        public void beforeCompletion() {
            for (int i = 0; i < interposedSynchronizations.size(); ++i) {
                final Synchronization sync = (Synchronization) interposedSynchronizations.get(i);
                sync.beforeCompletion();
            }
        }

        public void afterCompletion(final int status) {
            for (int i = 0; i < interposedSynchronizations.size(); ++i) {
                final Synchronization sync = (Synchronization) interposedSynchronizations.get(i);
                try {
                    sync.afterCompletion(status);
                } catch (final Throwable t) {
                    logger.info("Failed to process the after completion: " + sync + " " + status, t);
                }
            }
            transactionContexts.remove(tx);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setTransactionManager(TransactionManager tm) {
        this.tm = tm;
    }
}
