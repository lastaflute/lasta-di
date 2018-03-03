/*
 * Copyright 2015-2018 the original author or authors.
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
package org.lastaflute.di.core.assembler;

import org.lastaflute.di.core.ComponentDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AssemblerFactory {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private static Provider provider = new DefaultProvider();

    // ===================================================================================
    //                                                                    Create Assembler
    //                                                                    ================
    public static MethodAssembler createInitMethodAssembler(final ComponentDef cd) {
        return getProvider().createInitMethodAssembler(cd);
    }

    public static MethodAssembler createDestroyMethodAssembler(final ComponentDef cd) {
        return getProvider().createDestroyMethodAssembler(cd);
    }

    public static ConstructorAssembler createAutoConstructorAssembler(final ComponentDef cd) {
        return getProvider().createAutoConstructorAssembler(cd);
    }

    public static ConstructorAssembler createDefaultConstructorConstructorAssembler(final ComponentDef cd) {
        return getProvider().createDefaultConstructorConstructorAssembler(cd);
    }

    public static PropertyAssembler createAutoPropertyAssembler(final ComponentDef cd) {
        return getProvider().createAutoPropertyAssembler(cd);
    }

    public static PropertyAssembler createManualOnlyPropertyAssembler(final ComponentDef cd) {
        return getProvider().createManualOnlyPropertyAssembler(cd);
    }

    public static PropertyAssembler createSemiAutoPropertyAssembler(final ComponentDef cd) {
        return getProvider().createSemiAutoPropertyAssembler(cd);
    }

    // ===================================================================================
    //                                                                 Provider Definition
    //                                                                 ===================
    public interface Provider {

        MethodAssembler createInitMethodAssembler(ComponentDef cd);

        MethodAssembler createDestroyMethodAssembler(ComponentDef cd);

        ConstructorAssembler createAutoConstructorAssembler(ComponentDef cd);

        ConstructorAssembler createDefaultConstructorConstructorAssembler(ComponentDef cd);

        PropertyAssembler createAutoPropertyAssembler(ComponentDef cd);

        PropertyAssembler createManualOnlyPropertyAssembler(ComponentDef cd);

        PropertyAssembler createSemiAutoPropertyAssembler(ComponentDef cd);
    }

    public static class DefaultProvider implements Provider {

        public MethodAssembler createInitMethodAssembler(final ComponentDef cd) {
            return new DefaultInitMethodAssembler(cd);
        }

        public MethodAssembler createDestroyMethodAssembler(final ComponentDef cd) {
            return new DefaultDestroyMethodAssembler(cd);
        }

        public ConstructorAssembler createAutoConstructorAssembler(final ComponentDef cd) {
            return new AutoConstructorAssembler(cd);
        }

        public ConstructorAssembler createDefaultConstructorConstructorAssembler(final ComponentDef cd) {
            return new DefaultConstructorConstructorAssembler(cd);
        }

        public PropertyAssembler createAutoPropertyAssembler(final ComponentDef cd) {
            return new AutoPropertyAssembler(cd);
        }

        public PropertyAssembler createManualOnlyPropertyAssembler(final ComponentDef cd) {
            return new ManualOnlyPropertyAssembler(cd);
        }

        public PropertyAssembler createSemiAutoPropertyAssembler(final ComponentDef cd) {
            return new SemiAutoPropertyAssembler(cd);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public static Provider getProvider() {
        return provider;
    }

    public static void setProvider(final Provider p) {
        provider = p;
    }
}
