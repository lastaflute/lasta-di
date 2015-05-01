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
package org.dbflute.lasta.di.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbflute.lasta.di.exception.SQLRuntimeException;

/**
 * {@link ResultSet}のためのユーティリティクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class LdiResultSetUtil {

    /**
     * インスタンスを構築します。
     */
    protected LdiResultSetUtil() {
    }

    /**
     * 結果セットを閉じます。
     * 
     * @param resultSet
     *            結果セット
     * @throws SQLRuntimeException
     *             SQL例外が起こった場合。
     */
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
     * 結果セットを次に進めます。
     * 
     * @param resultSet
     *            結果セット
     * @return 次に進めたかどうか
     * @throws SQLRuntimeException
     *             SQL例外が起こった場合。
     */
    public static boolean next(ResultSet resultSet) {
        try {
            return resultSet.next();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * カーソルを指定した位置まで進めます。
     * 
     * @param resultSet
     *            結果セット
     * @param index
     *            位置
     * @return 指定した位置まで進めたかどうか
     * @throws SQLRuntimeException
     *             SQL例外が起こった場合。
     */
    public static boolean absolute(ResultSet resultSet, int index) throws SQLRuntimeException {
        try {
            return resultSet.absolute(index);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }
}