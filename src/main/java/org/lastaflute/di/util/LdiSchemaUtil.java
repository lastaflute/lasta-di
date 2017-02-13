/*
 * Copyright 2015-2017 the original author or authors.
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

import java.io.File;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.lastaflute.di.exception.SAXRuntimeException;
import org.xml.sax.SAXException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class LdiSchemaUtil {

    /**
     * @param schema
     * @return {@link Schema}
     */
    public static Schema newW3cXmlSchema(final File schema) {
        return newSchema(LdiSchemaFactoryUtil.newW3cXmlSchemaFactory(), schema);
    }

    /**
     * @param schema
     * @return {@link Schema}
     */
    public static Schema newW3cXmlSchema(final Source schema) {
        return newSchema(LdiSchemaFactoryUtil.newW3cXmlSchemaFactory(), schema);
    }

    /**
     * @param schema
     * @return {@link Schema}
     */
    public static Schema newW3cXmlSchema(final URL schema) {
        return newSchema(LdiSchemaFactoryUtil.newW3cXmlSchemaFactory(), schema);
    }

    /**
     * @param schema
     * @return {@link Schema}
     */
    public static Schema newRelaxNgSchema(final File schema) {
        return newSchema(LdiSchemaFactoryUtil.newRelaxNgSchemaFactory(), schema);
    }

    /**
     * @param schema
     * @return {@link Schema}
     */
    public static Schema newRelaxNgSchema(final Source schema) {
        return newSchema(LdiSchemaFactoryUtil.newRelaxNgSchemaFactory(), schema);
    }

    /**
     * @param schema
     * @return {@link Schema}
     */
    public static Schema newRelaxNgSchema(final URL schema) {
        return newSchema(LdiSchemaFactoryUtil.newRelaxNgSchemaFactory(), schema);
    }

    /**
     * @param factory
     * @param schema
     * @return {@link Schema}
     */
    public static Schema newSchema(final SchemaFactory factory, final File schema) {
        try {
            return factory.newSchema(schema);
        } catch (final SAXException e) {
            throw new SAXRuntimeException(e);
        }
    }

    /**
     * @param factory
     * @param schema
     * @return {@link Schema}
     */
    public static Schema newSchema(final SchemaFactory factory, final Source schema) {
        try {
            return factory.newSchema(schema);
        } catch (final SAXException e) {
            throw new SAXRuntimeException(e);
        }
    }

    /**
     * @param factory
     * @param schema
     * @return {@link Schema}
     */
    public static Schema newSchema(final SchemaFactory factory, final URL schema) {
        try {
            return factory.newSchema(schema);
        } catch (final SAXException e) {
            throw new SAXRuntimeException(e);
        }
    }

}
