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
package org.lastaflute.di.core.meta;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.deployer.ComponentDeployer;

/**
 * コンポーネントに対して<var>destroy</var>メソッド・インジェクションを定義するためのインターフェースです。
 * <p>
 * <var>destroy</var>メソッド・インジェクションとは、 S2コンテナによって管理されているコンポーネントが破棄される際に、
 * 1個以上の任意のメソッド(終了処理メソッド)を実行するという機能です。
 * </p>
 * <p>
 * コンポーネントの{@link InstanceDef インスタンス定義}が<code>singleton</code>の場合には、
 * S2コンテナが終了する際に<var>destroy</var>メソッド・インジェクションが実行されます。
 * </p>
 * 
 * @author modified by jflute (originated in Seasar)
 * @author belltree
 * 
 * @see ComponentDeployer#destroy()
 * @see ComponentDef#destroy()
 * @see LaContainer#destroy()
 * @see org.lastaflute.di.core.factory.LaContainerFactory#destroy()
 * @see org.lastaflute.di.core.factory.SingletonLaContainerFactory#destroy()
 */
public interface DestroyMethodDef extends MethodDef {

}