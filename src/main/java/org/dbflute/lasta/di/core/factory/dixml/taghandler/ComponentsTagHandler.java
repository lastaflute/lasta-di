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

import org.dbflute.lasta.di.core.LaContainer;
import org.dbflute.lasta.di.core.meta.impl.LaContainerImpl;
import org.dbflute.lasta.di.helper.xml.TagHandler;
import org.dbflute.lasta.di.helper.xml.TagHandlerContext;
import org.dbflute.lasta.di.util.LdiClassUtil;
import org.dbflute.lasta.di.util.LdiStringUtil;
import org.xml.sax.Attributes;

/**
 * diconファイルの<code>components</code>要素を解釈するためのクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class ComponentsTagHandler extends TagHandler {
    private static final long serialVersionUID = 3182865184697069169L;

    /**
     * {@link LaContainer}の実装クラスです。
     */
    protected Class containerImplClass = LaContainerImpl.class;

    /**
     * {@link LaContainer}の実装クラスを返します。
     * 
     * @return {@link LaContainer}の実装クラス
     */
    public Class getContainerImplClass() {
        return containerImplClass;
    }

    /**
     * {@link LaContainer}の実装クラスを設定します。
     * 
     * @param containerImplClass
     */
    public void setContainerImplClass(Class containerImplClass) {
        this.containerImplClass = containerImplClass;
    }

    public void start(TagHandlerContext context, Attributes attributes) {
        LaContainer container = createContainer();
        String path = (String) context.getParameter("path");
        container.setPath(path);
        String namespace = attributes.getValue("namespace");
        if (!LdiStringUtil.isEmpty(namespace)) {
            container.setNamespace(namespace);
        }
        String initializeOnCreate = attributes.getValue("initializeOnCreate");
        if (!LdiStringUtil.isEmpty(initializeOnCreate)) {
            container.setInitializeOnCreate(Boolean.valueOf(initializeOnCreate).booleanValue());
        }

        LaContainer parent = (LaContainer) context.getParameter("parent");
        if (parent != null) {
            container.setRoot(parent.getRoot());
        }
        context.push(container);
    }

    /**
     * {@link LaContainer}を作成します。
     * 
     * @return {@link LaContainer}
     */
    protected LaContainer createContainer() {
        return (LaContainer) LdiClassUtil.newInstance(containerImplClass);
    }
}
