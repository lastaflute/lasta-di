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
package org.lastaflute.di.naming;

/**
 * @author modified by jflute (originated in Seasar)
 */
public interface NamingConvention {

    // ===================================================================================
    //                                                                 Class Determination
    //                                                                 ===================
    boolean isTargetClassName(String className, String suffix); // can be injected?

    boolean isTargetClassName(String className); // (actually) under root packages?

    boolean isHotdeployTargetClassName(String className);

    boolean isIgnoreClassName(String className);

    // ===================================================================================
    //                                                                    Component Naming
    //                                                                    ================
    String fromSuffixToPackageName(String suffix); // e.g. Logic to logic, not null

    String fromClassNameToShortComponentName(String className); // e.g. ...app.logic.maihama.SeaLogic to seaLogic, not null

    String fromClassNameToComponentName(String className); // e.g. SeaAction.class to sea_seaAction, not null

    Class<?> fromComponentNameToClass(String componentName); // e.g. sea_seaAction to SeaAction.class, null allowed when non DI

    String fromComponentNameToSuffix(String componentName); // e.g. seaAction to Action, null allowed when no upper case

    String fromClassNameToSuffix(String className); // e.g. ...SeaAction to Action, null allowed when no upper case

    String fromComponentNameToPartOfClassName(String componentName); // e.g. sea_seaAction to sea.SeaAction, not null

    // ===================================================================================
    //                                                                    View Path Action
    //                                                                    ================
    // for e.g. Thymeleaf
    String fromPathToActionName(String path); // e.g. /view/sea/land_piari.html to sea_land_piariAction, not null

    String fromActionNameToPath(String actionName); // e.g. sea_land_piariAction to /view/sea/land/piari.html, not null

    // ===================================================================================
    //                                                            Interface Implementation
    //                                                            ========================
    String toImplementationClassName(String className); // e.g. ...SeaImpl for ...Sea, not null

    String toInterfaceClassName(String className); // e.g. ...Sea for ...SeaImpl, not null

    boolean isSkipClass(Class<?> clazz); // meaning manual mapping implementation class

    // ===================================================================================
    //                                                                      Complete Class
    //                                                                      ==============
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
    // fromSuffixToPackageName() is used instead of these methods, needed? 
    String getLogicPackageName();

    String getServicePackageName();

    String getRepositoryPackageName();

    String getHelperPackageName();

    String getInterceptorPackageName();

    String getValidatorPackageName();

    String getConverterPackageName();

    // only used
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
