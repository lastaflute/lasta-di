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
package org.lastaflute.di.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.lastaflute.di.exception.SQLRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiPreparedStatementUtil {

    protected LdiPreparedStatementUtil() {
    }

    /**
     * @param ps
     * @return {@link ResultSet}
     * @throws SQLRuntimeException
     */
    public static ResultSet executeQuery(PreparedStatement ps) throws SQLRuntimeException {
        try {
            return ps.executeQuery();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    public static int executeUpdate(PreparedStatement ps) throws SQLRuntimeException {
        try {
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    public static boolean execute(PreparedStatement ps) throws SQLRuntimeException {
        try {
            return ps.execute();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    public static int[] executeBatch(PreparedStatement ps) throws SQLRuntimeException {
        try {
            return ps.executeBatch();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    public static void addBatch(PreparedStatement ps) throws SQLRuntimeException {
        try {
            ps.addBatch();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }
}
