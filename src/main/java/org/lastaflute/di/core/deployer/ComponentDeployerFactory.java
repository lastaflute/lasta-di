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

import org.lastaflute.di.core.ComponentDef;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ComponentDeployerFactory {

    private static Provider provider = new DefaultProvider();

    /**
     * @return
     */
    public static Provider getProvider() {
        return provider;
    }

    /**
     * @param p
     */
    public static void setProvider(final Provider p) {
        provider = p;
    }

    /**
     * @param cd
     * @return
     */
    public static ComponentDeployer createSingletonComponentDeployer(final ComponentDef cd) {
        return getProvider().createSingletonComponentDeployer(cd);
    }

    /**
     * @param cd
     * @return
     */
    public static ComponentDeployer createPrototypeComponentDeployer(final ComponentDef cd) {
        return getProvider().createPrototypeComponentDeployer(cd);
    }

    /**
     * @param cd
     * @return
     */
    public static ComponentDeployer createServletContextComponentDeployer(final ComponentDef cd) {
        return getProvider().createApplicationComponentDeployer(cd);
    }

    /**
     * @param cd
     * @return
     */
    public static ComponentDeployer createSessionComponentDeployer(final ComponentDef cd) {
        return getProvider().createSessionComponentDeployer(cd);
    }

    /**
     * @param cd
     * @return
     */
    public static ComponentDeployer createRequestComponentDeployer(final ComponentDef cd) {
        return getProvider().createRequestComponentDeployer(cd);
    }

    /**
     * @param cd
     * @return
     */
    public static ComponentDeployer createOuterComponentDeployer(final ComponentDef cd) {
        return getProvider().createOuterComponentDeployer(cd);
    }

    /**
     * @author koichk
     * 
     */
    public interface Provider {

        /**
         * @param cd
         * @return
         */
        ComponentDeployer createSingletonComponentDeployer(ComponentDef cd);

        /**
         * @param cd
         * @return
         */
        ComponentDeployer createPrototypeComponentDeployer(ComponentDef cd);

        /**
         * @param cd
         * @return
         */
        ComponentDeployer createApplicationComponentDeployer(ComponentDef cd);

        /**
         * @param cd
         * @return
         */
        ComponentDeployer createSessionComponentDeployer(ComponentDef cd);

        /**
         * @param cd
         * @return
         */
        ComponentDeployer createRequestComponentDeployer(ComponentDef cd);

        /**
         * @param cd
         * @return
         */
        ComponentDeployer createOuterComponentDeployer(ComponentDef cd);
    }

    /**
     * @author koichk
     * 
     */
    public static class DefaultProvider implements Provider {

        public ComponentDeployer createSingletonComponentDeployer(final ComponentDef cd) {
            return new SingletonComponentDeployer(cd);
        }

        public ComponentDeployer createPrototypeComponentDeployer(final ComponentDef cd) {
            return new PrototypeComponentDeployer(cd);
        }

        public ComponentDeployer createRequestComponentDeployer(final ComponentDef cd) {
            throw new UnsupportedOperationException("createRequestComponentDeployer");
        }

        public ComponentDeployer createSessionComponentDeployer(final ComponentDef cd) {
            throw new UnsupportedOperationException("createSessionComponentDeployer");
        }

        public ComponentDeployer createApplicationComponentDeployer(final ComponentDef cd) {
            throw new UnsupportedOperationException("createApplicationComponentDeployer");
        }

        public ComponentDeployer createOuterComponentDeployer(final ComponentDef cd) {
            return new OuterComponentDeployer(cd);
        }
    }
}