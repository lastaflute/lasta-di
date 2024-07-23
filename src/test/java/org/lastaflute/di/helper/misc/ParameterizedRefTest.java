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
package org.lastaflute.di.helper.misc;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import org.lastaflute.di.unit.UnitLastaDiTestCase;

/**
 * @author jflute
 */
public class ParameterizedRefTest extends UnitLastaDiTestCase {

    public void test_getParameterizedType() throws Exception {
        ParameterizedRef<List<File>> ref = new ParameterizedRef<List<File>>() {
        };
        Type genericType = ref.getType().getActualTypeArguments()[0];
        log(genericType);
        assertEquals(File.class, genericType);
    }
}
