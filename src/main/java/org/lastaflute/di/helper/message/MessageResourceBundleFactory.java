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
package org.lastaflute.di.helper.message;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.lastaflute.di.Disposable;
import org.lastaflute.di.DisposableUtil;
import org.lastaflute.di.exception.ResourceNotFoundException;
import org.lastaflute.di.exception.ResourceNotFoundRuntimeException;
import org.lastaflute.di.util.LdiAssertionUtil;
import org.lastaflute.di.util.LdiResourceUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class MessageResourceBundleFactory {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final String PROPERTIES_EXT = ".properties";
    private static final Object NOT_FOUND = new Object();
    private static final Map<String, Object> cacheMap = new HashMap<String, Object>();
    private static boolean initialized = false;

    // ===================================================================================
    //                                                                          Get Bundle
    //                                                                          ==========
    public static MessageResourceBundle getBundle(String baseName) {
        return getBundle(baseName, Locale.getDefault());
    }

    public static MessageResourceBundle getBundle(String baseName, Locale locale) throws ResourceNotFoundRuntimeException {
        final MessageResourceBundle bundle = getNullableBundle(baseName, locale);
        if (bundle != null) {
            return bundle;
        }
        throw new ResourceNotFoundException("Not found the resource bundle: " + baseName + " " + locale);
    }

    // -----------------------------------------------------
    //                                      Null-able Bundle
    //                                      ----------------
    protected static MessageResourceBundle getNullableBundle(String baseName) {
        return getNullableBundle(baseName, Locale.getDefault());
    }

    protected static MessageResourceBundle getNullableBundle(String baseName, Locale locale) {
        LdiAssertionUtil.assertNotNull("baseName", baseName);
        LdiAssertionUtil.assertNotNull("locale", locale);

        final String base = baseName.replace('.', '/');
        final String[] bundleNames = calculateBundleNames(base, locale);
        MessageResourceBundleFacade parentFacade = null;
        MessageResourceBundleFacade facade = null;
        final int length = bundleNames.length;
        for (int i = 0; i < length; ++i) {
            facade = loadFacade(bundleNames[i] + PROPERTIES_EXT);
            if (parentFacade == null) {
                parentFacade = facade;
            } else if (facade != null) {
                facade.setParent(parentFacade);
                parentFacade = facade;
            }
        }

        if (parentFacade != null) {
            return parentFacade.getBundle();
        } else {
            return null;
        }
    }

    // ===================================================================================
    //                                                              Calculate Bundle Names
    //                                                              ======================
    protected static String[] calculateBundleNames(String baseName, Locale locale) {
        int length = 1;
        boolean hasLanguage = locale.getLanguage().length() > 0;
        if (hasLanguage) {
            length++;
        }
        boolean hasCountry = locale.getCountry().length() > 0;
        if (hasCountry) {
            length++;
        }
        boolean hasVariant = locale.getVariant().length() > 0;
        if (hasVariant) {
            length++;
        }
        String[] result = new String[length];
        int index = 0;
        result[index++] = baseName;

        if (!(hasLanguage || hasCountry || hasVariant)) {
            return result;
        }

        StringBuffer buffer = new StringBuffer(baseName);
        buffer.append('_');
        buffer.append(locale.getLanguage());
        if (hasLanguage) {
            result[index++] = new String(buffer);
        }

        if (!(hasCountry || hasVariant)) {
            return result;
        }
        buffer.append('_');
        buffer.append(locale.getCountry());
        if (hasCountry) {
            result[index++] = new String(buffer);
        }

        if (!hasVariant) {
            return result;
        }
        buffer.append('_');
        buffer.append(locale.getVariant());
        result[index++] = new String(buffer);

        return result;
    }

    // ===================================================================================
    //                                                                         Load Facade
    //                                                                         ===========
    protected static MessageResourceBundleFacade loadFacade(String path) {
        synchronized (cacheMap) {
            if (!initialized) {
                DisposableUtil.add(new Disposable() {
                    public void dispose() {
                        clear();
                        initialized = false;
                    }
                });
                initialized = true;
            }
            final Object cachedFacade = cacheMap.get(path);
            if (cachedFacade == NOT_FOUND) {
                return null;
            } else if (cachedFacade != null) {
                return (MessageResourceBundleFacade) cachedFacade;
            }
            final URL url = LdiResourceUtil.getResourceNoException(path);
            if (url != null) {
                final MessageResourceBundleFacade facade = new MessageResourceBundleFacade(url);
                cacheMap.put(path, facade);
                return facade;
            } else {
                cacheMap.put(path, NOT_FOUND);
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                                         Clear Cache
    //                                                                         ===========
    public static void clear() {
        cacheMap.clear();
    }
}
