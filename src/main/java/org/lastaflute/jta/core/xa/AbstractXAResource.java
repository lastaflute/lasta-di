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
package org.lastaflute.jta.core.xa;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.lastaflute.jta.exception.LjtXAException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractXAResource implements XAResource, XAResourceStatus {

    private Xid currentXid;
    private int status = RS_NONE;
    private int timeout = 0;

    public AbstractXAResource() {
    }

    public void start(Xid xid, int flags) throws XAException {
        switch (flags) {
        case TMNOFLAGS:
            begin(xid);
            break;
        case TMRESUME:
            resume(xid);
            break;
        default:
            throw new LjtXAException("Unexpected flags: " + flags);
        }
    }

    private void begin(Xid xid) throws XAException {
        assertCurrentXidNull();
        doBegin(xid);
        currentXid = xid;
        status = RS_ACTIVE;
    }

    private void assertCurrentXidNull() throws XAException {
        if (currentXid != null) {
            throw new LjtXAException("Unsupported nested transaction: currentXid=" + currentXid);
        }
    }

    protected abstract void doBegin(Xid xid) throws XAException;

    private void resume(Xid xid) throws XAException {
        assertCurrentXidSame(xid);
        assertStatusSuspended();
        doResume(xid);
        status = RS_ACTIVE;
    }

    private void assertCurrentXidSame(final Xid xid) throws XAException {
        if (currentXid != xid) {
            throw new LjtXAException("Not matched the current and specified xid: current=" + currentXid + " specified=" + xid);
        }
    }

    private void assertStatusSuspended() throws XAException {
        if (status != RS_SUSPENDED) {
            throw new LjtXAException("The status should be suspended but: " + status);
        }
    }

    protected abstract void doResume(Xid xid) throws XAException;

    public void end(Xid xid, int flags) throws XAException {
        assertCurrentXidSame(xid);
        assertStatusActive();
        switch (flags) {
        case TMSUSPEND:
            suspend(xid);
            break;
        case TMFAIL:
            fail(xid);
            break;
        case TMSUCCESS:
            success(xid);
            break;
        default:
            throw new LjtXAException("Unexpected the flags: " + flags + " xid=" + xid);
        }
    }

    private void assertStatusActive() throws XAException {
        if (status != RS_ACTIVE) {
            throw new LjtXAException("The status should be active: status=" + status);
        }
    }

    private void suspend(Xid xid) throws XAException {
        doSuspend(xid);
        status = RS_SUSPENDED;
    }

    protected abstract void doSuspend(Xid xid) throws XAException;

    private void fail(Xid xid) throws XAException {
        doFail(xid);
        status = RS_FAIL;
    }

    protected abstract void doFail(Xid xid) throws XAException;

    private void success(Xid xid) throws XAException {
        doSuccess(xid);
        status = RS_SUCCESS;
    }

    protected abstract void doSuccess(Xid xid) throws XAException;

    public int prepare(Xid xid) throws XAException {
        assertCurrentXidSame(xid);
        assertStatusSuccess();
        int ret = doPrepare(xid);
        if (ret == XA_OK) {
            status = RS_PREPARED;
        } else {
            status = RS_NONE;
        }
        return ret;
    }

    private void assertStatusSuccess() throws XAException {
        if (status != RS_SUCCESS) {
            throw new LjtXAException("The status should be success: " + status);
        }
    }

    protected abstract int doPrepare(Xid xid) throws XAException;

    public void commit(Xid xid, boolean onePhase) throws XAException {
        assertCurrentXidSame(xid);
        if (onePhase) {
            assertStatusSuccess();
        } else {
            assertStatusPrepared();
        }
        doCommit(xid, onePhase);
        init();
    }

    private void assertStatusPrepared() throws XAException {
        if (status != RS_PREPARED) {
            throw new LjtXAException("The status should be prepared: " + status);
        }
    }

    protected abstract void doCommit(Xid xid, boolean onePhase) throws XAException;

    private void init() {
        currentXid = null;
        status = RS_NONE;
    }

    public void forget(Xid xid) throws XAException {
        assertCurrentXidSame(xid);
        doForget(xid);
        init();
    }

    protected abstract void doForget(Xid xid) throws XAException;

    public Xid[] recover(final int flag) throws XAException {
        return null;
    }

    public void rollback(final Xid xid) throws XAException {
        assertCurrentXidSame(xid);
        assertStatusSuccessOrFailOrPrepared();
        doRollback(xid);
        init();
    }

    private void assertStatusSuccessOrFailOrPrepared() throws XAException {
        switch (status) {
        case RS_SUCCESS:
        case RS_FAIL:
        case RS_PREPARED:
            break;
        default:
            throw new LjtXAException("The status should be success or fail or prepared: " + status);
        }
    }

    protected abstract void doRollback(Xid xid) throws XAException;

    public boolean isSameRM(XAResource xar) throws XAException {
        return false;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "xaResource:{" + currentXid + ", status=" + status + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Xid getCurrentXid() {
        return currentXid;
    }

    public int getStatus() {
        return status;
    }

    public int getTransactionTimeout() throws XAException {
        return timeout;
    }

    public boolean setTransactionTimeout(int timeout) throws XAException {
        this.timeout = timeout;
        return true;
    }
}