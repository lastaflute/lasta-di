/*
 * Copyright 2015-2018 the original author or authors.
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
package org.lastaflute.di.tx;

import java.util.ArrayList;
import java.util.List;

import org.lastaflute.di.core.aop.frame.MethodInterceptor;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractTxInterceptor implements MethodInterceptor {

    protected TransactionManagerAdapter transactionManagerAdapter;
    protected final List<TxRule> txRules = new ArrayList<TxRule>();

    public AbstractTxInterceptor() {
    }

    public void addCommitRule(Class<?> exceptionClass) {
        txRules.add(new TxRule(exceptionClass, true));
    }

    public void addRollbackRule(Class<?> exceptionClass) {
        txRules.add(new TxRule(exceptionClass, false));
    }

    public void setTransactionControl(TransactionManagerAdapter transactionManagerAdapter) {
        this.transactionManagerAdapter = transactionManagerAdapter;
    }
}
