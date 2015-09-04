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
package org.lastaflute.di.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.lastaflute.di.exception.SQLRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiResultSetUtil {

    protected LdiResultSetUtil() {
    }

    public static void close(ResultSet resultSet) throws SQLRuntimeException {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * @param resultSet
     * @return 
     * @throws SQLRuntimeException
     */
    public static boolean next(ResultSet resultSet) {
        try {
            return resultSet.next();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    public static boolean absolute(ResultSet resultSet, int index) throws SQLRuntimeException {
        try {
            return resultSet.absolute(index);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }
}
