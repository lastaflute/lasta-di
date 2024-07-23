package org.lastaflute.di.core.aop.javassist;

import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 */
public class AbstractGeneratorTest extends UnitLastaDiTestCase {

    // ===================================================================================
    //                                                                       MethodHandles
    //                                                                       =============
    public void test_prepareMethodHandlesPrivateLookupInMethod_java21() {
        assertNotNull(AbstractGenerator.prepareMethodHandlesPrivateLookupInMethod());
    }

    public void test_prepareMethodHandlesLookupDefineClassMethod_java21() {
        assertNotNull(AbstractGenerator.prepareMethodHandlesLookupDefineClassMethod());
    }
}
