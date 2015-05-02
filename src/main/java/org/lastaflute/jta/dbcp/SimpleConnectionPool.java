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
package org.lastaflute.jta.dbcp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.lastaflute.jta.dbcp.exception.ConnectionPoolShortFreeSQLException;
import org.lastaflute.jta.dbcp.impl.ConnectionWrapperImpl;
import org.lastaflute.jta.exception.LjtIllegalStateException;
import org.lastaflute.jta.exception.LjtRuntimeException;
import org.lastaflute.jta.exception.LjtSQLException;
import org.lastaflute.jta.helper.LjtLinkedList;
import org.lastaflute.jta.helper.misc.LjtExceptionMessageBuilder;
import org.lastaflute.jta.helper.timer.LjtTimeoutManager;
import org.lastaflute.jta.helper.timer.LjtTimeoutTarget;
import org.lastaflute.jta.helper.timer.LjtTimeoutTask;
import org.lastaflute.jta.util.LjtTransactionManagerUtil;
import org.lastaflute.jta.util.LjtTransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SimpleConnectionPool implements ConnectionPool {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LoggerFactory.getLogger(SimpleConnectionPool.class);

    public static final String readOnly_BINDING = "bindingType=may";
    public static final String transactionIsolationLevel_BINDING = "bindingType=may";
    public static final int DEFAULT_TRANSACTION_ISOLATION_LEVEL = -1;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected XADataSource xaDataSource;
    protected TransactionManager transactionManager;
    protected int timeout = 600; // timeout seconds until closing free connection
    protected int maxPoolSize = 10; // maximum count of pooled connection
    protected int minPoolSize = 0; // minimum count of pooled connection

    // change default value from -1 to the big value *extension
    // because unlimited gives us system ending without info if application bugs
    // failure with big value might mean application bugs
    // so want to output exception and to show error message to user
    protected long maxWait = 10000; // milliseconds of waiting for free connection (-1: unlimited, 0: no wait)

    protected boolean allowLocalTx = true; // allow to check out in out of transaction?
    protected boolean readOnly = false;
    protected int transactionIsolationLevel = DEFAULT_TRANSACTION_ISOLATION_LEVEL;
    protected String validationQuery;
    protected long validationInterval;
    protected final Set<ConnectionWrapper> activePool = createActivePoolSet();
    protected final Map<Transaction, ConnectionWrapper> txActivePool = createTxActivePoolMap();
    protected final LjtLinkedList freePool = createFreePoolList();
    protected LjtTimeoutTask timeoutTask;

    protected Set<ConnectionWrapper> createActivePoolSet() {
        return new HashSet<ConnectionWrapper>();
    }

    protected Map<Transaction, ConnectionWrapper> createTxActivePoolMap() {
        return new HashMap<Transaction, ConnectionWrapper>();
    }

    protected LjtLinkedList createFreePoolList() {
        return new LjtLinkedList();
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SimpleConnectionPool() {
        timeoutTask = LjtTimeoutManager.getInstance().addTimeoutTarget(createTimeoutTarget(), Integer.MAX_VALUE, true);
    }

    protected LjtTimeoutTarget createTimeoutTarget() {
        return new LjtTimeoutTarget() {
            public void expired() {
            }
        };
    }

    // ===================================================================================
    //                                                                           Check Out
    //                                                                           =========
    public synchronized ConnectionWrapper checkOut() throws SQLException {
        final Transaction tx = getTransaction();
        if (tx == null && !isAllowLocalTx()) {
            throw new LjtIllegalStateException("Not begun transaction. (not allowed local transaction)");
        }
        ConnectionWrapper wrapper = getConnectionTxActivePool(tx);
        if (wrapper != null) {
            // TODO jflute lastaflute: [E] fitting: DI :: connection pool logical connection logging for internal debug
            //if (logger.isDebugEnabled()) {
            //    logger.debug("...Checking out logical connection from pool: {}", tx);
            //}
            return wrapper;
        }
        long wait = maxWait;
        while (getMaxPoolSize() > 0 && getActivePoolSize() + getTxActivePoolSize() >= getMaxPoolSize()) {
            if (wait == 0L) {
                throwConnectionPoolShortFreeException(); // *extension
            }
            final long startTime = System.currentTimeMillis();
            try {
                wait((maxWait == -1L) ? 0L : wait);
            } catch (InterruptedException e) {
                // TODO jflute lastaflute: [E] fitting: DI :: connection pool exception handling
                throw new LjtSQLException("Cannot wait the connection back to pool", e);
            }
            final long elapseTime = System.currentTimeMillis() - startTime;
            if (wait > 0L) {
                wait -= Math.min(wait, elapseTime);
            }
        }
        wrapper = checkOutFreePool(tx);
        if (wrapper == null) {
            wrapper = createConnection(tx);
        }
        if (tx == null) {
            setConnectionActivePool(wrapper);
        } else {
            LjtTransactionUtil.enlistResource(tx, wrapper.getXAResource());
            LjtTransactionUtil.registerSynchronization(tx, createSynchronizationImpl(tx));
            setConnectionTxActivePool(tx, wrapper);
        }
        wrapper.setReadOnly(readOnly);
        if (transactionIsolationLevel != DEFAULT_TRANSACTION_ISOLATION_LEVEL) {
            wrapper.setTransactionIsolation(transactionIsolationLevel);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("...Checking out logical connection from pool: {}", tx);
        }
        return wrapper;
    }

    protected Transaction getTransaction() {
        return LjtTransactionManagerUtil.getTransaction(transactionManager);
    }

    protected ConnectionWrapper getConnectionTxActivePool(Transaction tx) {
        return (ConnectionWrapper) txActivePool.get(tx);
    }

    protected ConnectionWrapper checkOutFreePool(Transaction tx) {
        if (freePool.isEmpty()) {
            return null;
        }
        final FreeItem item = (FreeItem) freePool.removeLast();
        final ConnectionWrapper wrapper = item.getConnection();
        wrapper.init(tx);
        item.destroy();
        if (validationQuery == null || validationQuery.isEmpty()) {
            return wrapper;
        }
        if (validateConnection(wrapper, item.getPooledTime())) {
            return wrapper;
        }
        return null;
    }

    protected boolean validateConnection(ConnectionWrapper wrapper, final long pooledTime) {
        final long currentTime = System.currentTimeMillis();
        if (currentTime - pooledTime < validationInterval) {
            return true;
        }
        try {
            final PreparedStatement ps = wrapper.prepareStatement(validationQuery);
            try {
                ps.executeQuery();
            } finally {
                ps.close();
            }
        } catch (final Exception e) {
            try {
                wrapper.close();
            } catch (final Exception ignore) {}
            for (LjtLinkedList.Entry entry = freePool.getFirstEntry(); entry != null; entry = entry.getNext()) {
                final FreeItem item = (FreeItem) entry.getElement();
                try {
                    item.getConnection().closeReally();
                } catch (final Exception ignore) {}
            }
            freePool.clear();
            logger.error("Destroyed the pooled connections because validation error: " + pooledTime, e);
            return false;
        }
        return true;
    }

    protected ConnectionWrapper createConnection(Transaction tx) throws SQLException {
        final XAConnection xaConn = xaDataSource.getXAConnection();
        final Connection conn = xaConn.getConnection();
        final ConnectionWrapper wrapper = createTransactionalConnectionWrapperImpl(xaConn, conn, tx);
        if (logger.isDebugEnabled()) {
            logger.debug("Created physical connection: tx={}, conn={}", tx, conn);
        }
        return wrapper;
    }

    protected ConnectionWrapperImpl createTransactionalConnectionWrapperImpl(XAConnection xaConnection, Connection conn, Transaction tx)
            throws SQLException {
        return new ConnectionWrapperImpl(xaConnection, conn, this, tx);
    }

    protected void setConnectionActivePool(ConnectionWrapper connection) {
        activePool.add(connection);
    }

    protected SynchronizationImpl createSynchronizationImpl(Transaction tx) {
        return new SynchronizationImpl(tx);
    }

    protected void setConnectionTxActivePool(Transaction tx, ConnectionWrapper wrapper) {
        txActivePool.put(tx, wrapper);
    }

    // ===================================================================================
    //                                                                             Release
    //                                                                             =======
    public synchronized void release(ConnectionWrapper connection) {
        activePool.remove(connection);
        final Transaction tx = getTransaction();
        if (tx != null) {
            txActivePool.remove(tx);
        }
        connection.closeReally();
        notify();
    }

    // ===================================================================================
    //                                                                            Check In
    //                                                                            ========
    public synchronized void checkIn(ConnectionWrapper wrapper) {
        activePool.remove(wrapper);
        checkInFreePool(wrapper);
    }

    protected void checkInFreePool(ConnectionWrapper wrapper) {
        if (getMaxPoolSize() > 0) {
            try {
                final Connection pc = wrapper.getPhysicalConnection();
                pc.setAutoCommit(true);
                final ConnectionWrapper newCon = createInheritedConnectionWrapperImpl(wrapper, pc);
                wrapper.cleanup();
                freePool.addLast(new FreeItem(newCon));
                notify();
            } catch (SQLException e) {
                throw new LjtRuntimeException("Failed to check in the free pool: " + wrapper, e);
            }
        } else {
            wrapper.closeReally();
        }
    }

    protected ConnectionWrapperImpl createInheritedConnectionWrapperImpl(ConnectionWrapper wrapper, Connection pc) throws SQLException {
        return new ConnectionWrapperImpl(wrapper.getXAConnection(), pc, this, null);
    }

    public synchronized void checkInTx(Transaction tx) {
        if (tx == null) {
            return;
        }
        if (getTransaction() != null) {
            return;
        }
        final ConnectionWrapper wrapper = (ConnectionWrapper) txActivePool.remove(tx);
        if (wrapper == null) {
            return;
        }
        checkInFreePool(wrapper);
    }

    // ===================================================================================
    //                                                                               Close
    //                                                                               =====
    public final synchronized void close() {
        for (LjtLinkedList.Entry e = freePool.getFirstEntry(); e != null; e = e.getNext()) {
            final FreeItem item = (FreeItem) e.getElement();
            item.getConnection().closeReally();
            item.destroy();
        }
        freePool.clear();
        for (Iterator<ConnectionWrapper> i = txActivePool.values().iterator(); i.hasNext();) {
            final ConnectionWrapper con = i.next();
            con.closeReally();
        }
        txActivePool.clear();
        for (Iterator<ConnectionWrapper> i = activePool.iterator(); i.hasNext();) {
            final ConnectionWrapper con = i.next();
            con.closeReally();
        }
        activePool.clear();
        timeoutTask.cancel();
    }

    protected class FreeItem implements LjtTimeoutTarget {

        protected ConnectionWrapper connectionWrapper_;
        protected LjtTimeoutTask timeoutTask_;
        protected long pooledTime; // millisecond

        protected FreeItem(ConnectionWrapper connectionWrapper) {
            connectionWrapper_ = connectionWrapper;
            timeoutTask_ = LjtTimeoutManager.getInstance().addTimeoutTarget(this, timeout, false);
            pooledTime = System.currentTimeMillis();
        }

        public ConnectionWrapper getConnection() {
            return connectionWrapper_;
        }

        public long getPooledTime() {
            return pooledTime;
        }

        public void expired() {
            synchronized (SimpleConnectionPool.this) {
                if (freePool.size() <= minPoolSize) {
                    return;
                }
                freePool.remove(this);
            }
            synchronized (this) {
                if (connectionWrapper_ != null) {
                    connectionWrapper_.closeReally();
                    connectionWrapper_ = null;
                }
            }
        }

        public synchronized void destroy() {
            if (timeoutTask_ != null) {
                timeoutTask_.cancel();
                timeoutTask_ = null;
            }
            connectionWrapper_ = null;
        }
    }

    /**
     * @author modified by jflute (originated in Seasar)
     */
    public class SynchronizationImpl implements Synchronization {

        protected final Transaction tx;

        public SynchronizationImpl(final Transaction tx) {
            this.tx = tx;
        }

        public final void beforeCompletion() {
        }

        public void afterCompletion(final int status) {
            switch (status) {
            case Status.STATUS_COMMITTED:
            case Status.STATUS_ROLLEDBACK:
                checkInTx(tx);
                break;
            }
        }
    }

    // ===================================================================================
    //                                                                 Traceable Extension
    //                                                                 ===================
    protected void throwConnectionPoolShortFreeException() throws SQLException {
        final LjtExceptionMessageBuilder br = new LjtExceptionMessageBuilder();
        br.addNotice("Connection pool did not have a free connection.");
        br.addItem("Pool Settings");
        br.addElement("timeout: " + timeout);
        br.addElement("maxPoolSize: " + maxPoolSize);
        br.addElement("minPoolSize: " + minPoolSize);
        br.addElement("maxWait: " + maxWait);
        br.addItem("Plain ActivePool");
        br.addElement("size: " + activePool.size());
        br.addItem("Transaction ActivePool");
        br.addElement("size: " + txActivePool.size());
        final List<String> expList = extractActiveTransactionExpList();
        for (String exp : expList) {
            br.addElement(exp);
        }
        final String msg = br.buildExceptionMessage();
        throw new ConnectionPoolShortFreeSQLException(msg);
    }

    public synchronized List<String> extractActiveTransactionExpList() {
        final List<String> expList = new ArrayList<String>(txActivePool.size());
        for (Entry<Transaction, ConnectionWrapper> entry : txActivePool.entrySet()) {
            final Transaction tx = entry.getKey();
            final ConnectionWrapper wrapper = entry.getValue();
            final String romantic = buildRomanticExp(tx, wrapper);
            expList.add(romantic);
        }
        return expList;
    }

    protected String buildRomanticExp(Transaction tx, ConnectionWrapper wrapper) {
        return tx.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public XADataSource getXADataSource() {
        return xaDataSource;
    }

    public void setXADataSource(XADataSource xaDataSource) {
        this.xaDataSource = xaDataSource;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isAllowLocalTx() {
        return allowLocalTx;
    }

    public void setAllowLocalTx(boolean allowLocalTx) {
        this.allowLocalTx = allowLocalTx;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public int getTransactionIsolationLevel() {
        return transactionIsolationLevel;
    }

    public void setTransactionIsolationLevel(int transactionIsolationLevel) {
        this.transactionIsolationLevel = transactionIsolationLevel;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public long getValidationInterval() {
        return validationInterval;
    }

    public void setValidationInterval(long validationInterval) {
        this.validationInterval = validationInterval;
    }

    public int getActivePoolSize() {
        return activePool.size();
    }

    public int getTxActivePoolSize() {
        return txActivePool.size();
    }

    public int getFreePoolSize() {
        return freePool.size();
    }
}