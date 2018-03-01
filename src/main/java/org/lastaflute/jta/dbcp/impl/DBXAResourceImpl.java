/*
 * Copyright 2015-2018 the original author or authors.
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

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import org.lastaflute.jta.core.xa.DefaultXAResource;
import org.lastaflute.jta.dbcp.DBXAResource;
import org.lastaflute.jta.exception.LjtXAException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class DBXAResourceImpl extends DefaultXAResource implements DBXAResource {

    protected final Connection connection;

    public DBXAResourceImpl(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    protected void doBegin(Xid xid) throws XAException {
        try {
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new LjtXAException("Failed to set auto-commit: xid=" + xid, e);
        }
    }

    @Override
    protected void doCommit(Xid xid, boolean onePhase) throws XAException {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new LjtXAException("Failed to commit: xid=" + xid, e);
        }
    }

    @Override
    protected int doPrepare(Xid xid) throws XAException {
        try {
            if (connection.isClosed()) {
                return XA_RDONLY;
            }
            return XA_OK;
        } catch (SQLException e) {
            throw new LjtXAException("Failed to determine connecton closed or not: xid=" + xid, e);
        }
    }

    @Override
    protected void doRollback(Xid xid) throws XAException {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new LjtXAException("Failed to roll-back: xid=" + xid, e);
        }
    }
}