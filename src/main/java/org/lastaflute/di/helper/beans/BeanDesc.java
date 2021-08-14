/*
 * Copyright 2015-2021 the original author or authors.
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
package org.lastaflute.di.helper.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.lastaflute.di.helper.beans.exception.BeanConstructorNotFoundException;
import org.lastaflute.di.helper.beans.exception.BeanFieldNotFoundException;
import org.lastaflute.di.helper.beans.exception.BeanIllegalDiiguException;
import org.lastaflute.di.helper.beans.exception.BeanMethodNotFoundException;
import org.lastaflute.di.helper.beans.exception.BeanPropertyNotFoundException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public interface BeanDesc {

    Class<?> getBeanClass();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    Object newInstance(Object[] args) throws BeanConstructorNotFoundException;

    Constructor<?> getSuitableConstructor(Object[] args) throws BeanConstructorNotFoundException;

    Constructor<?> getConstructor(Class<?>[] paramTypes);

    String[] getConstructorParameterNames(final Class<?>[] paramTypes); // Diigu

    String[] getConstructorParameterNames(Constructor<?> constructor); // Diigu

    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    boolean hasPropertyDesc(String propertyName);

    PropertyDesc getPropertyDesc(String propertyName) throws BeanPropertyNotFoundException;

    PropertyDesc getPropertyDesc(int index);

    int getPropertyDescSize();

    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    Object invoke(Object target, String methodName, Object[] args) throws BeanMethodNotFoundException;

    Method getMethod(String methodName) throws BeanMethodNotFoundException;

    Method getMethod(String methodName, Class<?>[] paramTypes) throws BeanMethodNotFoundException;

    Method getMethodNoException(String methodName);

    Method getMethodNoException(String methodName, Class<?>[] paramTypes);

    Method[] getMethods(String methodName) throws BeanMethodNotFoundException;

    boolean hasMethod(String methodName);

    String[] getMethodNames();

    String[] getMethodParameterNames(String methodName, final Class<?>[] paramTypes)
            throws BeanMethodNotFoundException, BeanIllegalDiiguException;

    String[] getMethodParameterNamesNoException(String methodName, final Class<?>[] paramTypes) throws BeanMethodNotFoundException;

    String[] getMethodParameterNames(Method method) throws BeanMethodNotFoundException, BeanIllegalDiiguException;

    String[] getMethodParameterNamesNoException(Method method) throws BeanMethodNotFoundException;

    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    Object getFieldValue(String fieldName, Object target) throws BeanFieldNotFoundException;

    boolean hasField(String fieldName);

    Field getField(String fieldName) throws BeanFieldNotFoundException;

    Field getField(int index);

    int getFieldSize();

    List<Field> getHiddenFieldList(String fieldName);
}