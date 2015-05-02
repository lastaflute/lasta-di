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
import javax.transaction.xa.Xid;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class DefaultXAResource extends AbstractXAResource {

    public DefaultXAResource() {
    }

    protected void doSuccess(Xid xid) throws XAException {
    }

    protected void doFail(Xid xid) throws XAException {
    }

    protected void doResume(Xid xid) throws XAException {
    }

    protected void doBegin(Xid xid) throws XAException {
    }

    protected int doPrepare(Xid xid) throws XAException {
        return XA_OK;
    }

    protected void doRollback(Xid xid) throws XAException {
    }

    protected void doSuspend(Xid xid) throws XAException {
    }

    protected void doForget(Xid xid) throws XAException {
    }

    protected void doCommit(Xid xid, boolean onePhase) throws XAException {
    }
}