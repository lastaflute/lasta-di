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
package org.lastaflute.di.helper.beans.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lastaflute.di.core.util.ClassPoolUtil;
import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.beans.annotation.ParameterName;
import org.lastaflute.di.helper.beans.exception.BeanConstructorNotFoundException;
import org.lastaflute.di.helper.beans.exception.BeanFieldNotFoundException;
import org.lastaflute.di.helper.beans.exception.BeanFieldSetAccessibleFailureException;
import org.lastaflute.di.helper.beans.exception.BeanIllegalDiiguException;
import org.lastaflute.di.helper.beans.exception.BeanMethodNotFoundException;
import org.lastaflute.di.helper.beans.exception.BeanNoClassDefFoundError;
import org.lastaflute.di.helper.beans.exception.BeanNoSuchFieldError;
import org.lastaflute.di.helper.beans.exception.BeanNoSuchMethodError;
import org.lastaflute.di.helper.beans.exception.BeanPropertyNotFoundException;
import org.lastaflute.di.helper.beans.factory.ParameterizedClassDescFactory;
import org.lastaflute.di.helper.log.LaLogger;
import org.lastaflute.di.util.ArrayMap;
import org.lastaflute.di.util.CaseInsensitiveMap;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiConstructorUtil;
import org.lastaflute.di.util.LdiDoubleConversionUtil;
import org.lastaflute.di.util.LdiFieldUtil;
import org.lastaflute.di.util.LdiFloatConversionUtil;
import org.lastaflute.di.util.LdiIntegerConversionUtil;
import org.lastaflute.di.util.LdiLongConversionUtil;
import org.lastaflute.di.util.LdiMethodUtil;
import org.lastaflute.di.util.LdiShortConversionUtil;
import org.lastaflute.di.util.LdiStringUtil;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class BeanDescImpl implements BeanDesc {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final LaLogger logger = LaLogger.getLogger(BeanDescImpl.class);
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final Class<?>[] EMPTY_PARAM_TYPES = new Class[0];
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String PARAMETER_NAME_ANNOTATION = ParameterName.class.getName();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Class<?> beanClass;
    private Constructor<?>[] constructors;
    private Map<TypeVariable<?>, Type> typeVariables;
    private CaseInsensitiveMap propertyDescCache = new CaseInsensitiveMap();
    private Map<String, Method[]> methodsCache = new HashMap<String, Method[]>();
    private ArrayMap<String, Field> fieldCache = new ArrayMap<String, Field>();
    private ArrayMap<String, List<Field>> hiddenFieldCache; // lazy loaded
    private transient Set<String> invalidPropertyNames = new HashSet<String>();
    private Map<Constructor<?>, String[]> constructorParameterNamesCache;
    private Map<Method, String[]> methodParameterNamesCache;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BeanDescImpl(Class<?> beanClass) throws EmptyRuntimeException {
        if (beanClass == null) {
            throw new EmptyRuntimeException("beanClass");
        }
        try {
            this.beanClass = beanClass;
            constructors = beanClass.getConstructors();
            typeVariables = ParameterizedClassDescFactory.getTypeVariables(beanClass);
            setupPropertyDescs();
            setupMethods();
            setupFields();
        } catch (NoClassDefFoundError e) { // these catches are for e.g. nested class error
            throw new BeanNoClassDefFoundError("Failed to analyze the bean class: " + beanClass.getName(), e);
        } catch (NoSuchMethodError e) {
            throw new BeanNoSuchMethodError("Failed to analyze the bean class: " + beanClass.getName(), e);
        } catch (NoSuchFieldError e) {
            throw new BeanNoSuchFieldError("Failed to analyze the bean class: " + beanClass.getName(), e);
        }
    }

    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    public Class<?> getBeanClass() {
        return beanClass;
    }

    // ===================================================================================
    //                                                                   Property Handling
    //                                                                   =================
    public boolean hasPropertyDesc(String propertyName) {
        return propertyDescCache.get(propertyName) != null;
    }

    public PropertyDesc getPropertyDesc(String propertyName) throws BeanPropertyNotFoundException {
        final PropertyDesc pd = (PropertyDesc) propertyDescCache.get(propertyName);
        if (pd == null) {
            String msg = "Not found the bean property: " + beanClass.getName() + "@" + propertyName;
            throw new BeanPropertyNotFoundException(msg, beanClass, propertyName);
        }
        return pd;
    }

    private PropertyDesc getPropertyDesc0(String propertyName) {
        return (PropertyDesc) propertyDescCache.get(propertyName);
    }

    public PropertyDesc getPropertyDesc(int index) {
        return (PropertyDesc) propertyDescCache.get(index);
    }

    public int getPropertyDescSize() {
        return propertyDescCache.size();
    }

    // ===================================================================================
    //                                                                      Field Handling
    //                                                                      ==============
    public boolean hasField(String fieldName) {
        return fieldCache.get(fieldName) != null;
    }

    @Override
    public Field getField(String fieldName) {
        final Field field = fieldCache.get(fieldName);
        if (field == null) {
            throw new BeanFieldNotFoundException(beanClass, fieldName);
        }
        return field;
    }

    @Override
    public int getFieldSize() {
        return fieldCache.size();
    }

    // ===================================================================================
    //                                                                Constructor Handling
    //                                                                ====================
    @Override
    public Object newInstance(Object[] args) throws BeanConstructorNotFoundException {
        Constructor<?> constructor = getSuitableConstructor(args);
        return LdiConstructorUtil.newInstance(constructor, args);
    }

    public Constructor<?> getSuitableConstructor(Object[] args) throws BeanConstructorNotFoundException {
        if (args == null) {
            args = EMPTY_ARGS;
        }
        Constructor<?> constructor = findSuitableConstructor(args);
        if (constructor != null) {
            return constructor;
        }
        constructor = findSuitableConstructorAdjustNumber(args);
        if (constructor != null) {
            return constructor;
        }
        throw new BeanConstructorNotFoundException(beanClass, args);
    }

    private Constructor<?> findSuitableConstructor(Object[] args) {
        outerLoop: for (int i = 0; i < constructors.length; ++i) {
            Class<?>[] paramTypes = constructors[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || LdiClassUtil.isAssignableFrom(paramTypes[j], args[j].getClass())) {
                    continue;
                }
                continue outerLoop;
            }
            return constructors[i];
        }
        return null;
    }

    private Constructor<?> findSuitableConstructorAdjustNumber(Object[] args) {
        outerLoop: for (int i = 0; i < constructors.length; ++i) {
            Class<?>[] paramTypes = constructors[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || LdiClassUtil.isAssignableFrom(paramTypes[j], args[j].getClass())
                        || adjustNumber(paramTypes, args, j)) {
                    continue;
                }
                continue outerLoop;
            }
            return constructors[i];
        }
        return null;
    }

    public Constructor<?> getConstructor(final Class<?>[] paramTypes) {
        for (int i = 0; i < constructors.length; ++i) {
            if (Arrays.equals(paramTypes, constructors[i].getParameterTypes())) {
                return constructors[i];
            }
        }
        throw new BeanConstructorNotFoundException(beanClass, paramTypes);
    }

    public String[] getConstructorParameterNames(final Class<?>[] parameterTypes) {
        return getConstructorParameterNames(getConstructor(parameterTypes));
    }

    public String[] getConstructorParameterNames(final Constructor<?> constructor) {
        if (constructorParameterNamesCache == null) {
            constructorParameterNamesCache = createConstructorParameterNamesCache();
        }
        if (!constructorParameterNamesCache.containsKey(constructor)) {
            throw new BeanConstructorNotFoundException(beanClass, constructor.getParameterTypes());
        }
        return (String[]) constructorParameterNamesCache.get(constructor);
    }

    private Map<Constructor<?>, String[]> createConstructorParameterNamesCache() {
        final Map<Constructor<?>, String[]> map = new HashMap<Constructor<?>, String[]>();
        final ClassPool pool = ClassPoolUtil.getClassPool(beanClass);
        for (int i = 0; i < constructors.length; ++i) {
            final Constructor<?> constructor = constructors[i];
            if (constructor.getParameterTypes().length == 0) {
                map.put(constructor, EMPTY_STRING_ARRAY);
                continue;
            }
            final CtClass clazz = ClassPoolUtil.toCtClass(pool, constructor.getDeclaringClass());
            final CtClass[] parameterTypes = ClassPoolUtil.toCtClassArray(pool, constructor.getParameterTypes());
            try {
                final String[] names = getParameterNames(clazz.getDeclaredConstructor(parameterTypes));
                map.put(constructor, names);
            } catch (final NotFoundException e) {
                logger.log("WSSR0084", new Object[] { beanClass.getName(), constructor });
            }
        }
        return map;
    }

    // ===================================================================================
    //                                                                     Method Handling
    //                                                                     ===============
    public Object invoke(Object target, String methodName, Object[] args) {
        Method method = getSuitableMethod(methodName, args);
        return LdiMethodUtil.invoke(method, target, args);
    }

    public Method getMethod(final String methodName) {
        return getMethod(methodName, EMPTY_PARAM_TYPES);
    }

    public Method getMethodNoException(final String methodName) {
        return getMethodNoException(methodName, EMPTY_PARAM_TYPES);
    }

    public Method getMethod(final String methodName, final Class<?>[] paramTypes) {
        Method method = getMethodNoException(methodName, paramTypes);
        if (method != null) {
            return method;
        }
        throw new BeanMethodNotFoundException(beanClass, methodName, paramTypes);
    }

    public Method getMethodNoException(final String methodName, final Class<?>[] paramTypes) {
        final Method[] methods = (Method[]) methodsCache.get(methodName);
        if (methods == null) {
            return null;
        }
        for (int i = 0; i < methods.length; ++i) {
            if (Arrays.equals(paramTypes, methods[i].getParameterTypes())) {
                return methods[i];
            }
        }
        return null;
    }

    public Method[] getMethods(String methodName) throws BeanMethodNotFoundException {
        Method[] methods = (Method[]) methodsCache.get(methodName);
        if (methods == null) {
            throw new BeanMethodNotFoundException(beanClass, methodName, null);
        }
        return methods;
    }

    public boolean hasMethod(String methodName) {
        return methodsCache.get(methodName) != null;
    }

    public String[] getMethodNames() {
        return (String[]) methodsCache.keySet().toArray(new String[methodsCache.size()]);
    }

    public String[] getMethodParameterNamesNoException(final String methodName, final Class<?>[] parameterTypes) {
        return getMethodParameterNamesNoException(getMethod(methodName, parameterTypes));
    }

    public String[] getMethodParameterNames(final String methodName, final Class<?>[] parameterTypes) {
        return getMethodParameterNames(getMethod(methodName, parameterTypes));
    }

    public String[] getMethodParameterNames(final Method method) {
        String[] names = getMethodParameterNamesNoException(method);
        if (names == null || names.length != method.getParameterTypes().length) {
            throw new BeanIllegalDiiguException();
        }
        return names;
    }

    public String[] getMethodParameterNamesNoException(final Method method) {
        if (methodParameterNamesCache == null) {
            methodParameterNamesCache = createMethodParameterNamesCache();
        }
        if (!methodParameterNamesCache.containsKey(method)) {
            throw new BeanMethodNotFoundException(beanClass, method.getName(), method.getParameterTypes());
        }
        return (String[]) methodParameterNamesCache.get(method);
    }

    private Map<Method, String[]> createMethodParameterNamesCache() {
        final Map<Method, String[]> map = new HashMap<Method, String[]>();
        final ClassPool pool = ClassPoolUtil.getClassPool(beanClass);
        for (final Iterator<Method[]> it = methodsCache.values().iterator(); it.hasNext();) {
            final Method[] methods = (Method[]) it.next();
            for (int i = 0; i < methods.length; ++i) {
                final Method method = methods[i];
                if (method.getParameterTypes().length == 0) {
                    map.put(methods[i], EMPTY_STRING_ARRAY);
                    continue;
                }
                final CtClass clazz = ClassPoolUtil.toCtClass(pool, method.getDeclaringClass());
                final CtClass[] parameterTypes = ClassPoolUtil.toCtClassArray(pool, method.getParameterTypes());
                try {
                    final String[] names = getParameterNames(clazz.getDeclaredMethod(method.getName(), parameterTypes));
                    map.put(methods[i], names);
                } catch (final NotFoundException e) {
                    logger.log("WSSR0085", new Object[] { beanClass.getName(), method });
                }
            }
        }
        return map;
    }

    private String[] getParameterNames(final CtBehavior behavior) throws NotFoundException {
        final MethodInfo methodInfo = behavior.getMethodInfo();
        final ParameterAnnotationsAttribute attribute =
                (ParameterAnnotationsAttribute) methodInfo.getAttribute(ParameterAnnotationsAttribute.visibleTag);
        if (attribute == null) {
            return null;
        }
        final int numParameters = behavior.getParameterTypes().length;
        final String[] parameterNames = new String[numParameters];
        final Annotation[][] annotationsArray = attribute.getAnnotations();
        if (annotationsArray == null || annotationsArray.length != numParameters) {
            return null;
        }
        for (int i = 0; i < numParameters; ++i) {
            final String parameterName = getParameterName(annotationsArray[i]);
            if (parameterName == null) {
                return null;
            }
            parameterNames[i] = parameterName;
        }
        return parameterNames;
    }

    private String getParameterName(final Annotation[] annotations) {
        Annotation nameAnnotation = null;
        for (int i = 0; i < annotations.length; ++i) {
            final Annotation annotation = annotations[i];
            if (PARAMETER_NAME_ANNOTATION.equals(annotation.getTypeName())) {
                nameAnnotation = annotation;
                break;
            }
        }
        if (nameAnnotation == null) {
            return null;
        }
        return ((StringMemberValue) nameAnnotation.getMemberValue("value")).getValue();
    }

    // ===================================================================================
    //                                                                     Set up Property
    //                                                                     ===============
    protected void setupPropertyDescs() {
        final Method[] methods = beanClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if (LdiMethodUtil.isBridgeMethod(method) || LdiMethodUtil.isSyntheticMethod(method)) {
                continue;
            }
            final String methodName = method.getName();
            if (methodName.startsWith("get")) {
                if (method.getParameterTypes().length != 0 || methodName.equals("getClass") || method.getReturnType() == void.class) {
                    continue;
                }
                final String propertyName = decapitalizePropertyName(methodName.substring(3));
                setupReadMethod(method, propertyName);
            } else if (methodName.startsWith("is")) {
                if (method.getParameterTypes().length != 0
                        || !method.getReturnType().equals(Boolean.TYPE) && !method.getReturnType().equals(Boolean.class)) {
                    continue;
                }
                final String propertyName = decapitalizePropertyName(methodName.substring(2));
                setupReadMethod(method, propertyName);
            } else if (methodName.startsWith("set")) {
                if (method.getParameterTypes().length != 1 || methodName.equals("setClass") || method.getReturnType() != void.class) {
                    continue;
                }
                final String propertyName = decapitalizePropertyName(methodName.substring(3));
                setupWriteMethod(method, propertyName);
            }
        }
        for (Iterator<?> i = invalidPropertyNames.iterator(); i.hasNext();) {
            propertyDescCache.remove(i.next());
        }
        invalidPropertyNames.clear();
    }

    private static String decapitalizePropertyName(String name) {
        if (LdiStringUtil.isEmpty(name)) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
            return name;
        }
        final char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private void addPropertyDesc(PropertyDesc propertyDesc) throws EmptyRuntimeException {
        if (propertyDesc == null) {
            throw new EmptyRuntimeException("propertyDesc");
        }
        propertyDescCache.put(propertyDesc.getPropertyName(), propertyDesc);
    }

    private void setupReadMethod(Method readMethod, String propertyName) {
        final Class<?> propertyType = readMethod.getReturnType();
        final PropertyDesc propDesc = getPropertyDesc0(propertyName);
        if (propDesc != null) {
            if (!propDesc.getPropertyType().equals(propertyType)) {
                invalidPropertyNames.add(propertyName);
            } else {
                propDesc.setReadMethod(readMethod);
            }
        } else {
            addPropertyDesc(new PropertyDescImpl(propertyName, propertyType, readMethod, null, null, this));
        }
    }

    private void setupWriteMethod(Method writeMethod, String propertyName) {
        final Class<?> propertyType = writeMethod.getParameterTypes()[0];
        final PropertyDesc propDesc = getPropertyDesc0(propertyName);
        if (propDesc != null) {
            if (!propDesc.getPropertyType().equals(propertyType)) {
                invalidPropertyNames.add(propertyName);
            } else {
                propDesc.setWriteMethod(writeMethod);
            }
        } else {
            addPropertyDesc(new PropertyDescImpl(propertyName, propertyType, null, writeMethod, null, this));
        }
    }

    private Method getSuitableMethod(String methodName, Object[] args) throws BeanMethodNotFoundException {
        if (args == null) {
            args = EMPTY_ARGS;
        }
        final Method[] methods = getMethods(methodName);
        Method method = findSuitableMethod(methods, args);
        if (method != null) {
            return method;
        }
        method = findSuitableMethodAdjustNumber(methods, args);
        if (method != null) {
            return method;
        }
        throw new BeanMethodNotFoundException(beanClass, methodName, args);
    }

    private Method findSuitableMethod(Method[] methods, Object[] args) {
        outerLoop: for (int i = 0; i < methods.length; ++i) {
            final Class<?>[] paramTypes = methods[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || LdiClassUtil.isAssignableFrom(paramTypes[j], args[j].getClass())) {
                    continue;
                }
                continue outerLoop;
            }
            return methods[i];
        }
        return null;
    }

    private Method findSuitableMethodAdjustNumber(Method[] methods, Object[] args) {
        outerLoop: for (int i = 0; i < methods.length; ++i) {
            Class<?>[] paramTypes = methods[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || LdiClassUtil.isAssignableFrom(paramTypes[j], args[j].getClass())
                        || adjustNumber(paramTypes, args, j)) {
                    continue;
                }
                continue outerLoop;
            }
            return methods[i];
        }
        return null;
    }

    // ===================================================================================
    //                                                                      Set up Methods
    //                                                                      ==============
    protected void setupMethods() {
        ArrayMap<String, List<Method>> methodListMap = new ArrayMap<String, List<Method>>();
        Method[] methods = beanClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (LdiMethodUtil.isBridgeMethod(method) || LdiMethodUtil.isSyntheticMethod(method)) {
                continue;
            }
            String methodName = method.getName();
            List<Method> list = (List<Method>) methodListMap.get(methodName);
            if (list == null) {
                list = new ArrayList<Method>();
                methodListMap.put(methodName, list);
            }
            list.add(method);
        }
        for (int i = 0; i < methodListMap.size(); ++i) {
            List<Method> methodList = (List<Method>) methodListMap.get(i);
            methodsCache.put((String) methodListMap.getKey(i), methodList.toArray(new Method[methodList.size()]));
        }
    }

    // ===================================================================================
    //                                                                       Set up Fields
    //                                                                       =============
    protected void setupFields() {
        setupFields(beanClass);
    }

    private void setupFields(Class<?> targetClass) {
        if (targetClass.isInterface()) {
            setupFieldsByInterface(targetClass);
        } else {
            setupFieldsByClass(targetClass);
        }
    }

    private void setupFieldsByInterface(Class<?> interfaceClass) {
        addFields(interfaceClass);
        final Class<?>[] interfaces = interfaceClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            setupFieldsByInterface(interfaces[i]);
        }
    }

    private void setupFieldsByClass(Class<?> targetClass) {
        addFields(targetClass);
        final Class<?>[] interfaces = targetClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            setupFieldsByInterface(interfaces[i]);
        }
        final Class<?> superClass = targetClass.getSuperclass();
        if (superClass != Object.class && superClass != null) {
            setupFieldsByClass(superClass);
        }
    }

    private void addFields(Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            final Field field = fields[i];
            final String fname = field.getName();
            if (!fieldCache.containsKey(fname)) {
                beAccessible(field);
                fieldCache.put(fname, field);
                if (LdiFieldUtil.isInstanceField(field)) {
                    if (hasPropertyDesc(fname)) {
                        final PropertyDesc pd = getPropertyDesc(field.getName());
                        pd.setField(field);
                    } else if (LdiFieldUtil.isPublicField(field)) {
                        final PropertyDesc pd = new PropertyDescImpl(field.getName(), field.getType(), null, null, field, this);
                        propertyDescCache.put(fname, pd);
                    }
                }
            } else { // hidden field (to inject them)
                beAccessible(field);
                if (hiddenFieldCache == null) {
                    hiddenFieldCache = new ArrayMap<String, List<Field>>();
                }
                List<Field> hiddenFieldList = hiddenFieldCache.get(fname);
                if (hiddenFieldList == null) {
                    hiddenFieldList = new ArrayList<Field>(2);
                    hiddenFieldCache.put(fname, hiddenFieldList);
                }
                hiddenFieldList.add(field);
            }
        }
    }

    private void beAccessible(Field field) {
        if (isExceptPrivateAccessible(field)) {
            return;
        }
        try {
            field.setAccessible(true);
        } catch (RuntimeException e) { // for Java9 headache
            String msg = "Failed to set accessible to the field: " + field;
            throw new BeanFieldSetAccessibleFailureException(msg, beanClass, field, e);
        }
    }

    private boolean isExceptPrivateAccessible(Field field) {
        // to avoid warning of JDK-internal access at Java11
        // Lasta Di does not need private access to the classes
        final String fqcn = field.getDeclaringClass().getName();
        return fqcn.startsWith("java.") || fqcn.startsWith("jdk.") || fqcn.startsWith("com.sun.") || fqcn.startsWith("sun.");
    }

    // ===================================================================================
    //                                                                        Field Access
    //                                                                        ============
    @Override
    public Object getFieldValue(String fieldName, Object target) throws BeanFieldNotFoundException {
        return LdiFieldUtil.get(getField(fieldName), target);
    }

    @Override
    public Field getField(int index) {
        return fieldCache.get(index);
    }

    public List<Field> getHiddenFieldList(String fieldName) {
        if (hiddenFieldCache != null) {
            final List<Field> fieldList = hiddenFieldCache.get(fieldName);
            if (fieldList != null) {
                return Collections.unmodifiableList(fieldList);
            }
        }
        return Collections.emptyList();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private static boolean adjustNumber(Class<?>[] paramTypes, Object[] args, int index) {
        if (paramTypes[index].isPrimitive()) {
            if (paramTypes[index] == int.class) {
                args[index] = LdiIntegerConversionUtil.toInteger(args[index]);
                return true;
            } else if (paramTypes[index] == double.class) {
                args[index] = LdiDoubleConversionUtil.toDouble(args[index]);
                return true;
            } else if (paramTypes[index] == long.class) {
                args[index] = LdiLongConversionUtil.toLong(args[index]);
                return true;
            } else if (paramTypes[index] == short.class) {
                args[index] = LdiShortConversionUtil.toShort(args[index]);
                return true;
            } else if (paramTypes[index] == float.class) {
                args[index] = LdiFloatConversionUtil.toFloat(args[index]);
                return true;
            }
        } else {
            if (paramTypes[index] == Integer.class) {
                args[index] = LdiIntegerConversionUtil.toInteger(args[index]);
                return true;
            } else if (paramTypes[index] == Double.class) {
                args[index] = LdiDoubleConversionUtil.toDouble(args[index]);
                return true;
            } else if (paramTypes[index] == Long.class) {
                args[index] = LdiLongConversionUtil.toLong(args[index]);
                return true;
            } else if (paramTypes[index] == Short.class) {
                args[index] = LdiShortConversionUtil.toShort(args[index]);
                return true;
            } else if (paramTypes[index] == Float.class) {
                args[index] = LdiFloatConversionUtil.toFloat(args[index]);
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    Map<TypeVariable<?>, Type> getTypeVariables() {
        return typeVariables;
    }
}
