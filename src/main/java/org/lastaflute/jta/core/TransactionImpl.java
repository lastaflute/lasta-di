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
package org.lastaflute.jta.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.lastaflute.jta.core.xa.XidImpl;
import org.lastaflute.jta.exception.LjtIllegalStateException;
import org.lastaflute.jta.exception.LjtRollbackException;
import org.lastaflute.jta.exception.LjtSystemException;
import org.lastaflute.jta.helper.LjtLinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TransactionImpl implements ExtendedTransaction, SynchronizationRegister {

    private static Logger logger = LoggerFactory.getLogger(TransactionImpl.class);

    private static final int VOTE_READONLY = 0;
    private static final int VOTE_COMMIT = 1;
    private static final int VOTE_ROLLBACK = 2;

    private Xid xid;
    private int status = Status.STATUS_NO_TRANSACTION;
    private List<XAResourceWrapper> xaResourceWrappers = new ArrayList<XAResourceWrapper>();
    private List<Synchronization> synchronizations = new ArrayList<Synchronization>();
    private List<Synchronization> interposedSynchronizations = new ArrayList<Synchronization>();
    private Map<Object, Object> resourceMap = new HashMap<Object, Object>();
    private boolean suspended = false;
    private int branchId = 0;

    public TransactionImpl() {
    }

    public void begin() throws NotSupportedException, SystemException {
        status = Status.STATUS_ACTIVE;
        init();
        if (logger.isDebugEnabled()) {
            logger.debug("Begin transaction: {}", this);
        }
    }

    public void suspend() throws SystemException {
        assertNotSuspended();
        assertActiveOrMarkedRollback();
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            final XAResourceWrapper xarw = getXAResourceWrapper(i);
            try {
                xarw.end(XAResource.TMSUSPEND);
            } catch (XAException e) {
                throw new LjtSystemException("Failed to suspend the transaction: xid=" + xid, e);
            }
        }
        suspended = true;
    }

    private void assertNotSuspended() throws IllegalStateException {
        if (suspended) {
            throw new LjtIllegalStateException("Already suspended the transaction: xid=" + xid);
        }
    }

    private void assertActive() throws IllegalStateException {
        switch (status) {
        case Status.STATUS_ACTIVE:
            break;
        default:
            throwIllegalStateException();
        }
    }

    private void throwIllegalStateException() throws IllegalStateException {
        switch (status) {
        case Status.STATUS_PREPARING:
            throw new LjtIllegalStateException("Already begun preparing the transaction: xid=" + xid);
        case Status.STATUS_PREPARED:
            throw new LjtIllegalStateException("Already prepared the transaction: xid=" + xid);
        case Status.STATUS_COMMITTING:
            throw new LjtIllegalStateException("Already begun committing the transaction: xid=" + xid);
        case Status.STATUS_COMMITTED:
            throw new LjtIllegalStateException("Already committed the transaction: xid=" + xid);
        case Status.STATUS_MARKED_ROLLBACK:
            throw new LjtIllegalStateException("Already marked as rollback the transaction: xid=" + xid);
        case Status.STATUS_ROLLING_BACK:
            throw new LjtIllegalStateException("Already begun rollback the transaction: xid=" + xid);
        case Status.STATUS_ROLLEDBACK:
            throw new LjtIllegalStateException("Already rollbacked the transaction: xid=" + xid);
        case Status.STATUS_NO_TRANSACTION:
            throw new LjtIllegalStateException("Not begun transaction: xid=" + xid);
        case Status.STATUS_UNKNOWN:
            throw new LjtIllegalStateException("Unknown status of transaction: " + xid);
        default:
            throw new LjtIllegalStateException("Unexpected transaction status: " + status + " xid=" + xid);
        }
    }

    private int getXAResourceWrapperSize() {
        return xaResourceWrappers.size();
    }

    private XAResourceWrapper getXAResourceWrapper(int index) {
        return (XAResourceWrapper) xaResourceWrappers.get(index);
    }

    public void resume() throws SystemException {
        assertSuspended();
        assertActiveOrMarkedRollback();
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            final XAResourceWrapper xarw = getXAResourceWrapper(i);
            try {
                xarw.start(XAResource.TMRESUME);
            } catch (XAException e) {
                throw new LjtSystemException("Failed to resume the transaction: xid=" + xid, e);
            }
        }
        suspended = false;
    }

    private void assertSuspended() throws IllegalStateException {
        if (!suspended) {
            throw new LjtIllegalStateException("Not suspended for resume(): xid=" + xid);
        }
    }

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException {
        try {
            assertNotSuspended();
            assertActive();
            beforeCompletion();
            if (status == Status.STATUS_ACTIVE) {
                endResources(XAResource.TMSUCCESS);
                if (getXAResourceWrapperSize() == 0) {
                    status = Status.STATUS_COMMITTED;
                } else if (getXAResourceWrapperSize() == 1) {
                    commitOnePhase();
                } else {
                    switch (prepareResources()) {
                    case VOTE_READONLY:
                        status = Status.STATUS_COMMITTED;
                        break;
                    case VOTE_COMMIT:
                        commitTwoPhase();
                        break;
                    case VOTE_ROLLBACK:
                        rollbackForVoteOK();
                    }
                }
                if (status == Status.STATUS_COMMITTED) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Commit transaction: {}", this);
                    }
                }
            }
            final boolean rolledBack = status != Status.STATUS_COMMITTED;
            afterCompletion();
            if (rolledBack) {
                throw new LjtRollbackException("Cannot commit the transaction: xid=" + xid);
            }
        } finally {
            destroy();
        }
    }

    private void beforeCompletion() {
        for (int i = 0; i < getSynchronizationSize() && status == Status.STATUS_ACTIVE; ++i) {
            beforeCompletion(getSynchronization(i));
        }
        for (int i = 0; i < getInterposedSynchronizationSize() && status == Status.STATUS_ACTIVE; ++i) {
            beforeCompletion(getInterposedSynchronization(i));
        }
    }

    private void beforeCompletion(Synchronization sync) {
        try {
            sync.beforeCompletion();
        } catch (Throwable t) {
            logger.debug("Failed to process the before completion: " + sync, t);
            status = Status.STATUS_MARKED_ROLLBACK;
            endResources(XAResource.TMFAIL);
            rollbackResources();
        }
    }

    private void endResources(int flag) {
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            XAResourceWrapper xarw = getXAResourceWrapper(i);
            try {
                xarw.end(flag);
            } catch (Throwable t) {
                logger.debug("Failed to end the XA resource: " + xarw + " " + flag, t);
                status = Status.STATUS_MARKED_ROLLBACK;
            }
        }
    }

    private void commitOnePhase() {
        status = Status.STATUS_COMMITTING;
        XAResourceWrapper xari = getXAResourceWrapper(0);
        try {
            xari.commit(true);
            status = Status.STATUS_COMMITTED;
        } catch (Throwable t) {
            logger.debug("Failed to commit the XA resource: " + xari, t);
            status = Status.STATUS_UNKNOWN;
        }
    }

    private int prepareResources() {
        status = Status.STATUS_PREPARING;
        int vote = VOTE_READONLY;
        LjtLinkedList xarwList = new LjtLinkedList();
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            XAResourceWrapper xarw = getXAResourceWrapper(i);
            if (xarw.isCommitTarget()) {
                xarwList.addFirst(xarw);
            }
        }
        for (int i = 0; i < xarwList.size(); ++i) {
            XAResourceWrapper xarw = (XAResourceWrapper) xarwList.get(i);
            try {
                if (i == xarwList.size() - 1) {
                    // last resource commit optimization
                    xarw.commit(true);
                    xarw.setVoteOk(false);
                    vote = VOTE_COMMIT;
                } else if (xarw.prepare() == XAResource.XA_OK) {
                    vote = VOTE_COMMIT;
                } else {
                    xarw.setVoteOk(false);
                }
            } catch (Throwable t) {
                logger.debug("Failed to vote the XA resource: " + xarw, t);
                xarw.setVoteOk(false);
                status = Status.STATUS_MARKED_ROLLBACK;
                return VOTE_ROLLBACK;
            }
        }
        if (status == Status.STATUS_PREPARING) {
            status = Status.STATUS_PREPARED;
        }
        return vote;
    }

    private void commitTwoPhase() {
        status = Status.STATUS_COMMITTING;
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            XAResourceWrapper xarw = getXAResourceWrapper(i);
            if (xarw.isCommitTarget() && xarw.isVoteOk()) {
                try {
                    xarw.commit(false);
                } catch (Throwable t) {
                    logger.debug("Failed to commit the XA resource: " + xarw, t);
                    status = Status.STATUS_UNKNOWN;
                }
            }
        }
        if (status == Status.STATUS_COMMITTING) {
            status = Status.STATUS_COMMITTED;
        }
    }

    private void rollbackForVoteOK() {
        status = Status.STATUS_ROLLING_BACK;
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            XAResourceWrapper xarw = getXAResourceWrapper(i);
            if (xarw.isVoteOk()) {
                try {
                    xarw.rollback();
                } catch (Throwable t) {
                    logger.debug("Failed to rollback the XA resource: " + xarw, t);
                    status = Status.STATUS_UNKNOWN;
                }
            }
        }
        if (status == Status.STATUS_ROLLING_BACK) {
            status = Status.STATUS_ROLLEDBACK;
        }
    }

    private void afterCompletion() {
        final int status = this.status;
        this.status = Status.STATUS_NO_TRANSACTION;
        for (int i = 0; i < getInterposedSynchronizationSize(); ++i) {
            afterCompletion(status, getInterposedSynchronization(i));
        }
        for (int i = 0; i < getSynchronizationSize(); ++i) {
            afterCompletion(status, getSynchronization(i));
        }
    }

    private void afterCompletion(final int status, final Synchronization sync) {
        try {
            sync.afterCompletion(status);
        } catch (Throwable t) {
            logger.debug("Failed to process the after completion: " + sync + " " + status, t);
        }
    }

    private int getSynchronizationSize() {
        return synchronizations.size();
    }

    private Synchronization getSynchronization(int index) {
        return (Synchronization) synchronizations.get(index);
    }

    private int getInterposedSynchronizationSize() {
        return interposedSynchronizations.size();
    }

    private Synchronization getInterposedSynchronization(int index) {
        return (Synchronization) interposedSynchronizations.get(index);
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        try {
            assertNotSuspended();
            assertActiveOrMarkedRollback();
            endResources(XAResource.TMFAIL);
            rollbackResources();
            if (logger.isDebugEnabled()) {
                logger.debug("Rollback transaction: {}", this);
            }
            afterCompletion();
        } finally {
            destroy();
        }
    }

    private void assertActiveOrMarkedRollback() throws IllegalStateException {
        switch (status) {
        case Status.STATUS_ACTIVE:
        case Status.STATUS_MARKED_ROLLBACK:
            break;
        default:
            throwIllegalStateException();
        }
    }

    private void rollbackResources() {
        status = Status.STATUS_ROLLING_BACK;
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            XAResourceWrapper xarw = getXAResourceWrapper(i);
            try {
                if (xarw.isCommitTarget()) {
                    xarw.rollback();
                }
            } catch (Throwable t) {
                logger.debug("Failed to rollback the XA resource: " + xarw, t);
                status = Status.STATUS_UNKNOWN;
            }
        }
        if (status == Status.STATUS_ROLLING_BACK) {
            status = Status.STATUS_ROLLEDBACK;
        }
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {

        assertNotSuspended();
        assertActiveOrPreparingOrPrepared();
        status = Status.STATUS_MARKED_ROLLBACK;
    }

    private void assertActiveOrPreparingOrPrepared() throws IllegalStateException {
        switch (status) {
        case Status.STATUS_ACTIVE:
        case Status.STATUS_PREPARING:
        case Status.STATUS_PREPARED:
            break;
        default:
            throwIllegalStateException();
        }
    }

    public boolean enlistResource(XAResource xaResource) throws RollbackException, IllegalStateException, SystemException {
        boolean oracled = xaResource.getClass().getName().startsWith("oracle");
        assertNotSuspended();
        assertActive();
        Xid xid = null;
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            XAResourceWrapper xarw = getXAResourceWrapper(i);
            if (xaResource.equals(xarw.getXAResource())) {
                return false;
            } else if (oracled) {
                continue;
            } else {
                try {
                    if (xaResource.isSameRM(xarw.getXAResource())) {
                        xid = xarw.getXid();
                        break;
                    }
                } catch (XAException ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
        }
        int flag = xid == null ? XAResource.TMNOFLAGS : XAResource.TMJOIN;
        boolean commitTarget = xid == null ? true : false;
        if (xid == null) {
            xid = createXidBranch();
        }
        try {
            xaResource.start(xid, flag);
            xaResourceWrappers.add(new XAResourceWrapper(xaResource, xid, commitTarget));
            return true;
        } catch (XAException ex) {
            IllegalStateException ise = new IllegalStateException(ex.toString());
            ise.initCause(ex);
            throw ise;
        }
    }

    private Xid createXidBranch() {
        return new XidImpl(xid, ++branchId);
    }

    public boolean delistResource(XAResource xaResource, int flag) throws IllegalStateException, SystemException {
        assertNotSuspended();
        assertActiveOrMarkedRollback();
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            XAResourceWrapper xarw = getXAResourceWrapper(i);
            if (xaResource.equals(xarw.getXAResource())) {
                try {
                    xarw.end(flag);
                    return true;
                } catch (XAException ex) {
                    logger.debug("Failed to end the XA resource: " + xarw + " " + flag, ex);
                    status = Status.STATUS_MARKED_ROLLBACK;
                    return false;
                }
            }
        }
        throw new LjtIllegalStateException("Unregistered XA resource: " + xaResource);
    }

    public int getStatus() {
        return status;
    }

    public void registerSynchronization(Synchronization sync) throws RollbackException, IllegalStateException, SystemException {
        assertNotSuspended();
        assertActive();
        synchronizations.add(sync);
    }

    public void registerInterposedSynchronization(Synchronization sync) throws IllegalStateException {
        assertNotSuspended();
        assertActive();
        interposedSynchronizations.add(sync);
    }

    public void putResource(Object key, Object value) throws IllegalStateException {
        assertNotSuspended();
        resourceMap.put(key, value);
    }

    public Object getResource(Object key) throws IllegalStateException {
        assertNotSuspended();
        return resourceMap.get(key);
    }

    public Xid getXid() {
        return xid;
    }

    public boolean isSuspended() {
        return suspended;
    }

    private void init() {
        xid = new XidImpl();
    }

    private void destroy() {
        status = Status.STATUS_NO_TRANSACTION;
        xaResourceWrappers.clear();
        synchronizations.clear();
        interposedSynchronizations.clear();
        resourceMap.clear();
        suspended = false;
    }

    public String toString() {
        return xid.toString();
    }

    public List<Synchronization> getSynchronizations() {
        return synchronizations;
    }

    public List<Synchronization> getInterposedSynchronizations() {
        return interposedSynchronizations;
    }
}
