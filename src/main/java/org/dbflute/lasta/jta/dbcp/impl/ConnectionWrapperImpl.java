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
package org.dbflute.lasta.jta.dbcp.impl;

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

import org.dbflute.lasta.jta.dbcp.ConnectionPool;
import org.dbflute.lasta.jta.dbcp.ConnectionWrapper;
import org.dbflute.lasta.jta.exception.LjtSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ConnectionWrapperImpl implements ConnectionWrapper, ConnectionEventListener {

    private static final Logger logger_ = LoggerFactory.getLogger(ConnectionWrapperImpl.class);

    private XAConnection xaConnection_;
    private Connection physicalConnection_;
    private XAResource xaResource_;
    private ConnectionPool connectionPool_;
    private boolean closed_ = false;
    private Transaction tx_;

    public ConnectionWrapperImpl(final XAConnection xaConnection, final Connection physicalConnection, final ConnectionPool connectionPool,
            final Transaction tx) throws SQLException {
        xaConnection_ = xaConnection;
        physicalConnection_ = physicalConnection;
        xaResource_ = new XAResourceWrapperImpl(xaConnection.getXAResource(), this);
        connectionPool_ = connectionPool;
        tx_ = tx;
        xaConnection_.addConnectionEventListener(this);
    }

    public void init(final Transaction tx) {
        closed_ = false;
        tx_ = tx;
    }

    public Connection getPhysicalConnection() {
        return physicalConnection_;
    }

    public XAResource getXAResource() {
        return xaResource_;
    }

    public XAConnection getXAConnection() {
        return xaConnection_;
    }

    public void cleanup() {
        xaConnection_.removeConnectionEventListener(this);
        closed_ = true;
        xaConnection_ = null;
        physicalConnection_ = null;
        tx_ = null;
    }

    public void closeReally() {
        if (xaConnection_ == null) {
            return;
        }
        closed_ = true;
        try {
            if (!physicalConnection_.isClosed()) {
                if (!physicalConnection_.getAutoCommit()) {
                    try {
                        physicalConnection_.rollback();
                        physicalConnection_.setAutoCommit(true);
                    } catch (final SQLException e) {
                        logger_.info("Failed to roll-back physical connection when closing really: " + physicalConnection_, e);
                    }
                }
                physicalConnection_.close();
            }
        } catch (final SQLException e) {
            logger_.info("Failed to close physical connection when closing really: " + physicalConnection_, e);
        } finally {
            physicalConnection_ = null;
        }
        try {
            xaConnection_.close();
            logger_.debug("Closed the physical connection: " + xaConnection_);
        } catch (final SQLException e) {
            logger_.info("Failed to close XA connection when closing really: " + xaConnection_, e);
        } finally {
            xaConnection_ = null;
        }
    }

    private void assertOpened() throws SQLException {
        if (closed_) {
            throw new LjtSQLException("Already closed the connection: " + xaConnection_);
        }
    }

    private void assertLocalTx() throws SQLException {
        if (tx_ != null) {
            throw new LjtSQLException("Cannot use when distributed transaction: " + tx_);
        }
    }

    public void release() throws SQLException {
        if (!closed_) {
            connectionPool_.release(this);
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
            return physicalConnection_.createStatement();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection_.prepareStatement(sql), sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public CallableStatement prepareCall(final String sql) throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.prepareCall(sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public String nativeSQL(final String sql) throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.nativeSQL(sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public boolean isClosed() throws SQLException {
        return closed_;
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getMetaData();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void setReadOnly(final boolean readOnly) throws SQLException {
        assertOpened();
        try {
            physicalConnection_.setReadOnly(readOnly);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public boolean isReadOnly() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.isReadOnly();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void setCatalog(final String catalog) throws SQLException {
        assertOpened();
        try {
            physicalConnection_.setCatalog(catalog);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public String getCatalog() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getCatalog();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void close() throws SQLException {
        if (closed_) {
            return;
        }
        // TODO jflute lastaflute: [E] fitting: DI :: connection pool logical connection logging for internal debug
        //if (logger_.isDebugEnabled()) {
        //    logger_.debug("Closed the logical connection: " + xaConnection_);
        //}
        if (tx_ == null) {
            connectionPool_.checkIn(this);
        } else {
            connectionPool_.checkInTx(tx_);
        }
    }

    public void setTransactionIsolation(final int level) throws SQLException {
        assertOpened();
        try {
            physicalConnection_.setTransactionIsolation(level);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public int getTransactionIsolation() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getTransactionIsolation();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getWarnings();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void clearWarnings() throws SQLException {
        assertOpened();
        try {
            physicalConnection_.clearWarnings();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void commit() throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection_.commit();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void rollback() throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection_.rollback();
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
            physicalConnection_.setAutoCommit(autoCommit);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public boolean getAutoCommit() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getAutoCommit();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {

        assertOpened();
        try {
            return physicalConnection_.createStatement(resultSetType, resultSetConcurrency);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public Map getTypeMap() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getTypeMap();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void setTypeMap(final Map map) throws SQLException {
        assertOpened();
        try {
            physicalConnection_.setTypeMap(map);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {

        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection_.prepareStatement(sql, resultSetType, resultSetConcurrency), sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {

        assertOpened();
        try {
            return physicalConnection_.prepareCall(sql, resultSetType, resultSetConcurrency);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public void setHoldability(final int holdability) throws SQLException {
        assertOpened();
        try {
            physicalConnection_.setHoldability(holdability);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public int getHoldability() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getHoldability();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public Savepoint setSavepoint() throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            return physicalConnection_.setSavepoint();
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public Savepoint setSavepoint(final String name) throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            return physicalConnection_.setSavepoint(name);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void rollback(final Savepoint savepoint) throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection_.rollback(savepoint);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection_.releaseSavepoint(savepoint);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability)
            throws SQLException {

        assertOpened();
        try {
            return physicalConnection_.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        } catch (SQLException ex) {
            release();
            throw ex;
        }
    }

    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {

        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection_.prepareStatement(sql, resultSetType, resultSetConcurrency,
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
            return physicalConnection_.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection_.prepareStatement(sql, autoGeneratedKeys), sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection_.prepareStatement(sql, columnIndexes), sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection_.prepareStatement(sql, columnNames), sql);
        } catch (SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    private SQLException wrapException(SQLException e, String sql) {
        return new LjtSQLException("Failed to execute the SQL: " + sql, e);
    }

    // #java8comp
    /* (non-Javadoc)
     * @see java.sql.Connection#createClob()
     */
    public Clob createClob() throws SQLException {

        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#createBlob()
     */
    public Blob createBlob() throws SQLException {

        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#createNClob()
     */
    public NClob createNClob() throws SQLException {

        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#createSQLXML()
     */
    public SQLXML createSQLXML() throws SQLException {

        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#isValid(int)
     */
    public boolean isValid(int timeout) throws SQLException {

        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setClientInfo(java.lang.String, java.lang.String)
     */
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setClientInfo(java.util.Properties)
     */
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getClientInfo(java.lang.String)
     */
    public String getClientInfo(String name) throws SQLException {

        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getClientInfo()
     */
    public Properties getClientInfo() throws SQLException {

        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#createArrayOf(java.lang.String, java.lang.Object[])
     */
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {

        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#createStruct(java.lang.String, java.lang.Object[])
     */
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {

        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setSchema(java.lang.String)
     */
    public void setSchema(String schema) throws SQLException {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getSchema()
     */
    public String getSchema() throws SQLException {

        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#abort(java.util.concurrent.Executor)
     */
    public void abort(Executor executor) throws SQLException {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setNetworkTimeout(java.util.concurrent.Executor, int)
     */
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getNetworkTimeout()
     */
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    /* (non-Javadoc)
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public Object unwrap(Class iface) throws SQLException {
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class iface) throws SQLException {
        return false;
    }
}
