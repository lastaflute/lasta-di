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
package org.lastaflute.di.unit;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * @author jflute
 */
public abstract class UnitLastaDiTestCase extends TestCase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The logger instance for sub class. (NotNull) */
    protected final Logger _xlogger = LoggerFactory.getLogger(getClass());

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The reserved title for logging test case beginning. (NullAllowed: before preparation or already showed) */
    protected String _xreservedTitle;

    // ===================================================================================
    //                                                                            Settings
    //                                                                            ========
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xreserveShowTitle();
        SingletonLaContainerFactory.setConfigPath(getConfigPath());
        SingletonLaContainerFactory.init();
    }

    protected void xreserveShowTitle() {
        // lazy-logging (no logging test case, no title)
        _xreservedTitle = "<<< " + xgetCaseDisp() + " >>>";
    }

    protected String getConfigPath() {
        return "test_app.xml";
    }

    @Override
    protected void tearDown() throws Exception {
        SingletonLaContainerFactory.destroy();
        super.tearDown();
    }

    // ===================================================================================
    //                                                                    Injection Helper
    //                                                                    ================
    protected <COMPONENT> COMPONENT getComponent(Class<COMPONENT> type) {
        return SingletonLaContainer.getComponent(type);
    }

    // ===================================================================================
    //                                                                      Logging Helper
    //                                                                      ==============
    /**
     * Log the messages. <br>
     * If you set an exception object to the last element, it shows stack traces.
     * <pre>
     * Member member = ...;
     * <span style="color: #FD4747">log</span>(member.getMemberName(), member.getBirthdate());
     * <span style="color: #3F7E5E">// -&gt; Stojkovic, 1965/03/03</span>
     * 
     * Exception e = ...;
     * <span style="color: #FD4747">log</span>(member.getMemberName(), member.getBirthdate(), e);
     * <span style="color: #3F7E5E">// -&gt; Stojkovic, 1965/03/03</span>
     * <span style="color: #3F7E5E">//  (and stack traces)</span>
     * </pre>
     * @param msgs The array of messages. (NotNull)
     */
    protected void log(Object... msgs) {
        if (msgs == null) {
            throw new IllegalArgumentException("The argument 'msgs' should not be null.");
        }
        Throwable cause = null;
        final int arrayLength = msgs.length;
        if (arrayLength > 0) {
            final Object lastElement = msgs[arrayLength - 1];
            if (lastElement instanceof Throwable) {
                cause = (Throwable) lastElement;
            }
        }
        final StringBuilder sb = new StringBuilder();
        int index = 0;
        int skipCount = 0;
        for (Object msg : msgs) {
            if (index == arrayLength - 1 && cause != null) { // last loop and it is cause
                break;
            }
            if (skipCount > 0) { // already resolved as variable
                --skipCount; // until count zero
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            final String appended;
            if (msg instanceof Timestamp) {
                appended = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(msg);
            } else if (msg instanceof Date) {
                appended = new SimpleDateFormat("yyyy/MM/dd").format(msg);
            } else {
                String strMsg = msg != null ? msg.toString() : null;
                int nextIndex = index + 1;
                skipCount = 0; // just in case
                while (strMsg != null && strMsg.contains("{}")) {
                    if (arrayLength <= nextIndex) {
                        break;
                    }
                    final Object nextObj = msgs[nextIndex];
                    final String replacement = nextObj != null ? nextObj.toString() : "null";
                    strMsg = strMsg.replaceFirst("\\{\\}", replacement);
                    ++skipCount;
                    ++nextIndex;
                }
                appended = strMsg;
            }
            sb.append(appended);
            ++index;
        }
        final String msg = sb.toString();
        if (_xreservedTitle != null) {
            _xlogger.debug("");
            _xlogger.debug(_xreservedTitle);
            _xreservedTitle = null;
        }
        if (cause != null) {
            _xlogger.debug(msg, cause);
        } else {
            _xlogger.debug(msg);
        }
        // see comment for logger definition for the detail
        //_xlogger.log(PlainTestCase.class.getName(), Level.DEBUG, msg, cause);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============

    // -----------------------------------------------------
    //                                             Exception
    //                                             ---------
    /**
     * Assert that the callback throws the exception.
     * <pre>
     * String str = null;
     * assertException(NullPointerException.class, () <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> str.toString());
     * </pre>
     * @param exceptionType The expected exception type. (NotNull) 
     * @param noArgInLambda The callback for calling methods that should throw the exception. (NotNull)
     */
    protected void assertException(Class<?> exceptionType, ExceptionExaminer noArgInLambda) {
        assertNotNull(exceptionType);
        boolean noThrow = false;
        try {
            noArgInLambda.examine();
            noThrow = true;
        } catch (Throwable cause) {
            final Class<? extends Throwable> causeClass = cause.getClass();
            final String msg = cause.getMessage();
            final String exp = (msg != null && msg.contains(ln()) ? ln() : "") + msg;
            if (!exceptionType.isAssignableFrom(causeClass)) {
                fail("expected: " + exceptionType.getSimpleName() + " but: " + causeClass.getSimpleName() + " => " + exp);
            }
            log("expected: " + exp);
        }
        if (noThrow) {
            fail("expected: " + exceptionType.getSimpleName() + " but: no exception");
        }
    }

    @FunctionalInterface
    public interface ExceptionExaminer {

        /**
         * Examine the process, should throw the specified exception.
         */
        void examine();
    }

    // ===================================================================================
    //                                                                       System Helper
    //                                                                       =============
    /**
     * Get the line separator. (LF fixedly)
     * @return The string of the line separator. (NotNull)
     */
    protected String ln() {
        return "\n";
    }

    protected String xgetCaseDisp() {
        return getClass().getSimpleName() + "." + getName() + "()";
    }
}
