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
package org.lastaflute.di.mockapp.job.firstpark;

import javax.annotation.Resource;

import org.lastaflute.di.mockapp.logic.firstpark.MockLandLogic;
import org.lastaflute.di.mockapp.logic.nearstation.MockPiariLogic;
import org.lastaflute.di.mockapp.logic.nearstation.butfar.MockAmphiLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jflute
 */
public class MockLandJob {

    private static final Logger logger = LoggerFactory.getLogger(MockLandJob.class);

    @Resource
    protected MockLandLogic landLogic;

    // unsupported
    //@Binding(bindingType = BindingType.MUST)
    @Resource
    protected MockPiariLogic piariLogic;

    protected MockAmphiLogic amphiLogic; // under org.dbflute so injected
    protected MockAmphiLogic anotherNameAmphiLogic; // under org.dbflute so injected

    public void dockside() {
        logger.debug("dockside");
        landLogic.showBase();
    }

    public void hanger() {
        logger.debug("hanger");
        piariLogic.sayjo();
    }

    public void magic() {
        logger.debug("magic");
        amphiLogic.theater();
    }

    public void mermaid() {
        logger.debug("mermaid");
        anotherNameAmphiLogic.theater();
    }

    public void setMockAmphiLogic(MockAmphiLogic mockAmphiLogic) {
        this.amphiLogic = mockAmphiLogic;
    }

    public void setAnotherNameMockAmphiLogic(MockAmphiLogic anotherNameMockAmphiLogic) {
        this.anotherNameAmphiLogic = anotherNameMockAmphiLogic;
    }
}
