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
package org.lastaflute.di.core.exception;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.expression.Expression;
import org.lastaflute.di.exception.SRuntimeException;

/**
 * コンポーネントのインスタンスを、 {@link ComponentDef コンポーネント定義}に指定されたクラスにキャスト出来ない場合にスローされます。
 * <p>
 * {@link  ComponentDef#setExpression(Expression)}でインスタンスの生成を定義している場合は、
 * そのインスタンスをコンポーネント定義に指定されたクラスにキャスト出来ないことを表します。
 * </p>
 * <p>
 * 外部コンポーネントを{@link LaContainer#injectDependency(Object)}などでインジェクションする場合は、
 * そのコンポーネントを、 コンポーネント定義に指定されたクラスにキャストできないことを表します。
 * </p>
 * 
 * @author modified by jflute (originated in Seasar)
 * @author belltree
 * 
 * @see org.lastaflute.di.core.assembler.ConstructorAssembler#assemble()
 * @see org.lastaflute.di.core.LaContainer#injectDependency(Object,
 *      Class)
 * @see org.lastaflute.di.core.LaContainer#injectDependency(Object,
 *      String)
 */
public class ClassUnmatchRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 1967770604202235241L;

    private Class componentClass_;

    private Class realComponentClass_;

    /**
     * <code>ClassUnmatchRuntimeException</code>を構築します。
     * 
     * @param componentClass
     *            コンポーネント定義に指定されたクラス
     * @param realComponentClass
     *            コンポーネントの実際の型
     */
    public ClassUnmatchRuntimeException(Class componentClass, Class realComponentClass) {
        super("ESSR0069", new Object[] { componentClass.getName(), realComponentClass != null ? realComponentClass.getName() : "null" });
        componentClass_ = componentClass;
        realComponentClass_ = realComponentClass;
    }

    /**
     * コンポーネント定義に指定されたクラスを返します。
     * 
     * @return コンポーネント定義に指定されたクラス
     */
    public Class getComponentClass() {
        return componentClass_;
    }

    /**
     * コンポーネントの実際の型を返します。
     * 
     * @return コンポーネントの実際の型
     */
    public Class getRealComponentClass() {
        return realComponentClass_;
    }
}