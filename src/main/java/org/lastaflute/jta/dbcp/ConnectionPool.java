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
package org.lastaflute.jta.dbcp;

import java.sql.SQLException;

import javax.transaction.Transaction;

/**
 * @author modified by jflute (originated in Seasar)
 */
public interface ConnectionPool {

    // ===================================================================================
    //                                                                  Connection Process
    //                                                                  ==================
    /**
     * Check out connection, may be on transaction.
     * @return The wrapped connection to database, may be new-created or recycled. (NotNull)
     * @throws SQLException
     */
    ConnectionWrapper checkOut() throws SQLException;

    /**
     * Check in checked-out connection without transaction.
     * @param connectionWrapper The wrapped connection to database, should be checked-out before. (NotNull)
     */
    void checkIn(ConnectionWrapper connectionWrapper);

    /**
     * Check in checked-out connection via specified transaction.
     * @param tx The transaction related to this connection pool. (NullAllowed: then do nothing just in case)
     */
    void checkInTx(Transaction tx);

    /**
     * Release connection from this connection pool.
     * @param connectionWrapper The released connection. (NotNull)
     */
    void release(ConnectionWrapper connectionWrapper);

    // ===================================================================================
    //                                                                          Management
    //                                                                          ==========
    void close(); // destroy connection pool

    // ===================================================================================
    //                                                                          Pool State
    //                                                                          ==========
    int getActivePoolSize(); // active (checked-out) connection without transaction

    int getTxActivePoolSize(); // active (checked-out) connection with transaction

    int getFreePoolSize(); // non-active (non-checked-out) connection

    int getMaxPoolSize(); // limit size of pooled connections as maximum

    int getMinPoolSize(); // limit size of pooled connections as minimum
}