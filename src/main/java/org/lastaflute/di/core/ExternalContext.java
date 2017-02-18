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
package org.lastaflute.di.core;

import java.util.Map;

/**
 * @author modified by jflute (originated in Seasar)
 */
public interface ExternalContext {

    Object getRequest();

    void setRequest(Object request);

    Object getResponse();

    void setResponse(Object response);

    Object getSession();

    Object getApplication();

    void setApplication(Object application);

    Map<String, Object> getApplicationMap();

    Map<String, Object> getInitParameterMap();

    Map<String, Object> getSessionMap();

    Map<String, Object> getRequestCookieMap();

    Map<String, Object> getRequestHeaderMap();

    Map<String, Object[]> getRequestHeaderValuesMap();

    Map<String, Object> getRequestMap();

    Map<String, Object> getRequestParameterMap();

    Map<String, Object[]> getRequestParameterValuesMap();
}
