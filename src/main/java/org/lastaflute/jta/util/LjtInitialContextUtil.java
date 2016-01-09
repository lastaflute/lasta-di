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
package org.lastaflute.jta.util;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.lastaflute.jta.exception.LjtNamingRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LjtInitialContextUtil {

    protected LjtInitialContextUtil() {
    }

    public static InitialContext create() {
        try {
            return new InitialContext();
        } catch (final NamingException ex) {
            throw new LjtNamingRuntimeException(ex);
        }
    }

    public static InitialContext create(final Hashtable<?, ?> env) {
        try {
            return new InitialContext(env);
        } catch (final NamingException ex) {
            throw new LjtNamingRuntimeException(ex);
        }
    }

    public static Object lookup(final InitialContext ctx, final String jndiName) throws LjtNamingRuntimeException {
        try {
            return ctx.lookup(jndiName);
        } catch (final NamingException ex) {
            throw new LjtNamingRuntimeException(ex);
        }
    }
}
