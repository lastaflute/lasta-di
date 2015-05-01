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
package org.dbflute.lasta.di.helper.log;

import java.util.HashMap;
import java.util.Map;

import org.dbflute.lasta.di.helper.message.MessageFormatter;
import org.slf4j.LoggerFactory;

/**
 * #delete
 */
public class SLogger {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SLogger.class);

    private static final Map loggers = new HashMap();

    private static boolean initialized;

    public static synchronized SLogger getLogger(final Class clazz) {
        if (!initialized) {
            initialize();
        }
        SLogger logger = (SLogger) loggers.get(clazz);
        if (logger == null) {
            logger = new SLogger();
            loggers.put(clazz, logger);
        }
        return logger;
    }

    /**
     * {@link SLogger}を初期化します。
     */
    public static synchronized void initialize() {
        initialized = true;
    }

    /**
     * リソースを開放します。
     */
    public synchronized static void dispose() {
        loggers.clear();
        initialized = false;
    }

    /**
     * DEBUG情報が出力されるかどうかを返します。
     * 
     * @return DEBUG情報が出力されるかどうか
     */
    public final boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /**
     * DEBUG情報を出力します。
     * 
     * @param message
     * @param throwable
     */
    public final void debug(Object message, Throwable throwable) {
        if (isDebugEnabled()) {
            log.debug(message != null ? message.toString() : null, throwable);
        }
    }

    /**
     * DEBUG情報を出力します。
     * 
     * @param message
     */
    public final void debug(Object message) {
        if (isDebugEnabled()) {
            log.debug(message != null ? message.toString() : null);
        }
    }

    /**
     * INFO情報が出力されるかどうかを返します。
     * 
     * @return INFO情報が出力されるかどうか
     */
    public final boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    /**
     * INFO情報を出力します。
     * 
     * @param message
     * @param throwable
     */
    public final void info(Object message, Throwable throwable) {
        if (isInfoEnabled()) {
            log.info(message != null ? message.toString() : null, throwable);
        }
    }

    /**
     * INFO情報を出力します。
     * 
     * @param message
     */
    public final void info(Object message) {
        if (isInfoEnabled()) {
            log.info(message != null ? message.toString() : null);
        }
    }

    /**
     * WARN情報を出力します。
     * 
     * @param message
     * @param throwable
     */
    public final void warn(Object message, Throwable throwable) {
        log.warn(message != null ? message.toString() : null, throwable);
    }

    /**
     * WARN情報を出力します。
     * 
     * @param message
     */
    public final void warn(Object message) {
        log.warn(message != null ? message.toString() : null);
    }

    /**
     * ERROR情報を出力します。
     * 
     * @param message
     * @param throwable
     */
    public final void error(Object message, Throwable throwable) {
        log.error(message != null ? message.toString() : null, throwable);
    }

    /**
     * ERROR情報を出力します。
     * 
     * @param message
     */
    public final void error(Object message) {
        log.error(message != null ? message.toString() : null);
    }

    /**
     * ログを出力します。
     * 
     * @param throwable
     */
    public final void log(Throwable throwable) {
        error(throwable.getMessage(), throwable);
    }

    /**
     * ログを出力します。
     * 
     * @param messageCode
     * @param args
     */
    public final void log(String messageCode, Object[] args) {
        log(messageCode, args, null);
    }

    /**
     * ログを出力します。
     * 
     * @param messageCode
     * @param args
     * @param throwable
     */
    public final void log(String messageCode, Object[] args, Throwable throwable) {
        char messageType = messageCode.charAt(0);
        if (isEnabledFor(messageType)) {
            String message = MessageFormatter.getSimpleMessage(messageCode, args);
            switch (messageType) {
            case 'D':
                log.debug(message, throwable);
                break;
            case 'I':
                log.info(message, throwable);
                break;
            case 'W':
                log.warn(message, throwable);
                break;
            case 'E':
                log.error(message, throwable);
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(messageType));
            }
        }
    }

    private boolean isEnabledFor(final char messageType) {
        switch (messageType) {
        case 'D':
            return log.isDebugEnabled();
        case 'I':
            return log.isInfoEnabled();
        case 'W':
            return log.isWarnEnabled();
        case 'E':
            return log.isErrorEnabled();
        default:
            throw new IllegalArgumentException(String.valueOf(messageType));
        }
    }
}