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
package org.lastaflute.di.core.smart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jflute
 */
public enum SmartDeployMode {

    COOL("cool"), HOT("hot"), WARM("warm");

    private static final Logger logger = LoggerFactory.getLogger(SmartDeployMode.class);
    private static SmartDeployMode value;

    private final String code;

    private SmartDeployMode(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static SmartDeployMode codeOf(String code) {
        if (code == null) {
            return COOL;
        }
        for (SmartDeployMode mode : values()) {
            if (mode.code().equalsIgnoreCase(code)) {
                return mode;
            }
        }
        throw new IllegalStateException("Unknown code of smart deploy mode: " + code);
    }

    public static boolean isCool() {
        return SmartDeployMode.COOL.equals(value);
    }

    public static SmartDeployMode getValue() {
        return value != null ? value : SmartDeployMode.COOL;
    }

    public static void setValue(SmartDeployMode newValue) { // called by e.g. container factory, unit test
        logger.info("...Setting smart deploy mode: {}", newValue);
        value = newValue;
    }

    @Override
    public String toString() {
        return code;
    }
}
