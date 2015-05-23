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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class LdiMessageDigestUtil {

    /**
     * インスタンスを構築します。
     */
    protected LdiMessageDigestUtil() {
    }

    /**
     * {@link MessageDigest#getInstance(String)}の例外処理をラップします。
     * 
     * @param algorithm
     * @return {@link MessageDigest}
     * @throws RuntimeException
     *             {@link NoSuchAlgorithmException}が発生した場合
     */
    public static MessageDigest getInstance(String algorithm) throws RuntimeException {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
