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
package org.lastaflute.di.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.lastaflute.di.exception.IllegalAccessRuntimeException;
import org.lastaflute.di.exception.InstantiationRuntimeException;
import org.lastaflute.di.exception.InvocationTargetRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiConstructorUtil {

    protected LdiConstructorUtil() {
    }

    public static Object newInstance(Constructor<?> constructor, Object[] args)
            throws InstantiationRuntimeException, IllegalAccessRuntimeException, InvocationTargetRuntimeException {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException ex) {
            throw new InstantiationRuntimeException(constructor.getDeclaringClass(), ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessRuntimeException(constructor.getDeclaringClass(), ex);
        } catch (InvocationTargetException ex) {
            throw new InvocationTargetRuntimeException(constructor.getDeclaringClass(), ex);
        }
    }
}
