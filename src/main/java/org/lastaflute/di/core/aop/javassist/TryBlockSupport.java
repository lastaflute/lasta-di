/*
 * Copyright 2015-2022 the original author or authors.
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
package org.lastaflute.di.core.aop.javassist;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TryBlockSupport {

    protected static final int STATUS_TRY = 0;
    protected static final int STATUS_CATCH = 1;
    protected static final int STATUS_FINALLY = 2;

    protected int status;
    protected StringBuffer codeBuf = new StringBuffer(500);

    public TryBlockSupport(final String src) {
        codeBuf.append("try {").append(src).append("}");
        status = STATUS_TRY;
    }

    public void addCatchBlock(final Class<?> exceptionType, final String src) {
        if (!Throwable.class.isAssignableFrom(exceptionType)) {
            throw new IllegalArgumentException("exceptionType must be Throwable.");
        }
        if (status != STATUS_TRY && status != STATUS_CATCH) {
            throw new IllegalStateException("could't append catch block after finally block.");
        }
        codeBuf.append("catch (").append(exceptionType.getName()).append(" e) {").append(src).append("}");
        status = STATUS_CATCH;
    }

    public void setFinallyBlock(final String src) {
        if (status != STATUS_TRY && status != STATUS_CATCH) {
            throw new IllegalStateException("finally block is already appended.");
        }
        codeBuf.append("finally {").append(src).append("}");
        status = STATUS_FINALLY;
    }

    public String getSourceCode() {
        if (status != STATUS_CATCH && status != STATUS_FINALLY) {
            throw new IllegalStateException("must set catch block or finally block.");
        }
        return new String(codeBuf);
    }
}
