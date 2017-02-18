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
package org.lastaflute.jta.dbcp;

import java.sql.SQLException;

import javax.transaction.Transaction;

/**
 * @author modified by jflute (originated in Seasar)
 */
public interface ConnectionPool {

    ConnectionWrapper checkOut() throws SQLException;

    void checkIn(ConnectionWrapper connectionWrapper);

    void checkInTx(Transaction tx);

    void release(ConnectionWrapper connectionWrapper);

    void close();

    int getActivePoolSize();

    int getTxActivePoolSize();

    int getFreePoolSize();

    int getMaxPoolSize();

    int getMinPoolSize();
}