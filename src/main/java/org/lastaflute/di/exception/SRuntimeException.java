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
package org.lastaflute.di.exception;

import org.lastaflute.di.helper.message.MessageFormatter;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -4452607868694297329L;

    private String messageCode;
    private Object[] args;
    private String message;
    private String simpleMessage;

    public SRuntimeException(String messageCode) {
        this(messageCode, null, null);
    }

    public SRuntimeException(String messageCode, Object[] args) {
        this(messageCode, args, null);
    }

    public SRuntimeException(String messageCode, Object[] args, Throwable cause) {
        super(cause);
        this.messageCode = messageCode;
        this.args = args;
        this.simpleMessage = MessageFormatter.getSimpleMessage(messageCode, args);
        this.message = "[" + messageCode + "]" + simpleMessage;
    }

    public final String getMessageCode() {
        return messageCode;
    }

    public final Object[] getArgs() {
        return args;
    }

    @Override
    public final String getMessage() {
        return message;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    public final String getSimpleMessage() {
        return simpleMessage;
    }
}
