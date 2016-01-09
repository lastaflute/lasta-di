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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiStringUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class AbstractAutoNaming implements AutoNaming {

    protected static final String IMPL = "Impl";
    protected static final String BEAN = "Bean";

    protected boolean decapitalize = true;
    protected Map<String, String> customizedNames = new HashMap<String, String>();
    protected Map<Pattern, String> replaceRules = new LinkedHashMap<Pattern, String>();

    public AbstractAutoNaming() {
        addIgnoreClassSuffix(IMPL);
        addIgnoreClassSuffix(BEAN);
    }

    public void setCustomizedName(final String fqcn, final String name) {
        customizedNames.put(fqcn, name);
    }

    public void addIgnoreClassSuffix(final String classSuffix) {
        addReplaceRule(classSuffix + "$", "");
    }

    public void addReplaceRule(final String regex, final String replacement) {
        replaceRules.put(Pattern.compile(regex), replacement);
    }

    public void clearReplaceRule() {
        customizedNames.clear();
        replaceRules.clear();
    }

    public void setDecapitalize(final boolean decapitalize) {
        this.decapitalize = decapitalize;
    }

    public String defineName(final String packageName, final String shortClassName) {
        final String customizedName = getCustomizedName(packageName, shortClassName);
        if (customizedName != null) {
            return customizedName;
        }
        return makeDefineName(packageName, shortClassName);
    }

    protected String getCustomizedName(final String packageName, final String shortClassName) {
        final String fqn = LdiClassUtil.concatName(packageName, shortClassName);
        return (String) customizedNames.get(fqn);
    }

    protected abstract String makeDefineName(final String packageName, final String shortClassName);

    protected String applyRule(String name) {
        for (Iterator<Entry<Pattern, String>> it = replaceRules.entrySet().iterator(); it.hasNext();) {
            final Entry<Pattern, String> entry = (Entry<Pattern, String>) it.next();
            final Pattern pattern = (Pattern) entry.getKey();
            final String replacement = (String) entry.getValue();
            final Matcher matcher = pattern.matcher(name);
            name = matcher.replaceAll(replacement);
        }
        name = normalize(name);
        if (decapitalize) {
            name = LdiStringUtil.decapitalize(name);
        }
        return name;
    }

    protected String normalize(final String name) {
        final String[] names = name.split("\\.");
        final StringBuffer buf = new StringBuffer(name.length());
        for (int i = 0; i < names.length; ++i) {
            buf.append(LdiStringUtil.capitalize(names[i]));
        }
        return new String(buf);
    }
}
