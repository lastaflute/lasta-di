package org.lastaflute.di.naming;

import org.lastaflute.di.mockapp.biz.MockBizRootLogic;
import org.lastaflute.di.mockapp.logic.MockSeaLogic;
import org.lastaflute.di.mockapp.logic.nearstation.MockBonvoLogic;
import org.lastaflute.di.mockapp.logic.nearstation.MockPiariLogic;
import org.lastaflute.di.mockapp.logic.nearstation.impl.MockBonvoLogicImpl;
import org.lastaflute.di.mockapp.nondi.MockNondiSeaLogic;
import org.lastaflute.di.mockapp.web.MockSeaAction;
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
        assertTrue(convention.isTargetClassName(MockSeaLogic.class.getName(), "Logic"));
        assertTrue(convention.isTargetClassName(MockPiariLogic.class.getName(), "Logic"));
        assertTrue(convention.isTargetClassName(MockBonvoLogic.class.getName(), "Logic"));
        assertTrue(convention.isTargetClassName(MockBonvoLogicImpl.class.getName(), "Logic"));
        assertFalse(convention.isTargetClassName(MockNondiSeaLogic.class.getName(), "Logic"));
        assertFalse(convention.isTargetClassName(getClass().getName(), "Test"));
        assertFalse(convention.isTargetClassName(MockBizRootLogic.class.getName(), "Logic"));
    }

    public void test_isTargetClassName_by_classNameOnly() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertTrue(convention.isTargetClassName(MockSeaAction.class.getName()));
        assertTrue(convention.isTargetClassName(MockSeaLogic.class.getName()));
        assertTrue(convention.isTargetClassName(MockPiariLogic.class.getName()));
        assertTrue(convention.isTargetClassName(MockBonvoLogic.class.getName()));
        assertTrue(convention.isTargetClassName(MockBonvoLogicImpl.class.getName()));
        assertTrue(convention.isTargetClassName(MockNondiSeaLogic.class.getName()));
        assertFalse(convention.isTargetClassName(getClass().getName()));
        assertTrue(convention.isTargetClassName(MockBizRootLogic.class.getName()));
    }

    // ===================================================================================
    //                                                                     Convert from-to
    //                                                                     ===============
    // -----------------------------------------------------
    //                                 Suffix to PackageName
    //                                 ---------------------
    public void test_fromSuffixToPackageName_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals("logic", convention.fromSuffixToPackageName("Logic"));
        assertEquals("service", convention.fromSuffixToPackageName("Service"));
        assertEquals("test", convention.fromSuffixToPackageName("Test")); // non DI suffix
    }

    // -----------------------------------------------------
    //                       ClassName to ShortComponentName
    //                       -------------------------------
    public void test_fromClassNameToShortComponentName_basic() throws Exception {
        StyledNamingConvention convention = createConvention();
        assertEquals("mockSeaAction", convention.fromClassNameToShortComponentName(MockSeaAction.class.getName()));
        assertEquals("mockSeaLogic", convention.fromClassNameToShortComponentName(MockSeaLogic.class.getName()));
        assertEquals("mockPiariLogic", convention.fromClassNameToShortComponentName(MockPiariLogic.class.getName()));
        assertEquals("mockBonvoLogic", convention.fromClassNameToShortComponentName(MockBonvoLogic.class.getName()));
        assertEquals("mockBonvoLogic", convention.fromClassNameToShortComponentName(MockBonvoLogicImpl.class.getName()));
    }

    // -----------------------------------------------------
    //                            ClassName to ComponentName
    //                            --------------------------

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
