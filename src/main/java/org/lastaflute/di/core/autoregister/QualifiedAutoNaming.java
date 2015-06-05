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
package org.lastaflute.di.core.autoregister;

import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class QualifiedAutoNaming extends AbstractAutoNaming {

    /**
     * @param packagePrefix
     */
    public void addIgnorePackagePrefix(final String packagePrefix) {
        String regex = "^" + LdiStringUtil.replace(packagePrefix, ".", "\\.");
        if (!regex.endsWith(".")) {
            regex += "\\.";
        }
        addReplaceRule(regex, "");
    }

    protected String makeDefineName(final String packageName, final String shortClassName) {
        return applyRule(LdiClassUtil.concatName(packageName, shortClassName));
    }
}
