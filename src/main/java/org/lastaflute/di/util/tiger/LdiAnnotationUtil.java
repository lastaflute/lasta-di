/*
 * Copyright 2015-2020 the original author or authors.
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
package org.lastaflute.di.util.tiger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.factory.BeanDescFactory;
import org.lastaflute.di.util.LdiMethodUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiAnnotationUtil {

    protected LdiAnnotationUtil() {
    }

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
