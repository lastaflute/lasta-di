/*
 * Copyright 2015-2016 the original author or authors.
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
    public static final String LASTA_DI_PROPERTIES = "lasta_di.properties";
    public static final String SMART_DEPLOY_MODE_LOCATION_KEY = "smart.deploy.mode.location";
    public static final String SMART_PACKAGE1_KEY = "smart.package1";
    public static final String SMART_PACKAGE2_KEY = "smart.package2";
    public static final String SMART_PACKAGE3_KEY = "smart.package3";
    public static final String PLAIN_PROPERTY_INJECTION_PACKAGE1_KEY = "plain.property.injection.package1";
    public static final String DIXML_SCRIPT_EXPRESSION_ENGINE_KEY = "dixml.script.expression.engine";
    public static final String INTERNAL_DEBUG_KEY = "internal.debug";
    public static final String SUPPRESS_LASTA_ENV_KEY = "suppress.lasta.env";
    public static final String LASTA_ENV = "lasta.env"; // system property

    private static final Logger logger = LoggerFactory.getLogger(LastaDiProperties.class);
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
    protected final boolean suppressLastaEnv;
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
        final String debugProp = getProperty(INTERNAL_DEBUG_KEY);
        internalDebug = debugProp != null && debugProp.equalsIgnoreCase("true");
        if (internalDebug) {
            logger.info("Lasta Di as Internal Debug by {}", INTERNAL_DEBUG_KEY);
        }
        final String suppressLastaEnvProp = getProperty(SUPPRESS_LASTA_ENV_KEY);
        suppressLastaEnv = suppressLastaEnvProp != null && suppressLastaEnvProp.equalsIgnoreCase("true");
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
                final String delimiter = ":";
                final int delimiterIndex = location.indexOf(":");
                if (delimiterIndex < 0) {
                    String msg = "The location should have delimiter colon ':' in " + LASTA_DI_PROPERTIES + " but: " + location;
                    throw new IllegalStateException(msg);
                }
                final String propName = resolveLastaEnvPath(location.substring(0, delimiterIndex).trim());
                final String modeKey = location.substring(delimiterIndex + delimiter.length()).trim();
                logger.info("...Loading specified properties and get by the key: {}, {}", propName, modeKey);
                final Properties read = loadProperties(propName);
                if (read == null) {
                    throwSmartDeployPropertiesFileNotFoundException(location, propName, modeKey);
                }
                final String realMode = read.getProperty(modeKey);
                if (realMode == null) {
                    throwSmartDeployPropertiesModeKeyNotFoundException(location, propName, modeKey);
                }
                smartDeployMode = realMode;
            } else {
                logger.info("*Not found the smart-deploy mode location: {} in {}", locKey, LASTA_DI_PROPERTIES);
            }
            smartDeployLocationDone = true;
            return smartDeployMode;
        }
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
                        // TODO jflute lastaflute: [E] fitting: DI :: expression engine creation error handling
                        diXmlScriptExpressionEngineType = LdiClassUtil.forName(engineName);
                    }
                    diXmlScriptExpressionEngineTypeDone = true;
                }
            }
        }
        return diXmlScriptExpressionEngineType;
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

    public boolean isSuppressLastaEnv() {
        return suppressLastaEnv;
    }

    public String getLastaEnv() { // null allowed
        if (isSuppressLastaEnv()) {
            return null;
        }
        return System.getProperty(LASTA_ENV);
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
        return props;
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
