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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.di.exception.ResourceNotFoundRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class LdiResourceUtil {

    protected LdiResourceUtil() {
    }

    public static String getResourcePath(String path, String extension) {
        if (extension == null) {
            return path;
        }
        extension = "." + extension;
        if (path.endsWith(extension)) {
            return path;
        }
        return path.replace('.', '/') + extension;
    }

    public static String getResourcePath(Class<?> clazz) {
        return clazz.getName().replace('.', '/') + ".class";
    }

    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static URL getResource(String path) {
        return getResource(path, null);
    }

    public static URL getResource(String path, String extension) throws ResourceNotFoundRuntimeException {
        URL url = getResourceNoException(path, extension);
        if (url != null) {
            return url;
        }
        throw new ResourceNotFoundRuntimeException(getResourcePath(path, extension));
    }

    public static URL getResourceNoException(String path) {
        return getResourceNoException(path, null);
    }

    public static URL getResourceNoException(String path, String extension) {
        return getResourceNoException(path, extension, Thread.currentThread().getContextClassLoader());
    }

    public static URL getResourceNoException(String path, String extension, ClassLoader loader) {
        if (path == null || loader == null) {
            return null;
        }
        path = getResourcePath(path, extension);
        return loader.getResource(path);
    }

    public static InputStream getResourceAsStream(String path) {
        return getResourceAsStream(path, null);
    }

    public static InputStream getResourceAsStream(String path, String extension) {
        URL url = getResource(path, extension);
        return LdiURLUtil.openStream(url);
    }

    public static InputStream getResourceAsStreamNoException(String path) {
        return getResourceAsStreamNoException(path, null);
    }

    public static InputStream getResourceAsStreamNoException(String path, String extension) {
        URL url = getResourceNoException(path, extension);
        if (url == null) {
            return null;
        }
        try {
            return url.openStream();
        } catch (final IOException e) {
            return null;
        }
    }

    public static boolean isExist(String path) {
        return getResourceNoException(path) != null;
    }

    public static boolean exists(String path) { // new
        return getResourceNoException(path) != null;
    }

    public static Properties getProperties(String path) throws IORuntimeException {
        Properties props = new Properties();
        InputStream is = getResourceAsStream(path);
        try {
            props.load(is);
            return props;
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            LdiInputStreamUtil.closeSilently(is);
        }
    }

    public static String getExtension(String path) {
        int extPos = path.lastIndexOf(".");
        if (extPos >= 0) {
            return path.substring(extPos + 1);
        }
        return null;
    }

    public static String removeExtension(String path) {
        int extPos = path.lastIndexOf(".");
        if (extPos >= 0) {
            return path.substring(0, extPos);
        }
        return path;
    }

    public static File getBuildDir(Class<?> clazz) {
        return getBuildDir(getResourcePath(clazz));
    }

    public static File getBuildDir(String path) {
        File dir = null;
        URL url = getResource(path);
        if ("file".equals(url.getProtocol())) {
            int num = path.split("/").length;
            dir = new File(getFileName(url));
            for (int i = 0; i < num; ++i, dir = dir.getParentFile()) {}
        } else {
            dir = new File(LdiJarFileUtil.toJarFilePath(url));
        }
        return dir;
    }

    public static String toExternalForm(URL url) {
        String s = url.toExternalForm();
        return LdiURLUtil.decode(s, "UTF8");
    }

    public static String getFileName(URL url) {
        String s = url.getFile();
        return LdiURLUtil.decode(s, "UTF8");
    }

    public static File getFile(URL url) {
        File file = new File(getFileName(url));
        if (file != null && file.exists()) {
            return file;
        }
        return null;
    }

    public static File getResourceAsFile(String path) {
        return getResourceAsFile(path, null);
    }

    public static File getResourceAsFile(String path, String extension) {
        return getFile(getResource(path, extension));
    }

    public static File getResourceAsFileNoException(Class<?> clazz) {
        return getResourceAsFileNoException(getResourcePath(clazz));
    }

    public static File getResourceAsFileNoException(String path) {
        URL url = getResourceNoException(path);
        if (url == null) {
            return null;
        }
        return getFile(url);
    }

    public static String convertPath(String path, Class<?> clazz) {
        if (isExist(path)) {
            return path;
        }
        String prefix = clazz.getName().replace('.', '/').replaceFirst("/[^/]+$", "");
        String extendedPath = prefix + "/" + path;
        if (LdiResourceUtil.getResourceNoException(extendedPath) != null) {
            return extendedPath;
        }
        return path;
    }
}