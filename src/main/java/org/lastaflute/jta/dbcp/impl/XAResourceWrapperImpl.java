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

import java.sql.SQLException;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.lastaflute.jta.dbcp.ConnectionWrapper;
import org.lastaflute.jta.exception.LjtXAException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class XAResourceWrapperImpl implements XAResource {

    protected final XAResource physicalXAResource;
    protected final ConnectionWrapper connectionWrapper;

    public XAResourceWrapperImpl(XAResource physicalXAResource, ConnectionWrapper connectionWrapper) {
        this.physicalXAResource = physicalXAResource;
        this.connectionWrapper = connectionWrapper;
    }

    public XAResource getPhysicalXAResource() {
        return physicalXAResource;
    }

    public ConnectionWrapper getConnectionWrapper() {
        return connectionWrapper;
    }

    protected void release() throws XAException {
        try {
            getConnectionWrapper().release();
        } catch (SQLException e) {
            throw new LjtXAException("Failed to release the connection: " + connectionWrapper, e);
        }
    }

    public int getTransactionTimeout() throws XAException {
        try {
            return getPhysicalXAResource().getTransactionTimeout();
        } catch (XAException ex) {
            release();
            throw ex;
        }
    }

    public boolean setTransactionTimeout(int arg0) throws XAException {
        try {
            return getPhysicalXAResource().setTransactionTimeout(arg0);
        } catch (XAException ex) {
            release();
            throw ex;
        }
    }

    public boolean isSameRM(XAResource arg0) throws XAException {
        try {
            return getPhysicalXAResource().isSameRM(arg0);
        } catch (XAException ex) {
            release();
            throw ex;
        }
    }

    public Xid[] recover(int arg0) throws XAException {
        try {
            return getPhysicalXAResource().recover(arg0);
        } catch (XAException ex) {
            release();
            throw ex;
        }
    }

    public int prepare(Xid arg0) throws XAException {
        try {
            return getPhysicalXAResource().prepare(arg0);
        } catch (XAException ex) {
            release();
            throw ex;
        }
    }

    public void forget(Xid arg0) throws XAException {
        try {
            getPhysicalXAResource().forget(arg0);
        } catch (XAException ex) {
            release();
            throw ex;
        }
    }

    public void rollback(Xid arg0) throws XAException {
        try {
            getPhysicalXAResource().rollback(arg0);
        } catch (XAException ex) {
            release();
            throw ex;
        }
    }

    public void end(Xid arg0, int arg1) throws XAException {
        try {
            getPhysicalXAResource().end(arg0, arg1);
        } catch (XAException ex) {
            release();
            throw ex;
        }
    }

    public void start(Xid arg0, int arg1) throws XAException {
        try {
            getPhysicalXAResource().start(arg0, arg1);
        } catch (XAException ex) {
            release();
            throw ex;
        }
    }

    public void commit(Xid arg0, boolean arg1) throws XAException {
        try {
            getPhysicalXAResource().commit(arg0, arg1);
        } catch (XAException ex) {
            release();
            throw ex;
        }
    }
}