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
package org.lastaflute.di.naming;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lastaflute.di.Disposable;
import org.lastaflute.di.DisposableUtil;
import org.lastaflute.di.core.LastaDiProperties;
import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.util.LdiArrayUtil;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiMapUtil;
import org.lastaflute.di.util.LdiResourceUtil;
import org.lastaflute.di.util.LdiResourcesUtil;
import org.lastaflute.di.util.LdiStringUtil;
import org.lastaflute.di.util.LdiResourcesUtil.Resources;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class StyledNamingConvention implements NamingConvention, Disposable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final char PACKAGE_SEPARATOR = '_';
    protected static final String PACKAGE_SEPARATOR_STR = "_";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean initialized;
    protected String actionSuffix = "Action";
    protected String formSuffix = "Form";
    protected String serviceSuffix = "Service";
    protected String logicSuffix = "Logic";
    protected String assistSuffix = "Assist";
    protected String helperSuffix = "Helper";
    protected String interceptorSuffix = "Interceptor";
    protected String validatorSuffix = "Validator";
    protected String converterSuffix = "Converter";
    protected String implementationSuffix = "Impl";
    protected String viewRootPath = "/view";
    protected String viewExtension = ".html";

    protected String[] rootPackageNames = new String[0];
    protected String subApplicationRootPackageName = "web";
    protected String[] ignorePackageNames = new String[0];

    protected final Set<String> hotdeployRootPackageNames = new HashSet<String>(4);
    protected final Map<String, Resources[]> existCheckerArrays = LdiMapUtil.createHashMap();
    protected final Map<String, String> interfaceToImplementationMap = new HashMap<String, String>(4);
    protected final Map<String, String> implementationToInterfaceMap = new HashMap<String, String>(4);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public StyledNamingConvention() {
        initialize();
        setupPropertiesSmartPackage();
    }

    protected void setupPropertiesSmartPackage() {
        LastaDiProperties.getInstance().getSmartPackageList().forEach(pkg -> {
            addRootPackageName(pkg);
        });
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    public void initialize() {
        if (!initialized) {
            for (int i = 0; i < rootPackageNames.length; ++i) {
                addExistChecker(rootPackageNames[i]);
            }
            DisposableUtil.add(this);
            initialized = true;
        }
    }

    // ===================================================================================
    //                                                           Root Package Registration
    //                                                           =========================
    public void addRootPackageName(final String rootPackageName) {
        addRootPackageName(rootPackageName, true);
    }

    public void addRootPackageName(final String rootPackageName, final boolean hotdeploy) {
        rootPackageNames = (String[]) LdiArrayUtil.add(rootPackageNames, rootPackageName);
        if (hotdeploy) {
            hotdeployRootPackageNames.add(rootPackageName);
        }
        addExistChecker(rootPackageName);
    }

    public void addIgnorePackageName(final String ignorePackageName) {
        ignorePackageNames = (String[]) LdiArrayUtil.add(ignorePackageNames, ignorePackageName);
    }

    public void addInterfaceToImplementationClassName(final String interfaceName, final String implementationClassName) {
        interfaceToImplementationMap.put(interfaceName, implementationClassName);
        implementationToInterfaceMap.put(implementationClassName, interfaceName);
    }

    // ===================================================================================
    //                                                          Root Package Determination
    //                                                          ==========================
    @Override
    public boolean isTargetClassName(final String className, final String suffix) {
        if (isIgnoreClassName(className)) {
            return false;
        }
        if (!LdiStringUtil.trimSuffix(className, implementationSuffix).endsWith(suffix)) {
            return false;
        }
        final String shortClassName = LdiClassUtil.getShortClassName(className);
        if (className.endsWith(implementationSuffix) && !className.endsWith("." + getImplementationPackageName() + "." + shortClassName)) {
            return false;
        }
        final String middlePkgName = fromSuffixToPackageName(suffix);
        for (int i = 0; i < rootPackageNames.length; ++i) {
            if (className.startsWith(rootPackageNames[i] + "." + middlePkgName + ".")) {
                return true;
            }
            if (className.startsWith(rootPackageNames[i] + "." + subApplicationRootPackageName + ".")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isTargetClassName(final String className) {
        if (isIgnoreClassName(className)) {
            return false;
        }
        for (int i = 0; i < rootPackageNames.length; ++i) {
            if (className.startsWith(rootPackageNames[i] + ".")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isHotdeployTargetClassName(final String className) {
        if (isIgnoreClassName(className)) {
            return false;
        }
        for (int i = 0; i < rootPackageNames.length; ++i) {
            if (className.startsWith(rootPackageNames[i] + ".")) {
                return hotdeployRootPackageNames.contains(rootPackageNames[i]);
            }
        }
        return false;
    }

    @Override
    public boolean isIgnoreClassName(final String className) {
        for (int i = 0; i < ignorePackageNames.length; ++i) {
            if (className.startsWith(ignorePackageNames[i] + ".")) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                        Convert from
    //                                                                        ============
    @Override
    public String fromSuffixToPackageName(final String suffix) {
        if (LdiStringUtil.isEmpty(suffix)) {
            throw new EmptyRuntimeException("suffix");
        }
        return suffix.toLowerCase();
    }

    @Override
    public String fromClassNameToShortComponentName(final String className) {
        if (LdiStringUtil.isEmpty(className)) {
            throw new EmptyRuntimeException("className");
        }
        String s = LdiStringUtil.decapitalize(LdiClassUtil.getShortClassName(className));
        if (s.endsWith(implementationSuffix)) {
            return s.substring(0, s.length() - implementationSuffix.length());
        }
        return s;
    }

    @Override
    public String fromClassNameToComponentName(final String className) {
        if (LdiStringUtil.isEmpty(className)) {
            throw new EmptyRuntimeException("className");
        }
        String cname = toInterfaceClassName(className);
        String suffix = fromClassNameToSuffix(cname);
        String middlePackageName = fromSuffixToPackageName(suffix);
        String name = null;
        for (int i = 0; i < rootPackageNames.length; ++i) {
            String prefix = rootPackageNames[i] + "." + middlePackageName + ".";
            if (cname.startsWith(prefix)) {
                name = cname.substring(prefix.length());
            }
        }
        if (LdiStringUtil.isEmpty(name)) {
            for (int i = 0; i < rootPackageNames.length; ++i) {
                String prefix = rootPackageNames[i] + "." + subApplicationRootPackageName + ".";
                if (cname.startsWith(prefix)) {
                    name = cname.substring(prefix.length());
                }
            }
            if (LdiStringUtil.isEmpty(name)) {
                return fromClassNameToShortComponentName(className);
            }
        }
        String[] array = LdiStringUtil.split(name, ".");
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if (i == array.length - 1) {
                buf.append(LdiStringUtil.decapitalize(array[i]));
            } else {
                buf.append(array[i]);
                buf.append('_');
            }
        }
        return buf.toString();
    }

    @Override
    public Class<?> fromComponentNameToClass(final String componentName) {
        if (LdiStringUtil.isEmpty(componentName)) {
            throw new EmptyRuntimeException("componentName");
        }
        String suffix = fromComponentNameToSuffix(componentName);
        if (suffix == null) {
            return null;
        }
        String middlePackageName = fromSuffixToPackageName(suffix);
        String partOfClassName = fromComponentNameToPartOfClassName(componentName);
        boolean subAppSuffix = isSubApplicationSuffix(suffix);
        for (int i = 0; i < rootPackageNames.length; ++i) {
            String rootPackageName = rootPackageNames[i];
            if (subAppSuffix) {
                Class<?> clazz = findClass(rootPackageName, subApplicationRootPackageName, partOfClassName);
                if (clazz != null) {
                    return clazz;
                }
                clazz = findClass(rootPackageName, middlePackageName, partOfClassName);
                if (clazz != null) {
                    return clazz;
                }
            } else {
                Class<?> clazz = findClass(rootPackageName, middlePackageName, partOfClassName);
                if (clazz != null) {
                    return clazz;
                }
                clazz = findClass(rootPackageName, subApplicationRootPackageName, partOfClassName);
                if (clazz != null) {
                    return clazz;
                }
            }
        }
        return null;
    }

    protected boolean isSubApplicationSuffix(final String suffix) {
        if (actionSuffix.equals(suffix)) {
            return true;
        }
        if (serviceSuffix.equals(suffix)) {
            return true;
        }
        return false;
    }

    protected Class<?> findClass(final String rootPackageName, final String middlePackageName, final String partOfClassName) {
        initialize();

        final String backPartOfClassName = LdiClassUtil.concatName(middlePackageName, partOfClassName);
        final String className = LdiClassUtil.concatName(rootPackageName, backPartOfClassName);
        final String backPartOfImplClassName = toImplementationClassName(backPartOfClassName);
        final String implClassName = LdiClassUtil.concatName(rootPackageName, backPartOfImplClassName);

        if (!isIgnoreClassName(implClassName) && isExist(rootPackageName, backPartOfImplClassName)) {
            return LdiClassUtil.forName(implClassName);
        }

        if (!isIgnoreClassName(className) && isExist(rootPackageName, backPartOfClassName)) {
            return LdiClassUtil.forName(className);
        }
        return null;
    }

    @Override
    public String fromComponentNameToSuffix(final String componentName) {
        return fromNameToSuffix(componentName);
    }

    @Override
    public String fromClassNameToSuffix(final String componentName) {
        return fromNameToSuffix(componentName);
    }

    @Override
    public String fromComponentNameToPartOfClassName(final String componentName) {
        if (componentName == null) {
            throw new EmptyRuntimeException("componentName");
        }
        String[] names = LdiStringUtil.split(componentName, PACKAGE_SEPARATOR_STR);
        StringBuffer buf = new StringBuffer(50);
        for (int i = 0; i < names.length; ++i) {
            if (i == names.length - 1) {
                buf.append(LdiStringUtil.capitalize(names[i]));
            } else {
                buf.append(names[i]).append(".");
            }
        }
        return buf.toString();
    }

    protected String fromNameToSuffix(final String name) {
        if (LdiStringUtil.isEmpty(name)) {
            throw new EmptyRuntimeException("name");
        }
        for (int i = name.length() - 1; i >= 0; --i) {
            if (Character.isUpperCase(name.charAt(i))) {
                return name.substring(i);
            }
        }
        return null;
    }

    protected String fromPathToComponentName(final String path, final String nameSuffix) {
        if (!path.startsWith(viewRootPath) || !path.endsWith(viewExtension)) {
            throw new IllegalArgumentException(path);
        }
        String componentName =
                (path.substring(adjustViewRootPath().length() + 1, path.length() - viewExtension.length()) + nameSuffix).replace('/', '_');
        int pos = componentName.lastIndexOf('_');
        if (pos == -1) {
            return LdiStringUtil.decapitalize(componentName);
        }
        return componentName.substring(0, pos + 1) + LdiStringUtil.decapitalize(componentName.substring(pos + 1));
    }

    @Override
    public String fromPathToActionName(final String path) {
        return fromPathToComponentName(path, actionSuffix);
    }

    @Override
    public String fromActionNameToPath(final String actionName) {
        if (!actionName.endsWith(actionSuffix)) {
            throw new IllegalArgumentException(actionName);
        }
        String name = actionName.substring(0, actionName.length() - actionSuffix.length());
        return adjustViewRootPath() + "/" + name.replace(PACKAGE_SEPARATOR, '/') + viewExtension;
    }

    // ===================================================================================
    //                                                            Interface Implementation
    //                                                            ========================
    @Override
    public String toImplementationClassName(final String className) {
        final String implementationClassName = interfaceToImplementationMap.get(className);
        if (implementationClassName != null) {
            return implementationClassName;
        }
        final int index = className.lastIndexOf('.');
        if (index < 0) {
            return getImplementationPackageName() + "." + className + implementationSuffix;
        }
        return className.substring(0, index) + "." + getImplementationPackageName() + "." + className.substring(index + 1)
                + implementationSuffix;
    }

    @Override
    public String toInterfaceClassName(final String className) {
        String interfaceClassName = (String) implementationToInterfaceMap.get(className);
        if (interfaceClassName != null) {
            return interfaceClassName;
        }
        if (!className.endsWith(implementationSuffix)) {
            return className;
        }
        String key = "." + getImplementationPackageName() + ".";
        int index = className.lastIndexOf(key);
        if (index < 0) {
            throw new IllegalArgumentException(className);
        }
        return className.substring(0, index) + "."
                + className.substring(index + key.length(), className.length() - implementationSuffix.length());
    }

    @Override
    public boolean isSkipClass(final Class<?> clazz) {
        if (clazz.isInterface()) {
            return false;
        }
        for (final Iterator<Entry<String, String>> it = interfaceToImplementationMap.entrySet().iterator(); it.hasNext();) {
            final Entry<String, String> entry = it.next();
            final Class<?> interfaceClass = LdiClassUtil.forName((String) entry.getKey());
            if (interfaceClass.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class<?> toCompleteClass(final Class<?> clazz) {
        if (!clazz.isInterface()) {
            return clazz;
        }
        String className = toImplementationClassName(clazz.getName());
        if (LdiResourceUtil.isExist(LdiClassUtil.getResourcePath(className))) {
            return LdiClassUtil.forName(className);
        }
        return clazz;
    }

    // ===================================================================================
    //                                                                   Existence Checker
    //                                                                   =================
    protected boolean isExist(String rootPackageName, String lastClassName) {
        final Resources[] checkerArray = getExistCheckerArray(rootPackageName);
        for (int i = 0; i < checkerArray.length; ++i) {
            if (checkerArray[i].isExistClass(lastClassName)) {
                return true;
            }
        }
        return false;
    }

    protected Resources[] getExistCheckerArray(final String rootPackageName) {
        return (Resources[]) existCheckerArrays.get(rootPackageName);
    }

    protected void addExistChecker(final String rootPackageName) {
        final Resources[] checkerArray = LdiResourcesUtil.getResourcesTypes(rootPackageName);
        existCheckerArrays.put(rootPackageName, checkerArray);
    }

    // ===================================================================================
    //                                                                             Dispose
    //                                                                             =======
    public void dispose() {
        for (final Iterator<Resources[]> it = existCheckerArrays.values().iterator(); it.hasNext();) {
            final Resources[] array = it.next();
            for (int i = 0; i < array.length; ++i) {
                array[i].close();
            }
        }
        existCheckerArrays.clear();
        initialized = false;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                      Component Suffix
    //                                      ----------------
    @Override
    public String getActionSuffix() {
        return actionSuffix;
    }

    public void setActionSuffix(final String actionSuffix) {
        this.actionSuffix = actionSuffix;
    }

    @Override
    public String getFormSuffix() {
        return formSuffix;
    }

    public void setFormSuffix(final String formSuffix) {
        this.formSuffix = formSuffix;
    }

    @Override
    public String getAssistSuffix() {
        return assistSuffix;
    }

    public void setAssistSuffix(final String assistSuffix) {
        this.assistSuffix = assistSuffix;
    }

    @Override
    public String getHelperSuffix() {
        return helperSuffix;
    }

    public void setHelperSuffix(final String helperSuffix) {
        this.helperSuffix = helperSuffix;
    }

    @Override
    public String getInterceptorSuffix() {
        return interceptorSuffix;
    }

    public void setInterceptorSuffix(final String interceptorSuffix) {
        this.interceptorSuffix = interceptorSuffix;
    }

    @Override
    public String getLogicSuffix() {
        return logicSuffix;
    }

    public void setLogicSuffix(final String logicSuffix) {
        this.logicSuffix = logicSuffix;
    }

    @Override
    public String getServiceSuffix() {
        return serviceSuffix;
    }

    public void setServiceSuffix(final String serviceSuffix) {
        this.serviceSuffix = serviceSuffix;
    }

    @Override
    public String getValidatorSuffix() {
        return validatorSuffix;
    }

    public void setValidatorSuffix(final String validatorSuffix) {
        this.validatorSuffix = validatorSuffix;
    }

    @Override
    public String getConverterSuffix() {
        return converterSuffix;
    }

    public void setConverterSuffix(final String converterSuffix) {
        this.converterSuffix = converterSuffix;
    }

    @Override
    public String getImplementationSuffix() {
        return implementationSuffix;
    }

    public void setImplementationSuffix(final String implementationSuffix) {
        this.implementationSuffix = implementationSuffix;
    }

    // -----------------------------------------------------
    //                                          Package Name
    //                                          ------------
    @Override
    public String getLogicPackageName() {
        return fromSuffixToPackageName(logicSuffix);
    }

    @Override
    public String getHelperPackageName() {
        return fromSuffixToPackageName(helperSuffix);
    }

    @Override
    public String getInterceptorPackageName() {
        return fromSuffixToPackageName(interceptorSuffix);
    }

    @Override
    public String getServicePackageName() {
        return fromSuffixToPackageName(serviceSuffix);
    }

    @Override
    public String getValidatorPackageName() {
        return fromSuffixToPackageName(validatorSuffix);
    }

    @Override
    public String getConverterPackageName() {
        return fromSuffixToPackageName(converterSuffix);
    }

    @Override
    public String getImplementationPackageName() {
        return fromSuffixToPackageName(implementationSuffix);
    }

    // -----------------------------------------------------
    //                                             View Root
    //                                             ---------
    @Override
    public String getViewExtension() {
        return viewExtension;
    }

    public void setViewExtension(final String viewExtension) {
        this.viewExtension = viewExtension;
    }

    @Override
    public String getViewRootPath() {
        return viewRootPath;
    }

    public void setViewRootPath(final String viewRootPath) {
        this.viewRootPath = viewRootPath;
    }

    @Override
    public String adjustViewRootPath() {
        return "/".equals(viewRootPath) ? "" : viewRootPath;
    }

    // -----------------------------------------------------
    //                                          Root Package
    //                                          ------------
    @Override
    public String[] getRootPackageNames() {
        return rootPackageNames;
    }

    @Override
    public String[] getIgnorePackageNames() {
        return ignorePackageNames;
    }

    @Override
    public String getSubApplicationRootPackageName() {
        return subApplicationRootPackageName;
    }

    public void setSubApplicationRootPackageName(final String subApplicationRootPackageName) {
        this.subApplicationRootPackageName = subApplicationRootPackageName;
    }
}
