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
package org.lastaflute.di.core.external;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.util.LdiMapUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class GenericExternalContext implements ExternalContext {

    protected static final Map<?, ?> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<Object, Object>());

    protected Map<String, Object> application = LdiMapUtil.createHashMap();
    protected ThreadLocal<Object> requests = new ThreadLocal<Object>();

    public Object getApplication() {
        return application;
    }

    public Map<String, Object> getApplicationMap() {
        return application;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getInitParameterMap() {
        return (Map<String, Object>) EMPTY_MAP;
    }

    public Object getRequest() {
        return requests.get();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getRequestCookieMap() {
        return (Map<String, Object>) EMPTY_MAP;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getRequestHeaderMap() {
        return (Map<String, Object>) EMPTY_MAP;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object[]> getRequestHeaderValuesMap() {
        return (Map<String, Object[]>) EMPTY_MAP;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getRequestMap() {
        return (Map<String, Object>) requests.get();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getRequestParameterMap() {
        return (Map<String, Object>) EMPTY_MAP;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object[]> getRequestParameterValuesMap() {
        return (Map<String, Object[]>) EMPTY_MAP;
    }

    public Object getResponse() {
        return null;
    }

    public Object getSession() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getSessionMap() {
        return (Map<String, Object>) EMPTY_MAP;
    }

    public void setApplication(final Object application) {
    }

    public void setRequest(final Object request) {
        requests.set(request);
    }

    public void setResponse(final Object response) {
    }
}
