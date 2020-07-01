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
package org.lastaflute.di.core.injection;

import org.lastaflute.di.mockcomp.dockside.MockDocksideStage;
import org.lastaflute.di.mockcomp.dockside.MockOverTheWaves;
import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 */
public class RichComponentBasicInjectionTest extends UnitLastaDiTestCase {

    @Override
    protected String getConfigPath() {
        return "test_basic.xml";
    }

    public void test_injection_by_basicComponent() throws Exception {
        // ## Arrange ##
        MockDocksideStage stage = getComponent(MockDocksideStage.class);
        log(stage);
        assertNotNull(stage);

        // ## Act ##
        // ## Assert ##
        MockOverTheWaves overTheWaves = stage.takeOverTheWaves();
        assertNotNull(overTheWaves);
        assertFalse(overTheWaves.isCreatedByFactoryMethod());
    }
}
