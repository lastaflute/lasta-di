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
package org.lastaflute.di.core.autoregister;

import java.io.File;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.lastaflute.di.exception.ResourceNotFoundRuntimeException;
import org.lastaflute.di.util.ClassTraversal;
import org.lastaflute.di.util.LdiJarFileUtil;
import org.lastaflute.di.util.LdiResourceUtil;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractJarComponentAutoRegister extends AbstractComponentAutoRegister {

    private String baseDir;

    private Pattern[] jarFileNamePatterns;

    /**
     * @return
     */
    public String getBaseDir() {
        return baseDir;
    }

    /**
     * @param baseDir
     */
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public void registerAll() {
        if (baseDir == null) {
            setupBaseDir();
        }
        File dir = new File(baseDir);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new ResourceNotFoundRuntimeException(baseDir);
        }
        String[] jars = dir.list();
        for (int i = 0; i < jars.length; ++i) {
            if (!isAppliedJar(jars[i])) {
                continue;
            }
            final JarFile jarFile = LdiJarFileUtil.create(findJar(jars[i]));
            ClassTraversal.forEach(jarFile, this);
        }
    }

    protected abstract void setupBaseDir();

    /**
     * @param jarFileName
     * @return 
     */
    protected boolean isAppliedJar(final String jarFileName) {
        if (jarFileNamePatterns == null) {
            return true;
        }
        String extention = LdiResourceUtil.getExtension(jarFileName);
        if (extention == null || !extention.equalsIgnoreCase("jar")) {
            return false;
        }
        String name = LdiResourceUtil.removeExtension(jarFileName);
        for (int i = 0; i < jarFileNamePatterns.length; ++i) {
            if (jarFileNamePatterns[i].matcher(name).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param jarFileName
     * @return 
     */
    protected File findJar(final String jarFileName) {
        return new File(baseDir, jarFileName);
    }

    /**
     * @param jarFileNames
     */
    public void setJarFileNames(String jarFileNames) {
        String[] array = LdiStringUtil.split(jarFileNames, ",");
        jarFileNamePatterns = new Pattern[array.length];
        for (int i = 0; i < array.length; ++i) {
            String s = array[i].trim();
            jarFileNamePatterns[i] = Pattern.compile(s);
        }
    }
}
