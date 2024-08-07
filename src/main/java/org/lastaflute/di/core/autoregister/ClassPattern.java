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
package org.lastaflute.di.core.autoregister;

import java.util.regex.Pattern;

import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 * 
 */
public class ClassPattern {

    private String packageName;

    private Pattern[] shortClassNamePatterns;

    public ClassPattern() {
    }

    /**
     * @param packageName
     * @param shortClassNames
     */
    public ClassPattern(String packageName, String shortClassNames) {
        setPackageName(packageName);
        setShortClassNames(shortClassNames);
    }

    /**
     * @return
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @param shortClassNames
     */
    public void setShortClassNames(String shortClassNames) {
        String[] classNames = LdiStringUtil.split(shortClassNames, ",");
        shortClassNamePatterns = new Pattern[classNames.length];
        for (int i = 0; i < classNames.length; ++i) {
            String s = classNames[i].trim();
            shortClassNamePatterns[i] = Pattern.compile(s);
        }
    }

    /**
     * @param shortClassName
     * @return
     */
    public boolean isAppliedShortClassName(String shortClassName) {
        if (shortClassNamePatterns == null) {
            return true;
        }
        for (int i = 0; i < shortClassNamePatterns.length; ++i) {
            if (shortClassNamePatterns[i].matcher(shortClassName).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param pName
     * @return
     */
    public boolean isAppliedPackageName(String pName) {
        if (!LdiStringUtil.isEmpty(pName) && !LdiStringUtil.isEmpty(packageName)) {
            return appendDelimiter(pName).startsWith(appendDelimiter(packageName));
        }
        if (LdiStringUtil.isEmpty(pName) && LdiStringUtil.isEmpty(packageName)) {
            return true;
        }
        return false;
    }

    /**
     * @param name
     * @return 
     */
    protected static String appendDelimiter(final String name) {
        return name.endsWith(".") ? name : name + ".";
    }
}
