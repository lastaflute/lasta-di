/*
 * Copyright 2015-2022 the original author or authors.
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
package org.lastaflute.di.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 */
public class ArrayMapTest extends UnitLastaDiTestCase {

    public void test_basic() throws Exception {
        // ## Arrange ##
        ArrayMap<String, Integer> map = new ArrayMap<String, Integer>();

        // ## Act ##
        map.put("sea", 1);
        map.put("land", 2);

        // ## Assert ##
        log(map);
        assertEquals(Integer.valueOf(1), map.get(0));
        assertEquals(Integer.valueOf(2), map.get(1));
        assertEquals(Integer.valueOf(1), map.get("sea"));
        assertEquals(Integer.valueOf(2), map.get("land"));
        assertEquals(Arrays.asList("sea", "land"), new ArrayList<String>(map.keySet()));

        // ## Act ##
        map.remove("sea");

        // ## Assert ##
        assertNull(map.get("sea"));
        assertEquals(Integer.valueOf(2), map.get("land"));
        boolean exists = false;
        for (Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            log(key, value);
            assertEquals("land", key);
            assertEquals(Integer.valueOf(2), value);
            exists = true;
        }
        assertTrue(exists);
    }
}
