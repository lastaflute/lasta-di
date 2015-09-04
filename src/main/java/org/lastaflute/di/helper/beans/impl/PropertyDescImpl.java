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
package org.lastaflute.di.helper.beans.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.exception.SIllegalArgumentException;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.ParameterizedClassDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.beans.exception.BeanIllegalPropertyException;
import org.lastaflute.di.helper.beans.factory.ParameterizedClassDescFactory;
import org.lastaflute.di.util.LdiBooleanConversionUtil;
import org.lastaflute.di.util.LdiCalendarConversionUtil;
import org.lastaflute.di.util.LdiConstructorUtil;
import org.lastaflute.di.util.LdiDateConversionUtil;
import org.lastaflute.di.util.LdiFieldUtil;
import org.lastaflute.di.util.LdiMethodUtil;
import org.lastaflute.di.util.LdiModifierUtil;
import org.lastaflute.di.util.LdiNumberConversionUtil;
import org.lastaflute.di.util.LdiSqlDateConversionUtil;
import org.lastaflute.di.util.LdiTimeConversionUtil;
import org.lastaflute.di.util.LdiTimestampConversionUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class PropertyDescImpl implements PropertyDesc {

    private static final Object[] EMPTY_ARGS = new Object[0];

    private String propertyName;
    private Class<?> propertyType;
    private Method readMethod;
    private Method writeMethod;
    private Field field;
    private BeanDesc beanDesc;
    private Constructor<?> stringConstructor;
    private Method valueOfMethod;
    private boolean readable = false;
    private boolean writable = false;
    private ParameterizedClassDesc parameterizedClassDesc;

    public PropertyDescImpl(String propertyName, Class<?> propertyType, Method readMethod, Method writeMethod, BeanDesc beanDesc) {
        this(propertyName, propertyType, readMethod, writeMethod, null, beanDesc);
    }

    public PropertyDescImpl(String propertyName, Class<?> propertyType, Method readMethod, Method writeMethod, Field field,
            BeanDesc beanDesc) {
        if (propertyName == null) {
            throw new EmptyRuntimeException("propertyName");
        }
        if (propertyType == null) {
            throw new EmptyRuntimeException("propertyType");
        }
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        setReadMethod(readMethod);
        setWriteMethod(writeMethod);
        setField(field);
        this.beanDesc = beanDesc;
        setupStringConstructor();
        setupValueOfMethod();
        setUpParameterizedClassDesc();
    }

    private void setupStringConstructor() {
        Constructor<?>[] cons = propertyType.getConstructors();
        for (int i = 0; i < cons.length; ++i) {
            Constructor<?> con = cons[i];
            if (con.getParameterTypes().length == 1 && con.getParameterTypes()[0].equals(String.class)) {
                stringConstructor = con;
                break;
            }
        }
    }

    private void setupValueOfMethod() {
        Method[] methods = propertyType.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if (LdiMethodUtil.isBridgeMethod(method) || LdiMethodUtil.isSyntheticMethod(method)) {
                continue;
            }
            if (LdiModifierUtil.isStatic(method.getModifiers()) && method.getName().equals("valueOf")
                    && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(String.class)) {
                valueOfMethod = method;
                break;
            }
        }
    }

    private void setUpParameterizedClassDesc() {
        final Map<TypeVariable<?>, Type> typeVariables = ((BeanDescImpl) beanDesc).getTypeVariables();
        if (field != null) {
            parameterizedClassDesc = ParameterizedClassDescFactory.createParameterizedClassDesc(field, typeVariables);
        } else if (readMethod != null) {
            parameterizedClassDesc = ParameterizedClassDescFactory.createParameterizedClassDesc(readMethod, typeVariables);
        } else if (writeMethod != null) {
            parameterizedClassDesc = ParameterizedClassDescFactory.createParameterizedClassDesc(writeMethod, 0, typeVariables);
        }
    }

    public final String getPropertyName() {
        return propertyName;
    }

    public final Class<?> getPropertyType() {
        return propertyType;
    }

    public final Method getReadMethod() {
        return readMethod;
    }

    public final void setReadMethod(Method readMethod) {
        this.readMethod = readMethod;
        if (readMethod != null) {
            readable = true;
            readMethod.setAccessible(true);
        }
    }

    public final boolean hasReadMethod() {
        return readMethod != null;
    }

    public final Method getWriteMethod() {
        return writeMethod;
    }

    public final void setWriteMethod(Method writeMethod) {
        this.writeMethod = writeMethod;
        if (writeMethod != null) {
            writable = true;
            writeMethod.setAccessible(true);
        }
    }

    public final boolean hasWriteMethod() {
        return writeMethod != null;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
        if (field != null && LdiModifierUtil.isPublic(field)) {
            readable = true;
            writable = !LdiModifierUtil.isFinal(field);
        }
    }

    public boolean isReadable() {
        return readable;
    }

    public boolean isWritable() {
        return writable;
    }

    public final Object getValue(Object target) {
        try {
            if (!readable) {
                throw new IllegalStateException(propertyName + " is not readable.");
            } else if (hasReadMethod()) {
                return LdiMethodUtil.invoke(readMethod, target, EMPTY_ARGS);
            } else {
                return LdiFieldUtil.get(field, target);
            }
        } catch (Throwable t) {
            throw new BeanIllegalPropertyException(beanDesc.getBeanClass(), propertyName, t);
        }
    }

    public final void setValue(Object target, Object value) {
        try {
            value = convertIfNeed(value);
            if (!writable) {
                throw new IllegalStateException(propertyName + " is not writable.");
            } else if (hasWriteMethod()) {
                try {
                    LdiMethodUtil.invoke(writeMethod, target, new Object[] { value });
                } catch (Throwable t) {
                    Class<?> clazz = writeMethod.getDeclaringClass();
                    Class<?> valueClass = value == null ? null : value.getClass();
                    Class<?> targetClass = target == null ? null : target.getClass();
                    throw new SIllegalArgumentException("ESSR0098",
                            new Object[] { clazz.getName(), clazz.getClassLoader(), propertyType.getName(), propertyType.getClassLoader(),
                                    propertyName, valueClass == null ? null : valueClass.getName(),
                                    valueClass == null ? null : valueClass.getClassLoader(), value,
                                    targetClass == null ? null : targetClass.getName(),
                                    targetClass == null ? null : targetClass.getClassLoader() }).initCause(t);
                }
            } else {
                LdiFieldUtil.set(field, target, value);
            }
        } catch (Throwable t) {
            // TODO jflute lastaflute: [E] fitting DI :: property desc exception message from DBFlute
            throw new BeanIllegalPropertyException(beanDesc.getBeanClass(), propertyName, t);
        }
    }

    public BeanDesc getBeanDesc() {
        return beanDesc;
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("property:{");
        sb.append(propertyName);
        sb.append(", ").append(propertyType.getName());
        sb.append(", reader=").append(readMethod != null ? readMethod.getName() : null);
        sb.append(", writer=").append(writeMethod != null ? writeMethod.getName() : null);
        sb.append("}@").append(Integer.toHexString(hashCode()));
        return sb.toString();
    }

    public Object convertIfNeed(Object arg) { // #date_parade
        if (propertyType.isPrimitive()) {
            return convertPrimitiveWrapper(arg);
        } else if (Number.class.isAssignableFrom(propertyType)) {
            return convertNumber(arg);
        } else if (java.util.Date.class.isAssignableFrom(propertyType)) {
            return convertDate(arg);
            // no prepared conversion logic so cannot, while no need to convert heres
            //} else if (LocalDate.class.isAssignableFrom(propertyType)) {
            //    return DfTypeUtil.toLocalDate(arg);
            //} else if (LocalDate.class.isAssignableFrom(propertyType)) {
            //    return DfTypeUtil.toLocalDate(arg);
            //} else if (LocalDateTime.class.isAssignableFrom(propertyType)) {
            //    return DfTypeUtil.toLocalDateTime(arg);
        } else if (Boolean.class.isAssignableFrom(propertyType)) {
            return LdiBooleanConversionUtil.toBoolean(arg);
        } else if (arg != null && arg.getClass() != String.class && String.class == propertyType) {
            return arg.toString();
        } else if (arg instanceof String && !String.class.equals(propertyType)) {
            return convertWithString(arg);
        } else if (java.util.Calendar.class.isAssignableFrom(propertyType)) {
            return LdiCalendarConversionUtil.toCalendar(arg);
        }
        return arg;
    }

    private Object convertPrimitiveWrapper(Object arg) {
        return LdiNumberConversionUtil.convertPrimitiveWrapper(propertyType, arg);
    }

    private Object convertNumber(Object arg) {
        return LdiNumberConversionUtil.convertNumber(propertyType, arg);
    }

    private Object convertDate(Object arg) {
        if (propertyType == java.util.Date.class) {
            return LdiDateConversionUtil.toDate(arg);
        } else if (propertyType == Timestamp.class) {
            return LdiTimestampConversionUtil.toTimestamp(arg);
        } else if (propertyType == java.sql.Date.class) {
            return LdiSqlDateConversionUtil.toDate(arg);
        } else if (propertyType == Time.class) {
            return LdiTimeConversionUtil.toTime(arg);
        }
        return arg;
    }

    private Object convertWithString(Object arg) {
        if (stringConstructor != null) {
            return LdiConstructorUtil.newInstance(stringConstructor, new Object[] { arg });
        }
        if (valueOfMethod != null) {
            return LdiMethodUtil.invoke(valueOfMethod, null, new Object[] { arg });
        }
        return arg;
    }

    public boolean isParameterized() {
        return parameterizedClassDesc != null && parameterizedClassDesc.isParameterizedClass();
    }

    public ParameterizedClassDesc getParameterizedClassDesc() {
        return parameterizedClassDesc;
    }

    public Class<?> getElementClassOfCollection() {
        if (!Collection.class.isAssignableFrom(propertyType) || !isParameterized()) {
            return null;
        }
        final ParameterizedClassDesc pcd = parameterizedClassDesc.getArguments()[0];
        if (pcd == null) {
            return null;
        }
        return pcd.getRawClass();
    }

    public Class<?> getKeyClassOfMap() {
        if (!Map.class.isAssignableFrom(propertyType) || !isParameterized()) {
            return null;
        }
        final ParameterizedClassDesc pcd = parameterizedClassDesc.getArguments()[0];
        if (pcd == null) {
            return null;
        }
        return pcd.getRawClass();
    }

    public Class<?> getValueClassOfMap() {
        if (!Map.class.isAssignableFrom(propertyType) || !isParameterized()) {
            return null;
        }
        final ParameterizedClassDesc pcd = parameterizedClassDesc.getArguments()[1];
        if (pcd == null) {
            return null;
        }
        return pcd.getRawClass();
    }
}
