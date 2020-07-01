/*
 * Copyright 2015-2020 the original author or authors.
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
package org.lastaflute.di.mockapp.logic;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jflute
 */
public class MockSeaLogic {

    private static final Logger logger = LoggerFactory.getLogger(MockSeaLogic.class);

    @Resource
    protected MockLandLogic mockLandLogic;

    // unsupported
    //@Binding(bindingType = BindingType.MUST)
    @Resource
    protected MockIkspiaryLogic mockIkspiaryLogic;

    protected MockAmphiLogic mockAmphiLogic; // under org.dbflute so injected
    protected MockAmphiLogic anotherNameMockAmphiLogic; // under org.dbflute so injected

    public void dockside() {
        logger.debug("dockside");
        mockLandLogic.showBase();
    }

    public void hanger() {
        logger.debug("hanger");
        mockIkspiaryLogic.sayjo();
    }

    public void magic() {
        logger.debug("magic");
        mockAmphiLogic.theater();
    }

    public void mermaid() {
        logger.debug("mermaid");
        anotherNameMockAmphiLogic.theater();
    }

    public void setMockAmphiLogic(MockAmphiLogic mockAmphiLogic) {
        this.mockAmphiLogic = mockAmphiLogic;
    }

    public void setAnotherNameMockAmphiLogic(MockAmphiLogic anotherNameMockAmphiLogic) {
        this.anotherNameMockAmphiLogic = anotherNameMockAmphiLogic;
    }
}
