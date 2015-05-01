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
package org.dbflute.lasta.di.util.tiger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.dbflute.lasta.di.helper.beans.BeanDesc;
import org.dbflute.lasta.di.helper.beans.factory.BeanDescFactory;
import org.dbflute.lasta.di.util.LdiMethodUtil;

/**
 * アノテーションのためのユーティリティクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 */
public class LdiAnnotationUtil {

    /**
     * インスタンスを構築します。
     */
    protected LdiAnnotationUtil() {
    }

    /**
     * アノテーションの要素を名前と値の{@link Map}として返します。
     * 
     * @param annotation
     *            アノテーション
     * @return アノテーションの要素の名前と値からなる{@link Map}
     */
    public static Map<String, Object> getProperties(Annotation annotation) {
        Map<String, Object> map = new HashMap<String, Object>();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(annotation.annotationType());
        String[] names = beanDesc.getMethodNames();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            Object v = getProperty(beanDesc, annotation, name);
            if (v != null) {
                map.put(name, v);
            }
        }
        return map;
    }

    /**
     * アノテーションの要素の値を返します。
     * 
     * @param beanDesc
     *            アノテーションを表す{@link BeanDesc}
     * @param annotation
     *            アノテーション
     * @param name
     *            要素の名前
     * @return アノテーションの要素の値
     */
    public static Object getProperty(BeanDesc beanDesc, Annotation annotation, String name) {
        Method m = beanDesc.getMethodNoException(name);
        if (m == null) {
            return null;
        }
        Object v = LdiMethodUtil.invoke(m, annotation, null);
        if (v != null && !"".equals(v)) {
            return v;
        }
        return null;
    }

}
