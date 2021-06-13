/*
 * Copyright 2015-2020 the original author or authors.
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
package org.lastaflute.di.util.tiger;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author jflute
 */
public class LdiGenericUtilTest extends PlainTestCase {

    public void test_getGenericFirstClass_returnType_basic() throws Exception {
        // ## Arrange ##
        Method declaredMethod = Sea.class.getDeclaredMethod("dockside", (Class<?>[]) null);
        Type genericReturnType = declaredMethod.getGenericReturnType();

        // ## Act ##
        // ## Assert ##
        log("return: exp={}", genericReturnType);
        assertEquals(String.class, LdiGenericUtil.getGenericFirstClass(genericReturnType));
        assertEquals(Integer.class, LdiGenericUtil.getGenericSecondClass(genericReturnType));

        Type[] genericParameterTypes = LdiGenericUtil.getGenericParameterTypes(genericReturnType);
        assertEquals(2, genericParameterTypes.length);
        Type firstParameterType = genericParameterTypes[0];
        log("firstParameterType: type={}", firstParameterType.getClass());
        log("firstParameterType: exp={}", firstParameterType.toString());
        assertEquals("class java.lang.String", firstParameterType.toString());

        Type[] nestedTypes = LdiGenericUtil.getGenericParameterTypes(firstParameterType);
        assertEquals(0, nestedTypes.length);
    }

    public void test_getGenericFirstClass_returnType_nestedList() throws Exception {
        // ## Arrange ##
        Method declaredMethod = Sea.class.getDeclaredMethod("hangarList", (Class<?>[]) null);
        Type genericReturnType = declaredMethod.getGenericReturnType();

        // ## Act ##
        // ## Assert ##
        log("return: exp={}", genericReturnType);
        assertEquals(List.class, LdiGenericUtil.getGenericFirstClass(genericReturnType));
        assertNull(LdiGenericUtil.getGenericSecondClass(genericReturnType));

        Type[] genericParameterTypes = LdiGenericUtil.getGenericParameterTypes(genericReturnType);
        assertEquals(1, genericParameterTypes.length);
        Type firstParameterType = genericParameterTypes[0];
        log("firstParameterType: type={}", firstParameterType.getClass());
        log("firstParameterType: exp={}", firstParameterType.toString());
        assertEquals("java.util.List<java.lang.String>", firstParameterType.toString());
        assertEquals(String.class, LdiGenericUtil.getGenericFirstClass(firstParameterType));
        assertNull(LdiGenericUtil.getGenericSecondClass(firstParameterType));

        Type[] nestedTypes = LdiGenericUtil.getGenericParameterTypes(firstParameterType);
        assertEquals(1, nestedTypes.length);
    }

    public void test_getGenericFirstClass_returnType_nestedMap() throws Exception {
        // ## Arrange ##
        Method declaredMethod = Sea.class.getDeclaredMethod("hangarMap", (Class<?>[]) null);
        Type genericReturnType = declaredMethod.getGenericReturnType();

        // ## Act ##
        // ## Assert ##
        log("return: exp={}", genericReturnType);
        assertEquals(Map.class, LdiGenericUtil.getGenericFirstClass(genericReturnType));
        assertNull(LdiGenericUtil.getGenericSecondClass(genericReturnType));

        Type[] genericParameterTypes = LdiGenericUtil.getGenericParameterTypes(genericReturnType);
        assertEquals(1, genericParameterTypes.length);
        Type firstParameterType = genericParameterTypes[0];
        log("firstParameterType: type={}", firstParameterType.getClass());
        log("firstParameterType: exp={}", firstParameterType.toString());
        assertEquals("java.util.Map<java.lang.String, java.lang.Integer>", firstParameterType.toString());
        assertEquals(String.class, LdiGenericUtil.getGenericFirstClass(firstParameterType));
        assertEquals(Integer.class, LdiGenericUtil.getGenericSecondClass(firstParameterType));

        Type[] nestedTypes = LdiGenericUtil.getGenericParameterTypes(firstParameterType);
        assertEquals(2, nestedTypes.length);
    }

    public static class Sea {
        public Dockside<String, Integer> dockside() {
            return null;
        }

        public Hangar<List<String>> hangarList() {
            return null;
        }

        public Hangar<Map<String, Integer>> hangarMap() {
            return null;
        }
    }

    public static class Dockside<A, B> {
    }

    public static class Hangar<A> {
    }
}
