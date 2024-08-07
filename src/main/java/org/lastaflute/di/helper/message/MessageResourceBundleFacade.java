/*
 * Copyright 2015-2024 the original author or authors.
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

import org.lastaflute.di.core.smart.hot.HotdeployUtil;
import org.lastaflute.di.util.LdiAssertionUtil;
import org.lastaflute.di.util.LdiFileInputStreamUtil;
import org.lastaflute.di.util.LdiInputStreamUtil;
import org.lastaflute.di.util.LdiPropertiesUtil;
import org.lastaflute.di.util.LdiResourceUtil;
import org.lastaflute.di.util.LdiURLUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class MessageResourceBundleFacade {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected File file; // null allowed
    protected long lastModified;
    protected MessageResourceBundle bundle; // changeable
    protected MessageResourceBundleFacade parent; // null allowed

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public MessageResourceBundleFacade(URL url) {
        setup(url);
    }

    protected void setup(URL url) {
        LdiAssertionUtil.assertNotNull("url", url);
        if (HotdeployUtil.isHotdeploy()) {
            file = LdiResourceUtil.getFile(url);
        }
        if (file != null) {
            lastModified = file.lastModified();
            bundle = createBundle(file);
        } else {
            bundle = createBundle(url);
        }
        if (parent != null) {
            bundle.setParent(parent.getBundle());
        }
    }

    // ===================================================================================
    //                                                                          Get Bundle
    //                                                                          ==========
    public synchronized MessageResourceBundle getBundle() {
        if (isModified()) {
            bundle = createBundle(file);
        }
        if (parent != null) {
            bundle.setParent(parent.getBundle());
        }
        return bundle;
    }

    protected boolean isModified() {
        if (file != null && file.lastModified() > lastModified) {
            return true;
        }
        return false;
    }

    // ===================================================================================
    //                                                                             Factory
    //                                                                             =======
    protected MessageResourceBundle createBundle(File file) {
        return new MessageResourceBundleImpl(createProperties(file));
    }

    protected MessageResourceBundle createBundle(URL url) {
        return new MessageResourceBundleImpl(createProperties(url));
    }

    protected Properties createProperties(File file) {
        return createProperties(LdiFileInputStreamUtil.create(file));
    }

    protected Properties createProperties(URL url) {
        return createProperties(LdiURLUtil.openStream(url));
    }

    protected Properties createProperties(InputStream ins) {
        LdiAssertionUtil.assertNotNull("ins", ins);
        if (!(ins instanceof BufferedInputStream)) {
            ins = new BufferedInputStream(ins);
        }
        try {
            Properties prop = createProperties();
            loadProperties(prop, ins, getPropertiesEncoding());
            return prop;
        } finally {
            LdiInputStreamUtil.close(ins);
        }
    }

    protected Properties createProperties() {
        return new Properties();
    }

    protected String getPropertiesEncoding() {
        return "UTF-8"; // as default
    }

    protected void loadProperties(Properties properties, InputStream ins, String encoding) {
        try {
            LdiPropertiesUtil.load(properties, new BufferedReader(new InputStreamReader(ins, encoding)));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported encoding: " + encoding);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public synchronized MessageResourceBundleFacade getParent() {
        return parent;
    }

    public synchronized void setParent(MessageResourceBundleFacade parent) {
        this.parent = parent;
    }
}