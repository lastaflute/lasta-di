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
package org.lastaflute.di.mockapp.biz.cleanarc.domain.interactor;

import javax.annotation.Resource;

import org.lastaflute.di.mockapp.biz.cleanarc.domain.repository.MockCleanArcRepository;
import org.lastaflute.di.mockapp.biz.cleanarc.usecase.MockCleanArcUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jflute
 */
public class MockCleanArcInteractor implements MockCleanArcUseCase {

    private static final Logger logger = LoggerFactory.getLogger(MockCleanArcInteractor.class);

    @Resource
    private MockCleanArcRepository cleanArcRepository;

    public void clean() {
        logger.debug("clean() here!");
        cleanArcRepository.save();
    }
}
