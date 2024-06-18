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
package org.lastaflute.di.core.aop.javassist;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

import org.lastaflute.di.core.util.ClassPoolUtil;
import org.lastaflute.di.exception.CannotCompileRuntimeException;
import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.di.exception.IllegalAccessRuntimeException;
import org.lastaflute.di.exception.InvocationTargetRuntimeException;
import org.lastaflute.di.exception.NoSuchMethodRuntimeException;
import org.lastaflute.di.exception.NotFoundRuntimeException;
import org.lastaflute.di.util.LdiClassUtil;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AbstractGenerator {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String DEFINE_CLASS_METHOD_NAME = "defineClass";
    protected static final ProtectionDomain protectionDomain;

    /**
     * Reflection to ClassLoader@defineClass() as private-accessible. <br>
     * but it may be not accessible when Java17 without add-opens option.
     */
    protected static final Method defineClassMethod;

    // static initializer
    static {
        protectionDomain = (ProtectionDomain) AccessController.doPrivileged(createAspectWeaverPrivilegedAction());
        defineClassMethod = (Method) AccessController.doPrivileged(createDefineClassPrivilegedAction());
    }

    protected static PrivilegedAction<Object> createAspectWeaverPrivilegedAction() {
        return new PrivilegedAction<Object>() {
            public Object run() {
                return AspectWeaver.class.getProtectionDomain();
            }
        };
    }

    protected static PrivilegedAction<Object> createDefineClassPrivilegedAction() {
        return new PrivilegedAction<Object>() {
            public Object run() {
                return prepareClassLoaderDefineClassMethod();
            }
        };
    }

    protected static Method prepareClassLoaderDefineClassMethod() {
        final Class<ClassLoader> typeAsResource = ClassLoader.class;
        final String defineClassMethodName = DEFINE_CLASS_METHOD_NAME;
        final Class<?>[] paramTypes = new Class[] { String.class, byte[].class, int.class, int.class, ProtectionDomain.class };
        final Method method;
        try {
            final Class<?> typeByCurrentContext = LdiClassUtil.forName(typeAsResource.getName()); // for what? by jflute
            method = typeByCurrentContext.getDeclaredMethod(defineClassMethodName, paramTypes);
            method.setAccessible(true); // depends on add-opens option since java17
        } catch (final NoSuchMethodException e) { // basically framework mistake
            throw new NoSuchMethodRuntimeException(typeAsResource, defineClassMethodName, paramTypes, e);
        }
        return method;
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ClassPool classPool; // not null

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected AbstractGenerator(final ClassPool classPool) {
        this.classPool = classPool;
    }

    // ===================================================================================
    //                                                                      Class Handling
    //                                                                      ==============
    protected CtClass toCtClass(final Class<?> clazz) {
        return ClassPoolUtil.toCtClass(classPool, clazz);
    }

    protected CtClass toCtClass(final String className) {
        return ClassPoolUtil.toCtClass(classPool, className);
    }

    protected CtClass[] toCtClassArray(final String[] classNames) {
        return ClassPoolUtil.toCtClassArray(classPool, classNames);
    }

    protected CtClass[] toCtClassArray(final Class<?>[] classes) {
        return ClassPoolUtil.toCtClassArray(classPool, classes);
    }

    protected CtClass createCtClass(final String name) {
        return ClassPoolUtil.createCtClass(classPool, name);
    }

    protected CtClass createCtClass(final String name, final Class<?> superClass) {
        return ClassPoolUtil.createCtClass(classPool, name, superClass);
    }

    protected CtClass createCtClass(final String name, final CtClass superClass) {
        return ClassPoolUtil.createCtClass(classPool, name, superClass);
    }

    protected CtClass getAndRenameCtClass(final Class<?> orgClass, final String newName) {
        return getAndRenameCtClass(LdiClassUtil.getSimpleClassName(orgClass), newName);
    }

    protected CtClass getAndRenameCtClass(final String orgName, final String newName) {
        try {
            return classPool.getAndRename(orgName, newName);
        } catch (final NotFoundException e) {
            throw new NotFoundRuntimeException(e);
        }
    }

    // -----------------------------------------------------
    //                                      Define the Class
    //                                      ----------------
    public Class<?> toClass(final ClassLoader classLoader, final CtClass ctClass) {
        return invokeClassLoaderDefineClass(classLoader, ctClass);
    }

    protected Class<?> invokeClassLoaderDefineClass(final ClassLoader classLoader, final CtClass ctClass) {
        try {
            final String className = ctClass.getName();
            final byte[] bytecode = ctClass.toBytecode();
            final Integer off = 0;
            final Integer len = bytecode.length;
            final Object[] args = new Object[] { className, bytecode, off, len, protectionDomain };
            return (Class<?>) defineClassMethod.invoke(classLoader, args);
        } catch (final CannotCompileException e) {
            throw new CannotCompileRuntimeException(e);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new IllegalAccessRuntimeException(ClassLoader.class, e);
        } catch (final InvocationTargetException e) {
            throw new InvocationTargetRuntimeException(ClassLoader.class, e);
        }
    }

    // ===================================================================================
    //                                                                  Interface Handling
    //                                                                  ==================
    protected void setInterface(final CtClass clazz, final Class<?> interfaceType) {
        clazz.setInterfaces(new CtClass[] { toCtClass(interfaceType) });
    }

    protected void setInterfaces(final CtClass clazz, final Class<?>[] interfaces) {
        clazz.setInterfaces(toCtClassArray(interfaces));
    }

    // ===================================================================================
    //                                                                Constructor Handling
    //                                                                ====================
    protected CtConstructor createDefaultConstructor(final Class<?> clazz) {
        return createDefaultConstructor(toCtClass(clazz));
    }

    protected CtConstructor createDefaultConstructor(final CtClass clazz) {
        try {
            final CtConstructor ctConstructor = CtNewConstructor.defaultConstructor(clazz);
            clazz.addConstructor(ctConstructor);
            return ctConstructor;
        } catch (final CannotCompileException e) {
            throw new CannotCompileRuntimeException(e);
        }
    }

    protected CtConstructor createConstructor(final CtClass clazz, final Constructor<?> constructor) {
        return createConstructor(clazz, toCtClassArray(constructor.getParameterTypes()), toCtClassArray(constructor.getExceptionTypes()));
    }

    protected CtConstructor createConstructor(final CtClass clazz, final CtClass[] parameterTypes, final CtClass[] exceptionTypes) {
        try {
            final CtConstructor ctConstructor = CtNewConstructor.make(parameterTypes, exceptionTypes, clazz);
            clazz.addConstructor(ctConstructor);
            return ctConstructor;
        } catch (final CannotCompileException e) {
            throw new CannotCompileRuntimeException(e);
        }
    }

    // ===================================================================================
    //                                                                     Method Handling
    //                                                                     ===============
    protected CtMethod getDeclaredMethod(final CtClass clazz, final String name, final CtClass[] argTypes) {
        try {
            return clazz.getDeclaredMethod(name, argTypes);
        } catch (final NotFoundException e) {
            throw new NotFoundRuntimeException(e);
        }
    }

    protected CtMethod createMethod(final CtClass clazz, final String src) {
        try {
            final CtMethod ctMethod = CtNewMethod.make(src, clazz);
            clazz.addMethod(ctMethod);
            return ctMethod;
        } catch (final CannotCompileException e) {
            throw new CannotCompileRuntimeException(e);
        }
    }

    protected CtMethod createMethod(final CtClass clazz, final Method method, final String body) {
        return createMethod(clazz, method.getModifiers(), method.getReturnType(), method.getName(), method.getParameterTypes(),
                method.getExceptionTypes(), body);
    }

    protected CtMethod createMethod(final CtClass clazz, final int modifier, final Class<?> returnType, final String methodName,
            final Class<?>[] parameterTypes, final Class<?>[] exceptionTypes, final String body) {
        try {
            final CtMethod ctMethod = CtNewMethod.make(modifier & ~(Modifier.ABSTRACT | Modifier.NATIVE), toCtClass(returnType), methodName,
                    toCtClassArray(parameterTypes), toCtClassArray(exceptionTypes), body, clazz);
            clazz.addMethod(ctMethod);
            return ctMethod;
        } catch (final CannotCompileException e) {
            throw new CannotCompileRuntimeException(e);
        }
    }

    protected void setMethodBody(final CtMethod method, final String src) {
        try {
            method.setBody(src);
        } catch (final CannotCompileException e) {
            throw new CannotCompileRuntimeException(e);
        }
    }

    // ===================================================================================
    //                                                                  Expression Utility
    //                                                                  ==================
    protected static String fromObject(final Class<?> type, final String expr) {
        if (type.equals(void.class) || type.equals(Object.class)) {
            return expr;
        }
        if (type.equals(boolean.class) || type.equals(char.class)) {
            final Class<?> wrapper = LdiClassUtil.getWrapperClass(type);
            return "((" + wrapper.getName() + ") " + expr + ")." + type.getName() + "Value()";
        }
        if (type.isPrimitive()) {
            return "((java.lang.Number) " + expr + ")." + type.getName() + "Value()";
        }
        return "(" + LdiClassUtil.getSimpleClassName(type) + ") " + expr;
    }

    protected static String toObject(final Class<?> type, final String expr) {
        if (type.isPrimitive()) {
            final Class<?> wrapper = LdiClassUtil.getWrapperClass(type);
            return "new " + wrapper.getName() + "(" + expr + ")";
        }
        return expr;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ClassPool getClassPool() {
        return classPool;
    }
}