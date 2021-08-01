/*
 * Copyright 2015-2021 the original author or authors.
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
    // can it be injected?
    boolean isTargetClassName(String className, String suffix); // used by creator's byType

    // (actually) under root packages?
    boolean isTargetClassName(String className); // used by cool deploy

    // to separate injection from hotdeploy
    boolean isHotdeployTargetClassName(String className); // used by hot deploy

    // completely out of convention style?
    boolean isIgnoreClassName(String className); // used by this

    // ===================================================================================
    //                                                                    Component Naming
    //                                                                    ================
    // e.g. Logic to logic, not null
    String fromSuffixToPackageName(String suffix); // used by this

    // e.g. SeaAction.class to sea_seaAction, BonvoLogic(Impl) to nearstation_bonvoLogic, not null
    String fromClassNameToComponentName(String className); // used by creator's component name

    // e.g. ...app.logic.maihama.SeaLogic to seaLogic, not null
    String fromClassNameToShortComponentName(String className); // used by this

    // e.g. SeaAction.class to SeaAction.class, BonvoLogic.class to BonvoLogicImpl.class
    Class<?> toCompleteClass(Class<?> clazz); // fromClassToComponentClass(), used by creator's byType

    // e.g. sea_seaAction to SeaAction.class, null allowed when non DI
    Class<?> fromComponentNameToClass(String componentName); // used by creator's byName

    // e.g. seaAction to Action, null allowed when no upper case
    String fromComponentNameToSuffix(String componentName); // used by this

    // e.g. ...SeaAction to Action, null allowed when no upper case
    String fromClassNameToSuffix(String className); // used by this

    // e.g. sea_seaAction to sea.SeaAction, not null
    String fromComponentNameToPartOfClassName(String componentName); // used by this

    // ===================================================================================
    //                                                                    View Path Action
    //                                                                    ================
    // e.g. /view/sea/land_piari.html to sea_land_piariAction, not null
    String fromPathToActionName(String path); // for e.g. Thymeleaf

    // e.g. sea_land_piariAction to /view/sea/land/piari.html, not null
    String fromActionNameToPath(String actionName); // me too

    // ===================================================================================
    //                                                            Interface Implementation
    //                                                            ========================
    // e.g. ...SeaImpl for ...Sea, not null
    String toImplementationClassName(String className); // used by this

    // e.g. ...Sea for ...SeaImpl, not null
    String toInterfaceClassName(String className); // used by this

    // meaning manual mapping implementation class
    boolean isSkipClass(Class<?> clazz); // used by cool deploy

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
