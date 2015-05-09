package org.lastaflute.di.core;

import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 */
public class LastaDiPropertiesTest extends UnitLastaDiTestCase {

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
