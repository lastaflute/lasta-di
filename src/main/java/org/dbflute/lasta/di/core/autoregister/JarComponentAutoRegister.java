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
package org.dbflute.lasta.di.core.autoregister;

import java.io.File;
import java.net.URL;

import org.dbflute.lasta.di.core.aop.frame.MethodInterceptor;
import org.dbflute.lasta.di.util.LdiFileUtil;
import org.dbflute.lasta.di.util.LdiJarFileUtil;
import org.dbflute.lasta.di.util.LdiResourceUtil;
import org.dbflute.lasta.di.util.LdiZipFileUtil;

/**
 * jarファイルの中にあるコンポーネントを自動登録するクラスです。
 * 
 * @author modified by jflute (originated in Seasar)
 * @author modified by jflute (originated in Seasar)
 */
public class JarComponentAutoRegister extends AbstractJarComponentAutoRegister {

    private Class referenceClass = MethodInterceptor.class;

    /**
     * 参照クラスを返します。
     * 
     * @return
     */
    public Class getReferenceClass() {
        return referenceClass;
    }

    /**
     * jarファイルに含まれているクラスを設定します。jarファイルに含まれているならどのクラスでもOKです。
     * このクラスを参照してjarファイルの物理的な位置を特定します。
     * 
     * @param referenceClass
     */
    public void setReferenceClass(Class referenceClass) {
        this.referenceClass = referenceClass;
    }

    protected void setupBaseDir() {
        String path = LdiResourceUtil.getResourcePath(referenceClass);
        URL url = LdiResourceUtil.getResource(path);
        String fileName = null;
        if ("zip".equals(url.getProtocol())) {
            fileName = LdiZipFileUtil.toZipFilePath(url);
        } else {
            fileName = LdiJarFileUtil.toJarFilePath(url);
        }
        File jarFile = new File(fileName);
        File dir = jarFile.getParentFile();
        setBaseDir(LdiFileUtil.getCanonicalPath(dir));
    }

}