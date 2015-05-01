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
package org.dbflute.lasta.jta.util;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.dbflute.lasta.jta.exception.LjtNamingRuntimeException;

/**
 * {@link javax.naming.InitialContext 初期コンテキスト}を扱うためのユーティリティ・クラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class LjtInitialContextUtil {

    /**
     * インスタンスを構築します。
     */
    protected LjtInitialContextUtil() {
    }

    /**
     * 初期コンテキストを作成して返します。
     * 
     * @return 初期コンテキスト
     * @throws org.dbflute.lasta.jta.exception.LjtNamingRuntimeException
     *             初期コンテキストを作成できなかった場合にスローされます
     */
    public static InitialContext create() {
        try {
            return new InitialContext();
        } catch (final NamingException ex) {
            throw new LjtNamingRuntimeException(ex);
        }
    }

    /**
     * 指定した環境を使用して初期コンテキストを作成して返します。
     * 
     * @param env
     *            初期コンテキストの作成に使用される環境。<code>mull</code>は空の環境を示す
     * @return 初期コンテキスト
     * @throws LjtNamingRuntimeException
     *             初期コンテキストを作成できなかった場合にスローされます
     */
    public static InitialContext create(final Hashtable env) {
        try {
            return new InitialContext(env);
        } catch (final NamingException ex) {
            throw new LjtNamingRuntimeException(ex);
        }
    }

    /**
     * 指定した初期コンテキストから指定されたオブジェクトを取得して返します。
     * 
     * @param ctx
     *            初期コンテキスト
     * @param jndiName
     *            検索するオブジェクトの名前
     * @return <code>jndiName</code>にバインドされているオブジェクト
     * @throws LjtNamingRuntimeException
     *             初期コンテキストを作成できなかった場合にスローされます
     */
    public static Object lookup(final InitialContext ctx, final String jndiName) throws LjtNamingRuntimeException {
        try {
            return ctx.lookup(jndiName);
        } catch (final NamingException ex) {
            throw new LjtNamingRuntimeException(ex);
        }
    }

}
