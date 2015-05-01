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
package org.dbflute.lasta.di.core.customizer.ext;

import org.dbflute.lasta.di.core.aop.Pointcut;
import org.dbflute.lasta.di.core.customizer.AspectCustomizer;
import org.dbflute.lasta.di.core.customizer.PublicBasisPointcut;

/**
 * @author jflute
 */
public class ConcreteDrivenAspectCustomizer extends AspectCustomizer {

    @Override
    protected Pointcut createPointcut() {
        final Pointcut pointcut = super.createPointcut();
        return pointcut != null ? pointcut : createDefaultPointcut();
    }

    protected Pointcut createDefaultPointcut() {
        return new PublicBasisPointcut();
    }
}
