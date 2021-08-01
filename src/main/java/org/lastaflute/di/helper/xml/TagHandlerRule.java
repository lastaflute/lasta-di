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
package org.lastaflute.di.helper.xml;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TagHandlerRule implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, TagHandler> tagHandlers = new HashMap<String, TagHandler>();

    public TagHandlerRule() {
    }

    public final void addTagHandler(String path, TagHandler tagHandler) {
        tagHandlers.put(path, tagHandler);
    }

    public final TagHandler getTagHandler(String path) {
        return tagHandlers.get(path);
    }
}
