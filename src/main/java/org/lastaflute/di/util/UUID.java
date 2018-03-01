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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class UUID {

    private static final byte[] DEFAULT_ADDRESS = new byte[] { (byte) 127, (byte) 0, (byte) 0, (byte) 1 };

    private static SecureRandom _random = new SecureRandom();

    private static String _base = LdiStringUtil.toHex(getAddress()) + LdiStringUtil.toHex(System.identityHashCode(_random));

    protected UUID() {
    }

    public static String create() {
        StringBuffer buf = new StringBuffer(_base.length() * 2);
        buf.append(_base);
        int lowTime = (int) System.currentTimeMillis() >> 32;
        LdiStringUtil.appendHex(buf, lowTime);
        LdiStringUtil.appendHex(buf, _random.nextInt());
        return buf.toString();
    }

    private static byte[] getAddress() {
        try {
            return InetAddress.getLocalHost().getAddress();
        } catch (UnknownHostException ignore) {
            return DEFAULT_ADDRESS;
        }
    }
}
