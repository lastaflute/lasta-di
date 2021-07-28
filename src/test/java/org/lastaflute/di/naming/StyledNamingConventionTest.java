package org.lastaflute.di.naming;

import org.dbflute.util.Srl;
import org.lastaflute.di.mockapp.biz.MockBizRootLogic;
import org.lastaflute.di.mockapp.biz.cleanarc.domain.interactor.MockCleanArcInteractor;
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
import org.lastaflute.di.mockapp.web.MockSeaAction;
import org.lastaflute.di.mockapp.web.mock.land.MockLandAction;
import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 * @since 0.8.4 (2021/07/16)
 */
public class StyledNamingConventionTest extends UnitLastaDiTestCase {

    // ===================================================================================
    //                                                                 Class Determination
    //                                                                 ===================
    public void test_isTargetClassName_by_classNameAndSuffix() throws Exception { // can it be injected?
        StyledNamingConvention convention = createConvention();
        assertTrue(convention.isTargetClassName(MockSeaAction.class.getName(), "Action"));
        assertTrue(convention.isTargetClassName(MockLandAction.class.getName(), "Action"));
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
        assertFalse(convention.isTargetClassName(MockCleanArcInteractor.class.getName(), "Interactor"));
    }

    public void test_isTargetClassName_by_classNameOnly() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertTrue(convention.isTargetClassName(MockSeaAction.class.getName()));
        assertTrue(convention.isTargetClassName(MockLandAction.class.getName()));
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
        assertTrue(convention.isTargetClassName(MockCleanArcInteractor.class.getName()));
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
        assertEquals("job", convention.fromSuffixToPackageName("Job"));
        assertEquals("logic", convention.fromSuffixToPackageName("Logic"));
        assertEquals("service", convention.fromSuffixToPackageName("Service"));
        assertEquals("detarame", convention.fromSuffixToPackageName("Detarame")); // non DI suffix
        assertEquals("test", convention.fromSuffixToPackageName("Test")); // non DI suffix
        assertEquals("interactor", convention.fromSuffixToPackageName("Interactor"));
    }

    // -----------------------------------------------------
    //                       ClassName to ShortComponentName
    //                       -------------------------------
    public void test_fromClassNameToShortComponentName_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals("mockSeaAction", convention.fromClassNameToShortComponentName(MockSeaAction.class.getName()));
        assertEquals("mockLandAction", convention.fromClassNameToShortComponentName(MockLandAction.class.getName()));
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
        assertEquals(Srl.initUncap(getClass().getSimpleName()), convention.fromClassNameToShortComponentName(getClass().getName()));
        assertEquals("mockBizRootLogic", convention.fromClassNameToShortComponentName(MockBizRootLogic.class.getName()));
        assertEquals("mockCleanArcInteractor", convention.fromClassNameToShortComponentName(MockCleanArcInteractor.class.getName()));
    }

    // -----------------------------------------------------
    //                            ClassName to ComponentName
    //                            --------------------------
    public void test_fromClassNameToComponentName_basic() throws Exception { // *important
        StyledNamingConvention convention = createConvention();
        assertEquals("mockSeaAction", convention.fromClassNameToComponentName(MockSeaAction.class.getName()));
        assertEquals("mock_land_mockLandAction", convention.fromClassNameToComponentName(MockLandAction.class.getName()));
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
        assertEquals(Srl.initUncap(getClass().getSimpleName()), convention.fromClassNameToComponentName(getClass().getName()));
        assertEquals("mockBizRootLogic", convention.fromClassNameToComponentName(MockBizRootLogic.class.getName()));
        assertEquals("mockCleanArcInteractor", convention.fromClassNameToComponentName(MockCleanArcInteractor.class.getName()));
    }

    // -----------------------------------------------------
    //                                ComponentName to Class
    //                                ----------------------

    // -----------------------------------------------------
    //                               ComponentName to Suffix
    //                               -----------------------

    // -----------------------------------------------------
    //                      ComponentName to PartOfClassName
    //                      --------------------------------

    // -----------------------------------------------------
    //                                    Path to ActionName
    //                                    ------------------

    // -----------------------------------------------------
    //                                    ActionName to Path
    //                                    ------------------

    // ===================================================================================
    //                                                            Interface Implementation
    //                                                            ========================

    // ===================================================================================
    //                                                                      Complete Class
    //                                                                      ==============

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    private StyledNamingConvention createConvention() {
        StyledNamingConvention convention = new StyledNamingConvention();
        convention.initialize();
        convention.addRootPackageName("org.lastaflute.di.naming.mockapp");
        return convention;
    }
}
