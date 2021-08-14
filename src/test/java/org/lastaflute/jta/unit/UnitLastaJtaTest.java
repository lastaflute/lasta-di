/*
 * Copyright 2015-2021 the original author or authors.
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
package org.lastaflute.jta.unit;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lastaflute.di.util.LdiSrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * @author jflute
 */
public abstract class UnitLastaJtaTest extends TestCase {

    /** The logger instance for sub class. (NotNull) */
    protected final Logger _xlogger = LoggerFactory.getLogger(getClass());

    /** The reserved title for logging test case beginning. (NullAllowed: before preparation or already showed) */
    protected String _xreservedTitle;

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
    //                                                                       System Helper
    //                                                                       =============
    /**
     * Get the line separator. (LF fixedly)
     * @return The string of the line separator. (NotNull)
     */
    protected String ln() {
        return "\n";
    }
}
