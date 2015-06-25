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
package org.lastaflute.di.helper.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.lastaflute.di.helper.beans.exception.ConstructorNotFoundRuntimeException;
import org.lastaflute.di.helper.beans.exception.FieldNotFoundRuntimeException;
import org.lastaflute.di.helper.beans.exception.IllegalDiiguRuntimeException;
import org.lastaflute.di.helper.beans.exception.MethodNotFoundRuntimeException;
import org.lastaflute.di.helper.beans.exception.PropertyNotFoundRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public interface BeanDesc {

    Class<?> getBeanClass();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    Object newInstance(Object[] args) throws ConstructorNotFoundRuntimeException;

    Constructor<?> getSuitableConstructor(Object[] args) throws ConstructorNotFoundRuntimeException;

    Constructor<?> getConstructor(Class<?>[] paramTypes);

    String[] getConstructorParameterNames(final Class<?>[] paramTypes); // Diigu

    String[] getConstructorParameterNames(Constructor<?> constructor); // Diigu

    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    boolean hasPropertyDesc(String propertyName);

    PropertyDesc getPropertyDesc(String propertyName) throws PropertyNotFoundRuntimeException;

    PropertyDesc getPropertyDesc(int index);

    int getPropertyDescSize();

    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    Object invoke(Object target, String methodName, Object[] args) throws MethodNotFoundRuntimeException;

    Method getMethod(String methodName) throws MethodNotFoundRuntimeException;

    Method getMethod(String methodName, Class<?>[] paramTypes) throws MethodNotFoundRuntimeException;

    Method getMethodNoException(String methodName);

    Method getMethodNoException(String methodName, Class<?>[] paramTypes);

    Method[] getMethods(String methodName) throws MethodNotFoundRuntimeException;

    boolean hasMethod(String methodName);

    String[] getMethodNames();

    String[] getMethodParameterNames(String methodName, final Class<?>[] paramTypes)
            throws MethodNotFoundRuntimeException, IllegalDiiguRuntimeException;

    String[] getMethodParameterNamesNoException(String methodName, final Class<?>[] paramTypes) throws MethodNotFoundRuntimeException;

    String[] getMethodParameterNames(Method method) throws MethodNotFoundRuntimeException, IllegalDiiguRuntimeException;

    String[] getMethodParameterNamesNoException(Method method) throws MethodNotFoundRuntimeException;

    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    Object getFieldValue(String fieldName, Object target) throws FieldNotFoundRuntimeException;

    boolean hasField(String fieldName);

    Field getField(String fieldName) throws FieldNotFoundRuntimeException;

    Field getField(int index);

    int getFieldSize();

    List<Field> getHiddenFieldList(String fieldName);
}