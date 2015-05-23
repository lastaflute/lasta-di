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

import org.lastaflute.jta.helper.timer.LjtTimeoutManager;
import org.lastaflute.jta.helper.timer.LjtTimeoutTarget;
import org.lastaflute.jta.helper.timer.LjtTimeoutTask;

import junit.framework.TestCase;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LtxTimeoutManagerTest extends TestCase {

    private int expiredCount;

    @Override
    protected void setUp() throws Exception {
        expiredCount = 0;
        LjtTimeoutManager.getInstance().clear();
    }

    @Override
    protected void tearDown() throws Exception {
        LjtTimeoutManager.getInstance().clear();
    }

    public void test_expired() throws Exception {
        LjtTimeoutTask task = LjtTimeoutManager.getInstance().addTimeoutTarget(new LjtTimeoutTarget() {
            public void expired() {
                System.out.println("expired");
                expiredCount++;
            }
        }, 1, true);

        assertNotNull(LjtTimeoutManager.getInstance().thread);
        Thread.sleep(2000);
        assertTrue(expiredCount > 0);
        assertEquals(1, LjtTimeoutManager.getInstance().getTimeoutTaskCount());
        LjtTimeoutManager.getInstance().stop();
        assertNull(LjtTimeoutManager.getInstance().thread);
        Thread.sleep(10);
        int count = expiredCount;
        task.stop();
        LjtTimeoutManager.getInstance().start();
        assertNotNull(LjtTimeoutManager.getInstance().thread);
        Thread.sleep(2000);
        assertEquals(count, expiredCount);
        assertEquals(1, LjtTimeoutManager.getInstance().getTimeoutTaskCount());
        task.cancel();
        Thread.sleep(2000);
        assertEquals(0, LjtTimeoutManager.getInstance().getTimeoutTaskCount());
        assertNull(LjtTimeoutManager.getInstance().thread);
    }
}
