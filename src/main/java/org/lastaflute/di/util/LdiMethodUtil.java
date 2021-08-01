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
package org.lastaflute.di.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.lastaflute.di.exception.IllegalAccessRuntimeException;
import org.lastaflute.di.exception.InvocationTargetRuntimeException;
import org.lastaflute.di.util.tiger.LdiReflectionUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiMethodUtil {

    private static final Method IS_BRIDGE_METHOD = getIsBridgeMethod();
    private static final Method IS_SYNTHETIC_METHOD = getIsSyntheticMethod();

    protected static final String REFLECTION_UTIL_CLASS_NAME = LdiReflectionUtil.class.getName();
    protected static final Method GET_ELEMENT_TYPE_OF_COLLECTION_FROM_PARAMETER_METHOD = getElementTypeFromParameterMethod("Collection");
    protected static final Method GET_ELEMENT_TYPE_OF_COLLECTION_FROM_RETURN_METHOD = getElementTypeFromReturnMethod("Collection");
    protected static final Method GET_ELEMENT_TYPE_OF_LIST_FROM_PARAMETER_METHOD = getElementTypeFromParameterMethod("List");
    protected static final Method GET_ELEMENT_TYPE_OF_LIST_FROM_RETURN_METHOD = getElementTypeFromReturnMethod("List");
    protected static final Method GET_ELEMENT_TYPE_OF_SET_FROM_PARAMETER_METHOD = getElementTypeFromParameterMethod("Set");
    protected static final Method GET_ELEMENT_TYPE_OF_SET_FROM_RETURN_METHOD = getElementTypeFromReturnMethod("Set");

    protected LdiMethodUtil() {
    }

    public static Object invoke(Method method, Object target, Object[] args)
            throws InvocationTargetRuntimeException, IllegalAccessRuntimeException {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new InvocationTargetRuntimeException(method.getDeclaringClass(), e);
        } catch (IllegalArgumentException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Illegal argument: method=").append(method);
            sb.append(" target=").append(target);
            sb.append(" args=").append(args != null ? Arrays.asList(args) : null);
            String msg = sb.toString();
            throw new IllegalArgumentException(msg, e);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessRuntimeException(method.getDeclaringClass(), e);
        }
    }

    public static boolean isAbstract(Method method) {
        int mod = method.getModifiers();
        return Modifier.isAbstract(mod);
    }

    public static String getSignature(String methodName, Class<?>[] argTypes) {
        StringBuffer buf = new StringBuffer(100);
        buf.append(methodName);
        buf.append("(");
        if (argTypes != null) {
            for (int i = 0; i < argTypes.length; ++i) {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(argTypes[i].getName());
            }
        }
        buf.append(")");
        return buf.toString();
    }

    public static String getSignature(String methodName, Object[] methodArgs) {
        StringBuffer buf = new StringBuffer(100);
        buf.append(methodName);
        buf.append("(");
        if (methodArgs != null) {
            for (int i = 0; i < methodArgs.length; ++i) {
                if (i > 0) {
                    buf.append(", ");
                }
                if (methodArgs[i] != null) {
                    buf.append(methodArgs[i].getClass().getName());
                } else {
                    buf.append("null");
                }
            }
        }
        buf.append(")");
        return buf.toString();
    }

    public static boolean isEqualsMethod(Method method) {
        return method != null && method.getName().equals("equals") && method.getReturnType() == boolean.class
                && method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == Object.class;
    }

    public static boolean isHashCodeMethod(Method method) {
        return method != null && method.getName().equals("hashCode") && method.getReturnType() == int.class
                && method.getParameterTypes().length == 0;
    }

    public static boolean isToStringMethod(Method method) {
        return method != null && method.getName().equals("toString") && method.getReturnType() == String.class
                && method.getParameterTypes().length == 0;
    }

    public static boolean isBridgeMethod(final Method method) {
        if (IS_BRIDGE_METHOD == null) {
            return false;
        }
        return ((Boolean) LdiMethodUtil.invoke(IS_BRIDGE_METHOD, method, null)).booleanValue();
    }

    public static boolean isSyntheticMethod(final Method method) {
        if (IS_SYNTHETIC_METHOD == null) {
            return false;
        }
        return ((Boolean) LdiMethodUtil.invoke(IS_SYNTHETIC_METHOD, method, null)).booleanValue();
    }

    private static Method getIsBridgeMethod() {
        try {
            return Method.class.getMethod("isBridge", (Class<?>[]) null);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    private static Method getIsSyntheticMethod() {
        try {
            return Method.class.getMethod("isSynthetic", (Class<?>[]) null);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    public static Class<?> getElementTypeOfCollectionFromParameterType(final Method method, final int position) {
        if (GET_ELEMENT_TYPE_OF_COLLECTION_FROM_PARAMETER_METHOD == null) {
            return null;
        }
        return (Class<?>) LdiMethodUtil.invoke(GET_ELEMENT_TYPE_OF_COLLECTION_FROM_PARAMETER_METHOD, null,
                new Object[] { method, new Integer(position) });
    }

    public static Class<?> getElementTypeOfCollectionFromReturnType(final Method method) {
        if (GET_ELEMENT_TYPE_OF_COLLECTION_FROM_RETURN_METHOD == null) {
            return null;
        }
        return (Class<?>) LdiMethodUtil.invoke(GET_ELEMENT_TYPE_OF_COLLECTION_FROM_RETURN_METHOD, null, new Object[] { method });
    }

    public static Class<?> getElementTypeOfListFromParameterType(final Method method, final int position) {
        if (GET_ELEMENT_TYPE_OF_LIST_FROM_PARAMETER_METHOD == null) {
            return null;
        }
        return (Class<?>) LdiMethodUtil.invoke(GET_ELEMENT_TYPE_OF_LIST_FROM_PARAMETER_METHOD, null,
                new Object[] { method, new Integer(position) });
    }

    public static Class<?> getElementTypeOfListFromReturnType(final Method method) {
        if (GET_ELEMENT_TYPE_OF_LIST_FROM_RETURN_METHOD == null) {
            return null;
        }
        return (Class<?>) LdiMethodUtil.invoke(GET_ELEMENT_TYPE_OF_LIST_FROM_RETURN_METHOD, null, new Object[] { method });
    }

    public static Class<?> getElementTypeOfSetFromParameterType(final Method method, final int position) {
        if (GET_ELEMENT_TYPE_OF_SET_FROM_PARAMETER_METHOD == null) {
            return null;
        }
        return (Class<?>) LdiMethodUtil.invoke(GET_ELEMENT_TYPE_OF_SET_FROM_PARAMETER_METHOD, null,
                new Object[] { method, new Integer(position) });
    }

    public static Class<?> getElementTypeOfSetFromReturnType(final Method method) {
        if (GET_ELEMENT_TYPE_OF_SET_FROM_RETURN_METHOD == null) {
            return null;
        }
        return (Class<?>) LdiMethodUtil.invoke(GET_ELEMENT_TYPE_OF_SET_FROM_RETURN_METHOD, null, new Object[] { method });
    }

    protected static Method getElementTypeFromParameterMethod(final String type) {
        try {
            final Class<?> reflectionUtilClass = Class.forName(REFLECTION_UTIL_CLASS_NAME);
            return reflectionUtilClass.getMethod("getElementTypeOf" + type + "FromParameterType", new Class[] { Method.class, int.class });
        } catch (final Throwable ignore) {}
        return null;
    }

    protected static Method getElementTypeFromReturnMethod(final String type) {
        try {
            final Class<?> reflectionUtilClass = Class.forName(REFLECTION_UTIL_CLASS_NAME);
            return reflectionUtilClass.getMethod("getElementTypeOf" + type + "FromReturnType", new Class[] { Method.class });
        } catch (final Throwable ignore) {}
        return null;
    }
}