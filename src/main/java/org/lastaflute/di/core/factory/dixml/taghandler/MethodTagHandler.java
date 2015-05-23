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
package org.lastaflute.di.core.factory.dixml.taghandler;

import org.lastaflute.di.core.factory.dixml.exception.TagAttributeNotDefinedRuntimeException;
import org.lastaflute.di.core.meta.MethodDef;
import org.lastaflute.di.helper.xml.TagHandlerContext;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * diconファイルに指定されたメソッドインジェクションに共通する処理のための抽象クラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public abstract class MethodTagHandler extends AbstractTagHandler {

    private static final long serialVersionUID = 1L;

    /**
     * 指定された{@link org.lastaflute.di.core.meta.MethodDef メソッド定義}に対して、
     * {@link org.lastaflute.di.core.expression.Expression 式}を設定します。
     * 
     * @param methodDef
     *            メソッド定義
     * @param expression
     *            式を表す文字列
     * @param tagName
     *            処理対象の要素名
     * @param context
     *            コンテキスト
     * @throws TagAttributeNotDefinedRuntimeException
     *             メソッド定義の<code>name</code>属性および式のいずれも指定されなかった場合
     */
    protected void processExpression(MethodDef methodDef, String expression, String tagName, TagHandlerContext context) {
        String expr = expression;
        if (expr != null) {
            expr = expr.trim();
            if (!LdiStringUtil.isEmpty(expr)) {
                methodDef.setExpression(createExpression(context, expr));
            } else {
                expr = null;
            }
        }
        if (methodDef.getMethodName() == null && expr == null) {
            throw new TagAttributeNotDefinedRuntimeException(tagName, "name");
        }
    }
}
