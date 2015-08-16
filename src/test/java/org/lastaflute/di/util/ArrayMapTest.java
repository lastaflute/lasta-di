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
