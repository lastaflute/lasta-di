/*
 * Copyright 2014-2015 the original author or authors.
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
package org.dbflute.lasta.di.redefiner.core;

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.assembler.AssemblerFactory.DefaultProvider;
import org.dbflute.lasta.di.core.assembler.ConstructorAssembler;
import org.dbflute.lasta.di.core.assembler.MethodAssembler;
import org.dbflute.lasta.di.core.assembler.PropertyAssembler;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class ClassAssemblerProvider extends DefaultProvider {

    @Override
    public ConstructorAssembler createAutoConstructorAssembler(ComponentDef cd) {
        return new ClassAutoConstructorAssembler(cd);
    }

    @Override
    public PropertyAssembler createAutoPropertyAssembler(ComponentDef cd) {
        return new ClassAutoPropertyAssembler(cd);
    }

    @Override
    public MethodAssembler createInitMethodAssembler(ComponentDef cd) {
        return new ClassInitMethodAssembler(cd);
    }

    @Override
    public MethodAssembler createDestroyMethodAssembler(ComponentDef cd) {
        return new ClassDestroyMethodAssembler(cd);
    }
}
