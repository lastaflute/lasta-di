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
package org.lastaflute.di.core.meta;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.assembler.ConstructorAssembler;
import org.lastaflute.di.core.assembler.PropertyAssembler;

/**
 * @author modified by jflute (originated in Seasar)
 */
public interface AutoBindingDef {

    String AUTO_NAME = "auto";
    String SEMIAUTO_NAME = "semiauto";
    String CONSTRUCTOR_NAME = "constructor";
    String PROPERTY_NAME = "property";
    String NONE_NAME = "none";

    String getName();

    ConstructorAssembler createConstructorAssembler(ComponentDef componentDef);

    PropertyAssembler createPropertyAssembler(ComponentDef componentDef);
}
