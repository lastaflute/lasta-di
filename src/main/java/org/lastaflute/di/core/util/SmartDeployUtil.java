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
package org.lastaflute.di.core.util;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.LaContainerFactory;
import org.lastaflute.di.core.factory.provider.LaContainerProvider;
import org.lastaflute.di.core.meta.impl.LaContainerBehavior;
import org.lastaflute.di.core.smart.cool.LaContainerFactoryCoolProvider;
import org.lastaflute.di.core.smart.hot.HotdeployUtil;
import org.lastaflute.di.core.smart.warm.WarmdeployBehavior;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.factory.BeanDescFactory;
import org.lastaflute.di.util.LdiFieldUtil;

/**
 * @author shot
 */
public class SmartDeployUtil {

    protected SmartDeployUtil() {
    }

    public static boolean isSmartdeployMode(LaContainer container) {
        return isHotdeployMode(container) || isCooldeployMode(container) || isWarmdeployMode(container);
    }

    public static boolean isHotdeployMode(LaContainer container) {
        return HotdeployUtil.isHotdeploy();
    }

    public static boolean isCooldeployMode(LaContainer container) {
        BeanDesc bd = BeanDescFactory.getBeanDesc(LaContainerFactory.class);
        LaContainerProvider provider = (LaContainerProvider) LdiFieldUtil.get(bd.getField("provider"), null);
        if (provider instanceof LaContainerFactoryCoolProvider) {
            return true;
        }
        return false;
    }

    public static boolean isWarmdeployMode(LaContainer container) {
        LaContainerBehavior.Provider provider = LaContainerBehavior.getProvider();
        return provider instanceof WarmdeployBehavior;
    }

    public static String getDeployMode(LaContainer container) {
        if (isHotdeployMode(container)) {
            return "Hot Deploy";
        } else if (isCooldeployMode(container)) {
            return "Cool Deploy";
        } else if (isWarmdeployMode(container)) {
            return "Warm Deploy";
        } else {
            return "Normal Mode";
        }
    }
}
