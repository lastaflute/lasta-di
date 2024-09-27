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
package org.lastaflute.di.core.expression.dwarf;

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.LastaDiProperties;
import org.lastaflute.di.core.meta.impl.LaContainerImpl;
import org.lastaflute.di.unit.UnitLastaDiTestCase;
import org.lastaflute.jta.core.LaTransaction;
import org.lastaflute.jta.helper.timer.LjtTimeoutManager;

/**
 * @author jflute
 */
public class SimpleExpressionPlainHookTest extends UnitLastaDiTestCase {

    // ===================================================================================
    //                                                                      Basic Handling
    //                                                                      ==============
    public void test_hookPlainly_simpleString_basic() {
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String exp = "\"sea\"";
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createContainer();
        Class<?> resultType = Object.class; // unused

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        assertNotNull(result);
        assertTrue(result instanceof String);
        assertEquals("sea", result);
    }

    // ===================================================================================
    //                                                                          Simple new
    //                                                                          ==========
    public void test_hookPlainly_simpleNew_basic() {
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String exp = "new org.lastaflute.jta.core.LaTransaction()";
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createContainer();
        Class<?> resultType = Object.class; // unused

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        assertNotNull(result);
        assertTrue(result instanceof LaTransaction);
    }

    public void test_hookPlainly_simpleNew_doubleQuoted() {
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String exp = "\"new org.lastaflute.jta.core.LaTransaction()\"";
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createContainer();
        Class<?> resultType = Object.class; // unused

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        assertNotNull(result);
        assertTrue(result instanceof LaTransaction);
    }

    // ===================================================================================
    //                                                                         Simple Type
    //                                                                         ===========
    public void test_hookPlainly_simpleType_class_basic() {
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String exp = "@org.lastaflute.jta.helper.timer.LjtTimeoutManager@class";
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createContainer();
        Class<?> resultType = Object.class; // unused

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        assertNotNull(result);
        assertTrue(LjtTimeoutManager.class.equals(result));
    }

    public void test_hookPlainly_simpleType_field_basic() {
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String exp = "@org.lastaflute.di.core.LastaDiProperties@LASTA_DI_PROPERTIES";
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createContainer();
        Class<?> resultType = Object.class; // unused

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        assertNotNull(result);
        assertTrue(result instanceof String);
        assertEquals(LastaDiProperties.LASTA_DI_PROPERTIES, result);
    }

    public void test_hookPlainly_simpleType_method_basic() {
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String exp = "@org.lastaflute.jta.helper.timer.LjtTimeoutManager@getInstance()";
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createContainer();
        Class<?> resultType = Object.class; // unused

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        assertNotNull(result);
        assertTrue(result instanceof LjtTimeoutManager);
    }

    // ===================================================================================
    //                                                                     Provider Config
    //                                                                     ===============
    // -----------------------------------------------------
    //                                          Config get()
    //                                          ------------
    public void test_hookPlainly_providerConfig_get() {
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String exp = "provider.config().getJdbcUrl()";
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createProviderContainer(new MyMockProvider(false));
        Class<?> resultType = String.class;

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        assertNotNull(result);
        assertTrue(result instanceof String);
        assertEquals(MyMockConfig.JDBC_URL, (String) result);
    }

    // -----------------------------------------------------
    //                                 Config getOrDefault()
    //                                 ---------------------
    public void test_hookPlainly_providerConfig_getOrDefault_null() {
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String exp = "provider.config().getOrDefault(\"jdbc.url\", null)";
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createProviderContainer(new MyMockProvider(false));
        Class<?> resultType = String.class;

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        assertNotNull(result);
        assertEquals(ExpressionPlainHook.NULL_RETURN, result);
    }
    // #for_now jflute unsupported if valid default value and no space before comma for now (2024/09/23)

    // -----------------------------------------------------
    //                                  Config Determination
    //                                  --------------------
    public void test_hookPlainly_providerConfig_isDetermination() { // since 1.0.0
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String exp = "provider.config().isDevelopmentHere()";
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createProviderContainer(new MyMockProvider(true));
        Class<?> resultType = Boolean.class;

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        assertNotNull(result);
        assertTrue(result instanceof Boolean);
        assertTrue((Boolean) result);
    }

    // ===================================================================================
    //                                                                        Hatena Colon
    //                                                                        ============
    public void test_hookPlainly_hatenaColon_false() { // since 1.0.0
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String determinationExp = "provider.config().isDevelopmentHere()";
        String firstSelectionExp = "new org.lastaflute.jta.core.LaTransaction()";
        String secondSelectionExp = "@org.lastaflute.jta.helper.timer.LjtTimeoutManager@getInstance()";
        String exp = determinationExp + " ? " + firstSelectionExp + " : " + secondSelectionExp;
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createProviderContainer(new MyMockProvider(false));
        Class<?> resultType = Object.class; // unused

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        log("result: {}", result);
        assertNotNull(result);
        assertTrue(result instanceof LjtTimeoutManager);
    }

    public void test_hookPlainly_hatenaColon_true() { // since 1.0.0
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String determinationExp = "provider.config().isDevelopmentHere()";
        String firstSelectionExp = "new org.lastaflute.jta.core.LaTransaction()";
        String secondSelectionExp = "@org.lastaflute.jta.helper.timer.LjtTimeoutManager@getInstance()";
        String exp = determinationExp + " ? " + firstSelectionExp + " : " + secondSelectionExp;
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createProviderContainer(new MyMockProvider(true));
        Class<?> resultType = Object.class; // unused

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        log("result: {}", result);
        assertNotNull(result);
        assertTrue(result instanceof LaTransaction);
    }

    public void test_hookPlainly_hatenaColon_variousFormat() { // since 1.0.0
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();
        String determinationExp = "provider.config().isDevelopmentHere()";
        String firstSelectionExp = "provider.config().getJdbcUrl()";
        String secondSelectionExp = "@org.lastaflute.jta.helper.timer.LjtTimeoutManager@class";
        String exp = determinationExp + "\n    ? " + firstSelectionExp + "  \n: \n" + secondSelectionExp;
        Map<String, Object> contextMap = new HashMap<>();
        LaContainer container = createProviderContainer(new MyMockProvider(true));
        Class<?> resultType = Object.class; // unused

        // ## Act ##
        Object result = hook.hookPlainly(exp, contextMap, container, resultType);

        // ## Assert ##
        log("result: {}", result);
        assertNotNull(result);
        assertTrue(result instanceof String);
        assertEquals(MyMockConfig.JDBC_URL, result);
    }

    // ===================================================================================
    //                                                                       Detail Method
    //                                                                       =============
    public void test_resolveSimpleNumber_basic() {
        // ## Arrange ##
        SimpleExpressionPlainHook hook = new SimpleExpressionPlainHook();

        // ## Act ##
        // ## Assert ##
        assertEquals(1, hook.resolveSimpleNumber("1", createContainer(), Object.class));
        assertEquals(123, hook.resolveSimpleNumber("123", createContainer(), Object.class));
        assertEquals(12345678, hook.resolveSimpleNumber("12345678", createContainer(), Object.class));
        assertEquals(123456789, hook.resolveSimpleNumber("123456789", createContainer(), Object.class));
        assertEquals(1234567890L, hook.resolveSimpleNumber("1234567890", createContainer(), Object.class));
        assertEquals(2222222222L, hook.resolveSimpleNumber("2222222222", createContainer(), Object.class));
        assertEquals(12345678901L, hook.resolveSimpleNumber("12345678901", createContainer(), Object.class));
        assertEquals(99999999999L, hook.resolveSimpleNumber("99999999999", createContainer(), Object.class));
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    private LaContainer createContainer() {
        return new LaContainerImpl();
    }

    private LaContainer createContainer(Map<Object, Object> componentMap) {
        return new LaContainerImpl() {
            @Override
            public boolean hasComponentDef(Object componentKey) {
                Object mock = componentMap.get(componentKey);
                if (mock != null) {
                    return true;
                }
                return super.hasComponentDef(componentKey);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <COMPONENT> COMPONENT getComponent(Object componentKey) {
                Object mock = componentMap.get(componentKey);
                if (mock != null) {
                    return (COMPONENT) mock;
                }
                return super.getComponent(componentKey);
            }
        };
    }

    private LaContainer createProviderContainer(MyMockProvider provider) {
        Map<Object, Object> componentMap = new HashMap<>();
        componentMap.put("provider", provider);
        return createContainer(componentMap);
    }

    public static class MyMockProvider {

        private boolean developmentHere;

        public MyMockProvider(boolean developmentHere) {
            this.developmentHere = developmentHere;
        }

        public MyMockConfig config() {
            return new MyMockConfig(developmentHere);
        }
    }

    public static class MyMockConfig {

        public static final String JDBC_URL = "jdbc:mysql://localhost:3306/resortlinedb";

        private boolean developmentHere;

        public MyMockConfig(boolean developmentHere) {
            this.developmentHere = developmentHere;
        }

        public boolean isDevelopmentHere() {
            return developmentHere;
        }

        public String getJdbcUrl() {
            return JDBC_URL;
        }

        public String getOrDefault(String key, Object defaultValue) {
            if ("jdbc.url".equals(key)) {
                return (String) defaultValue; // always default
            }
            return null;
        }
    }

    public static class Sea {

        private final Integer number;

        public Sea(Integer number) {
            super();
            this.number = number;
        }

        public Integer getNumber() {
            return number;
        }
    }
}
