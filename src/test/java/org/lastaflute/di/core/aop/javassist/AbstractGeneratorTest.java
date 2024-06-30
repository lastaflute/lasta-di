package org.lastaflute.di.core.aop.javassist;

import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 */
public class AbstractGeneratorTest extends UnitLastaDiTestCase {

    // ===================================================================================
    //                                                                       MethodHandles
    //                                                                       =============
    public void test_prepareMethodHandlesPrivateLookupInMethod_java8() {
        assertNull(AbstractGenerator.prepareMethodHandlesPrivateLookupInMethod());
    }

    public void test_prepareMethodHandlesLookupDefineClassMethod_java8() {
        assertNull(AbstractGenerator.prepareMethodHandlesLookupDefineClassMethod());
    }
}
