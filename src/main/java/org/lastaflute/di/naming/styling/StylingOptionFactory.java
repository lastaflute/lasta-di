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
package org.lastaflute.di.naming.styling;

import org.lastaflute.di.core.LastaDiProperties;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;
import org.lastaflute.di.util.LdiClassUtil;

/**
 * @author jflute
 * @since 0.9.0 (2021/10/03 Sunday at roppongi japanese)
 */
public class StylingOptionFactory {

    public StylingFreedomInterfaceMapper prepareFreedomInterfaceMapper() { // null allowed
        final String mapperName = LastaDiProperties.getInstance().getNamingStylingFreedomInterfaceMapper();
        if (mapperName == null) {
            return null;
        }
        final Object mapperInstance;
        try {
            final Class<?> mapperType = LdiClassUtil.forName(mapperName);
            mapperInstance = LdiClassUtil.newInstance(mapperType);
        } catch (RuntimeException e) {
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot instantiate the freedom interface mapper.");
            br.addItem("Advice");
            br.addElement("Confirm your mapper definition specified in lasta_di.properties.");
            br.addElement("For example, the mapper needs default constructor.");
            br.addItem("Mapper Name");
            br.addElement(mapperName);
            final String msg = br.buildExceptionMessage();
            throw new IllegalStateException(msg, e);
        }
        try {
            return StylingFreedomInterfaceMapper.class.cast(mapperInstance);
        } catch (ClassCastException e) {
            final String interfaceName = StylingFreedomInterfaceMapper.class.getSimpleName();
            final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
            br.addNotice("Cannot cast the freedom interface mapper.");
            br.addItem("Advice");
            br.addElement("Confirm your mapper definition specified in lasta_di.properties.");
            br.addElement("For example, the mapper should implement " + interfaceName + " interface.");
            br.addItem("Mapper Name");
            br.addElement(mapperName);
            final String msg = br.buildExceptionMessage();
            throw new IllegalStateException(msg, e);
        }
    }
}
