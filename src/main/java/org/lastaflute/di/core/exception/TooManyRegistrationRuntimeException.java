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
package org.lastaflute.di.core.exception;

import org.lastaflute.di.exception.SRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 * @author belltree
 * 
 * @see org.lastaflute.di.core.meta.impl.TooManyRegistrationComponentDefImpl#getComponent()
 */
public class TooManyRegistrationRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = -6522677955855595193L;

    private Object key_;

    private Class[] componentClasses_;

    /**
     * @param key
     * @param componentClasses
     */
    public TooManyRegistrationRuntimeException(Object key, Class[] componentClasses) {
        super("ESSR0045", new Object[] { key, getClassNames(componentClasses) });
        key_ = key;
        componentClasses_ = componentClasses;
    }

    /**
     * @return 
     */
    public Object getKey() {
        return key_;
    }

    /**
     * @return 
     */
    public Class[] getComponentClasses() {
        return componentClasses_;
    }

    private static String getClassNames(Class[] componentClasses) {
        StringBuffer buf = new StringBuffer(255);
        for (int i = 0; i < componentClasses.length; ++i) {
            if (componentClasses[i] != null) {
                buf.append(componentClasses[i].getName());
            } else {
                buf.append("<unknown>");
            }
            buf.append(", ");
        }
        buf.setLength(buf.length() - 2);
        return buf.toString();
    }
}