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
package org.lastaflute.di.naming;

/**
 * @author modified by jflute (originated in Seasar)
 */
public interface NamingConvention {

    // ===================================================================================
    //                                                          Root Package Determination
    //                                                          ==========================
    boolean isTargetClassName(String className, String suffix);

    boolean isTargetClassName(String className);

    boolean isHotdeployTargetClassName(String className);

    boolean isIgnoreClassName(String className);

    // ===================================================================================
    //                                                                        Convert from
    //                                                                        ============
    String fromSuffixToPackageName(String suffix);

    String fromClassNameToShortComponentName(String className);

    String fromClassNameToComponentName(String className);

    Class<?> fromComponentNameToClass(String componentName);

    String fromComponentNameToPartOfClassName(String componentName);

    String fromComponentNameToSuffix(String componentName); // e.g. seaAction to action

    String fromClassNameToSuffix(String className);

    String fromPathToActionName(String path);

    String fromActionNameToPath(String actionName);

    // ===================================================================================
    //                                                            Interface Implementation
    //                                                            ========================
    String toImplementationClassName(String className);

    String toInterfaceClassName(String className);

    boolean isSkipClass(Class<?> clazz);

    Class<?> toCompleteClass(Class<?> clazz);

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                      Component Suffix
    //                                      ----------------
    String getActionSuffix();

    String getFormSuffix();

    String getAssistSuffix();

    String getLogicSuffix();

    String getServiceSuffix();

    String getRepositorySuffix();

    String getHelperSuffix();

    String getInterceptorSuffix();

    String getValidatorSuffix();

    String getConverterSuffix();

    String getJobSuffix();

    String getImplementationSuffix();

    // -----------------------------------------------------
    //                                          Package Name
    //                                          ------------
    String getLogicPackageName();

    String getServicePackageName();

    String getRepositoryPackageName();

    String getHelperPackageName();

    String getInterceptorPackageName();

    String getValidatorPackageName();

    String getConverterPackageName();

    String getImplementationPackageName();

    // -----------------------------------------------------
    //                                             View Root
    //                                             ---------
    String getViewExtension();

    String getViewRootPath();

    String adjustViewRootPath(); // e.g. "/" => "", "/sea" => "/sea"

    // -----------------------------------------------------
    //                                          Root Package
    //                                          ------------
    String[] getRootPackageNames(); // not null

    String[] getIgnorePackageNames(); // not null

    String getSubApplicationRootPackageName(); // not null basically 'web' for compatible

    String getWebRootPackageName(); // not null basically 'web'

    String getJobRootPackageName(); // not null basically 'job'
}
