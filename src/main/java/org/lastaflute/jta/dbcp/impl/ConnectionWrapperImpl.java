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
package org.lastaflute.jta.dbcp.impl;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.XAConnection;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

import org.lastaflute.di.core.LastaDiProperties;
import org.lastaflute.jta.dbcp.ConnectionPool;
import org.lastaflute.jta.dbcp.ConnectionWrapper;
import org.lastaflute.jta.exception.LjtSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ConnectionWrapperImpl implements ConnectionWrapper, ConnectionEventListener {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LoggerFactory.getLogger(ConnectionWrapperImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private XAConnection xaConnection;
    private Connection physicalConnection;
    private XAResource xaResource;
    private ConnectionPool connectionPool;
    private boolean closed = false;
    private Transaction tx;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ConnectionWrapperImpl(final XAConnection xaConnection, final Connection physicalConnection, final ConnectionPool connectionPool,
            final Transaction tx) throws SQLException {
        this.xaConnection = xaConnection;
        this.physicalConnection = physicalConnection;
        this.xaResource = new XAResourceWrapperImpl(xaConnection.getXAResource(), this);
        this.connectionPool = connectionPool;
        this.tx = tx;
        this.xaConnection.addConnectionEventListener(this);
    }

    public void init(final Transaction tx) {
        closed = false;
        this.tx = tx;
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public Connection getPhysicalConnection() {
        return physicalConnection;
    }

    public XAResource getXAResource() {
        return xaResource;
    }

    public XAConnection getXAConnection() {
        return xaConnection;
    }

    public void cleanup() {
        xaConnection.removeConnectionEventListener(this);
        closed = true;
        xaConnection = null;
        physicalConnection = null;
        tx = null;
    }

    public void closeReally() {
        if (xaConnection == null) {
            return;
        }
        closed = true;
        try {
            if (!physicalConnection.isClosed()) {
                if (!physicalConnection.getAutoCommit()) {
                    try {
                        physicalConnection.rollback();
                        physicalConnection.setAutoCommit(true);
                    } catch (final SQLException e) {
                        logger.info("Failed to roll-back physical connection when closing really: " + physicalConnection, e);
                    }
                }
                physicalConnection.close();
            }
        } catch (final SQLException e) {
            logger.info("Failed to close physical connection when closing really: " + physicalConnection, e);
        } finally {
            physicalConnection = null;
        }
        try {
            xaConnection.close();
            logger.debug("Closed the physical connection: " + xaConnection);
        } catch (final SQLException e) {
            logger.info("Failed to close XA connection when closing really: " + xaConnection, e);
        } finally {
            xaConnection = null;
        }
    }

    private void assertOpened() throws SQLException {
        if (closed) {
            throw new LjtSQLException("Already closed the connection: " + xaConnection);
        }
    }

    private void assertLocalTx() throws SQLException {
        if (tx != null) {
            throw new LjtSQLException("Cannot use when distributed transaction: " + tx);
        }
    }

    public void release() throws SQLException {
        if (!closed) {
            connectionPool.release(this);
        }
    }

    public void connectionClosed(final ConnectionEvent event) {
    }

    public void connectionErrorOccurred(final ConnectionEvent event) {
        try {
            release();
        } catch (final SQLException ignore) {}
    }

    public Statement createStatement() throws SQLException {
        assertOpened();
        try {
            return physicalConnection.createStatement();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection.prepareStatement(sql), sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public CallableStatement prepareCall(final String sql) throws SQLException {
        assertOpened();
        try {
            return physicalConnection.prepareCall(sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public String nativeSQL(final String sql) throws SQLException {
        assertOpened();
        try {
            return physicalConnection.nativeSQL(sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public boolean isClosed() throws SQLException {
        return closed;
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        assertOpened();
        try {
            return physicalConnection.getMetaData();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void setReadOnly(final boolean readOnly) throws SQLException {
        assertOpened();
        try {
            physicalConnection.setReadOnly(readOnly);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public boolean isReadOnly() throws SQLException {
        assertOpened();
        try {
            return physicalConnection.isReadOnly();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void setCatalog(final String catalog) throws SQLException {
        assertOpened();
        try {
            physicalConnection.setCatalog(catalog);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public String getCatalog() throws SQLException {
        assertOpened();
        try {
            return physicalConnection.getCatalog();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void close() throws SQLException {
        if (closed) {
            return;
        }
        if (LastaDiProperties.getInstance().isInternalDebug()) {
            logger.debug("Closed the logical connection: {}", xaConnection);
        }
        if (tx == null) {
            connectionPool.checkIn(this);
        } else {
            connectionPool.checkInTx(tx);
        }
    }

    public void setTransactionIsolation(final int level) throws SQLException {
        assertOpened();
        try {
            physicalConnection.setTransactionIsolation(level);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public int getTransactionIsolation() throws SQLException {
        assertOpened();
        try {
            return physicalConnection.getTransactionIsolation();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        assertOpened();
        try {
            return physicalConnection.getWarnings();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void clearWarnings() throws SQLException {
        assertOpened();
        try {
            physicalConnection.clearWarnings();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void commit() throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection.commit();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void rollback() throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection.rollback();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        assertOpened();
        if (autoCommit) {
            assertLocalTx();
        }
        try {
            physicalConnection.setAutoCommit(autoCommit);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public boolean getAutoCommit() throws SQLException {
        assertOpened();
        try {
            return physicalConnection.getAutoCommit();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {

        assertOpened();
        try {
            return physicalConnection.createStatement(resultSetType, resultSetConcurrency);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        assertOpened();
        try {
            return physicalConnection.getTypeMap();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        assertOpened();
        try {
            physicalConnection.setTypeMap(map);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection.prepareStatement(sql, resultSetType, resultSetConcurrency), sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {

        assertOpened();
        try {
            return physicalConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public void setHoldability(final int holdability) throws SQLException {
        assertOpened();
        try {
            physicalConnection.setHoldability(holdability);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public int getHoldability() throws SQLException {
        assertOpened();
        try {
            return physicalConnection.getHoldability();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public Savepoint setSavepoint() throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            return physicalConnection.setSavepoint();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public Savepoint setSavepoint(final String name) throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            return physicalConnection.setSavepoint(name);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void rollback(final Savepoint savepoint) throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection.rollback(savepoint);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection.releaseSavepoint(savepoint);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability)
            throws SQLException {

        assertOpened();
        try {
            return physicalConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {

        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection.prepareStatement(sql, resultSetType, resultSetConcurrency,
                    resultSetHoldability), sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        assertOpened();
        try {
            return physicalConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection.prepareStatement(sql, autoGeneratedKeys), sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection.prepareStatement(sql, columnIndexes), sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection.prepareStatement(sql, columnNames), sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    private SQLException wrapException(SQLException e, String sql) {
        return new LjtSQLException("Failed to execute the SQL: " + sql, e);
    }

    // #java8comp
    public Clob createClob() throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public Blob createBlob() throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public NClob createNClob() throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public SQLXML createSQLXML() throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public boolean isValid(int timeout) throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new IllegalStateException("Not implemented yet");
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new IllegalStateException("Not implemented yet");
    }

    public String getClientInfo(String name) throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public Properties getClientInfo() throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public void setSchema(String schema) throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public String getSchema() throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public void abort(Executor executor) throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public int getNetworkTimeout() throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    @SuppressWarnings("unchecked")
    public Object unwrap(@SuppressWarnings("rawtypes") Class iface) throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new IllegalStateException("Not implemented yet");
    }
}
