package org.lastaflute.di.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 */
public class LastaDiPropertiesTest extends UnitLastaDiTestCase {

    // ===================================================================================
    //                                                                     SmartDeployMode
    //                                                                     ===============
    public void test_deriveSmartDeployModeFromLocation_basic() throws Exception {
        // ## Arrange ##
        Set<String> markSet = new HashSet<>();
        LastaDiProperties prop = new LastaDiProperties() {
            @Override
            protected Properties loadProperties(String fileName) {
                if (fileName.equals(LASTA_DI_PROPERTIES)) {
                    return new Properties();
                }
                assertEquals("maihama_env.properties", fileName);
                Properties prop = new Properties();
                prop.setProperty("sea", "mystic");
                markSet.add("called");
                return prop;
            }
        };

        // ## Act ##
        String derived = prop.deriveSmartDeployModeFromLocation("maihama_env.properties: sea");

        // ## Assert ##
        assertEquals("mystic", derived);
        assertFalse(markSet.isEmpty());
    }

    public void test_deriveSmartDeployModeFromLocation_extends_override() throws Exception {
        // ## Arrange ##
        Set<String> markSet = new HashSet<>();
        LastaDiProperties prop = new LastaDiProperties() {
            @Override
            protected Properties loadProperties(String fileName) {
                if (fileName.equals(LASTA_DI_PROPERTIES)) {
                    return new Properties();
                }
                Properties prop = new Properties();
                if (fileName.startsWith("maihama_env.")) {
                    fail();
                } else if (fileName.startsWith("orleans_env.")) {
                    prop.setProperty("sea", "over");
                    markSet.add("orleans");
                }
                return prop;
            }
        };

        // ## Act ##
        String derived = prop.deriveSmartDeployModeFromLocation("orleans_env.properties extends maihama_env.properties: sea");

        // ## Assert ##
        assertEquals("over", derived);
        assertFalse(markSet.isEmpty());
    }

    public void test_deriveSmartDeployModeFromLocation_extends_parentOnly_basic() throws Exception {
        // ## Arrange ##
        Set<String> markSet = new LinkedHashSet<>();
        LastaDiProperties prop = new LastaDiProperties() {
            @Override
            protected Properties loadProperties(String fileName) {
                if (fileName.equals(LASTA_DI_PROPERTIES)) {
                    return new Properties();
                }
                Properties prop = new Properties();
                if (fileName.startsWith("maihama_env.")) {
                    prop.setProperty("sea", "over");
                    markSet.add("maihama");
                } else if (fileName.startsWith("orleans_env.")) {
                    markSet.add("orleans");
                }
                return prop;
            }
        };

        // ## Act ##
        String derived = prop.deriveSmartDeployModeFromLocation("orleans_env.properties extends maihama_env.properties: sea");

        // ## Assert ##
        assertEquals("over", derived);
        assertFalse(markSet.isEmpty());
        Iterator<String> ite = markSet.iterator();
        assertEquals("orleans", ite.next());
        assertEquals("maihama", ite.next());
        assertFalse(ite.hasNext());
    }

    // ===================================================================================
    //                                                                        LastaEnvPath
    //                                                                        ============
    public void test_resolveLastaEnvPath_basic() throws Exception {
        // ## Arrange ##
        LastaDiProperties prop = new LastaDiProperties() {
            @Override
            public String getLastaEnv() {
                return "ut";
            }
        };

        // ## Act ##
        // ## Assert ##
        assertEquals("maihama_env_ut.properties", prop.resolveLastaEnvPath("maihama_env.properties"));
        assertEquals("maihama_env_ut.txt", prop.resolveLastaEnvPath("maihama_env.txt"));
        assertEquals("maihama_config.properties", prop.resolveLastaEnvPath("maihama_config.properties"));
    }

    public void test_resolveLastaEnvPath_non() throws Exception {
        // ## Arrange ##
        LastaDiProperties prop = new LastaDiProperties() {
            @Override
            public String getLastaEnv() {
                return null;
            }
        };

        // ## Act ##
        // ## Assert ##
        assertEquals("maihama_env.properties", prop.resolveLastaEnvPath("maihama_env.properties"));
    }
}
