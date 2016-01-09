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
package org.lastaflute.di.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.lastaflute.di.exception.IORuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiJarFileUtil {

    protected LdiJarFileUtil() {
    }

    /**
     * @param file
     * @return 
     * @throws IORuntimeException
     */
    public static JarFile create(final String file) {
        try {
            return new JarFile(file);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * @param file
     * @return 
     * @throws IORuntimeException
     */
    public static JarFile create(final File file) {
        try {
            return new JarFile(file);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * @param file
     * @param entry
     * @return 
     * @throws IORuntimeException
     */
    public static InputStream getInputStream(final JarFile file, final ZipEntry entry) {
        try {
            return file.getInputStream(entry);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * @param jarUrl
     * @return 
     * @throws IORuntimeException
     */
    public static JarFile toJarFile(final URL jarUrl) {
        final URLConnection con = LdiURLUtil.openConnection(jarUrl);
        if (con instanceof JarURLConnection) {
            return LdiJarURLConnectionUtil.getJarFile((JarURLConnection) con);
        }
        return create(new File(toJarFilePath(jarUrl)));
    }

    /**
     * @param jarUrl
     * @return 
     * @throws IORuntimeException
     */
    public static String toJarFilePath(final URL jarUrl) {
        final URL nestedUrl = LdiURLUtil.create(jarUrl.getPath());
        final String nestedUrlPath = nestedUrl.getPath();
        final int pos = nestedUrlPath.lastIndexOf('!');
        final String jarFilePath = nestedUrlPath.substring(0, pos);
        final File jarFile = new File(LdiURLUtil.decode(jarFilePath, "UTF8"));
        return LdiFileUtil.getCanonicalPath(jarFile);
    }

    /**
     * @param jarFile
     * @throws IORuntimeException
     */
    public static void close(final JarFile jarFile) {
        try {
            jarFile.close();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

}
