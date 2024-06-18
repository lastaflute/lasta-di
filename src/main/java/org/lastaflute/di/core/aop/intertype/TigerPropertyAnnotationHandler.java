/*
 * Copyright 2015-2024 the original author or authors.
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
package org.lastaflute.di.core.aop.intertype;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import org.lastaflute.di.core.annotation.Property;
import org.lastaflute.di.core.annotation.PropertyType;
import org.lastaflute.di.core.aop.intertype.PropertyInterType.PropertyAnnotationHandler;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class TigerPropertyAnnotationHandler implements PropertyAnnotationHandler {

    public int getPropertyType(Class<?> clazz, int defaultValue) {
        return getPropertyTypeInternal(clazz, defaultValue);
    }

    public int getPropertyType(Field field, int defaultValue) {
        return getPropertyTypeInternal(field, defaultValue);
    }

    /**
     * @param element
     * @param defaultValue
     * @return 
     */
    public int getPropertyTypeInternal(AnnotatedElement element, int defaultValue) {
        Property property = element.getAnnotation(Property.class);
        int propertyType = defaultValue;
        if (property != null) {
            PropertyType type = property.value();
            if (type == PropertyType.NONE) {
                propertyType = PropertyInterType.NONE;
            } else if (type == PropertyType.READ) {
                propertyType = PropertyInterType.READ;
            } else if (type == PropertyType.WRITE) {
                propertyType = PropertyInterType.WRITE;
            } else if (type == PropertyType.READWRITE) {
                propertyType = PropertyInterType.READWRITE;
            }
        }
        return propertyType;
    }
}
