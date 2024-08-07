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
package org.lastaflute.di.helper.beans.exception;

import org.lastaflute.di.exception.SRuntimeException;
import org.lastaflute.di.util.LdiMethodUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class BeanMethodNotFoundException extends SRuntimeException {

    private static final long serialVersionUID = -3508955801981550317L;

    private Class<?> targetClass;
    private String methodName;
    private Class<?>[] methodArgClasses;

    public BeanMethodNotFoundException(Class<?> targetClass, String methodName, Object[] methodArgs) {
        super("ESSR0049", new Object[] { targetClass.getName(), LdiMethodUtil.getSignature(methodName, methodArgs) });
        this.targetClass = targetClass;
        this.methodName = methodName;
        if (methodArgs != null) {
            methodArgClasses = new Class[methodArgs.length];
            for (int i = 0; i < methodArgs.length; ++i) {
                if (methodArgs[i] != null) {
                    methodArgClasses[i] = methodArgs[i].getClass();
                }
            }
        }
    }

    public BeanMethodNotFoundException(Class<?> targetClass, String methodName, Class<?>[] methodArgClasses) {
        super("ESSR0049", new Object[] { targetClass.getName(), LdiMethodUtil.getSignature(methodName, methodArgClasses) });
        this.targetClass = targetClass;
        this.methodName = methodName;
        this.methodArgClasses = methodArgClasses;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getMethodArgClasses() {
        return methodArgClasses;
    }
}
