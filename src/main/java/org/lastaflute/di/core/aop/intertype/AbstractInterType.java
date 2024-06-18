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

import java.util.Arrays;

import org.lastaflute.di.core.aop.InterType;
import org.lastaflute.di.core.util.ClassPoolUtil;
import org.lastaflute.di.exception.CannotCompileRuntimeException;
import org.lastaflute.di.exception.NotFoundRuntimeException;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractInterType implements InterType {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String COMPONENT = "instance = prototype";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Class<?> targetClass; // not null while introducing
    protected CtClass enhancedClass; // me too
    protected ClassPool classPool; // me too

    // ===================================================================================
    //                                                                           Introduce
    //                                                                           =========
    public void introduce(final Class<?> targetClass, final CtClass enhancedClass) {
        this.targetClass = targetClass;
        this.enhancedClass = enhancedClass;
        this.classPool = enhancedClass.getClassPool();
        try {
            introduce();
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot introduce the inter-type by Javassist.");
            br.addItem("targetClass");
            br.addElement(targetClass);
            br.addItem("enhancedClass");
            br.addElement(enhancedClass);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        } catch (final NotFoundException e) {
            throw new NotFoundRuntimeException(e);
        } finally {
            this.targetClass = null;
            this.enhancedClass = null;
            this.classPool = null;
        }
    }

    protected abstract void introduce() throws CannotCompileException, NotFoundException;

    // ===================================================================================
    //                                                                  Interface Handling
    //                                                                  ==================
    protected void addInterface(final Class<?> clazz) {
        enhancedClass.addInterface(toCtClass(clazz));
    }

    // ===================================================================================
    //                                                                      Field Handling
    //                                                                      ==============
    protected void addField(final Class<?> type, final String name) {
        addField(Modifier.PRIVATE, type, name);
    }

    protected void addField(final Class<?> type, final String name, final String init) {
        addField(Modifier.PRIVATE, type, name, init);
    }

    protected void addStaticField(final Class<?> type, final String name) {
        addStaticField(Modifier.PRIVATE, type, name);
    }

    protected void addStaticField(final Class<?> type, final String name, final String init) {
        addStaticField(Modifier.PRIVATE, type, name, init);
    }

    protected void addConstant(final Class<?> type, final String name, final String init) {
        addStaticField(Modifier.PUBLIC | Modifier.FINAL, type, name, init);
    }

    protected void addStaticField(final int modifiers, final Class<?> type, final String name) {
        addField(Modifier.STATIC | modifiers, type, name);
    }

    protected void addStaticField(final int modifiers, final Class<?> type, final String name, final String init) {
        addField(Modifier.STATIC | modifiers, type, name, init);
    }

    protected void addStaticField(final int modifiers, final Class<?> type, final String name, final CtField.Initializer init) {
        addField(Modifier.STATIC | modifiers, type, name, init);
    }

    protected void addField(final int modifiers, final Class<?> type, final String name) {
        try {
            final CtField field = new CtField(toCtClass(type), name, enhancedClass);
            field.setModifiers(modifiers);
            enhancedClass.addField(field);
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot add the field to the class by Javassist.");
            br.addItem("enhancedClass");
            br.addElement(enhancedClass);
            br.addItem("Field Modifiers");
            br.addElement(modifiers);
            br.addItem("Field Type");
            br.addElement(type);
            br.addItem("Field Name");
            br.addElement(name);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        }
    }

    protected void addField(final String src) {
        try {
            enhancedClass.addField(CtField.make(src, enhancedClass));
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot add the field to the class by Javassist.");
            br.addItem("enhancedClass");
            br.addElement(enhancedClass);
            br.addItem("Field Source");
            br.addElement(src);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        }
    }

    protected void addField(final int modifiers, final Class<?> type, final String name, final String init) {
        try {
            final CtField field = new CtField(toCtClass(type), name, enhancedClass);
            field.setModifiers(modifiers);
            enhancedClass.addField(field, init);
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot add the field to the class by Javassist.");
            br.addItem("enhancedClass");
            br.addElement(enhancedClass);
            br.addItem("Field Modifiers");
            br.addElement(modifiers);
            br.addItem("Field Type");
            br.addElement(type);
            br.addItem("Field Name");
            br.addElement(name);
            br.addItem("Field DefaultValue");
            br.addElement(init);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        }
    }

    protected void addField(final int modifiers, final Class<?> type, final String name, final CtField.Initializer init) {
        try {
            final CtField field = new CtField(toCtClass(type), name, enhancedClass);
            field.setModifiers(modifiers);
            enhancedClass.addField(field, init);
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot add the field to the class by Javassist.");
            br.addItem("enhancedClass");
            br.addElement(enhancedClass);
            br.addItem("Field Modifiers");
            br.addElement(modifiers);
            br.addItem("Field Type");
            br.addElement(type);
            br.addItem("Field Name");
            br.addElement(name);
            br.addItem("Field DefaultValue Initializer");
            br.addElement(init);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        }
    }

    // ===================================================================================
    //                                                                     Method Handling
    //                                                                     ===============
    protected void addMethod(final String name, final String src) {
        addMethod(Modifier.PUBLIC, void.class, name, null, null, src);
    }

    protected void addMethod(final String name, final Class<?>[] paramTypes, final String src) {
        addMethod(Modifier.PUBLIC, void.class, name, paramTypes, null, src);
    }

    protected void addMethod(final String name, final Class<?>[] paramTypes, Class<?>[] exceptionTypes, final String src) {
        addMethod(Modifier.PUBLIC, void.class, name, paramTypes, exceptionTypes, src);
    }

    protected void addMethod(final Class<?> returnType, final String name, final String src) {
        addMethod(Modifier.PUBLIC, returnType, name, null, null, src);
    }

    protected void addMethod(final Class<?> returnType, final String name, final Class<?>[] paramTypes, final String src) {
        addMethod(Modifier.PUBLIC, returnType, name, paramTypes, null, src);
    }

    protected void addMethod(final Class<?> returnType, final String name, final Class<?>[] paramTypes, Class<?>[] exceptionTypes,
            final String src) {
        addMethod(Modifier.PUBLIC, returnType, name, paramTypes, exceptionTypes, src);
    }

    protected void addStaticMethod(final String name, final String src) {
        addMethod(Modifier.PUBLIC | Modifier.STATIC, void.class, name, null, null, src);
    }

    protected void addStaticMethod(final String name, final Class<?>[] paramTypes, final String src) {
        addMethod(Modifier.PUBLIC | Modifier.STATIC, void.class, name, paramTypes, null, src);
    }

    protected void addStaticMethod(final String name, final Class<?>[] paramTypes, Class<?>[] exceptionTypes, final String src) {
        addMethod(Modifier.PUBLIC | Modifier.STATIC, void.class, name, paramTypes, exceptionTypes, src);
    }

    protected void addStaticMethod(final Class<?> returnType, final String name, final String src) {
        addMethod(Modifier.PUBLIC | Modifier.STATIC, returnType, name, null, null, src);
    }

    protected void addStaticMethod(final Class<?> returnType, final String name, final Class<?>[] paramTypes, final String src) {
        addMethod(Modifier.PUBLIC | Modifier.STATIC, returnType, name, paramTypes, null, src);
    }

    protected void addStaticMethod(final Class<?> returnType, final String name, final Class<?>[] paramTypes, Class<?>[] exceptionTypes,
            final String src) {
        addMethod(Modifier.PUBLIC | Modifier.STATIC, returnType, name, paramTypes, exceptionTypes, src);
    }

    protected void addMethod(final int modifiers, final Class<?> returnType, final String name, final Class<?>[] paramTypes,
            Class<?>[] exceptionTypes, final String src) {
        try {
            final CtClass returnCtClass = toCtClass(returnType);
            final CtClass[] paramCtClassArray = toCtClassArray(paramTypes);
            final CtClass[] expCtClassArray = toCtClassArray(exceptionTypes);
            final CtMethod ctMethod =
                    CtNewMethod.make(modifiers, returnCtClass, name, paramCtClassArray, expCtClassArray, src, enhancedClass);
            enhancedClass.addMethod(ctMethod);
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot add the method to the class by Javassist.");
            br.addItem("enhancedClass");
            br.addElement(enhancedClass);
            br.addItem("Method Modifiers");
            br.addElement(modifiers);
            br.addItem("Return Type");
            br.addElement(returnType);
            br.addItem("Method Name");
            br.addElement(name);
            br.addItem("Parameter Types");
            br.addElement(paramTypes != null ? Arrays.asList(paramTypes) : null);
            br.addItem("Exception Types");
            br.addElement(exceptionTypes != null ? Arrays.asList(exceptionTypes) : null);
            br.addItem("Method Source");
            br.addElement(src);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        }
    }

    protected void addMethod(final String src) {
        try {
            enhancedClass.addMethod(CtNewMethod.make(src, enhancedClass));
        } catch (final CannotCompileException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot add the method to the class by Javassist.");
            br.addItem("enhancedClass");
            br.addElement(enhancedClass);
            br.addElement(src);
            throw new CannotCompileRuntimeException(br.buildExceptionMessage(), e);
        }
    }

    // ===================================================================================
    //                                                                    CtClass Handling
    //                                                                    ================
    protected CtClass toCtClass(final Class<?> clazz) {
        return ClassPoolUtil.toCtClass(classPool, clazz);
    }

    protected CtClass[] toCtClassArray(final Class<?>[] classes) {
        return ClassPoolUtil.toCtClassArray(classPool, classes);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected Class<?> getTargetClass() {
        return targetClass;
    }

    protected CtClass getEnhancedClass() {
        return enhancedClass;
    }

    protected ClassPool getClassPool() {
        return classPool;
    }
}
