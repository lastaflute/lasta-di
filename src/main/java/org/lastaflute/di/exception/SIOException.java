/*
 * Copyright 2015-2024 the original author or authors.
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
package org.lastaflute.di.exception;

import java.io.IOException;

import org.lastaflute.di.helper.message.MessageFormatter;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SIOException extends IOException {

    private static final long serialVersionUID = -1337051265518112537L;

    private String messageCode;

    private Object[] args;

    public SIOException(String messageCode, Object[] args) {
        super(MessageFormatter.getMessage(messageCode, args));

        this.messageCode = messageCode;
        this.args = args;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public Object[] getArgs() {
        return args;
    }
}
