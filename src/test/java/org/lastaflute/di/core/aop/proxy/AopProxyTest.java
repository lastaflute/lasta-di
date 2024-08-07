package org.lastaflute.di.core.aop.proxy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.lastaflute.di.core.aop.Aspect;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.aop.frame.MethodInvocation;
import org.lastaflute.di.core.aop.impl.AspectImpl;
import org.lastaflute.di.core.aop.impl.PointcutImpl;
import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 * @since 0.9.2 (2024/06/18 Tuesday at ichihara)
 */
public class AopProxyTest extends UnitLastaDiTestCase {

    // ===================================================================================
    //                                                                         Interceptor
    //                                                                         ===========
    public void test_create_interceptor_basic() throws Exception {
        // ## Arrange ##
        Set<String> markSet = new HashSet<>();
        Aspect aspect = new AspectImpl(new HangarInterceptor(markSet), new PointcutImpl(EnhancedSea.class));
        AopProxy aopProxy = new AopProxy(EnhancedSea.class, new Aspect[] { aspect });

        // ## Act ##
        EnhancedSea sea = (EnhancedSea) aopProxy.create();

        // ## Assert ##
        assertNotNull(sea);
        assertTrue(markSet.isEmpty());
        String result = sea.hangar();
        log(result, markSet);
        assertEquals("mystic", result);
        assertFalse(markSet.isEmpty());
        assertEquals(1, markSet.size());
        assertEquals("invocation hangar", markSet.iterator().next());
    }

    public void test_create_interceptor_multiple() throws Exception {
        // ## Arrange ##
        Set<String> markSet = new HashSet<>();
        Aspect aspectOne = new AspectImpl(new HangarInterceptor(markSet), new PointcutImpl(EnhancedSea.class));
        Aspect aspectTwo = new AspectImpl(new MagiclampInterceptor(markSet), new PointcutImpl(EnhancedSea.class));
        AopProxy aopProxy = new AopProxy(EnhancedSea.class, new Aspect[] { aspectOne, aspectTwo });

        // ## Act ##
        EnhancedSea sea = (EnhancedSea) aopProxy.create();

        // ## Assert ##
        assertNotNull(sea);
        assertTrue(markSet.isEmpty());
        String result = sea.hangar();
        log(result, markSet);
        assertEquals("mystic", result);
        assertFalse(markSet.isEmpty());
        assertEquals(2, markSet.size());
        Iterator<String> iterator = markSet.iterator();
        assertEquals("invocation hangar", iterator.next());
        assertEquals("invocation magiclamp", iterator.next());
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    public static class EnhancedSea {

        public String hangar() {
            return "mystic";
        }
    }

    public static class HangarInterceptor implements MethodInterceptor {

        protected final Set<String> markSet;

        public HangarInterceptor(Set<String> markSet) {
            this.markSet = markSet;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            markSet.add("invocation hangar");
            return invocation.proceed();
        }
    }

    public static class MagiclampInterceptor implements MethodInterceptor {

        protected final Set<String> markSet;

        public MagiclampInterceptor(Set<String> markSet) {
            this.markSet = markSet;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            markSet.add("invocation magiclamp");
            return invocation.proceed();
        }
    }
}
