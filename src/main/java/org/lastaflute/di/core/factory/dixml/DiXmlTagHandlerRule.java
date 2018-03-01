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
package org.lastaflute.di.core.factory.dixml;

import org.lastaflute.di.core.factory.dixml.taghandler.ArgTagHandler;
import org.lastaflute.di.core.factory.dixml.taghandler.AspectTagHandler;
import org.lastaflute.di.core.factory.dixml.taghandler.ComponentTagHandler;
import org.lastaflute.di.core.factory.dixml.taghandler.ComponentsTagHandler;
import org.lastaflute.di.core.factory.dixml.taghandler.DestroyMethodTagHandler;
import org.lastaflute.di.core.factory.dixml.taghandler.IncludeTagHandler;
import org.lastaflute.di.core.factory.dixml.taghandler.InitMethodTagHandler;
import org.lastaflute.di.core.factory.dixml.taghandler.InterTypeTagHandler;
import org.lastaflute.di.core.factory.dixml.taghandler.MetaTagHandler;
import org.lastaflute.di.core.factory.dixml.taghandler.PropertyTagHandler;
import org.lastaflute.di.helper.xml.TagHandlerRule;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class DiXmlTagHandlerRule extends TagHandlerRule {

    private static final long serialVersionUID = 1L;

    public DiXmlTagHandlerRule() {
        addTagHandler("/components", new ComponentsTagHandler());
        addTagHandler("component", new ComponentTagHandler());
        addTagHandler("arg", new ArgTagHandler());
        addTagHandler("property", new PropertyTagHandler());
        addTagHandler("meta", new MetaTagHandler());
        addTagHandler("postConstruct", new InitMethodTagHandler());
        addTagHandler("preDestroy", new DestroyMethodTagHandler());
        addTagHandler("aspect", new AspectTagHandler());
        addTagHandler("interType", new InterTypeTagHandler());
        addTagHandler("/components/include", new IncludeTagHandler());
    }
}
