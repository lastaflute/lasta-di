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
package org.lastaflute.di.core.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.lastaflute.di.core.aop.interceptors.HotAwareDelegateInterceptor;

/**
 * メソッドに{@link HotAwareDelegateInterceptor}を適用することを指定します。
 * 
 * @author modified by jflute (originated in Seasar)
 * @see HotAwareDelegateInterceptor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Interceptor
public @interface HotAwareDelegate {

    /**
     * 移譲する対象となるコンポーネント名です。
     * 
     * @return 移譲する対象となるコンポーネント名
     */
    String targetName();

}