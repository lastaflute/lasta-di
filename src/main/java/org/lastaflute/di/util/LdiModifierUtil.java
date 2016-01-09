/*
 * Copyright 2015-2016 the original author or authors.
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiModifierUtil {

    static final int BRIDGE = 0x00000040;
    static final int SYNTHETIC = 0x00001000;

    protected LdiModifierUtil() {
    }

    public static boolean isPublic(Method m) {
        return isPublic(m.getModifiers());
    }

    public static boolean isPublic(Field f) {
        return isPublic(f.getModifiers());
    }

    public static boolean isPublicStaticFinalField(Field f) {
        return isPublicStaticFinal(f.getModifiers());
    }

    public static boolean isPublicStaticFinal(int modifier) {
        return isPublic(modifier) && isStatic(modifier) && isFinal(modifier);
    }

    public static boolean isPublic(int modifier) {
        return Modifier.isPublic(modifier);
    }

    public static boolean isAbstract(Class<?> clazz) {
        return isAbstract(clazz.getModifiers());
    }

    public static boolean isAbstract(int modifier) {
        return Modifier.isAbstract(modifier);
    }

    public static boolean isStatic(int modifier) {
        return Modifier.isStatic(modifier);
    }

    public static boolean isFinal(int modifier) {
        return Modifier.isFinal(modifier);
    }

    public static boolean isFinal(Method method) {
        return isFinal(method.getModifiers());
    }

    public static boolean isFinal(Field field) {
        return isFinal(field.getModifiers());
    }

    public static boolean isTransient(Field field) {
        return isTransient(field.getModifiers());
    }

    public static boolean isTransient(int modifier) {
        return Modifier.isTransient(modifier);
    }

    public static boolean isInstanceField(Field field) {
        int m = field.getModifiers();
        return !isStatic(m) && !isFinal(m);
    }
}
