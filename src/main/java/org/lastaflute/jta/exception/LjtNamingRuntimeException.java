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
package org.lastaflute.jta.exception;

import javax.naming.NamingException;

/**
 * {@link NamingException}をラップする例外です。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class LjtNamingRuntimeException extends LjtRuntimeException {

    private static final long serialVersionUID = -3176447530746274091L;

    public LjtNamingRuntimeException(NamingException cause) {
        super("Failed to handle naming", cause);
    }
}
