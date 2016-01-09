/*
 * Copyright 2015-2016 the original author or authors.
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

import org.lastaflute.di.util.ClassTraversal;
import org.lastaflute.di.util.LdiResourceUtil;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class FileSystemComponentAutoRegister extends AbstractComponentAutoRegister {

    public void registerAll() {
        final File packageDir = getRootDir();
        final String[] referencePackages = getTargetPackages();
        for (int i = 0; i < referencePackages.length; ++i) {
            ClassTraversal.forEach(packageDir, referencePackages[i], this);
        }
    }

    /**
     * @return 
     */
    protected File getRootDir() {
        final String path = getContainer().getPath();
        final String[] names = LdiStringUtil.split(path, "/");
        File file = LdiResourceUtil.getResourceAsFile(path);
        for (int i = 0; i < names.length; ++i) {
            file = file.getParentFile();
        }
        return file;
    }

}
