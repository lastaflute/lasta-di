package org.lastaflute.di.naming;

import org.lastaflute.di.mockapp.biz.MockBizRootLogic;
import org.lastaflute.di.mockapp.biz.cleanarc.domain.interactor.MockCleanArcInteractor;
import org.lastaflute.di.mockapp.biz.cleanarc.domain.repository.MockCleanArcRepository;
import org.lastaflute.di.mockapp.biz.cleanarc.infrastructure.MockCleanArcLoggingRepository;
import org.lastaflute.di.mockapp.biz.cleanarc.usecase.MockCleanArcUseCase;
import org.lastaflute.di.mockapp.job.MockSeaJob;
import org.lastaflute.di.mockapp.job.firstpark.MockLandJob;
import org.lastaflute.di.mockapp.logic.MockSeaLogic;
import org.lastaflute.di.mockapp.logic.firstpark.MockLandLogic;
import org.lastaflute.di.mockapp.logic.nearstation.MockBonvoLogic;
import org.lastaflute.di.mockapp.logic.nearstation.MockPiariLogic;
import org.lastaflute.di.mockapp.logic.nearstation.butfar.MockAmphiLogic;
import org.lastaflute.di.mockapp.logic.nearstation.impl.MockBonvoLogicImpl;
import org.lastaflute.di.mockapp.logic.objoriented.MockAbstractLogic;
import org.lastaflute.di.mockapp.logic.objoriented.MockConcreteLogic;
import org.lastaflute.di.mockapp.nondi.MockNondiSeaLogic;
import org.lastaflute.di.mockapp.web.MockMiracoAssist;
import org.lastaflute.di.mockapp.web.MockSeaAction;
import org.lastaflute.di.mockapp.web.inter.MockDohotelAssist;
import org.lastaflute.di.mockapp.web.mock.land.MockLandAction;
import org.lastaflute.di.mockapp.web.mock.land.assist.MockLandoAssist;
import org.lastaflute.di.unit.UnitLastaDiTestCase;
import org.lastaflute.di.util.LdiSrl;

/**
 * @author jflute
 * @since 0.8.4 (2021/07/16)
 */
public class StyledNamingConventionTest extends UnitLastaDiTestCase {

    // ===================================================================================
    //                                                                 Class Determination
    //                                                                 ===================
    public void test_isTargetClassName_by_classNameAndSuffix() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertTrue(convention.isTargetClassName(MockSeaAction.class.getName(), "Action"));
        assertTrue(convention.isTargetClassName(MockLandAction.class.getName(), "Action"));
        assertTrue(convention.isTargetClassName(MockMiracoAssist.class.getName(), "Assist"));
        assertTrue(convention.isTargetClassName(MockDohotelAssist.class.getName(), "Assist"));
        assertTrue(convention.isTargetClassName(MockLandoAssist.class.getName(), "Assist"));
        assertTrue(convention.isTargetClassName(MockSeaJob.class.getName(), "Job"));
        assertTrue(convention.isTargetClassName(MockLandJob.class.getName(), "Job"));
        assertTrue(convention.isTargetClassName(MockSeaLogic.class.getName(), "Logic"));
        assertTrue(convention.isTargetClassName(MockPiariLogic.class.getName(), "Logic"));
        assertTrue(convention.isTargetClassName(MockBonvoLogic.class.getName(), "Logic"));
        assertTrue(convention.isTargetClassName(MockBonvoLogicImpl.class.getName(), "Logic"));
        assertTrue(convention.isTargetClassName(MockAmphiLogic.class.getName(), "Logic"));
        assertTrue(convention.isTargetClassName(MockAbstractLogic.class.getName(), "Logic"));
        assertTrue(convention.isTargetClassName(MockConcreteLogic.class.getName(), "Logic"));
        assertFalse(convention.isTargetClassName(MockNondiSeaLogic.class.getName(), "Logic"));
        assertFalse(convention.isTargetClassName(getClass().getName(), "Test"));
        assertFalse(convention.isTargetClassName(MockBizRootLogic.class.getName(), "Logic"));
        assertFalse(convention.isTargetClassName(MockCleanArcUseCase.class.getName(), "Case"));
        assertFalse(convention.isTargetClassName(MockCleanArcInteractor.class.getName(), "Interactor"));
        assertFalse(convention.isTargetClassName(MockCleanArcRepository.class.getName(), "Repository"));
        assertFalse(convention.isTargetClassName(MockCleanArcLoggingRepository.class.getName(), "Repository"));
    }

    public void test_isTargetClassName_by_classNameOnly() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertTrue(convention.isTargetClassName(MockSeaAction.class.getName()));
        assertTrue(convention.isTargetClassName(MockLandAction.class.getName()));
        assertTrue(convention.isTargetClassName(MockMiracoAssist.class.getName()));
        assertTrue(convention.isTargetClassName(MockDohotelAssist.class.getName()));
        assertTrue(convention.isTargetClassName(MockLandoAssist.class.getName()));
        assertTrue(convention.isTargetClassName(MockSeaJob.class.getName()));
        assertTrue(convention.isTargetClassName(MockLandJob.class.getName()));
        assertTrue(convention.isTargetClassName(MockSeaLogic.class.getName()));
        assertTrue(convention.isTargetClassName(MockPiariLogic.class.getName()));
        assertTrue(convention.isTargetClassName(MockBonvoLogic.class.getName()));
        assertTrue(convention.isTargetClassName(MockBonvoLogicImpl.class.getName()));
        assertTrue(convention.isTargetClassName(MockAmphiLogic.class.getName()));
        assertTrue(convention.isTargetClassName(MockAbstractLogic.class.getName()));
        assertTrue(convention.isTargetClassName(MockConcreteLogic.class.getName()));
        assertTrue(convention.isTargetClassName(MockNondiSeaLogic.class.getName()));
        assertFalse(convention.isTargetClassName(getClass().getName()));
        assertTrue(convention.isTargetClassName(MockBizRootLogic.class.getName()));
        assertTrue(convention.isTargetClassName(MockCleanArcUseCase.class.getName()));
        assertTrue(convention.isTargetClassName(MockCleanArcInteractor.class.getName()));
        assertTrue(convention.isTargetClassName(MockCleanArcRepository.class.getName()));
        assertTrue(convention.isTargetClassName(MockCleanArcLoggingRepository.class.getName()));
    }

    // ===================================================================================
    //                                                                     Convert from-to
    //                                                                     ===============
    // -----------------------------------------------------
    //                                 Suffix to PackageName
    //                                 ---------------------
    public void test_fromSuffixToPackageName_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals("action", convention.fromSuffixToPackageName("Action"));
        assertEquals("assist", convention.fromSuffixToPackageName("Assist"));
        assertEquals("job", convention.fromSuffixToPackageName("Job"));
        assertEquals("logic", convention.fromSuffixToPackageName("Logic"));
        assertEquals("service", convention.fromSuffixToPackageName("Service"));
        assertEquals("detarame", convention.fromSuffixToPackageName("Detarame")); // non DI suffix
        assertEquals("test", convention.fromSuffixToPackageName("Test")); // non DI suffix
        assertEquals("usecase", convention.fromSuffixToPackageName("UseCase"));
        assertEquals("interactor", convention.fromSuffixToPackageName("Interactor"));
        assertEquals("repository", convention.fromSuffixToPackageName("Repository"));
    }

    // -----------------------------------------------------
    //                            ClassName to ComponentName
    //                            --------------------------
    public void test_fromClassNameToComponentName_basic() throws Exception { // *important
        StyledNamingConvention convention = createConvention();
        assertEquals("mockSeaAction", convention.fromClassNameToComponentName(MockSeaAction.class.getName()));
        assertEquals("mock_land_mockLandAction", convention.fromClassNameToComponentName(MockLandAction.class.getName()));
        assertEquals("mockMiracoAssist", convention.fromClassNameToComponentName(MockMiracoAssist.class.getName()));

        // #thinking jflute how to derive inter_mockDohotelAssist from MockLandoAssist (2021/07/29)
        assertEquals("inter_mockDohotelAssist", convention.fromClassNameToComponentName(MockDohotelAssist.class.getName()));
        assertEquals("mock_land_assist_mockLandoAssist", convention.fromClassNameToComponentName(MockLandoAssist.class.getName()));

        assertEquals("mockSeaJob", convention.fromClassNameToComponentName(MockSeaJob.class.getName()));
        assertEquals("firstpark_mockLandJob", convention.fromClassNameToComponentName(MockLandJob.class.getName()));
        assertEquals("mockSeaLogic", convention.fromClassNameToComponentName(MockSeaLogic.class.getName()));
        assertEquals("firstpark_mockLandLogic", convention.fromClassNameToComponentName(MockLandLogic.class.getName()));
        assertEquals("nearstation_mockPiariLogic", convention.fromClassNameToComponentName(MockPiariLogic.class.getName()));
        assertEquals("nearstation_mockBonvoLogic", convention.fromClassNameToComponentName(MockBonvoLogic.class.getName()));
        assertEquals("nearstation_mockBonvoLogic", convention.fromClassNameToComponentName(MockBonvoLogicImpl.class.getName()));
        assertEquals("nearstation_butfar_mockAmphiLogic", convention.fromClassNameToComponentName(MockAmphiLogic.class.getName()));
        assertEquals("objoriented_mockAbstractLogic", convention.fromClassNameToComponentName(MockAbstractLogic.class.getName()));
        assertEquals("objoriented_mockConcreteLogic", convention.fromClassNameToComponentName(MockConcreteLogic.class.getName()));

        // non DI so simple name here
        assertEquals("mockNondiSeaLogic", convention.fromClassNameToComponentName(MockNondiSeaLogic.class.getName()));
        assertEquals(LdiSrl.initUncap(getClass().getSimpleName()), convention.fromClassNameToComponentName(getClass().getName()));
        assertEquals("mockBizRootLogic", convention.fromClassNameToComponentName(MockBizRootLogic.class.getName()));
        assertEquals("mockCleanArcUseCase", convention.fromClassNameToComponentName(MockCleanArcUseCase.class.getName()));
        assertEquals("mockCleanArcInteractor", convention.fromClassNameToComponentName(MockCleanArcInteractor.class.getName()));
        assertEquals("mockCleanArcRepository", convention.fromClassNameToComponentName(MockCleanArcRepository.class.getName()));
        assertEquals("mockCleanArcLoggingRepository",
                convention.fromClassNameToComponentName(MockCleanArcLoggingRepository.class.getName()));
    }

    // -----------------------------------------------------
    //                       ClassName to ShortComponentName
    //                       -------------------------------
    public void test_fromClassNameToShortComponentName_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals("mockSeaAction", convention.fromClassNameToShortComponentName(MockSeaAction.class.getName()));
        assertEquals("mockLandAction", convention.fromClassNameToShortComponentName(MockLandAction.class.getName()));
        assertEquals("mockMiracoAssist", convention.fromClassNameToShortComponentName(MockMiracoAssist.class.getName()));
        assertEquals("mockDohotelAssist", convention.fromClassNameToShortComponentName(MockDohotelAssist.class.getName()));
        assertEquals("mockLandoAssist", convention.fromClassNameToShortComponentName(MockLandoAssist.class.getName()));
        assertEquals("mockSeaJob", convention.fromClassNameToShortComponentName(MockSeaJob.class.getName()));
        assertEquals("mockLandJob", convention.fromClassNameToShortComponentName(MockLandJob.class.getName()));
        assertEquals("mockSeaLogic", convention.fromClassNameToShortComponentName(MockSeaLogic.class.getName()));
        assertEquals("mockLandLogic", convention.fromClassNameToShortComponentName(MockLandLogic.class.getName()));
        assertEquals("mockPiariLogic", convention.fromClassNameToShortComponentName(MockPiariLogic.class.getName()));
        assertEquals("mockBonvoLogic", convention.fromClassNameToShortComponentName(MockBonvoLogic.class.getName()));
        assertEquals("mockBonvoLogic", convention.fromClassNameToShortComponentName(MockBonvoLogicImpl.class.getName()));
        assertEquals("mockAmphiLogic", convention.fromClassNameToShortComponentName(MockAmphiLogic.class.getName()));
        assertEquals("mockAbstractLogic", convention.fromClassNameToShortComponentName(MockAbstractLogic.class.getName()));
        assertEquals("mockConcreteLogic", convention.fromClassNameToShortComponentName(MockConcreteLogic.class.getName()));
        assertEquals("mockNondiSeaLogic", convention.fromClassNameToShortComponentName(MockNondiSeaLogic.class.getName()));
        assertEquals(LdiSrl.initUncap(getClass().getSimpleName()), convention.fromClassNameToShortComponentName(getClass().getName()));
        assertEquals("mockBizRootLogic", convention.fromClassNameToShortComponentName(MockBizRootLogic.class.getName()));
        assertEquals("mockCleanArcUseCase", convention.fromClassNameToShortComponentName(MockCleanArcUseCase.class.getName()));
        assertEquals("mockCleanArcInteractor", convention.fromClassNameToShortComponentName(MockCleanArcInteractor.class.getName()));
        assertEquals("mockCleanArcRepository", convention.fromClassNameToShortComponentName(MockCleanArcRepository.class.getName()));
        assertEquals("mockCleanArcLoggingRepository",
                convention.fromClassNameToShortComponentName(MockCleanArcLoggingRepository.class.getName()));
    }

    // -----------------------------------------------------
    //                   Class to Complete (Component) Class
    //                   -----------------------------------
    public void test_toCompleteClass_basic() throws Exception {
        {
            StyledNamingConvention convention = createConvention();
            assertEquals(MockSeaAction.class, convention.toCompleteClass(MockSeaAction.class));
            assertEquals(MockDohotelAssist.class, convention.toCompleteClass(MockDohotelAssist.class));
            assertEquals(MockLandoAssist.class, convention.toCompleteClass(MockLandoAssist.class));
            assertEquals(MockBonvoLogicImpl.class, convention.toCompleteClass(MockBonvoLogic.class)); // here
            assertEquals(MockBonvoLogicImpl.class, convention.toCompleteClass(MockBonvoLogicImpl.class));
        }
        {
            StyledNamingConvention convention = createConventionUsingInterface(MockDohotelAssist.class, MockLandoAssist.class);
            assertEquals(MockSeaAction.class, convention.toCompleteClass(MockSeaAction.class));
            assertEquals(MockLandoAssist.class, convention.toCompleteClass(MockDohotelAssist.class));
            assertEquals(MockLandoAssist.class, convention.toCompleteClass(MockLandoAssist.class));
        }
    }

    // -----------------------------------------------------
    //                                ComponentName to Class
    //                                ----------------------
    public void test_fromComponentNameToClass_basic() throws Exception { // *important
        StyledNamingConvention convention = createConvention();
        assertEquals(MockSeaAction.class, convention.fromComponentNameToClass("mockSeaAction"));
        assertEquals(MockLandAction.class, convention.fromComponentNameToClass("mock_land_mockLandAction"));
        assertEquals(MockMiracoAssist.class, convention.fromComponentNameToClass("mockMiracoAssist"));

        // #thinking jflute how to derive MockDohotelAssist from inter_mockDohotelAssist (2021/07/29)
        assertEquals(MockDohotelAssist.class, convention.fromComponentNameToClass("inter_mockDohotelAssist"));
        assertEquals(MockLandoAssist.class, convention.fromComponentNameToClass("mock_land_assist_mockLandoAssist"));

        assertEquals(MockSeaJob.class, convention.fromComponentNameToClass("mockSeaJob"));
        assertEquals(MockLandJob.class, convention.fromComponentNameToClass("firstpark_mockLandJob"));
        assertEquals(MockSeaLogic.class, convention.fromComponentNameToClass("mockSeaLogic"));
        assertEquals(MockLandLogic.class, convention.fromComponentNameToClass("firstpark_mockLandLogic"));
        assertEquals(MockPiariLogic.class, convention.fromComponentNameToClass("nearstation_mockPiariLogic"));
        assertEquals(MockBonvoLogicImpl.class, convention.fromComponentNameToClass("nearstation_mockBonvoLogic"));
        assertEquals(MockAmphiLogic.class, convention.fromComponentNameToClass("nearstation_butfar_mockAmphiLogic"));
        assertEquals(MockAbstractLogic.class, convention.fromComponentNameToClass("objoriented_mockAbstractLogic"));
        assertEquals(MockConcreteLogic.class, convention.fromComponentNameToClass("objoriented_mockConcreteLogic"));

        // non DI so null here
        assertNull(convention.fromComponentNameToClass("mockNondiSeaLogic"));
        assertNull(convention.fromComponentNameToClass("nondi_mockNondiSeaLogic"));
        assertNull(convention.fromComponentNameToClass("mockBizRootLogic"));
        assertNull(convention.fromComponentNameToClass("biz_mockBizRootLogic"));
        assertNull(convention.fromComponentNameToClass("mockCleanArcUseCase"));
        assertNull(convention.fromComponentNameToClass("biz_cleanarc_usecase_mockCleanArcUseCase"));
        assertNull(convention.fromComponentNameToClass("mockCleanArcInteractor"));
        assertNull(convention.fromComponentNameToClass("biz_cleanarc_domain_interactor_mockCleanArcInteractor"));
        assertNull(convention.fromComponentNameToClass("mockCleanArcRepository"));
        assertNull(convention.fromComponentNameToClass("biz_cleanarc_domain_repository_mockCleanArcRepository"));
        assertNull(convention.fromComponentNameToClass("mockCleanArcLoggingRepository"));
        assertNull(convention.fromComponentNameToClass("biz_cleanarc_infrastructure_mockCleanArcLoggingRepository"));
    }

    // -----------------------------------------------------
    //                         Component/ClassName to Suffix
    //                         -----------------------------
    public void test_fromComponentNameToSuffix_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals("Action", convention.fromComponentNameToSuffix("mockSeaAction"));
        assertEquals("Action", convention.fromComponentNameToSuffix("mock_land_mockLandAction"));
        assertEquals("Assist", convention.fromComponentNameToSuffix("mockMiracoAssist"));
        assertEquals("Job", convention.fromComponentNameToSuffix("mockSeaJob"));
        assertEquals("Job", convention.fromComponentNameToSuffix("firstpark_mockLandJob"));
        assertEquals("Logic", convention.fromComponentNameToSuffix("mockSeaLogic"));
        assertEquals("Logic", convention.fromComponentNameToSuffix("firstpark_mockLandLogic"));
        assertEquals("Logic", convention.fromComponentNameToSuffix("nearstation_mockPiariLogic"));
        assertEquals("Logic", convention.fromComponentNameToSuffix("nearstation_mockBonvoLogic"));
        assertEquals("Logic", convention.fromComponentNameToSuffix("nearstation_butfar_mockAmphiLogic"));
        assertEquals("Logic", convention.fromComponentNameToSuffix("objoriented_mockAbstractLogic"));
        assertEquals("Logic", convention.fromComponentNameToSuffix("objoriented_mockConcreteLogic"));
        assertEquals("Logic", convention.fromComponentNameToSuffix("mockNondiSeaLogic"));
        assertEquals("Logic", convention.fromComponentNameToSuffix("mockBizRootLogic"));
        assertEquals("Case", convention.fromComponentNameToSuffix("mockCleanArcUseCase"));
        assertEquals("Interactor", convention.fromComponentNameToSuffix("mockCleanArcInteractor"));
        assertEquals("Interactor", convention.fromComponentNameToSuffix("biz_cleanarc_domain_interactor_mockCleanArcInteractor"));
        assertEquals("Repository", convention.fromComponentNameToSuffix("mockCleanArcRepository"));
        assertEquals("Repository", convention.fromComponentNameToSuffix("mockCleanArcLoggingRepository"));

        assertEquals("Action", convention.fromComponentNameToSuffix("AAAction"));
        assertEquals("Detarame", convention.fromComponentNameToSuffix("SeaDetarame"));
        assertNull(convention.fromComponentNameToSuffix("detarame"));
    }

    public void test_fromClassNameToSuffix_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals("Action", convention.fromClassNameToSuffix(MockSeaAction.class.getName()));
        assertEquals("Action", convention.fromClassNameToSuffix(MockLandAction.class.getName()));
        assertEquals("Assist", convention.fromClassNameToSuffix(MockMiracoAssist.class.getName()));
        assertEquals("Job", convention.fromClassNameToSuffix(MockSeaJob.class.getName()));
        assertEquals("Job", convention.fromClassNameToSuffix(MockLandJob.class.getName()));
        assertEquals("Logic", convention.fromClassNameToSuffix(MockSeaLogic.class.getName()));
        assertEquals("Logic", convention.fromClassNameToSuffix(MockLandLogic.class.getName()));
        assertEquals("Logic", convention.fromClassNameToSuffix(MockPiariLogic.class.getName()));
        assertEquals("Logic", convention.fromClassNameToSuffix(MockBonvoLogic.class.getName()));
        assertEquals("Logic", convention.fromClassNameToSuffix(MockAmphiLogic.class.getName()));
        assertEquals("Logic", convention.fromClassNameToSuffix(MockAbstractLogic.class.getName()));
        assertEquals("Logic", convention.fromClassNameToSuffix(MockConcreteLogic.class.getName()));
        assertEquals("Logic", convention.fromClassNameToSuffix(MockNondiSeaLogic.class.getName()));
        assertEquals("Logic", convention.fromClassNameToSuffix(MockBizRootLogic.class.getName()));
        assertEquals("Case", convention.fromClassNameToSuffix(MockCleanArcUseCase.class.getName()));
        assertEquals("Interactor", convention.fromClassNameToSuffix(MockCleanArcInteractor.class.getName()));
        assertEquals("Repository", convention.fromClassNameToSuffix(MockCleanArcRepository.class.getName()));
        assertEquals("Repository", convention.fromClassNameToSuffix(MockCleanArcLoggingRepository.class.getName()));
        assertNull(convention.fromClassNameToSuffix("detarame"));
    }

    // -----------------------------------------------------
    //                      ComponentName to PartOfClassName
    //                      --------------------------------
    public void test_fromComponentNameToPartOfClassName_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals("MockSeaAction", convention.fromComponentNameToPartOfClassName("mockSeaAction"));
        assertEquals("mock.land.MockLandAction", convention.fromComponentNameToPartOfClassName("mock_land_mockLandAction"));
        assertEquals("MockSeaJob", convention.fromComponentNameToPartOfClassName("mockSeaJob"));
        assertEquals("firstpark.MockLandJob", convention.fromComponentNameToPartOfClassName("firstpark_mockLandJob"));
        assertEquals("biz.cleanarc.domain.interactor.MockCleanArcInteractor",
                convention.fromComponentNameToPartOfClassName("biz_cleanarc_domain_interactor_mockCleanArcInteractor"));
    }

    // ===================================================================================
    //                                                                    View Path Action
    //                                                                    ================
    // -----------------------------------------------------
    //                                    Path to ActionName
    //                                    ------------------
    public void test_fromPathToActionName_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals("seaAction", convention.fromPathToActionName("/view/sea.html"));
        assertEquals("sea_landAction", convention.fromPathToActionName("/view/sea_land.html"));
        assertEquals("sea_sea_landAction", convention.fromPathToActionName("/view/sea/sea_land.html"));
        assertEquals("sea_land_piariAction", convention.fromPathToActionName("/view/sea/land_piari.html"));
        assertEquals("sea_land_piariBonvoAction", convention.fromPathToActionName("/view/sea/land_piariBonvo.html"));
        assertException(IllegalArgumentException.class, () -> convention.fromPathToActionName("sea.html"));
        assertException(IllegalArgumentException.class, () -> convention.fromPathToActionName("/view/sea"));
    }

    // -----------------------------------------------------
    //                                    ActionName to Path
    //                                    ------------------
    public void test_fromActionNameToPath_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals("/view/sea.html", convention.fromActionNameToPath("seaAction"));
        assertEquals("/view/sea/land.html", convention.fromActionNameToPath("sea_landAction"));
        assertEquals("/view/sea/sea/land.html", convention.fromActionNameToPath("sea_sea_landAction"));
        assertEquals("/view/sea/land/piari.html", convention.fromActionNameToPath("sea_land_piariAction"));
    }

    // ===================================================================================
    //                                                            Interface Implementation
    //                                                            ========================
    public void test_toImplementationClassName_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals(deriveExpectedImpl(MockSeaAction.class), convention.toImplementationClassName(MockSeaAction.class.getName()));
        assertEquals(deriveExpectedImpl(MockMiracoAssist.class), convention.toImplementationClassName(MockMiracoAssist.class.getName()));

        // #thinking jflute how to derive MockLandoAssist from MockDohotelAssist (2021/07/29)
        assertEquals(deriveExpectedImpl(MockDohotelAssist.class), convention.toImplementationClassName(MockDohotelAssist.class.getName()));
        assertEquals(deriveExpectedImpl(MockLandoAssist.class), convention.toImplementationClassName(MockLandoAssist.class.getName()));

        assertEquals(deriveExpectedImpl(MockSeaLogic.class), convention.toImplementationClassName(MockSeaLogic.class.getName()));
        assertEquals(MockBonvoLogicImpl.class.getName(), convention.toImplementationClassName(MockBonvoLogic.class.getName()));
        assertEquals(deriveExpectedImpl(MockBonvoLogicImpl.class),
                convention.toImplementationClassName(MockBonvoLogicImpl.class.getName()));
        assertEquals(deriveExpectedImpl(MockAbstractLogic.class), convention.toImplementationClassName(MockAbstractLogic.class.getName()));
        assertEquals(deriveExpectedImpl(MockConcreteLogic.class), convention.toImplementationClassName(MockConcreteLogic.class.getName()));
        assertEquals(deriveExpectedImpl(MockCleanArcInteractor.class),
                convention.toImplementationClassName(MockCleanArcInteractor.class.getName()));
    }

    private String deriveExpectedImpl(Class<?> type) {
        return type.getPackage().getName() + ".impl." + type.getSimpleName() + "Impl";
    }

    public void test_toInterfaceClassName_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals(MockSeaAction.class.getName(), convention.toInterfaceClassName(MockSeaAction.class.getName()));

        // #thinking jflute how to derive MockDohotelAssist from MockLandoAssist (2021/07/29)
        assertEquals(MockLandoAssist.class.getName(), convention.toInterfaceClassName(MockLandoAssist.class.getName()));

        assertEquals(MockSeaLogic.class.getName(), convention.toInterfaceClassName(MockSeaLogic.class.getName()));
        assertEquals(MockBonvoLogic.class.getName(), convention.toInterfaceClassName(MockBonvoLogic.class.getName()));
        assertEquals(MockBonvoLogic.class.getName(), convention.toInterfaceClassName(MockBonvoLogicImpl.class.getName()));
        assertEquals(MockAbstractLogic.class.getName(), convention.toInterfaceClassName(MockAbstractLogic.class.getName()));
        assertEquals(MockConcreteLogic.class.getName(), convention.toInterfaceClassName(MockConcreteLogic.class.getName()));
        assertEquals(MockCleanArcInteractor.class.getName(), convention.toInterfaceClassName(MockCleanArcInteractor.class.getName()));
    }

    public void test_isSkipClass_basic() throws Exception {
        {
            StyledNamingConvention convention = createConvention();
            assertFalse(convention.isSkipClass(MockSeaAction.class));
            assertFalse(convention.isSkipClass(MockDohotelAssist.class));
            assertFalse(convention.isSkipClass(MockLandoAssist.class));
            assertFalse(convention.isSkipClass(MockBonvoLogic.class));
            assertFalse(convention.isSkipClass(MockBonvoLogicImpl.class));
            assertFalse(convention.isSkipClass(MockAbstractLogic.class));
            assertFalse(convention.isSkipClass(MockConcreteLogic.class));
            assertFalse(convention.isSkipClass(MockCleanArcInteractor.class));
        }
        {
            StyledNamingConvention convention = createConventionUsingInterface(MockDohotelAssist.class, MockLandoAssist.class);
            assertFalse(convention.isSkipClass(MockSeaAction.class));
            assertFalse(convention.isSkipClass(MockDohotelAssist.class));
            assertTrue(convention.isSkipClass(MockLandoAssist.class)); // here
            assertFalse(convention.isSkipClass(MockBonvoLogic.class));
            assertFalse(convention.isSkipClass(MockBonvoLogicImpl.class));
            assertFalse(convention.isSkipClass(MockAbstractLogic.class));
            assertFalse(convention.isSkipClass(MockConcreteLogic.class));
        }
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    private StyledNamingConvention createConvention() {
        StyledNamingConvention convention = new StyledNamingConvention();
        convention.initialize();
        convention.addRootPackageName("org.lastaflute.di.naming.mockapp");
        return convention;
    }

    private StyledNamingConvention createConventionUsingInterface(Class<?> interfaceType, Class<?> implementationType) {
        StyledNamingConvention convention = createConvention();
        convention.addInterfaceToImplementationClassName(interfaceType.getName(), implementationType.getName());
        return convention;
    }
}
