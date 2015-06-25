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
package org.lastaflute.di.core.deployer;

/**
 * @author modified by jflute (originated in Seasar)
 * @author belltree
 */
public interface ComponentDeployer {

    /**
     * @return 
     * 
     * @see org.lastaflute.di.core.deployer.SingletonComponentDeployer#deploy()
     * @see org.lastaflute.di.core.deployer.PrototypeComponentDeployer#deploy()
     * @see org.lastaflute.di.core.deployer.ApplicationComponentDeployer#deploy()
     * @see org.lastaflute.di.core.deployer.RequestComponentDeployer#deploy()
     * @see org.lastaflute.di.core.deployer.SessionComponentDeployer#deploy()
     */
    public Object deploy();

    /**
     * @param outerComponent
     * 
     * @see org.lastaflute.di.core.deployer.OuterComponentDeployer#injectDependency(Object)
     */
    public void injectDependency(Object outerComponent);

    /**
     * @see org.lastaflute.di.core.deployer.SingletonComponentDeployer#init()
     * @see org.lastaflute.di.core.assembler.DefaultInitMethodAssembler#assemble(Object)
     */
    public void init();

    /**
     * @see org.lastaflute.di.core.deployer.SingletonComponentDeployer#destroy()
     * @see org.lastaflute.di.core.assembler.DefaultDestroyMethodAssembler#assemble(Object)
     */
    public void destroy();
}
