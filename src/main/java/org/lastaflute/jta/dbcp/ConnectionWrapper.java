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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

/**
 * @author modified by jflute (originated in Seasar)
 */
public interface ConnectionWrapper extends Connection {

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    void init(Transaction tx);

    void cleanup();

    void closeReally();

    Connection getPhysicalConnection();

    XAResource getXAResource();

    XAConnection getXAConnection();

    void release() throws SQLException;

    // ===================================================================================
    //                                                                           Traceable
    //                                                                           =========
    void saveCheckOutHistory();

    void saveCheckInHistory();

    String toTraceableView();

    void inheritHistory(ConnectionWrapper wrapper);
}