/*
 * Copyright 2015-2017 the original author or authors.
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

import java.util.List;

import org.lastaflute.di.core.ComponentDef;

/**
 * @author jflute
 */
public class TooManyRegistrationComponentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected final Object componentKey;
    protected final List<ComponentDef> componentDefList; // for e.g. LastaFlute login manager handling

    public TooManyRegistrationComponentException(String msg, Object componentKey, List<ComponentDef> componentDefList) {
        super(msg);
        this.componentKey = componentKey;
        this.componentDefList = componentDefList;
    }

    public Object getComponentKey() {
        return componentKey;
    }

    public List<ComponentDef> getComponentDefList() {
        return componentDefList;
    }
}