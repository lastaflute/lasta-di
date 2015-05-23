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
package org.lastaflute.di.core.deployer;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.core.meta.AspectDef;
import org.lastaflute.di.core.meta.DestroyMethodDef;
import org.lastaflute.di.core.meta.InitMethodDef;
import org.lastaflute.di.core.meta.InstanceDef;

/**
 * コンポーネントデプロイヤは、 コンポーネントを利用可能な状態にして提供するためのインターフェースです。
 * <p>
 * このインターフェースは、 {@link InstanceDef インスタンス定義}に応じて以下の機能を提供します。
 * <dl>
 * <dt> シングルトン({@link org.lastaflute.di.core.meta.InstanceDef#SINGLETON_NAME singleton})の場合</dt>
 * <dd>
 * <ul>
 * <li>コンポーネントを1回だけ生成して保持</li>
 * <li>保持しているコンポーネントの提供</li>
 * </ul>
 * </dd>
 * <dt>プロトタイプ({@link org.lastaflute.di.core.meta.InstanceDef#PROTOTYPE_NAME prototype})の場合</dt>
 * <dd>
 * <ul>
 * <li>新しいコンポーネントインスタンスの生成と提供</li>
 * </ul>
 * </dd>
 * <dt>外部コンテキスト({@link org.lastaflute.di.core.meta.InstanceDef#APPLICATION_NAME application}、
 * {@link org.lastaflute.di.core.meta.InstanceDef#SESSION_NAME session}、
 * {@link org.lastaflute.di.core.meta.InstanceDef#REQUEST_NAME request})の場合</dt>
 * <dd>
 * <ul>
 * <li>コンポーネントインスタンスの生成と{@link ExternalContext 外部コンテキスト}への配備</li>
 * <li>外部コンテキストから取得したコンポーネントインスタンスの提供</li>
 * </ul>
 * </dd>
 * <dt> 外部コンポーネント({@link org.lastaflute.di.core.meta.InstanceDef#OUTER_NAME outer})の場合</dt>
 * <dd>
 * <ul>
 * <li>外部コンポーネントに対する依存コンポーネントのインジェクション</li>
 * <ul>
 * </dd>
 * </dl>
 * </p>
 * <p>
 * インスタンス定義と有効なメソッドの対応表を以下に示します。 <table border="1" style="text-align:center;">
 * <tr>
 * <th colspan="8">インスタンス定義と有効なメソッドとの関係</th>
 * </tr>
 * <tr>
 * <th colspan="2" rowspan="2">{@link InstanceDef インスタンス定義}</th>
 * <th>シングルトン</td>
 * <th>プロトタイプ</td>
 * <th colspan="3">外部コンテキスト</td>
 * <th>外部コンポーネント</td>
 * </tr>
 * <tr>
 * <th>{@link org.lastaflute.di.core.meta.impl.InstanceSingletonDef singleton}</td>
 * <th>{@link org.lastaflute.di.core.meta.impl.InstancePrototypeDef prototype}</td>
 * <th>{@link org.lastaflute.di.core.meta.impl.InstanceApplicationDef application}</td>
 * <th>{@link org.lastaflute.di.core.meta.impl.InstanceSessionDef session}</td>
 * <th>{@link org.lastaflute.di.core.meta.impl.InstanceRequestDef request}</td>
 * <th>{@link org.lastaflute.di.core.meta.impl.InstanceOuterDef outer}</td>
 * </tr>
 * <tr>
 * <th rowspan="4">メソッド</th>
 * <th style="text-align:left;">{@link #init()}</th>
 * <td>{@link org.lastaflute.di.core.deployer.SingletonComponentDeployer#init() ○}</td>
 * <td>△</td>
 * <td>△</td>
 * <td>△</td>
 * <td>△</td>
 * <td>△</td>
 * </tr>
 * <tr>
 * <th style="text-align:left;">{@link #deploy()}</th>
 * <td>{@link org.lastaflute.di.core.deployer.SingletonComponentDeployer#deploy() ○}</td>
 * <td>{@link org.lastaflute.di.core.deployer.PrototypeComponentDeployer#deploy() ○}</td>
 * <td>{@link org.lastaflute.di.core.deployer.ApplicationComponentDeployer#deploy() ○}</td>
 * <td>{@link org.lastaflute.di.core.deployer.SessionComponentDeployer#deploy() ○}</td>
 * <td>{@link org.lastaflute.di.core.deployer.RequestComponentDeployer#deploy() ○}</td>
 * <td>×</td>
 * </tr>
 * <tr>
 * <th style="text-align:left;">{@link #injectDependency(Object)}</th>
 * <td>×</td>
 * <td>×</td>
 * <td>×</td>
 * <td>×</td>
 * <td>×</td>
 * <td>{@link org.lastaflute.di.core.deployer.OuterComponentDeployer#injectDependency(Object) ○}</td>
 * </tr>
 * <tr>
 * <th style="text-align:left;">{@link #destroy()}</th>
 * <td>{@link org.lastaflute.di.core.deployer.SingletonComponentDeployer#destroy() ○}</td>
 * <td>△</td>
 * <td>△</td>
 * <td>△</td>
 * <td>△</td>
 * <td>△</td>
 * </tr>
 * </table> ○:適切な処理が行われます △:例外は発生しませんが何も処理を行いません ×:{@link UnsupportedOperationException 例外}が発生します
 * </p>
 * 
 * @author modified by jflute (originated in Seasar)
 * @author belltree
 */
public interface ComponentDeployer {

    /**
     * インスタンス定義に応じてインスタンス生成や外部コンテキストへの配備などを行った後に、 そのコンポーネントのインスタンスを返します。
     * 
     * @return コンポーネントのインスタンス
     * 
     * @see org.lastaflute.di.core.deployer.SingletonComponentDeployer#deploy()
     * @see org.lastaflute.di.core.deployer.PrototypeComponentDeployer#deploy()
     * @see org.lastaflute.di.core.deployer.ApplicationComponentDeployer#deploy()
     * @see org.lastaflute.di.core.deployer.RequestComponentDeployer#deploy()
     * @see org.lastaflute.di.core.deployer.SessionComponentDeployer#deploy()
     */
    public Object deploy();

    /**
     * 外部コンポーネント<code>outerComponent</code>に対し、 この{@link ComponentDeployer コンポーネントデプロイヤ}の{@link ComponentDef コンポーネント定義}に基づいて、
     * S2コンテナ上のコンポーネントをインジェクションします。
     * 
     * @param outerComponent
     *            外部コンポーネント
     * 
     * @see org.lastaflute.di.core.deployer.OuterComponentDeployer#injectDependency(Object)
     */
    public void injectDependency(Object outerComponent);

    /**
     * コンポーネントデプロイヤを初期化します。
     * <p>
     * デプロイするコンポーネントの{@link InstanceDef インスタンス定義}が<code>singleton</code>の場合には、
     * {@link AspectDef アスペクト}を適用したインスタンスの生成、 配備、 プロパティ設定の後に、
     * {@link InitMethodDef initMethod}が呼ばれます。
     * </p>
     * 
     * @see org.lastaflute.di.core.deployer.SingletonComponentDeployer#init()
     * @see org.lastaflute.di.core.assembler.DefaultInitMethodAssembler#assemble(Object)
     */
    public void init();

    /**
     * コンポーネントデプロイヤを破棄します。
     * <p>
     * デプロイするコンポーネントの{@link InstanceDef インスタンス定義}が<code>singleton</code>の場合には、
     * {@link DestroyMethodDef destoryMethod}が呼ばれます。
     * </p>
     * 
     * @see org.lastaflute.di.core.deployer.SingletonComponentDeployer#destroy()
     * @see org.lastaflute.di.core.assembler.DefaultDestroyMethodAssembler#assemble(Object)
     */
    public void destroy();
}
