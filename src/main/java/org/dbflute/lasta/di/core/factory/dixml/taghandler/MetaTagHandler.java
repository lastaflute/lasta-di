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
package org.dbflute.lasta.di.core.factory.dixml.taghandler;

import org.dbflute.lasta.di.core.meta.MetaDef;
import org.dbflute.lasta.di.core.meta.MetaDefAware;
import org.dbflute.lasta.di.core.meta.impl.MetaDefImpl;
import org.dbflute.lasta.di.helper.xml.TagHandlerContext;
import org.dbflute.lasta.di.util.LdiStringUtil;
import org.xml.sax.Attributes;

/**
 * diconファイルの<code>meta</code>要素を解釈するためのクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class MetaTagHandler extends AbstractTagHandler {
    private static final long serialVersionUID = -3372861766134433725L;

    public void start(TagHandlerContext context, Attributes attributes) {
        String name = attributes.getValue("name");
        context.push(createMetaDef(name));
    }

    public void end(TagHandlerContext context, String body) {
        MetaDef metaDef = (MetaDef) context.pop();
        if (!LdiStringUtil.isEmpty(body)) {
            metaDef.setExpression(createExpression(context, body));
        }
        MetaDefAware metaDefAware = (MetaDefAware) context.peek();
        metaDefAware.addMetaDef(metaDef);
    }

    /**
     * メタ定義を作成します。
     * 
     * @param name
     * @return メタ定義
     */
    protected MetaDefImpl createMetaDef(String name) {
        return new MetaDefImpl(name);
    }
}
