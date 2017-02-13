/*
 * Copyright 2015-2017 the original author or authors.
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
package org.lastaflute.di.core.assembler;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.exception.EmptyRuntimeException;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.beans.exception.BeanIllegalPropertyException;

/**
 * @author modified by jflute (originated in Seasar)
 * 
 */
public abstract class AbstractPropertyAssembler extends AbstractAssembler implements PropertyAssembler {

    public AbstractPropertyAssembler(ComponentDef componentDef) {
        super(componentDef);
    }

    protected void bindExternally(final BeanDesc beanDesc, final ComponentDef componentDef, final Object component, final Set<String> names)
            throws EmptyRuntimeException {
        final ExternalContext extCtx = componentDef.getContainer().getRoot().getExternalContext();
        if (extCtx == null) {
            throw new EmptyRuntimeException("externalContext");
        }

        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            final PropertyDesc pd = beanDesc.getPropertyDesc(i);
            if (!pd.isWritable()) {
                continue;
            }
            final String name = pd.getPropertyName();
            if (names.contains(name)) {
                continue;
            }
            final Object value = getValue(name, pd.getPropertyType(), extCtx);
            if (value == null) {
                continue;
            }
            try {
                pd.setValue(component, value);
                names.add(name);
            } catch (final BeanIllegalPropertyException ignore) {}
        }
    }

    protected Object getValue(final String name, final Class<?> type, final ExternalContext extCtx) {
        if (type.isArray()) {
            Object[] values = getValues(name, extCtx);
            if (values != null) {
                return values;
            }
        } else if (List.class.isAssignableFrom(type)) {
            final Object[] values = getValues(name, extCtx);
            if (values != null) {
                return Arrays.asList(values);
            }
        }
        return getValue(name, extCtx);
    }

    protected Object getValue(final String name, final ExternalContext extCtx) {
        Object value = extCtx.getRequestParameterMap().get(name);
        if (value != null) {
            return value;
        }
        value = extCtx.getRequestHeaderMap().get(name);
        if (value != null) {
            return value;
        }
        return extCtx.getRequestMap().get(name);
    }

    protected Object[] getValues(final String name, final ExternalContext extCtx) {
        Object[] values = (Object[]) extCtx.getRequestParameterValuesMap().get(name);
        if (values != null && values.length > 0) {
            return values;
        }
        values = (Object[]) extCtx.getRequestHeaderValuesMap().get(name);
        if (values != null && values.length > 0) {
            return values;
        }
        return null;
    }
}
