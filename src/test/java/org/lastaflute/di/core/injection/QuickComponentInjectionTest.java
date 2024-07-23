/*
 * Copyright 2015-2024 the original author or authors.
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

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.exception.AutoBindingFailureException;
import org.lastaflute.di.core.exception.ComponentNotFoundException;
import org.lastaflute.di.core.exception.IllegalPropertyDefinitionException;
import org.lastaflute.di.core.factory.LaContainerFactory;
import org.lastaflute.di.exception.NoSuchConstructorRuntimeException;
import org.lastaflute.di.mockapp.biz.cleaneg.adapter.MockCleanEgController;
import org.lastaflute.di.mockapp.biz.cleaneg.adapter.MockCleanEgLoggingPresenter;
import org.lastaflute.di.mockapp.biz.cleaneg.domain.interactor.MockCleanEgInteractor;
import org.lastaflute.di.mockapp.biz.cleaneg.domain.interactor.MockCleanEgPresenter;
import org.lastaflute.di.mockapp.biz.cleaneg.domain.repository.MockCleanEgRepository;
import org.lastaflute.di.mockapp.biz.cleaneg.infrastructure.MockCleanEgLoggingRepository;
import org.lastaflute.di.mockapp.biz.cleaneg.usecase.MockCleanEgUseCase;
import org.lastaflute.di.mockapp.biz.onionarc.application.MockOnionArcAppService;
import org.lastaflute.di.mockapp.biz.onionarc.domain.MockOnionArcDomainService;
import org.lastaflute.di.mockapp.biz.onionarc.domain.MockOnionArcRepository;
import org.lastaflute.di.mockapp.biz.onionarc.infrastructure.MockOnionArcLoggingRepository;
import org.lastaflute.di.mockapp.logic.MockSeaLogic;
import org.lastaflute.di.mockapp.logic.firstpark.MockLandLogic;
import org.lastaflute.di.mockapp.logic.nearstation.MockBonvoLogic;
import org.lastaflute.di.mockapp.logic.nearstation.impl.MockBonvoLogicImpl;
import org.lastaflute.di.mockapp.logic.objoriented.MockAbstractLogic;
import org.lastaflute.di.mockapp.logic.objoriented.MockConcreteLogic;
import org.lastaflute.di.mockapp.web.MockMiracoAssist;
import org.lastaflute.di.mockapp.web.inter.MockAmbaAssist;
import org.lastaflute.di.mockapp.web.inter.MockDohotelAssist;
import org.lastaflute.di.mockapp.web.inter.caller.MockBaysideStationAssist;
import org.lastaflute.di.mockapp.web.inter.caller.MockLandoStationAssist;
import org.lastaflute.di.mockapp.web.mock.land.assist.MockLandoAssist;
import org.lastaflute.di.naming.StyledNamingConvention;
import org.lastaflute.di.naming.styling.StylingFreedomInterfaceMapper;
import org.lastaflute.di.unit.UnitLastaDiTestCase;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiSrl;

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
        getComponent(MockAmbaAssist.class).welcome(); // has standard implementation

        getComponent(MockBonvoLogic.class).welcome();
        getComponent(MockBonvoLogicImpl.class).welcome();

        assertException(ComponentNotFoundException.class, () -> getComponent(MockAbstractLogic.class));
        getComponent(MockConcreteLogic.class).getSuperLogic();
    }

    public void test_injection_quickInterface_freedomInterface() throws Exception { // for warm deploy
        assertException(ComponentNotFoundException.class, () -> getComponent(MockDohotelAssist.class)).handle(cause -> {
            log(cause);
        });
        mappingInterfaceToImplementation(MockDohotelAssist.class, MockLandoAssist.class);
        getComponent(MockDohotelAssist.class).lan();

        mappingInterfaceToImplementation(MockOnionArcRepository.class, MockOnionArcLoggingRepository.class);
        getComponent(MockOnionArcRepository.class).saveRepoAnything();
    }

    // ===================================================================================
    //                                                                         biz Package
    //                                                                         ===========
    public void test_injection_bizPackage_basic() throws Exception {
        // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
        // clean example in biz package
        // _/_/_/_/_/_/_/_/_/_/
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgController.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgLoggingPresenter.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgUseCase.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgInteractor.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgPresenter.class));

        // #for_now jflute cannot resolve freedom interface as default (needs cool or customization) (2021/10/03)
        assertException(NoSuchConstructorRuntimeException.class, () -> getComponent(MockCleanEgRepository.class));
        getComponent(MockCleanEgLoggingRepository.class).save();

        // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
        // onion architecture in biz package
        // _/_/_/_/_/_/_/_/_/_/
        // also here
        assertException(IllegalPropertyDefinitionException.class, () -> getComponent(MockOnionArcAppService.class));
        assertException(IllegalPropertyDefinitionException.class, () -> getComponent(MockOnionArcDomainService.class));
        assertException(NoSuchConstructorRuntimeException.class, () -> getComponent(MockOnionArcRepository.class));
        getComponent(MockOnionArcLoggingRepository.class).saveRepoAnything();
    }

    public void test_injection_bizPackage_freedomInterfaceMapping() throws Exception {
        // ## Arrange ##
        freedomMappingInterfaceToImplementation();

        // ## Act ##
        // ## Assert ##
        // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
        // clean example in biz package
        // _/_/_/_/_/_/_/_/_/_/
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgController.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgLoggingPresenter.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgUseCase.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgInteractor.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgPresenter.class));

        // #for_now jflute cannot resolve freedom interface as default (needs cool or customization) (2021/10/03)
        getComponent(MockCleanEgRepository.class).save();
        getComponent(MockCleanEgLoggingRepository.class).save();

        // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
        // onion architecture in biz package
        // _/_/_/_/_/_/_/_/_/_/
        getComponent(MockOnionArcAppService.class).saveAppAnything();
        getComponent(MockOnionArcDomainService.class).saveDomainAnything(); // see logging repository
        getComponent(MockOnionArcRepository.class).saveRepoAnything(); // can be executed after logging initialized
        getComponent(MockOnionArcLoggingRepository.class).saveRepoAnything();
    }

    public void test_injection_bizPackage_manualInterfaceMapping() throws Exception {
        // ## Arrange ##
        mappingInterfaceToImplementation(MockCleanEgRepository.class, MockCleanEgLoggingRepository.class);
        mappingInterfaceToImplementation(MockOnionArcRepository.class, MockOnionArcLoggingRepository.class);

        // ## Act ##
        // ## Assert ##
        // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
        // clean example in biz package
        // _/_/_/_/_/_/_/_/_/_/
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgController.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgLoggingPresenter.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgUseCase.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgInteractor.class));
        assertException(ComponentNotFoundException.class, () -> getComponent(MockCleanEgPresenter.class));

        // #for_now jflute cannot resolve freedom interface as default (needs cool or customization) (2021/10/03)
        getComponent(MockCleanEgRepository.class).save();
        getComponent(MockCleanEgLoggingRepository.class).save();

        // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
        // onion architecture in biz package
        // _/_/_/_/_/_/_/_/_/_/
        getComponent(MockOnionArcAppService.class).saveAppAnything();
        getComponent(MockOnionArcDomainService.class).saveDomainAnything(); // see logging repository
        getComponent(MockOnionArcRepository.class).saveRepoAnything(); // can be executed after logging initialized
        getComponent(MockOnionArcLoggingRepository.class).saveRepoAnything();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
    // #thinking jflute duplicate convention instances between configuration and application (2021/10/03)
    // _/_/_/_/_/_/_/_/_/_/
    private void freedomMappingInterfaceToImplementation() {
        LaContainer container = LaContainerFactory.getConfigurationContainer();
        container.getComponent(StyledNamingConvention.class);
        StyledNamingConvention convention = container.getComponent(StyledNamingConvention.class);
        StylingFreedomInterfaceMapper freedomInterfaceMapper = new StylingFreedomInterfaceMapper() {
            public String toImplementationClassName(String interfaceClassName) {
                if (interfaceClassName.contains("biz.cleaneg.domain.repository.") && interfaceClassName.endsWith("Repository")) {
                    String filtered = LdiSrl.replace(interfaceClassName, "biz.cleaneg.domain.repository.", "biz.cleaneg.infrastructure.");
                    filtered = LdiSrl.replace(filtered, "Repository", "LoggingRepository");
                    LdiClassUtil.forName(filtered); // assert
                    return filtered;
                }
                if (interfaceClassName.contains("biz.onionarc.domain.") && interfaceClassName.endsWith("Repository")) {
                    String filtered = LdiSrl.replace(interfaceClassName, "biz.onionarc.domain.", "biz.onionarc.infrastructure.");
                    filtered = LdiSrl.replace(filtered, "Repository", "LoggingRepository");
                    LdiClassUtil.forName(filtered); // assert
                    return filtered;
                }
                return null;
            }

            public String toInterfaceClassName(String implementationClassName) {
                if (implementationClassName.contains("biz.cleaneg.infrastructure.") && implementationClassName.endsWith("Repository")) {
                    String filtered =
                            LdiSrl.replace(implementationClassName, "biz.cleaneg.infrastructure.", "biz.cleaneg.domain.repository.");
                    filtered = LdiSrl.replace(filtered, "LoggingRepository", "Repository");
                    LdiClassUtil.forName(filtered); // assert
                    return filtered;
                }
                if (implementationClassName.contains("biz.onionarc.infrastructure.") && implementationClassName.endsWith("Repository")) {
                    String filtered = LdiSrl.replace(implementationClassName, "biz.onionarc.infrastructure.", "biz.onionarc.domain.");
                    filtered = LdiSrl.replace(filtered, "LoggingRepository", "Repository");
                    LdiClassUtil.forName(filtered); // assert
                    return filtered;
                }

                return null;
            }
        };
        convention.useFreedomInterfaceMapper(freedomInterfaceMapper);
    }

    private void mappingInterfaceToImplementation(Class<?> interfaceType, Class<?> implType) {
        LaContainer container = LaContainerFactory.getConfigurationContainer();
        container.getComponent(StyledNamingConvention.class);
        StyledNamingConvention convention = container.getComponent(StyledNamingConvention.class);
        convention.addInterfaceToImplementationClassName(interfaceType.getName(), implType.getName());
    }
}
