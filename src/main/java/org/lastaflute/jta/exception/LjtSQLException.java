/*
 * Copyright 2015-2022 the original author or authors.
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
package org.lastaflute.jta.exception;

import java.sql.SQLException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LjtSQLException extends SQLException {

    private static final long serialVersionUID = 1L;

    public LjtSQLException(String msg) {
        super(msg);
    }

    public LjtSQLException(String msg, Throwable cause) { // for normal exception (except SQLException)
        super(msg, cause);
        setupNextCause(cause);
    }

    public LjtSQLException(String msg, String sqlState, int vendorCode, SQLException cause) {
        super(msg, sqlState, vendorCode, cause);
        setupNextCause(cause);
    }

    protected void setupNextCause(Throwable cause) {
        if (cause instanceof SQLException) {
            setNextException((SQLException) cause);
        }
    }
}