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
package org.lastaflute.jta.dbcp.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class XAConnectionImpl implements XAConnection {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Connection connection;
    private final XAResource xaResource;
    private final List<ConnectionEventListener> listeners = new ArrayList<ConnectionEventListener>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public XAConnectionImpl(Connection connection) {
        this.connection = connection;
        this.xaResource = newDBXAResourceImpl(connection);
    }

    protected DBXAResourceImpl newDBXAResourceImpl(Connection connection) {
        return new DBXAResourceImpl(connection);
    }

    // ===================================================================================
    //                                                                   Listener Handling
    //                                                                   =================
    public synchronized void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }

    // #java8comp
    public void addStatementEventListener(StatementEventListener listener) {
    }

    public void removeStatementEventListener(StatementEventListener listener) {
    }

    // ===================================================================================
    //                                                                             close()
    //                                                                             =======
    public void close() throws SQLException {
        if (connection == null) {
            return;
        }
        if (!connection.isClosed()) {
            connection.close();
        }
        connection = null;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "xaConnection:{" + xaResource + "}@" + Integer.toHexString(hashCode());
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Connection getConnection() throws SQLException {
        return connection;
    }

    public XAResource getXAResource() {
        return xaResource;
    }
}