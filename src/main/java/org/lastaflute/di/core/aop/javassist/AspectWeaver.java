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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lastaflute.di.core.aop.InterType;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.util.ClassPoolUtil;
import org.lastaflute.di.exception.NoSuchFieldRuntimeException;
import org.lastaflute.di.util.LdiClassLoaderUtil;
import org.lastaflute.di.util.LdiFieldUtil;
import org.lastaflute.di.util.LdiMethodUtil;
import org.lastaflute.di.util.LdiSrl;

import javassist.ClassPool;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class AspectWeaver {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PREFIX_ENHANCED_CLASS = "$$";
    public static final String SUFFIX_ENHANCED_CLASS = "$$EnhancedByLastaDi$$";
    public static final String SUFFIX_METHOD_INVOCATION_CLASS = "$$MethodInvocation$$";
    public static final String SUFFIX_INVOKE_SUPER_METHOD = "$$invokeSuperMethod$$";
    public static final String SEPARATOR_METHOD_INVOCATION_CLASS = "$$";

    // -----------------------------------------------------
    //                                               Mutable
    //                                               -------
    protected static final Set<String> enhancedClassNames = Collections.synchronizedSet(new HashSet<String>());

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Class<?> targetClass; // not null
    protected final Map<?, ?> parameters; // null allowed, no modification here

    protected final ClassPool classPool; // not null
    protected final String enhancedClassName; // not null
    protected final EnhancedClassGenerator enhancedClassGenerator; // not null

    // -----------------------------------------------------
    //                                               Mutable
    //                                               -------
    protected final List<Class<?>> methodInvocationClassList = new ArrayList<Class<?>>();
    protected Class<?> enhancedClass; // null allowed as lazy loaded

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AspectWeaver(final Class<?> targetClass, final Map<?, ?> parameters) {
        this.targetClass = targetClass;
        this.parameters = parameters;

        classPool = initializeClassPool(targetClass);
        enhancedClassName = buildEnhancedClassName();
        enhancedClassGenerator = createEnhancedClassGenerator(targetClass);
    }

    protected ClassPool initializeClassPool(final Class<?> targetClass) {
        return ClassPoolUtil.getClassPool(targetClass);
    }

    protected EnhancedClassGenerator createEnhancedClassGenerator(final Class<?> targetClass) {
        return newEnhancedClassGenerator(classPool, targetClass, enhancedClassName);
    }

    protected EnhancedClassGenerator newEnhancedClassGenerator(final ClassPool classPool, final Class<?> targetClass,
            final String enhancedClassName) {
        return new EnhancedClassGenerator(classPool, targetClass, enhancedClassName);
    }

    // ===================================================================================
    //                                                                         Interceptor
    //                                                                         ===========
    public void setInterceptors(final Method method, final MethodInterceptor[] interceptors) {
        final String methodInvocationClassName = buildMethodInvocationClassName(method);
        final MethodInvocationClassGenerator methodInvocationClassGenerator =
                createMethodInvocationClassGenerator(methodInvocationClassName);

        final String invokeSuperMethodName = createInvokeSuperMethod(method);
        methodInvocationClassGenerator.createProceedMethod(method, invokeSuperMethodName);
        enhancedClassGenerator.createTargetMethod(method, methodInvocationClassName);

        final Class<?> methodInvocationClass = methodInvocationClassGenerator.toClass(LdiClassLoaderUtil.getClassLoader(targetClass));
        setStaticField(methodInvocationClass, "method", method);
        setStaticField(methodInvocationClass, "interceptors", interceptors);
        setStaticField(methodInvocationClass, "parameters", parameters);
        methodInvocationClassList.add(methodInvocationClass);
    }

    protected MethodInvocationClassGenerator createMethodInvocationClassGenerator(final String methodInvocationClassName) {
        return newMethodInvocationClassGenerator(classPool, enhancedClassName, methodInvocationClassName);
    }

    protected MethodInvocationClassGenerator newMethodInvocationClassGenerator(final ClassPool classPool, final String enhancedClassName,
            final String methodInvocationClassName) {
        return new MethodInvocationClassGenerator(classPool, enhancedClassName, methodInvocationClassName);
    }

    // ===================================================================================
    //                                                                          Inter Type
    //                                                                          ==========
    public void setInterTypes(final InterType[] interTypes) {
        if (interTypes == null) {
            return;
        }

        for (int i = 0; i < interTypes.length; ++i) {
            enhancedClassGenerator.applyInterType(interTypes[i]);
        }
    }

    // ===================================================================================
    //                                                                    Define the Class
    //                                                                    ================
    public Class<?> generateClass() {
        if (enhancedClass == null) {
            // define the class to the class loader
            enhancedClass = enhancedClassGenerator.toClass(LdiClassLoaderUtil.getClassLoader(targetClass));

            for (int i = 0; i < methodInvocationClassList.size(); ++i) {
                final Class<?> methodInvocationClass = (Class<?>) methodInvocationClassList.get(i);
                setStaticField(methodInvocationClass, "targetClass", targetClass);
            }
        }
        return enhancedClass;
    }

    // ===================================================================================
    //                                                                        Static Field
    //                                                                        ============
    public void setStaticField(final Class<?> clazz, final String name, final Object value) {
        try {
            final Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            LdiFieldUtil.set(field, name, value);
            field.setAccessible(false);
        } catch (final NoSuchFieldException e) {
            throw new NoSuchFieldRuntimeException(enhancedClass, name, e);
        }
    }

    // ===================================================================================
    //                                                                       Name Handling
    //                                                                       =============
    public String buildEnhancedClassName() {
        final StringBuilder sb = new StringBuilder(200);
        final String targetClassName = targetClass.getName();
        final Package pkg = targetClass.getPackage();
        if (targetClassName.startsWith("java.") || (pkg != null && pkg.isSealed())) {
            sb.append(PREFIX_ENHANCED_CLASS);
        }
        sb.append(targetClassName).append(SUFFIX_ENHANCED_CLASS).append(Integer.toHexString(hashCode()));

        // #for_now jflute strictly not thread-safe? (2024/06/18)
        final int length = sb.length();
        for (int i = 0; enhancedClassNames.contains(new String(sb)); ++i) {
            sb.setLength(length);
            sb.append("_").append(i);
        }

        String name = new String(sb);
        enhancedClassNames.add(name);
        return name;
    }

    public String buildMethodInvocationClassName(final Method method) {
        // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
        // MethodHandles way requires that lookup class's package is same as enhanced class's one:
        // { @log
        //  Caused by: java.lang.IllegalArgumentException:
        //  org.docksidestage.unit.DemoTest$Sea$$EnhancedByLastaDi$$2d66530f$$MethodInvocation$$mystic$$0
        //  not in same package as lookup class
        //   at java.base/java.lang.invoke.MethodHandleStatics.newIllegalArgumentException(MethodHandleStatics.java:167)
        //   at java.base/java.lang.invoke.MethodHandles$Lookup$ClassFile.newInstance(MethodHandles.java:2283)
        // }
        // so add the template class's package and convert enhanced class's package to name part
        //  e.g. org.lastaflute.di.core.aop.javassist.OrgDocksidestageSea$$EnhancedByLastaDi$$2d66530f$$MethodInvocation$$mystic$$0
        // _/_/_/_/_/_/_/_/_/_/
        final String pkg = MethodInvocationClassGenerator.getTemplateClass().getPackage().getName(); // always has package
        final String enhancedIdentity = LdiSrl.camelize(enhancedClassName, ".");
        final String invocationSuffix = SUFFIX_METHOD_INVOCATION_CLASS;
        final String methodName = method.getName();
        final String invocationSeparator = SEPARATOR_METHOD_INVOCATION_CLASS;
        final int classCount = methodInvocationClassList.size();
        return pkg + "." + enhancedIdentity + invocationSuffix + methodName + invocationSeparator + classCount;
        // before:
        //return enhancedClassName + SUFFIX_METHOD_INVOCATION_CLASS + method.getName() + SEPARATOR_METHOD_INVOCATION_CLASS
        //        + methodInvocationClassList.size();
    }

    public String createInvokeSuperMethod(final Method method) { // should be createInvokeSuperMethod[Name]()
        final String invokeSuperMethodName = PREFIX_ENHANCED_CLASS + method.getName() + SUFFIX_INVOKE_SUPER_METHOD;
        if (!LdiMethodUtil.isAbstract(method)) {
            enhancedClassGenerator.createInvokeSuperMethod(method, invokeSuperMethodName);
        }
        return invokeSuperMethodName;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Map<?, ?> getParameters() { // not null, read-only
        return parameters != null ? Collections.unmodifiableMap(parameters) : Collections.emptyMap();
    }

    public ClassPool getClassPool() {
        return classPool;
    }

    public String getEnhancedClassName() {
        return enhancedClassName;
    }

    public EnhancedClassGenerator getEnhancedClassGenerator() {
        return enhancedClassGenerator;
    }
}