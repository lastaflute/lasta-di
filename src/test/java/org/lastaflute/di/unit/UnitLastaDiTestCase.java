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
package org.lastaflute.di.unit;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.di.unit.flute.exception.ExceptionExaminer;
import org.lastaflute.di.unit.flute.exception.ExceptionExpectationAfter;
import org.lastaflute.di.util.LdiSrl;
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
    protected void runTest() throws Throwable {
        try {
            super.runTest();
        } catch (Throwable e) { // to record in application log
            log("Failed to finish the test: " + xgetCaseDisp(), e);
            throw e;
        }
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
                    final String replacement;
                    if (nextObj != null) {
                        // escape two special characters of replaceFirst() to avoid illegal group reference
                        replacement = LdiSrl.replace(LdiSrl.replace(nextObj.toString(), "\\", "\\\\"), "$", "\\$");
                    } else {
                        replacement = "null";
                    }
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
     * String <span style="color: #553000">str</span> = <span style="color: #70226C">null</span>;
     * <span style="color: #CC4747">assertException</span>(NullPointerException.<span style="color: #70226C">class</span>, () <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> <span style="color: #553000">str</span>.toString());
     * 
     * <span style="color: #CC4747">assertException</span>(NullPointerException.<span style="color: #70226C">class</span>, () <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> <span style="color: #553000">str</span>.toString()).<span style="color: #994747">handle</span>(<span style="color: #553000">cause</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     assertContains(<span style="color: #553000">cause</span>.getMessage(), ...);
     * });
     * </pre>
     * @param <CAUSE> The type of expected cause exception. 
     * @param exceptionType The expected exception type. (NotNull)
     * @param noArgInLambda The callback for calling methods that should throw the exception. (NotNull)
     * @return The after object that has handler of expected cause for chain call. (NotNull) 
     */
    protected <CAUSE extends Throwable> ExceptionExpectationAfter<CAUSE> assertException(Class<CAUSE> exceptionType,
            ExceptionExaminer noArgInLambda) {
        assertNotNull(exceptionType);
        final String expected = exceptionType.getSimpleName();
        Throwable cause = null;
        try {
            noArgInLambda.examine();
        } catch (Throwable e) {
            cause = e;
            final Class<? extends Throwable> causeClass = cause.getClass();
            final String exp = buildExceptionSimpleExp(cause);
            if (!exceptionType.isAssignableFrom(causeClass)) {
                final String actual = causeClass.getSimpleName();
                log("*Different exception, expected: {} but...", exceptionType.getName(), cause);
                fail("*Different exception, expected: " + expected + " but: " + actual + " => " + exp);
            } else {
                log("expected: " + exp);
            }
        }
        if (cause == null) {
            fail("*No exception, expected: " + expected);
        }
        @SuppressWarnings("unchecked")
        final CAUSE castCause = (CAUSE) cause;
        return new ExceptionExpectationAfter<CAUSE>(castCause);
    }

    private String buildExceptionSimpleExp(Throwable cause) {
        final StringBuilder sb = new StringBuilder();
        final String firstMsg = cause.getMessage();
        boolean line = firstMsg != null && firstMsg.contains(ln());
        sb.append("(").append(cause.getClass().getSimpleName()).append(")").append(firstMsg);
        final Throwable secondCause = cause.getCause();
        if (secondCause != null) {
            final String secondMsg = secondCause.getMessage();
            line = line || secondMsg != null && secondMsg.contains(ln());
            sb.append(line ? ln() : " / ");
            sb.append("(").append(secondCause.getClass().getSimpleName()).append(")").append(secondMsg);
            final Throwable thirdCause = secondCause.getCause();
            if (thirdCause != null) {
                final String thirdMsg = thirdCause.getMessage();
                line = line || thirdMsg != null && thirdMsg.contains(ln());
                sb.append(line ? ln() : " / ");
                sb.append("(").append(thirdCause.getClass().getSimpleName()).append(")").append(thirdMsg);
            }
        }
        final String whole = sb.toString();
        return (whole.contains(ln()) ? ln() : "") + whole;
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
