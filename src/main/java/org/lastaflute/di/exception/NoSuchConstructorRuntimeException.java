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
package org.lastaflute.di.exception;

import java.lang.reflect.Constructor;

import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiMethodUtil;

/**
 * {@link Constructor}が見つからない場合にスローされる{@link NoSuchMethodException}をラップする例外です。
 * 
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class NoSuchConstructorRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 8688818589925114466L;

    private Class targetClass;

    private Class[] argTypes;

    /**
     * {@link NoSuchConstructorRuntimeException}を作成します。
     * 
     * @param targetClass
     * @param argTypes
     * @param cause
     */
    public NoSuchConstructorRuntimeException(Class targetClass, Class[] argTypes, NoSuchMethodException cause) {

        super("ESSR0064", new Object[] { targetClass.getName(),
                LdiMethodUtil.getSignature(LdiClassUtil.getShortClassName(targetClass), argTypes), cause }, cause);
        this.targetClass = targetClass;
        this.argTypes = argTypes;
    }

    /**
     * ターゲットのクラスを返します。
     * 
     * @return
     */
    public Class getTargetClass() {
        return targetClass;
    }

    /**
     * 引数の型の配列を返します。
     * 
     * @return
     */
    public Class[] getArgTypes() {
        return argTypes;
    }
}
