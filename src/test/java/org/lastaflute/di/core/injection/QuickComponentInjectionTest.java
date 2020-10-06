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

import org.lastaflute.di.mockapp.logic.MockLandLogic;
import org.lastaflute.di.mockapp.logic.MockSeaLogic;
import org.lastaflute.di.mockapp.logic.pattern.MockConcreteLogic;
import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 */
public class QuickComponentInjectionTest extends UnitLastaDiTestCase {

    public void test_injection_by_resource_annotation() throws Exception {
        // ## Arrange ##
        MockSeaLogic logic = getComponent(MockSeaLogic.class);
        log(logic);
        assertNotNull(logic);

        // ## Act ##
        // ## Assert ##
        logic.dockside();
    }

    public void test_injection_by_binding_annotation() throws Exception {
        // ## Arrange ##
        MockSeaLogic logic = getComponent(MockSeaLogic.class);
        log(logic);
        assertNotNull(logic);

        // ## Act ##
        // ## Assert ##
        logic.hanger();
    }

    public void test_injection_by_setter_under_dbflute_basic() throws Exception {
        // ## Arrange ##
        MockSeaLogic logic = getComponent(MockSeaLogic.class);
        log(logic);
        assertNotNull(logic);

        // ## Act ##
        // ## Assert ##
        logic.magic(); // under org.dbflute so injected
    }

    public void test_injection_by_setter_under_dbflute_otherName() throws Exception {
        // ## Arrange ##
        MockSeaLogic logic = getComponent(MockSeaLogic.class);
        log(logic);
        assertNotNull(logic);

        // ## Act ##
        // ## Assert ##
        logic.mermaid(); // under org.dbflute so injected
    }

    public void test_injection_hiddenSuper() throws Exception {
        // ## Arrange ##
        MockConcreteLogic logic = getComponent(MockConcreteLogic.class);
        log(logic);
        assertNotNull(logic);

        // ## Act ##
        MockLandLogic superLogic = logic.getSuperLogic();
        MockLandLogic subClassLogic = logic.getSubClassLogic();

        // ## Assert ##
        assertNotNull(superLogic);
        assertNotNull(subClassLogic);
    }
}
