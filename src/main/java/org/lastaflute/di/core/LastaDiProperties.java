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
package org.lastaflute.di.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiSrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jflute
 */
public class LastaDiProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LoggerFactory.getLogger(LastaDiProperties.class);

    public static final String LASTA_DI_PROPERTIES = "lasta_di.properties";
    public static final String SMART_DEPLOY_MODE_LOCATION_KEY = "smart.deploy.mode.location";
    public static final String SMART_PACKAGE1_KEY = "smart.package1";
    public static final String SMART_PACKAGE2_KEY = "smart.package2";
    public static final String SMART_PACKAGE3_KEY = "smart.package3";
    public static final String PLAIN_PROPERTY_INJECTION_PACKAGE1_KEY = "plain.property.injection.package1";
    public static final String DIXML_SCRIPT_EXPRESSION_ENGINE_KEY = "dixml.script.expression.engine";
    public static final String DIXML_SCRIPT_MANAGED_ENGINE_NAME_KEY = "dixml.script.managed.engine.name";
    public static final String INTERNAL_DEBUG_KEY = "internal.debug";
    public static final String SUPPRESS_LASTA_ENV_KEY = "suppress.lasta.env";
    public static final String LASTA_ENV = "lasta.env"; // system property

    private static LastaDiProperties instance; // lazy loaded

    public static LastaDiProperties getInstance() {
        if (instance == null) {
            synchronized (LastaDiProperties.class) {
                if (instance == null) {
                    instance = new LastaDiProperties();
                }
            }
        }
        return instance;
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Properties props;
    protected final boolean internalDebug;
    protected final boolean suppressLastaEnv; // ignoring lasta.env forcedly for e.g. emergency debug
    // memorable code (not needed, GCP can use lasta.env (has dot property key))
    //protected final boolean useNodotLastaEnv; // uses 'lastaenv' (no dot) for e.g. GCP
    protected String smartDeployMode; // load loaded
    protected boolean smartDeployLocationDone;
    protected List<String> smartPackageList; // load loaded
    protected Class<?> diXmlScriptExpressionEngineType; // load loaded
    protected boolean diXmlScriptExpressionEngineTypeDone;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected LastaDiProperties() {
        final String propName = LASTA_DI_PROPERTIES;
        final Properties read = loadProperties(propName);
        if (read != null) {
            props = read;
        } else {
            logger.info("*Not found the {} in your classpath.", propName);
            props = new Properties();
        }
        internalDebug = isProperty(INTERNAL_DEBUG_KEY);
        if (internalDebug) {
            logger.info("Lasta Di as Internal Debug by {}", INTERNAL_DEBUG_KEY);
        }
        suppressLastaEnv = isProperty(SUPPRESS_LASTA_ENV_KEY);
        if (suppressLastaEnv) {
            logger.info("Lasta Di suppresses lasta.env by {}", SUPPRESS_LASTA_ENV_KEY);
        }
    }

    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    public String getProperty(String propertyKey) {
        return props.getProperty(propertyKey);
    }

    public String getProperty(String propertyKey, String defaultValue) {
        return props.getProperty(propertyKey, defaultValue);
    }

    public boolean isProperty(String propertyKey) {
        return getProperty(propertyKey, "false").equalsIgnoreCase("true");
    }

    // -----------------------------------------------------
    //                                          Smart Deploy
    //                                          ------------
    public String getSmartDeployMode() {
        if (smartDeployMode != null || smartDeployLocationDone) {
            return smartDeployMode;
        }
        synchronized (this) {
            if (smartDeployMode != null || smartDeployLocationDone) {
                return smartDeployMode;
            }
            final String locKey = SMART_DEPLOY_MODE_LOCATION_KEY;
            final String location = getProperty(locKey);
            if (location != null && !location.isEmpty()) {
                smartDeployMode = deriveSmartDeployModeFromLocation(location);
            } else {
                logger.info("*Not found the smart-deploy mode location: {} in {}", locKey, LASTA_DI_PROPERTIES);
            }
            smartDeployLocationDone = true;
            return smartDeployMode;
        }
    }

    protected String deriveSmartDeployModeFromLocation(String location) {
        final String delimiter = ":";
        final int delimiterIndex = location.indexOf(":");
        if (delimiterIndex < 0) {
            String msg = "The location should have delimiter colon ':' in " + LASTA_DI_PROPERTIES + " but: " + location;
            throw new IllegalStateException(msg);
        }
        // e.g.
        //  maihana_env.properties: lasta_di.smart.deploy.mode
        //  orleans_env.properties extends maihana_env.properties: lasta_di.smart.deploy.mode
        final String modeKey = location.substring(delimiterIndex + delimiter.length()).trim(); // e.g. lasta_di.smart.deploy.mode
        final String propExp = location.substring(0, delimiterIndex).trim(); // e.g. maihana_env.properties
        final List<String> propList = LdiSrl.splitListTrimmed(propExp, " extends ");
        String realMode = null;
        for (String propName : propList) { // e.g. [orleans_env.properties, maihana_env.properties]
            final String resolvedName = resolveLastaEnvPath(propName); // e.g. maihana_env_production.properties
            logger.info("...Loading specified properties and get by the key: {}, {}", resolvedName, modeKey);
            final Properties read = loadProperties(resolvedName);
            if (read == null) {
                throwSmartDeployPropertiesFileNotFoundException(location, resolvedName, modeKey);
            }
            realMode = read.getProperty(modeKey);
            if (realMode != null) {
                break;
            }
        }
        if (realMode == null) {
            throwSmartDeployPropertiesModeKeyNotFoundException(location, propExp, modeKey);
        }
        return realMode;
    }

    protected void throwSmartDeployPropertiesFileNotFoundException(String location, String propName, String modeKey) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Not found the properties for smart deploy.");
        br.addItem("Advice");
        br.addElement("Make sure your properties or location setting.");
        br.addElement("  (x): at your " + LASTA_DI_PROPERTIES);
        br.addElement("    " + SMART_DEPLOY_MODE_LOCATION_KEY + " = no_exist.properties: lasta_di.smart.deploy.mode");
        br.addElement("");
        br.addElement("  (o): at your " + LASTA_DI_PROPERTIES + ", e.g. maihama project");
        br.addElement("    " + SMART_DEPLOY_MODE_LOCATION_KEY + " = maihama_env.properties: lasta_di.smart.deploy.mode");
        br.addItem("Location");
        br.addElement(location);
        br.addItem("NotFound File");
        br.addElement(propName);
        br.addItem("Deploy Mode Key");
        br.addElement(modeKey);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    protected void throwSmartDeployPropertiesModeKeyNotFoundException(String location, String propName, String modeKey) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Not found the property in your smart deploy properties.");
        br.addItem("Advice");
        br.addElement("Make sure your property key setting.");
        br.addElement("  (x): at your " + LASTA_DI_PROPERTIES + ", e.g. maihama project");
        br.addElement("    " + SMART_DEPLOY_MODE_LOCATION_KEY + " = maihama_env.properties: no_exist.smart.deploy.mode");
        br.addElement("");
        br.addElement("  (o): at your " + LASTA_DI_PROPERTIES + ", e.g. maihama project");
        br.addElement("    " + SMART_DEPLOY_MODE_LOCATION_KEY + " = maihama_env.properties: lasta_di.smart.deploy.mode");
        br.addElement("");
        br.addElement("  (o): at your maihama_env.properties, e.g. maihama project");
        br.addElement("    lasta_di.smart.deploy.mode = hot");
        br.addItem("Location");
        br.addElement(location);
        br.addItem("Properties File");
        br.addElement(propName);
        br.addItem("NotFound Key");
        br.addElement(modeKey);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    // -----------------------------------------------------
    //                                         Smart Package
    //                                         -------------
    public List<String> getSmartPackageList() {
        if (smartPackageList != null) {
            return smartPackageList;
        }
        synchronized (this) {
            if (smartPackageList != null) {
                return smartPackageList;
            }
            final List<String> pkgList = new ArrayList<String>(3);
            pkgList.add(getSmartPackage1());
            pkgList.add(getSmartPackage2());
            pkgList.add(getSmartPackage3());
            smartPackageList = pkgList.stream().filter(pkg -> pkg != null).collect(Collectors.toList());
            return Collections.unmodifiableList(smartPackageList);
        }
    }

    public String getSmartPackage1() {
        return getProperty(SMART_PACKAGE1_KEY);
    }

    public String getSmartPackage2() {
        return getProperty(SMART_PACKAGE2_KEY);
    }

    public String getSmartPackage3() {
        return getProperty(SMART_PACKAGE3_KEY);
    }

    // -----------------------------------------------------
    //                                         Di XML Script
    //                                         -------------
    public String getDiXmlScriptExpressionEngine() {
        return getProperty(DIXML_SCRIPT_EXPRESSION_ENGINE_KEY);
    }

    public Class<?> getDiXmlScriptExpressionEngineType() { // null allowed
        if (diXmlScriptExpressionEngineType == null && !diXmlScriptExpressionEngineTypeDone) {
            synchronized (this) {
                if (diXmlScriptExpressionEngineType == null) {
                    final String engineName = getDiXmlScriptExpressionEngine();
                    if (engineName != null) {
                        diXmlScriptExpressionEngineType = forNameScriptExpressionEngineType(engineName);
                    }
                    diXmlScriptExpressionEngineTypeDone = true;
                }
            }
        }
        return diXmlScriptExpressionEngineType;
    }

    protected Class<?> forNameScriptExpressionEngineType(String engineName) {
        try {
            return LdiClassUtil.forName(engineName);
        } catch (RuntimeException e) {
            LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Failed to find the type of script expression engine.");
            br.addItem("Advice");
            br.addElement("Confirm your engine type in " + LASTA_DI_PROPERTIES + ".");
            br.addElement("The property key of engine type is " + DIXML_SCRIPT_EXPRESSION_ENGINE_KEY + ".");
            br.addItem("Specifyed Engine");
            br.addElement(engineName);
            final String msg = br.buildExceptionMessage();
            throw new IllegalStateException(msg, e);
        }
    }

    public String getDiXmlScriptManagedEngineName() {
        return getProperty(DIXML_SCRIPT_MANAGED_ENGINE_NAME_KEY);
    }

    // -----------------------------------------------------
    //                              Plain Property Injection
    //                              ------------------------
    public String getPlainPropertyInjectionPackage1() { // e.g. for S2Robot's DBFlute
        return getProperty(PLAIN_PROPERTY_INJECTION_PACKAGE1_KEY);
    }

    // -----------------------------------------------------
    //                                        Internal Debug
    //                                        --------------
    public boolean isInternalDebug() {
        return internalDebug;
    }

    // ===================================================================================
    //                                                                  System Environment
    //                                                                  ==================
    public String resolveLastaEnvPath(String envPath) {
        if (envPath == null) {
            throw new IllegalArgumentException("The argument 'envPath' should not be null.");
        }
        final String lastaEnv = getLastaEnv();
        if (lastaEnv != null && envPath.contains("_env.")) { // e.g. maihama_env.properties to maihama_env_prod.properties
            final String front = LdiSrl.substringLastFront(envPath, "_env.");
            final String rear = LdiSrl.substringLastRear(envPath, "_env.");
            return front + "_env_" + lastaEnv + "." + rear;
        } else {
            return envPath;
        }
    }

    public String getLastaEnv() { // null allowed
        if (isSuppressLastaEnv()) {
            return null;
        }
        return System.getProperty(LASTA_ENV);
    }

    // -----------------------------------------------------
    //                                       LastaEnv Option
    //                                       ---------------
    public boolean isSuppressLastaEnv() {
        return suppressLastaEnv;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected Properties loadProperties(String fileName) {
        final Properties props = new Properties();
        final InputStream ins = LastaDiProperties.class.getClassLoader().getResourceAsStream(fileName);
        if (ins == null) {
            return null;
        }
        try {
            props.load(ins);
        } catch (RuntimeException | IOException e) {
            handleLoadingFailureException(fileName, e);
        } finally {
            try {
                ins.close();
            } catch (IOException ignored) {}
        }
        mergePropIfExists(fileName, props);
        return props;
    }

    protected void mergePropIfExists(String originalFileName, Properties props) {
        final String ext = ".properties";
        if (originalFileName.endsWith(ext)) {
            final int extIndex = originalFileName.lastIndexOf(ext);
            final String noExtName = originalFileName.substring(0, extIndex);
            final String mergeFileName = noExtName + "+m" + ext; // e.g. lasta_di+m.properties
            final Properties mergeProp = loadProperties(mergeFileName);
            if (mergeProp != null) {
                props.putAll(mergeProp);
            }
        }
    }

    protected void handleLoadingFailureException(String fileName, Exception e) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to load the properties.");
        br.addItem("Properties File");
        br.addElement(fileName);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg, e);
    }
}
