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
package org.dbflute.lasta.di.core;

import org.dbflute.lasta.di.core.assembler.ConstructorAssembler;
import org.dbflute.lasta.di.core.assembler.MethodAssembler;
import org.dbflute.lasta.di.core.assembler.PropertyAssembler;
import org.dbflute.lasta.di.core.exception.ClassUnmatchRuntimeException;
import org.dbflute.lasta.di.core.exception.ComponentNotFoundRuntimeException;
import org.dbflute.lasta.di.core.exception.ContainerNotRegisteredRuntimeException;
import org.dbflute.lasta.di.core.exception.CyclicReferenceRuntimeException;
import org.dbflute.lasta.di.core.exception.TooManyRegistrationRuntimeException;
import org.dbflute.lasta.di.core.external.ExternalContextComponentDefRegister;
import org.dbflute.lasta.di.core.meta.InstanceDef;
import org.dbflute.lasta.di.core.meta.MetaDefAware;

/**
 * DIとAOPをサポートしたS2コンテナのインターフェースです。
 * 
 * <h4>S2Containerの役割について</h4>
 * <p>
 * コンポーネントの管理を行う機能を提供します。 コンポーネントとは１つかまたそれ以上のクラスで構成されるJavaオブジェクトです。
 * S2コンテナはコンポーネントの生成、コンポーネントの初期化、コンポーネントの取得を提供します。
 * コンポーネントを取得するキーには、コンポーネント名、コンポーネントのクラス、またはコンポーネントが実装するインターフェースを指定することができます。
 * </p>
 * <h4>S2コンテナのインスタンス階層について</h4>
 * <p>
 * S2コンテナ全体は複数のコンテナにより階層化されています。 一つのコンテナは複数のコンテナをインクルードすることができます。
 * 複数のコンテナが同一のコンテナをインクルードすることができます。
 * </p>
 * <p>
 * インクルードの参照範囲についてのイメージを示します。<br>
 * <img
 * src="http://s2container.seasar.org/ja/images/include_range_20040706.png"/><br>
 * コンテナの検索順についてのイメージを示します。<br>
 * <img
 * src="http://s2container.seasar.org/ja/images/include_search_20040706.png"/>
 * </p>
 * <h4>S2コンテナのインジェクションの種類について</h4>
 * <p>
 * S2コンテナは3種類のインジェクションをサポートします。
 * <dl>
 * <dt>{@link ConstructorAssembler コンストラクタ・インジェクション}</dt>
 * <dd>コンストラクタ引数を利用してコンポーネントをセットします。 </dd>
 * <dt>{@link PropertyAssembler セッター・インジェクション}</dt>
 * <dd>セッターメソッドを利用してコンポーネントをセットします。 </dd>
 * <dt>{@link MethodAssembler メソッド・インジェクション}</dt>
 * <dd>任意のメソッドを利用してコンポーネントをセットします。 </dd>
 * </dl>
 * </p>
 * <h4>S2Containerが持つメソッドの分類について</h4>
 * <p>
 * コンテナへの登録、コンテナからのコンポーネント取得、検索などを行うコンポーネントを管理する機能
 * <ul>
 * <li>{@link LaContainer#getComponent getComponent}</li>
 * <li>{@link LaContainer#getComponentDefSize getComponentDefSize}</li>
 * <li>{@link LaContainer#getComponentDef getComponentDef}</li>
 * <li>{@link LaContainer#findComponents findComponents}</li>
 * <li>{@link LaContainer#findAllComponents findAllComponents}</li>
 * <li>{@link LaContainer#findLocalComponents findLocalComponents}</li>
 * <li>{@link LaContainer#findComponentDefs findComponentDefs}</li>
 * <li>{@link LaContainer#findAllComponentDefs findAllComponentDefs}</li>
 * <li>{@link LaContainer#findLocalComponentDefs findLocalComponentDefs}</li>
 * <li>{@link LaContainer#hasComponentDef hasComponentDef}</li>
 * <li>{@link LaContainer#register register}</li>
 * <li>{@link LaContainer#injectDependency injectDependency}</li>
 * </ul>
 * コンテナの初期化、終了処理、コンテナの階層化、階層化されたコンテナへのアクセスなどコンテナを管理する機能
 * <ul>
 * <li>{@link LaContainer#getNamespace getNamespace}</li>
 * <li>{@link LaContainer#setNamespace setNamespace}</li>
 * <li>{@link LaContainer#getPath getPath getPath getPath}</li>
 * <li>{@link LaContainer#setPath setPath setPath setPath}</li>
 * <li>{@link LaContainer#getClassLoader getClassLoader}</li>
 * <li>{@link LaContainer#setClassLoader setClassLoader}</li>
 * <li>{@link LaContainer#init init}</li>
 * <li>{@link LaContainer#destroy destroy}</li>
 * <li>{@link LaContainer#getExternalContext getExternalContext}</li>
 * <li>{@link LaContainer#setExternalContext setExternalContext}</li>
 * <li>{@link LaContainer#getExternalContextComponentDefRegister getExternalContextComponentDefRegister}</li>
 * <li>{@link LaContainer#setExternalContextComponentDefRegister setExternalContextComponentDefRegister}</li>
 * <li>{@link LaContainer#hasDescendant hasDescendant}</li>
 * <li>{@link LaContainer#getDescendant getDescendant}</li>
 * <li>{@link LaContainer#registerDescendant registerDescendant}</li>
 * <li>{@link LaContainer#include include}</li>
 * <li>{@link LaContainer#getChildSize getChildSize}</li>
 * <li>{@link LaContainer#getChild getChild}</li>
 * <li>{@link LaContainer#getParentSize getParentSize}</li>
 * <li>{@link LaContainer#getParent getParent}</li>
 * <li>{@link LaContainer#addParent addParent}</li>
 * <li>{@link LaContainer#getRoot getRoot}</li>
 * <li>{@link LaContainer#setRoot setRoot}</li>
 * <li>{@link LaContainer#registerMap registerMap}</li>
 * </ul>
 * </p>
 * 
 * @author modified by jflute (originated in Seasar)
 * @author vestige &amp; SeasarJavaDoc Committers
 */
public interface LaContainer extends MetaDefAware {

    <COMPONENT> COMPONENT getComponent(Object componentKey) throws ComponentNotFoundRuntimeException, TooManyRegistrationRuntimeException,
            CyclicReferenceRuntimeException;

    Object[] findComponents(Object componentKey) throws CyclicReferenceRuntimeException;

    Object[] findAllComponents(Object componentKey) throws CyclicReferenceRuntimeException;

    /**
     * @param componentKey The key of the component. (NotNull)
     * @return The array of found components. (NotNull: if not found, returns empty)
     * @throws CyclicReferenceRuntimeException
     */
    Object[] findLocalComponents(Object componentKey) throws CyclicReferenceRuntimeException;

    /**
     * <code>outerComponent</code>のクラスをキーとして登録された
     * {@link ComponentDef コンポーネント定義}に従って、必要なコンポーネントのインジェクションを実行します。
     * アスペクト、コンストラクタ・インジェクションは適用できません。
     * <p>
     * {@link ComponentDef コンポーネント定義}の{@link InstanceDef インスタンス定義}は
     * {@link InstanceDef#OUTER_NAME outer}でなくてはなりません。
     * </p>
     * 
     * @param outerComponent
     *            外部コンポーネント
     * @throws ClassUnmatchRuntimeException
     *             適合するコンポーネント定義が見つからない場合
     */
    void injectDependency(Object outerComponent) throws ClassUnmatchRuntimeException;

    /**
     * <code>componentClass</code>をキーとして登録された {@link ComponentDef コンポーネント定義}に従って、必要なコンポーネントのインジェクションを実行します。
     * アスペクト、コンストラクタ・インジェクションは適用できません。
     * <p>
     * {@link ComponentDef コンポーネント定義}の{@link InstanceDef インスタンス定義}は
     * {@link InstanceDef#OUTER_NAME outer}でなくてはなりません。
     * </p>
     * 
     * @param outerComponent
     *            外部コンポーネント
     * @param componentClass
     *            コンポーネント定義のキー (クラス)
     * @throws ClassUnmatchRuntimeException
     *             適合するコンポーネント定義が見つからない場合
     */
    void injectDependency(Object outerComponent, Class<?> componentClass) throws ClassUnmatchRuntimeException;

    /**
     * <code>componentName</code>をキーとして登録された {@link ComponentDef コンポーネント定義}に従って、インジェクションを実行します。
     * アスペクト、コンストラクタ・インジェクションは適用できません。
     * <p>
     * {@link ComponentDef コンポーネント定義}の{@link InstanceDef インスタンス定義}は
     * {@link InstanceDef#OUTER_NAME outer}でなくてはなりません。
     * </p>
     * 
     * @param outerComponent
     *            外部コンポーネント
     * @param componentName
     *            コンポーネント定義のキー (名前)
     * @throws ClassUnmatchRuntimeException
     */
    void injectDependency(Object outerComponent, String componentName) throws ClassUnmatchRuntimeException;

    void register(Object component); // as anonymous component

    void register(Object component, String componentName);

    void register(Class<?> componentClass);

    void register(Class<?> componentClass, String componentName);

    void register(ComponentDef componentDef);

    void registerByClass(ComponentDef componentDef); // for e.g. expression lazy type

    int getComponentDefSize();

    ComponentDef getComponentDef(int index);

    ComponentDef getComponentDef(Object componentKey) throws ComponentNotFoundRuntimeException;

    ComponentDef[] findComponentDefs(Object componentKey);

    /**
     * 指定されたキーに対応する複数のコンポーネント定義を検索して返します。
     * <p>
     * 検索の範囲は現在のS2コンテナおよび、インクルードしているS2コンテナの階層全体です。
     * キーに対応するコンポーネントが最初に見つかったS2コンテナとその子孫コンテナの全てを対象とします。
     * 対象になるS2コンテナ全体から、キーに対応する全てのコンポーネント定義を配列で返します。
     * </p>
     * 
     * @param componentKey
     *            コンポーネント定義を取得するためのキー
     * @return キーに対応するコンポーネント定義の配列を返します。 キーに対応するコンポーネント定義が存在しない場合は長さ0の配列を返します。
     * @see #findComponentDefs
     * @see #findLocalComponentDefs
     */
    ComponentDef[] findAllComponentDefs(Object componentKey);

    /**
     * 指定されたキーに対応する複数のコンポーネント定義を検索して返します。
     * <p>
     * 検索の範囲は現在のS2コンテナのみです。 現在のS2コンテナから、キーに対応する全てのコンポーネント定義を配列で返します。
     * </p>
     * 
     * @param componentKey
     *            コンポーネント定義を取得するためのキー
     * @return キーに対応するコンポーネント定義の配列を返します。 キーに対応するコンポーネント定義が存在しない場合は長さ0の配列を返します。
     * @see #findComponentDefs
     * @see #findAllComponentDefs
     */
    ComponentDef[] findLocalComponentDefs(Object componentKey);

    boolean hasComponentDef(Object componentKey);

    boolean hasDescendant(String path);

    LaContainer getDescendant(String path) throws ContainerNotRegisteredRuntimeException;

    void registerDescendant(LaContainer descendant);

    void include(LaContainer child);

    int getChildSize();

    LaContainer getChild(int index);

    int getParentSize();

    LaContainer getParent(int index);

    void addParent(LaContainer parent);

    void init();

    void destroy();

    void registerMap(Object key, ComponentDef componentDef, LaContainer container);

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    String getNamespace();

    void setNamespace(String namespace);

    boolean isInitializeOnCreate();

    void setInitializeOnCreate(boolean initializeOnCreate);

    String getPath();

    void setPath(String path);

    LaContainer getRoot();

    void setRoot(LaContainer root);

    ExternalContext getExternalContext();

    void setExternalContext(ExternalContext externalContext);

    ExternalContextComponentDefRegister getExternalContextComponentDefRegister();

    void setExternalContextComponentDefRegister(ExternalContextComponentDefRegister externalContextComponentDefRegister);

    ClassLoader getClassLoader();

    void setClassLoader(ClassLoader classLoader);
}