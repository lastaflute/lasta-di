/*
 * Copyright 2015-2018 the original author or authors.
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
package org.lastaflute.di.core.expression.dwarf;

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.meta.impl.LaContainerImpl;
import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 */
public class SimpleExpressionPlainHookTest extends UnitLastaDiTestCase {

    public void test_resolveSimpleNumber_basic() {
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        Map<String, ? extends Object> contextMap = new HashMap<>();

        // ## Act ##
        // ## Assert ##
        assertEquals(1, hook.resolveSimpleNumber("1", contextMap, createContainer(), Object.class));
        assertEquals(123, hook.resolveSimpleNumber("123", contextMap, createContainer(), Object.class));
        assertEquals(12345678, hook.resolveSimpleNumber("12345678", contextMap, createContainer(), Object.class));
        assertEquals(123456789, hook.resolveSimpleNumber("123456789", contextMap, createContainer(), Object.class));
        assertEquals(1234567890L, hook.resolveSimpleNumber("1234567890", contextMap, createContainer(), Object.class));
        assertEquals(2222222222L, hook.resolveSimpleNumber("2222222222", contextMap, createContainer(), Object.class));
        assertEquals(12345678901L, hook.resolveSimpleNumber("12345678901", contextMap, createContainer(), Object.class));
        assertEquals(99999999999L, hook.resolveSimpleNumber("99999999999", contextMap, createContainer(), Object.class));
    }

    private LaContainer createContainer() {
        return new LaContainerImpl();
    }
}
