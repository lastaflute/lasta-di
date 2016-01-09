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
package org.lastaflute.jta.dbcp;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class PooledDataSource implements DataSource, Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ConnectionPool connectionPool;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PooledDataSource(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    // ===================================================================================
    //                                                                           Operation
    //                                                                           =========
    public Connection getConnection() throws SQLException {
        return connectionPool.checkOut();
    }

    public Connection getConnection(String user, String password) throws SQLException {
        return getConnection();
    }

    // ===================================================================================
    //                                                                              Unused
    //                                                                              ======
    public void setLoginTimeout(int loginTimeout) throws SQLException {
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public void setLogWriter(PrintWriter logWriter) throws SQLException {
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    // #java8comp
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }
}