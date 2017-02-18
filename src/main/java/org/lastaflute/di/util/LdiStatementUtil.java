/*
 * Copyright 2015-2017 the original author or authors.
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
package org.lastaflute.di.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.lastaflute.di.exception.SQLRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiStatementUtil {

    protected LdiStatementUtil() {
    }

    /**
     * @param statement
     * @param sql
     * @return 
     * @throws SQLRuntimeException
     * @see Statement#execute(String)
     */
    public static boolean execute(Statement statement, String sql) throws SQLRuntimeException {
        try {
            return statement.execute(sql);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * @param statement
     * @param fetchSize
     * @throws SQLRuntimeException
     * @see Statement#setFetchSize(int)
     */
    public static void setFetchSize(Statement statement, int fetchSize) throws SQLRuntimeException {
        try {
            statement.setFetchSize(fetchSize);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * @param statement
     * @param maxRows
     * @throws SQLRuntimeException
     * @see Statement#setMaxRows(int)
     */
    public static void setMaxRows(Statement statement, int maxRows) throws SQLRuntimeException {
        try {
            statement.setMaxRows(maxRows);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * @param statement
     * @param queryTimeout
     * @throws SQLRuntimeException
     * @see Statement#setQueryTimeout(int)
     */
    public static void setQueryTimeout(Statement statement, int queryTimeout) throws SQLRuntimeException {
        try {
            statement.setQueryTimeout(queryTimeout);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * @param statement
     * @throws SQLRuntimeException
     * @see Statement#close()
     */
    public static void close(Statement statement) throws SQLRuntimeException {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * @param statement
     * @return 
     * @throws SQLRuntimeException
     */
    public static ResultSet getResultSet(Statement statement) throws SQLRuntimeException {
        try {
            return statement.getResultSet();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * @param statement
     * @return 
     * @see Statement#getUpdateCount()
     */
    public static int getUpdateCount(Statement statement) {
        try {
            return statement.getUpdateCount();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }
}
