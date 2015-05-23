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
package org.lastaflute.jta.helper.timer;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LjtTimeoutTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private final static int ACTIVE = 0;
    private final static int STOPPED = 1;
    private final static int CANCELED = 2;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final LjtTimeoutTarget timeoutTarget_;
    private final long timeoutMillis_;
    private final boolean permanent_;
    private long startTime_;
    private int status_ = ACTIVE;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    LjtTimeoutTask(LjtTimeoutTarget timeoutTarget, int timeout, boolean permanent) {
        timeoutTarget_ = timeoutTarget;
        timeoutMillis_ = timeout * 1000L;
        permanent_ = permanent;
        startTime_ = System.currentTimeMillis();
    }

    // ===================================================================================
    //                                                                           Operation
    //                                                                           =========
    // -----------------------------------------------------
    //                                               Expired
    //                                               -------
    public boolean isExpired() {
        return System.currentTimeMillis() >= startTime_ + timeoutMillis_;
    }

    void expired() {
        timeoutTarget_.expired();
    }

    // -----------------------------------------------------
    //                                                Cancel
    //                                                ------
    public boolean isCanceled() {
        return status_ == CANCELED;
    }

    public void cancel() {
        status_ = CANCELED;
    }

    // -----------------------------------------------------
    //                                                  Stop
    //                                                  ----
    public boolean isStopped() {
        return status_ == STOPPED;
    }

    public void stop() {
        if (status_ != ACTIVE) {
            String msg = "Failed to stop the timer because of illegal status: " + startTime_;
            throw new IllegalStateException(msg);
        }
        status_ = STOPPED;
    }

    // -----------------------------------------------------
    //                                               Restart
    //                                               -------
    public void restart() {
        status_ = ACTIVE;
        startTime_ = System.currentTimeMillis();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isPermanent() {
        return permanent_;
    }
}
