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
package org.lastaflute.di.core.injection;

import org.lastaflute.di.core.exception.AutoBindingFailureException;
import org.lastaflute.di.core.exception.ComponentNotFoundException;
import org.lastaflute.di.mockapp.biz.cleanarc.adapter.MockCleanArcController;
import org.lastaflute.di.mockapp.biz.cleanarc.adapter.MockCleanArcLoggingPresenter;
import org.lastaflute.di.mockapp.biz.cleanarc.domain.interactor.MockCleanArcInteractor;
import org.lastaflute.di.mockapp.biz.cleanarc.domain.interactor.MockCleanArcPresenter;
import org.lastaflute.di.mockapp.biz.cleanarc.domain.repository.MockCleanArcRepository;
import org.lastaflute.di.mockapp.biz.cleanarc.infrastructure.MockCleanArcLoggingRepository;
import org.lastaflute.di.mockapp.biz.cleanarc.usecase.MockCleanArcUseCase;
import org.lastaflute.di.mockapp.logic.MockSeaLogic;
import org.lastaflute.di.mockapp.logic.firstpark.MockLandLogic;
import org.lastaflute.di.mockapp.logic.nearstation.MockBonvoLogic;
import org.lastaflute.di.mockapp.logic.nearstation.impl.MockBonvoLogicImpl;
import org.lastaflute.di.mockapp.logic.objoriented.MockAbstractLogic;
import org.lastaflute.di.mockapp.logic.objoriented.MockConcreteLogic;
import org.lastaflute.di.mockapp.web.MockMiracoAssist;
import org.lastaflute.di.mockapp.web.inter.MockDohotelAssist;
import org.lastaflute.di.mockapp.web.inter.caller.MockBaysideStationAssist;
import org.lastaflute.di.mockapp.web.inter.caller.MockLandoStationAssist;
import org.lastaflute.di.mockapp.web.mock.land.assist.MockLandoAssist;
import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 */
public class QuickComponentInjectionTest extends UnitLastaDiTestCase {

    // ===================================================================================
    //                                                                       Injection way
    //                                                                       =============
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

    // ===================================================================================
    //                                                                        Hidden Super
    //                                                                        ============
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

    // ===================================================================================
    //                                                                     Quick Interface
    //                                                                     ===============
    public void test_injection_quickInterface_basic() throws Exception { // for warm deploy
        getComponent(MockMiracoAssist.class).sta();

        // #thinking jflute how to inject by interface before implementation initialization even if warm/hot deploy (2021/07/29)
        assertException(AutoBindingFailureException.class, () -> getComponent(MockBaysideStationAssist.class)); // before
        assertException(ComponentNotFoundException.class, () -> getComponent(MockDohotelAssist.class)); // before
        getComponent(MockLandoStationAssist.class).lando();
        getComponent(MockBaysideStationAssist.class); // because of implementation already initialized
        getComponent(MockDohotelAssist.class).lan(); // me too
        getComponent(MockLandoAssist.class).lan();

        getComponent(MockBonvoLogic.class).welcome();
        getComponent(MockBonvoLogicImpl.class).welcome();

        assertException(ComponentNotFoundException.class, () -> getComponent(MockAbstractLogic.class));
        getComponent(MockConcreteLogic.class).getSuperLogic();
    }

    // simple execution for code trace
    public void test_injection_quickInterface_freedomInterface() throws Exception { // for warm deploy
        assertException(ComponentNotFoundException.class, () -> getComponent(MockDohotelAssist.class)).handle(cause -> {
            log(cause);
        });
    }

    // ===================================================================================
    //                                                                         biz Package
    //                                                                         ===========
    public void test_injection_bizPackage_basic() throws Exception {
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanArcController.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanArcUseCase.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanArcInteractor.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanArcRepository.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanArcPresenter.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanArcLoggingRepository.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanArcLoggingPresenter.class));
    }
}
