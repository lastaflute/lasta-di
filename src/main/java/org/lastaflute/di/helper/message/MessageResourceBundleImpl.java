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

import java.util.Properties;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class MessageResourceBundleImpl implements MessageResourceBundle {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Properties prop;
    protected MessageResourceBundle parent;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public MessageResourceBundleImpl(Properties prop) {
        this.prop = prop;
    }

    public MessageResourceBundleImpl(Properties prop, MessageResourceBundle parent) {
        this(prop);
        setParent(parent);
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public String get(String key) {
        if (key == null) {
            return null;
        }
        if (prop.containsKey(key)) {
            return prop.getProperty(key);
        }
        return parent != null ? parent.get(key) : null;
    }

    public MessageResourceBundle getParent() {
        return parent;
    }

    public void setParent(MessageResourceBundle parent) {
        this.parent = parent;
    }
}