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
package org.lastaflute.di.helper.timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TimeoutManager implements Runnable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final TimeoutManager instance = new TimeoutManager();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Thread thread;
    protected final TimeoutLinkedList timeoutTaskList = new TimeoutLinkedList();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    private TimeoutManager() {
    }

    public static TimeoutManager getInstance() {
        return instance;
    }

    // ===================================================================================
    //                                                                          Management
    //                                                                          ==========
    public synchronized void start() {
        if (thread == null) {
            thread = new Thread(this, "Lasta_Di-TimeoutManager");
            thread.setDaemon(true);
            thread.start();
        }
    }

    public synchronized void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public boolean stop(long timeoutMillis) throws InterruptedException {
        Thread thread = this.thread;
        synchronized (this) {
            if (thread == null) {
                return true;
            }
            this.thread = null;
        }
        thread.interrupt();
        thread.join(timeoutMillis);
        return !thread.isAlive();
    }

    public synchronized void clear() {
        timeoutTaskList.clear();
    }

    public synchronized TimeoutTask addTimeoutTarget(final TimeoutTarget timeoutTarget, final int timeout, final boolean permanent) {
        final TimeoutTask task = new TimeoutTask(timeoutTarget, timeout, permanent);
        timeoutTaskList.addLast(task);
        if (timeoutTaskList.size() == 1) {
            start();
        }
        return task;
    }

    public synchronized int getTimeoutTaskCount() {
        return timeoutTaskList.size();
    }

    public void run() {
        boolean interrupted = false;
        for (;;) {
            final List<TimeoutTask> expiredTask = getExpiredTask();
            for (final Iterator<TimeoutTask> it = expiredTask.iterator(); it.hasNext();) {
                final TimeoutTask task = (TimeoutTask) it.next();
                task.expired();
                if (task.isPermanent()) {
                    task.restart();
                }
            }
            if (interrupted || thread.isInterrupted() || stopIfLeisure()) {
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                interrupted = true;
            }
        }
    }

    protected synchronized List<TimeoutTask> getExpiredTask() {
        final List<TimeoutTask> expiredTask = new ArrayList<TimeoutTask>();
        try {
            if (timeoutTaskList == null || timeoutTaskList.isEmpty()) {
                return expiredTask;
            }
        } catch (NullPointerException e) {
            return expiredTask;
        }
        for (TimeoutLinkedList.Entry e = timeoutTaskList.getFirstEntry(); e != null; e = e.getNext()) {
            final TimeoutTask task = (TimeoutTask) e.getElement();
            if (task.isCanceled()) {
                e.remove();
                continue;
            }
            if (task.isStopped()) {
                continue;
            }
            if (task.isExpired()) {
                expiredTask.add(task);
                if (!task.isPermanent()) {
                    e.remove();
                }
            }
        }
        return expiredTask;
    }

    protected synchronized boolean stopIfLeisure() {
        try {
            if (timeoutTaskList == null || timeoutTaskList.isEmpty()) {
                thread = null;
                return true;
            }
        } catch (NullPointerException e) {
            return true;
        }
        return false;
    }
}
