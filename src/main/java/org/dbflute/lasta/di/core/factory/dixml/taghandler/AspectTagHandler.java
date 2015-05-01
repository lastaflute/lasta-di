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

import org.dbflute.lasta.di.core.ComponentDef;
import org.dbflute.lasta.di.core.aop.Pointcut;
import org.dbflute.lasta.di.core.aop.impl.PointcutImpl;
import org.dbflute.lasta.di.core.meta.AspectDef;
import org.dbflute.lasta.di.core.meta.impl.AspectDefImpl;
import org.dbflute.lasta.di.helper.xml.TagHandlerContext;
import org.dbflute.lasta.di.util.LdiStringUtil;
import org.xml.sax.Attributes;

/**
 * diconファイルの<code>aspect</code>要素を解釈するためのクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class AspectTagHandler extends AbstractTagHandler {
    private static final long serialVersionUID = 5619707344253136193L;

    public void start(TagHandlerContext context, Attributes attributes) {
        AspectDef aspectDef = null;
        String pointcutStr = attributes.getValue("pointcut");
        if (pointcutStr != null) {
            String[] methodNames = LdiStringUtil.split(pointcutStr, ", ");
            aspectDef = createAspectDef(createPointcut(methodNames));
        } else {
            aspectDef = createAspectDef();
        }
        context.push(aspectDef);
    }

    public void end(TagHandlerContext context, String body) {
        AspectDef aspectDef = (AspectDef) context.pop();
        if (!LdiStringUtil.isEmpty(body)) {
            aspectDef.setExpression(createExpression(context, body));
        }
        ComponentDef componentDef = (ComponentDef) context.peek();
        componentDef.addAspectDef(aspectDef);
    }

    /**
     * アスペクト定義を作成します。
     * 
     * @return アスペクト定義
     */
    protected AspectDefImpl createAspectDef() {
        return new AspectDefImpl();
    }

    /**
     * アスペクト定義を作成します。
     * 
     * @param pointcut
     * @return アスペクト定義
     */
    protected AspectDefImpl createAspectDef(Pointcut pointcut) {
        return new AspectDefImpl(pointcut);
    }

    /**
     * ポイントカットを作成します。
     * 
     * @param methodNames
     * @return ポイントカット
     */
    protected Pointcut createPointcut(String[] methodNames) {
        return new PointcutImpl(methodNames);
    }
}
