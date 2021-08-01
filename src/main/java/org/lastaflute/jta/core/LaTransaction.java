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
package org.lastaflute.jta.core;

import java.util.ArrayList;
import java.util.Collections;
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
import org.lastaflute.jta.helper.misc.LjtExceptionMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LaTransaction implements ExtendedTransaction, SynchronizationRegister {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LoggerFactory.getLogger(LaTransaction.class);

    private static final int VOTE_READONLY = 0;
    private static final int VOTE_COMMIT = 1;
    private static final int VOTE_ROLLBACK = 2;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Xid xid;
    protected int status = Status.STATUS_NO_TRANSACTION;
    protected final List<XAResourceWrapper> xaResourceWrappers = new ArrayList<XAResourceWrapper>();
    protected final List<Synchronization> synchronizations = new ArrayList<Synchronization>();
    protected final List<Synchronization> interposedSynchronizations = new ArrayList<Synchronization>();
    protected final Map<Object, Object> resourceMap = new HashMap<Object, Object>();
    protected boolean suspended = false;
    protected int branchId = 0;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public LaTransaction() {
    }

    // ===================================================================================
    //                                                                               Begin
    //                                                                               =====
    public void begin() throws NotSupportedException, SystemException {
        status = Status.STATUS_ACTIVE;
        init();
        if (logger.isDebugEnabled()) {
            logger.debug("Begin transaction: {}", this);
        }
    }

    protected void init() {
        xid = new XidImpl();
    }

    // ===================================================================================
    //                                                                             Suspend
    //                                                                             =======
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

    protected void assertNotSuspended() throws IllegalStateException {
        if (suspended) {
            throw new LjtIllegalStateException("Already suspended the transaction: xid=" + xid);
        }
    }

    protected void assertActive() throws IllegalStateException {
        switch (status) {
        case Status.STATUS_ACTIVE:
            break;
        default:
            throwIllegalStateException();
        }
    }

    protected void throwIllegalStateException() throws IllegalStateException {
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

    protected int getXAResourceWrapperSize() {
        return xaResourceWrappers.size();
    }

    protected XAResourceWrapper getXAResourceWrapper(int index) {
        return (XAResourceWrapper) xaResourceWrappers.get(index);
    }

    // ===================================================================================
    //                                                                              Resume
    //                                                                              ======
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

    protected void assertSuspended() throws IllegalStateException {
        if (!suspended) {
            throw new LjtIllegalStateException("Not suspended for resume(): xid=" + xid);
        }
    }

    // ===================================================================================
    //                                                                              Commit
    //                                                                              ======
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException {
        try {
            assertNotSuspended();
            assertActive();
            final DetachedSubResult subResult = new DetachedSubResult();
            beforeCompletion(subResult);
            if (status == Status.STATUS_ACTIVE) {
                endResources(XAResource.TMSUCCESS, subResult);
                if (getXAResourceWrapperSize() == 0) {
                    status = Status.STATUS_COMMITTED;
                } else if (getXAResourceWrapperSize() == 1) {
                    commitOnePhase(subResult);
                } else {
                    final int vote = prepareResources(subResult);
                    switch (vote) {
                    case VOTE_READONLY:
                        status = Status.STATUS_COMMITTED;
                        break;
                    case VOTE_COMMIT:
                        commitTwoPhase(subResult);
                        break;
                    case VOTE_ROLLBACK:
                        rollbackForVoteOK(subResult);
                    }
                }
                if (status == Status.STATUS_COMMITTED) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Commit transaction: {}", this);
                    }
                }
            }
            final boolean rolledBack = status != Status.STATUS_COMMITTED;
            afterCompletion(subResult);
            if (rolledBack) {
                throwCommitRollbackException(subResult);
            }
        } finally {
            destroy();
        }
    }

    protected DetachedSubResult beforeCompletion(DetachedSubResult subResult) {
        final DetachedSubResult result = new DetachedSubResult();
        for (int i = 0; i < getSynchronizationSize() && status == Status.STATUS_ACTIVE; ++i) {
            beforeCompletion(getSynchronization(i), subResult);
        }
        for (int i = 0; i < getInterposedSynchronizationSize() && status == Status.STATUS_ACTIVE; ++i) {
            beforeCompletion(getInterposedSynchronization(i), subResult);
        }
        return result;
    }

    protected void beforeCompletion(Synchronization sync, DetachedSubResult subResult) {
        try {
            sync.beforeCompletion();
        } catch (Throwable cause) {
            logger.warn("Failed to process the before completion: " + toCauseHash(cause) + " " + sync, cause);
            status = Status.STATUS_MARKED_ROLLBACK;
            subResult.addDetachedCause(cause);
            endResources(XAResource.TMFAIL, subResult);
            rollbackResources(subResult);
        }
    }

    protected void endResources(int flag, DetachedSubResult subResult) {
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            final XAResourceWrapper xarw = getXAResourceWrapper(i);
            try {
                xarw.end(flag);
            } catch (Throwable cause) {
                logger.warn("Failed to end the XA resource: " + toCauseHash(cause) + " " + xarw + " " + flag, cause);
                status = Status.STATUS_MARKED_ROLLBACK;
                subResult.addDetachedCause(cause);
            }
        }
    }

    protected void commitOnePhase(DetachedSubResult subResult) {
        status = Status.STATUS_COMMITTING;
        final XAResourceWrapper xari = getXAResourceWrapper(0);
        try {
            xari.commit(true);
            status = Status.STATUS_COMMITTED;
        } catch (Throwable cause) {
            logger.warn("Failed to commit the XA resource: " + toCauseHash(cause) + " " + xari, cause);
            status = Status.STATUS_UNKNOWN;
            subResult.addDetachedCause(cause);
        }
    }

    protected int prepareResources(DetachedSubResult subResult) {
        status = Status.STATUS_PREPARING;
        int vote = VOTE_READONLY;
        final LjtLinkedList xarwList = new LjtLinkedList();
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            final XAResourceWrapper xarw = getXAResourceWrapper(i);
            if (xarw.isCommitTarget()) {
                xarwList.addFirst(xarw);
            }
        }
        for (int i = 0; i < xarwList.size(); ++i) {
            final XAResourceWrapper xarw = (XAResourceWrapper) xarwList.get(i);
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
            } catch (Throwable cause) {
                logger.warn("Failed to vote the XA resource: " + toCauseHash(cause) + " " + xarw, cause);
                xarw.setVoteOk(false);
                status = Status.STATUS_MARKED_ROLLBACK;
                subResult.addDetachedCause(cause);
                return VOTE_ROLLBACK;
            }
        }
        if (status == Status.STATUS_PREPARING) {
            status = Status.STATUS_PREPARED;
        }
        return vote;
    }

    protected void commitTwoPhase(DetachedSubResult subResult) {
        status = Status.STATUS_COMMITTING;
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            final XAResourceWrapper xarw = getXAResourceWrapper(i);
            if (xarw.isCommitTarget() && xarw.isVoteOk()) {
                try {
                    xarw.commit(false);
                } catch (Throwable cause) {
                    logger.warn("Failed to commit the XA resource: " + toCauseHash(cause) + " " + xarw, cause);
                    status = Status.STATUS_UNKNOWN;
                    subResult.addDetachedCause(cause);
                }
            }
        }
        if (status == Status.STATUS_COMMITTING) {
            status = Status.STATUS_COMMITTED;
        }
    }

    protected void rollbackForVoteOK(DetachedSubResult subResult) {
        status = Status.STATUS_ROLLING_BACK;
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            final XAResourceWrapper xarw = getXAResourceWrapper(i);
            if (xarw.isVoteOk()) {
                try {
                    xarw.rollback();
                } catch (Throwable cause) {
                    logger.warn("Failed to rollback the XA resource: " + toCauseHash(cause) + " " + xarw, cause);
                    status = Status.STATUS_UNKNOWN;
                    subResult.addDetachedCause(cause);
                }
            }
        }
        if (status == Status.STATUS_ROLLING_BACK) {
            status = Status.STATUS_ROLLEDBACK;
        }
    }

    protected void afterCompletion(DetachedSubResult subResult) {
        final int status = this.status;
        this.status = Status.STATUS_NO_TRANSACTION;
        for (int i = 0; i < getInterposedSynchronizationSize(); ++i) {
            afterCompletion(status, getInterposedSynchronization(i), subResult);
        }
        for (int i = 0; i < getSynchronizationSize(); ++i) {
            afterCompletion(status, getSynchronization(i), subResult);
        }
    }

    protected void afterCompletion(final int status, final Synchronization sync, DetachedSubResult subResult) {
        try {
            sync.afterCompletion(status);
        } catch (Throwable cause) {
            logger.warn("Failed to process the after completion: " + toCauseHash(cause) + " " + sync + " " + status, cause);
            subResult.addDetachedCause(cause);
        }
    }

    protected int getSynchronizationSize() {
        return synchronizations.size();
    }

    protected Synchronization getSynchronization(int index) {
        return (Synchronization) synchronizations.get(index);
    }

    protected int getInterposedSynchronizationSize() {
        return interposedSynchronizations.size();
    }

    protected Synchronization getInterposedSynchronization(int index) {
        return (Synchronization) interposedSynchronizations.get(index);
    }

    protected void throwCommitRollbackException(DetachedSubResult subResult) throws LjtRollbackException {
        final LjtExceptionMessageBuilder br = new LjtExceptionMessageBuilder();
        br.addNotice("Cannot commit the transaction so roll-back.");
        setupDetachedCausePart(br, subResult.getDetachedCauseList());
        br.addItem("javax.transaction.Status");
        br.addElement(status);
        br.addItem("Transaction XID");
        br.addElement(xid);
        final String msg = br.buildExceptionMessage();
        final LjtRollbackException rollbackException = new LjtRollbackException(msg);
        final Throwable detachedCause = subResult.getFirstDetachedCause();
        if (detachedCause != null) {
            rollbackException.initCause(detachedCause); // because of no-cause constructor of JTA
        }
        throw rollbackException;
    }

    // ===================================================================================
    //                                                                           Roll-back
    //                                                                           =========
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        try {
            assertNotSuspended();
            assertActiveOrMarkedRollback();
            final DetachedSubResult subResult = new DetachedSubResult();
            endResources(XAResource.TMFAIL, subResult);
            rollbackResources(subResult);
            if (logger.isDebugEnabled()) {
                logger.debug("Rollback transaction: {}", this);
            }
            afterCompletion(subResult);
            handleRollbackFailure(subResult);
        } finally {
            destroy();
        }
    }

    protected void assertActiveOrMarkedRollback() throws IllegalStateException {
        switch (status) {
        case Status.STATUS_ACTIVE:
        case Status.STATUS_MARKED_ROLLBACK:
            break;
        default:
            throwIllegalStateException();
        }
    }

    protected void rollbackResources(DetachedSubResult subResult) {
        status = Status.STATUS_ROLLING_BACK;
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            final XAResourceWrapper xarw = getXAResourceWrapper(i);
            try {
                if (xarw.isCommitTarget()) {
                    xarw.rollback();
                }
            } catch (Throwable cause) {
                logger.warn("Failed to rollback the XA resource: " + toCauseHash(cause) + " " + xarw, cause);
                status = Status.STATUS_UNKNOWN;
                subResult.addDetachedCause(cause);
            }
        }
        if (status == Status.STATUS_ROLLING_BACK) {
            status = Status.STATUS_ROLLEDBACK;
        }
    }

    protected void handleRollbackFailure(DetachedSubResult subResult) {
        final Throwable firstDetachedCause = subResult.getFirstDetachedCause();
        if (firstDetachedCause == null) {
            return; // completely success
        }
        final LjtExceptionMessageBuilder br = new LjtExceptionMessageBuilder();
        br.addNotice("Failed to execute several roll-back processes.");
        setupDetachedCausePart(br, subResult.getDetachedCauseList());
        final String msg = br.buildExceptionMessage();
        logger.warn(msg, firstDetachedCause); // logging only because of roll-back (and keep compatible)
    }

    // ===================================================================================
    //                                                                      Roll-back Only
    //                                                                      ==============
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        assertNotSuspended();
        assertActiveOrPreparingOrPrepared();
        status = Status.STATUS_MARKED_ROLLBACK;
    }

    protected void assertActiveOrPreparingOrPrepared() throws IllegalStateException {
        switch (status) {
        case Status.STATUS_ACTIVE:
        case Status.STATUS_PREPARING:
        case Status.STATUS_PREPARED:
            break;
        default:
            throwIllegalStateException();
        }
    }

    // ===================================================================================
    //                                                                     Enlist Resource
    //                                                                     ===============
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

    protected Xid createXidBranch() {
        return new XidImpl(xid, ++branchId);
    }

    // ===================================================================================
    //                                                                     Delist Resource
    //                                                                     ===============
    public boolean delistResource(XAResource xaResource, int flag) throws IllegalStateException, SystemException {
        assertNotSuspended();
        assertActiveOrMarkedRollback();
        for (int i = 0; i < getXAResourceWrapperSize(); ++i) {
            XAResourceWrapper xarw = getXAResourceWrapper(i);
            if (xaResource.equals(xarw.getXAResource())) {
                try {
                    xarw.end(flag);
                    return true;
                } catch (XAException cause) {
                    logger.warn("Failed to end the XA resource: " + xarw + " " + flag, cause);
                    status = Status.STATUS_MARKED_ROLLBACK;
                    return false;
                }
            }
        }
        throw new LjtIllegalStateException("Unregistered XA resource: " + xaResource);
    }

    // ===================================================================================
    //                                                                  Transaction Status
    //                                                                  ==================
    public int getStatus() {
        return status;
    }

    // ===================================================================================
    //                                                                     Synchronization
    //                                                                     ===============
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

    // ===================================================================================
    //                                                                   Resource Handling
    //                                                                   =================
    public void putResource(Object key, Object value) throws IllegalStateException {
        assertNotSuspended();
        resourceMap.put(key, value);
    }

    public Object getResource(Object key) throws IllegalStateException {
        assertNotSuspended();
        return resourceMap.get(key);
    }

    // ===================================================================================
    //                                                                             Destroy
    //                                                                             =======
    protected void destroy() {
        status = Status.STATUS_NO_TRANSACTION;
        xaResourceWrappers.clear();
        synchronizations.clear();
        interposedSynchronizations.clear();
        resourceMap.clear();
        suspended = false;
    }

    // ===================================================================================
    //                                                                            Detached
    //                                                                            ========
    protected static class DetachedSubResult {

        private List<Throwable> detachedCauseList; // lazy-loaded

        public List<Throwable> getDetachedCauseList() { // treated as read-only
            return detachedCauseList != null ? detachedCauseList : Collections.emptyList();
        }

        public Throwable getFirstDetachedCause() { // null alllowed
            return detachedCauseList != null && !detachedCauseList.isEmpty() ? detachedCauseList.get(0) : null;
        }

        public void addDetachedCause(Throwable cause) {
            if (detachedCauseList == null) {
                detachedCauseList = new ArrayList<Throwable>();
            }
            detachedCauseList.add(cause);
        }
    }

    protected void setupDetachedCausePart(LjtExceptionMessageBuilder br, List<Throwable> detachedCauseList) {
        if (detachedCauseList.isEmpty()) {
            return;
        }
        br.addItem("Detached Cause");
        for (Throwable detachedCause : detachedCauseList) {
            final String className = detachedCause.getClass().getSimpleName();
            final String causeHash = toCauseHash(detachedCause);
            final String causeMessage = detachedCause.getMessage();
            br.addElement(className + ": " + causeHash + " " + causeMessage);
        }
        br.addElement("(You can search the details in WARN log by the hash codes)");
    }

    protected String toCauseHash(Throwable cause) {
        return "#" + Integer.toHexString(cause.hashCode());
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return xid.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Xid getXid() {
        return xid;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public List<Synchronization> getSynchronizations() {
        return synchronizations;
    }

    public List<Synchronization> getInterposedSynchronizations() {
        return interposedSynchronizations;
    }
}
