/*
 * Copyright 2015-2022 the original author or authors.
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
package org.lastaflute.di.core.factory.dixml.taghandler;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.aop.Pointcut;
import org.lastaflute.di.core.aop.impl.PointcutImpl;
import org.lastaflute.di.core.meta.AspectDef;
import org.lastaflute.di.core.meta.impl.AspectDefImpl;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.lastaflute.di.util.LdiStringUtil;
import org.xml.sax.Attributes;

/**
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
     * @return 
     */
    protected AspectDefImpl createAspectDef() {
        return new AspectDefImpl();
    }

    /**
     * @param pointcut
     * @return 
     */
    protected AspectDefImpl createAspectDef(Pointcut pointcut) {
        return new AspectDefImpl(pointcut);
    }

    /**
     * @param methodNames
     * @return 
     */
    protected Pointcut createPointcut(String[] methodNames) {
        return new PointcutImpl(methodNames);
    }
}
