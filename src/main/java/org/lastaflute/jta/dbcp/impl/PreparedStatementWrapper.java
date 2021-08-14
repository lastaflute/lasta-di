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
package org.lastaflute.jta.dbcp.impl;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.lastaflute.jta.exception.LjtSQLException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class PreparedStatementWrapper implements PreparedStatement {

    protected final PreparedStatement original;
    protected final String sql;

    public PreparedStatementWrapper(PreparedStatement original, String sql) {
        this.original = original;
        this.sql = sql;
    }

    private SQLException wrapException(SQLException e) {
        return wrapException(e, sql);
    }

    private SQLException wrapException(SQLException e, String sql) {
        if (sql != null) {
            return new LjtSQLException("Failed to execute the SQL: " + sql, e.getSQLState(), e.getErrorCode(), e);
        }
        return e;
    }

    public ResultSet executeQuery() throws SQLException {
        try {
            return original.executeQuery();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int executeUpdate() throws SQLException {
        try {
            return original.executeUpdate();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return original.getMetaData();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        try {
            return original.getParameterMetaData();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getMaxRows() throws SQLException {
        try {
            return original.getMaxRows();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getQueryTimeout() throws SQLException {
        return original.getQueryTimeout();
    }

    public SQLWarning getWarnings() throws SQLException {
        try {
            return original.getWarnings();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public ResultSet getResultSet() throws SQLException {
        try {
            return original.getResultSet();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getUpdateCount() throws SQLException {
        try {
            return original.getUpdateCount();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public boolean getMoreResults() throws SQLException {
        try {
            return original.getMoreResults();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getFetchDirection() throws SQLException {
        try {
            return original.getFetchDirection();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getFetchSize() throws SQLException {
        try {
            return original.getFetchSize();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getResultSetConcurrency() throws SQLException {
        try {
            return original.getResultSetConcurrency();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getResultSetType() throws SQLException {
        try {
            return original.getResultSetType();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int[] executeBatch() throws SQLException {
        try {
            return original.executeBatch();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return original.getConnection();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public boolean getMoreResults(final int current) throws SQLException {
        try {
            return original.getMoreResults();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        try {
            return original.getGeneratedKeys();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int executeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        try {
            return original.executeUpdate();
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public int getResultSetHoldability() throws SQLException {
        try {
            return original.getResultSetHoldability();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void addBatch() throws SQLException {
        try {
            original.addBatch();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void addBatch(final String sql) throws SQLException {
        try {
            original.addBatch(sql);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public void cancel() throws SQLException {
        try {
            original.cancel();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void clearBatch() throws SQLException {
        try {
            original.clearBatch();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void clearParameters() throws SQLException {
        try {
            original.clearParameters();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void clearWarnings() throws SQLException {
        try {
            original.clearWarnings();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void close() throws SQLException {
        try {
            original.close();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public boolean execute() throws SQLException {
        try {
            return original.execute();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public boolean execute(final String sql, final int autoGeneratedKeys) throws SQLException {
        try {
            return original.execute(sql, autoGeneratedKeys);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public boolean execute(final String sql, final int[] columnIndexes) throws SQLException {
        try {
            return original.execute(sql, columnIndexes);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public boolean execute(final String sql, final String[] columnNames) throws SQLException {
        try {
            return original.execute(sql, columnNames);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public boolean execute(final String sql) throws SQLException {
        try {
            return original.execute(sql);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public ResultSet executeQuery(final String sql) throws SQLException {
        try {
            return original.executeQuery(sql);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public int executeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
        try {
            return original.executeUpdate(sql, columnIndexes);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public int executeUpdate(final String sql, final String[] columnNames) throws SQLException {
        try {
            return original.executeUpdate(sql, columnNames);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public int executeUpdate(final String sql) throws SQLException {
        try {
            return original.executeUpdate(sql);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public int getMaxFieldSize() throws SQLException {
        try {
            return original.getMaxFieldSize();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setArray(final int i, final Array x) throws SQLException {
        try {
            original.setArray(i, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        try {
            original.setAsciiStream(parameterIndex, x, length);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
        try {
            original.setBigDecimal(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        try {
            original.setBinaryStream(parameterIndex, x, length);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setBlob(final int i, final Blob x) throws SQLException {
        try {
            original.setBlob(i, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
        try {
            original.setBoolean(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setByte(final int parameterIndex, final byte x) throws SQLException {
        try {
            original.setByte(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
        try {
            original.setBytes(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
        try {
            original.setCharacterStream(parameterIndex, reader, length);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setClob(final int i, final Clob x) throws SQLException {
        try {
            original.setClob(i, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setCursorName(final String name) throws SQLException {
        try {
            original.setCursorName(name);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
        try {
            original.setDate(parameterIndex, x, cal);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setDate(final int parameterIndex, final Date x) throws SQLException {
        try {
            original.setDate(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setDouble(final int parameterIndex, final double x) throws SQLException {
        try {
            original.setDouble(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setEscapeProcessing(final boolean enable) throws SQLException {
        try {
            original.setEscapeProcessing(enable);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setFetchDirection(final int direction) throws SQLException {
        try {
            original.setFetchDirection(direction);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setFetchSize(final int rows) throws SQLException {
        try {
            original.setFetchSize(rows);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setFloat(final int parameterIndex, final float x) throws SQLException {
        try {
            original.setFloat(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setInt(final int parameterIndex, final int x) throws SQLException {
        try {
            original.setInt(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setLong(final int parameterIndex, final long x) throws SQLException {
        try {
            original.setLong(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setMaxFieldSize(final int max) throws SQLException {
        try {
            original.setMaxFieldSize(max);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setMaxRows(final int max) throws SQLException {
        try {
            original.setMaxRows(max);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setNull(final int paramIndex, final int sqlType, final String typeName) throws SQLException {
        try {
            original.setNull(paramIndex, sqlType, typeName);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
        try {
            original.setNull(parameterIndex, sqlType);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scale) throws SQLException {
        try {
            original.setObject(parameterIndex, x, targetSqlType, scale);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
        try {
            original.setObject(parameterIndex, x, targetSqlType);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setObject(final int parameterIndex, final Object x) throws SQLException {
        try {
            original.setObject(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setQueryTimeout(final int seconds) throws SQLException {
        try {
            original.setQueryTimeout(seconds);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setRef(final int i, final Ref x) throws SQLException {
        try {
            original.setRef(i, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setShort(final int parameterIndex, final short x) throws SQLException {
        try {
            original.setShort(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setString(final int parameterIndex, final String x) throws SQLException {
        try {
            original.setString(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
        try {
            original.setTime(parameterIndex, x, cal);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setTime(final int parameterIndex, final Time x) throws SQLException {
        try {
            original.setTime(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
        try {
            original.setTimestamp(parameterIndex, x, cal);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
        try {
            original.setTimestamp(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    /**
     * @deprecated
     */
    public void setUnicodeStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        try {
            original.setUnicodeStream(parameterIndex, x, length);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setURL(final int parameterIndex, final URL x) throws SQLException {
        try {
            original.setURL(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public String toString() {
        return original.toString();
    }

    // #java8comp
    public boolean isClosed() throws SQLException {
        return original.isClosed();
    }

    public void setPoolable(boolean poolable) throws SQLException {
        original.setPoolable(poolable);
    }

    public boolean isPoolable() throws SQLException {
        return original.isPoolable();
    }

    public void closeOnCompletion() throws SQLException {
        original.closeOnCompletion();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        return original.isCloseOnCompletion();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return original.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return original.isWrapperFor(iface);
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        original.setRowId(parameterIndex, x);
    }

    public void setNString(int parameterIndex, String value) throws SQLException {
        original.setNString(parameterIndex, value);
    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        original.setNCharacterStream(parameterIndex, value, length);
    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        original.setNClob(parameterIndex, value);
    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        original.setClob(parameterIndex, reader, length);
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        original.setBlob(parameterIndex, inputStream, length);
    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        original.setNClob(parameterIndex, reader, length);
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        original.setSQLXML(parameterIndex, xmlObject);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        original.setAsciiStream(parameterIndex, x, length);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        original.setBinaryStream(parameterIndex, x, length);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        original.setCharacterStream(parameterIndex, reader, length);
    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        original.setAsciiStream(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        original.setBinaryStream(parameterIndex, x);
    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        original.setCharacterStream(parameterIndex, reader);
    }

    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        original.setNCharacterStream(parameterIndex, value);
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        original.setClob(parameterIndex, reader);
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        original.setBlob(parameterIndex, inputStream);
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        original.setNClob(parameterIndex, reader);
    }
}
