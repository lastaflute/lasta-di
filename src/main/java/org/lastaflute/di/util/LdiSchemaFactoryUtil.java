/*
 * Copyright 2015-2018 the original author or authors.
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
package org.lastaflute.di.util;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class LdiSchemaFactoryUtil {

    public static SchemaFactory newW3cXmlSchemaFactory() {
        return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    public static SchemaFactory newRelaxNgSchemaFactory() {
        return SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
    }

}
