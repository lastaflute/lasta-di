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
package org.lastaflute.di.core.aop.frame;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author modified by jflute (originated in AOP Alliance)
 */
public class AspectException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String message;
    private String stackTrace;
    private Throwable t;

    public AspectException(String s) {
        super(s);
        this.message = s;
        this.stackTrace = s;
    }

    public AspectException(String s, Throwable t) {
        super(s + "; nested exception is " + t.getMessage());
        this.t = t;
        StringWriter out = new StringWriter();
        t.printStackTrace(new PrintWriter(out));
        this.stackTrace = out.toString();
    }

    public Throwable getCause() {
        return t;
    }

    public String toString() {
        return this.getMessage();
    }

    public String getMessage() {
        return this.message;
    }

    public void printStackTrace() {
        System.err.print(this.stackTrace);
    }

    public void printStackTrace(PrintStream out) {
        printStackTrace(new PrintWriter(out));
    }

    public void printStackTrace(PrintWriter out) {
        out.print(this.stackTrace);
    }
}
