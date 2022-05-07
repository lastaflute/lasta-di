/*
 * Copyright 2015-2022 the original author or authors.
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.lastaflute.di.helper.log.LaLogger;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class PropertyInterType extends AbstractInterType {

    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";

    protected static final int NONE = 0;
    protected static final int READ = 1;
    protected static final int WRITE = 2;
    protected static final int READWRITE = 3;

    protected static final String STR_NONE = "none";
    protected static final String STR_READ = "read";
    protected static final String STR_WRITE = "write";
    protected static final String STR_READWRITE = "readwrite";

    private static LaLogger logger = LaLogger.getLogger(PropertyInterType.class);

    private static PropertyAnnotationHandler annotationHandler = new DefaultPropertyAnnotationHandler();

    private int defaultPropertyType = READWRITE;

    static {
        setupAnnotationHandler();
    }

    protected static int valueOf(String type) {
        int propertyType = NONE;
        if (STR_READ.equals(type)) {
            propertyType = READ;
        } else if (STR_WRITE.equals(type)) {
            propertyType = WRITE;
        } else if (STR_READWRITE.equals(type)) {
            propertyType = READWRITE;
        }
        return propertyType;
    }

    private static void setupAnnotationHandler() {
        annotationHandler = (PropertyAnnotationHandler) LdiClassUtil.newInstance(TigerPropertyAnnotationHandler.class);
    }

    public void setDefaultPropertyType(String defaultPropertyType) {
        this.defaultPropertyType = valueOf(defaultPropertyType);
    }

    protected void introduce() {
        if (logger.isDebugEnabled()) {
            logger.debug("[PropertyInterType] Introducing... " + targetClass.getName());
        }

        int defaultValue = annotationHandler.getPropertyType(getTargetClass(), defaultPropertyType);
        List<Field> targetFields = getTargetFields(targetClass);

        for (Iterator<Field> iter = targetFields.iterator(); iter.hasNext();) {
            Field field = (Field) iter.next();
            int property = annotationHandler.getPropertyType(field, defaultValue);
            switch (property) {
            case READ:
                createGetter(targetClass, field);
                break;

            case WRITE:
                createSetter(targetClass, field);
                break;

            case READWRITE:
                createGetter(targetClass, field);
                createSetter(targetClass, field);
                break;

            default:
                break;
            }
        }
    }

    private void createGetter(Class<?> targetClass, Field targetField) {
        String targetFieldName = targetField.getName();
        String methodName = GETTER_PREFIX + createMethodName(targetFieldName);
        if (hasMethod(methodName, null)) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[PropertyInterType] Creating getter " + targetClass.getName() + "#" + methodName);
        }

        StringBuffer src = new StringBuffer(512);
        src.append("{");
        src.append("return this.");
        src.append(targetFieldName);
        src.append(";}");

        addMethod(targetField.getType(), methodName, src.toString());
    }

    private void createSetter(Class<?> targetClass, Field targetField) {
        String targetFieldName = targetField.getName();
        String methodName = SETTER_PREFIX + createMethodName(targetFieldName);
        if (hasMethod(methodName, targetField.getType())) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[PropertyInterType] Creating setter " + targetClass.getName() + "#" + methodName);
        }

        StringBuffer src = new StringBuffer(512);
        src.append("{");
        src.append("this.");
        src.append(targetFieldName);
        src.append(" = $1;}");

        addMethod(methodName, new Class[] { targetField.getType() }, src.toString());
    }

    private List<Field> getTargetFields(Class<?> targetClass) {
        final Map<String, Field> nominationFields = new LinkedHashMap<String, Field>();
        gatherFields(targetClass, nominationFields);
        final List<Field> targetFields = new ArrayList<Field>(nominationFields.size());
        for (final Iterator<Field> ite = nominationFields.values().iterator(); ite.hasNext();) {
            final Field field = ite.next();
            final int modifier = field.getModifiers();
            if (!Modifier.isPrivate(modifier)) {
                targetFields.add(field);
            }
        }
        return targetFields;
    }

    private void gatherFields(final Class<?> targetClass, final Map<String, Field> fields) {
        final Field[] declaredFields = targetClass.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; ++i) {
            final Field field = declaredFields[i];
            final String name = field.getName();
            if (!fields.containsKey(name)) {
                fields.put(name, field);
            }
        }
        final Class<?> superClass = targetClass.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            gatherFields(superClass, fields);
        }
    }

    private String createMethodName(String fieldName) {
        String methodName = LdiStringUtil.capitalize(fieldName);
        if (methodName.endsWith("_")) {
            methodName = methodName.substring(0, methodName.length() - 1);
        }

        return methodName;
    }

    private boolean hasMethod(String methodName, Class<?> paramType) {
        Class<?>[] param = null;
        if (paramType != null) {
            param = new Class[] { paramType };
        }
        try {
            getTargetClass().getMethod(methodName, param);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public interface PropertyAnnotationHandler {

        int getPropertyType(Class<?> clazz, int defaultValue);

        int getPropertyType(Field field, int defaultValue);
    }

    public static class DefaultPropertyAnnotationHandler implements PropertyAnnotationHandler {

        public int getPropertyType(Class<?> clazz, int defaultValue) {
            return defaultValue;
        }

        public int getPropertyType(Field field, int defaultValue) {
            return defaultValue;
        }
    }
}
