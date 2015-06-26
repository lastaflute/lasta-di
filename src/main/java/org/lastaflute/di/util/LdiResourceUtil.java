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

    public static String getResourcePath(Class clazz) {
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

    /**
     * リソースを返します。見つからなかった場合は<code>null</code>を返します。
     * 
     * @param path
     * @param extension
     * @param loader
     * @return リソース
     * @see #getResourcePath(String, String)
     */
    public static URL getResourceNoException(String path, String extension, ClassLoader loader) {
        if (path == null || loader == null) {
            return null;
        }
        path = getResourcePath(path, extension);
        return loader.getResource(path);
    }

    /**
     * リソースをストリームとして返します。
     * 
     * @param path
     * @return ストリーム
     * @see #getResourceAsStream(String, String)
     */
    public static InputStream getResourceAsStream(String path) {
        return getResourceAsStream(path, null);
    }

    /**
     * リソースをストリームとして返します。
     * 
     * @param path
     * @param extension
     * @return ストリーム
     * @see #getResource(String, String)
     */
    public static InputStream getResourceAsStream(String path, String extension) {
        URL url = getResource(path, extension);
        return LdiURLUtil.openStream(url);
    }

    /**
     * リソースをストリームとして返します。リソースが見つからなかった場合は<code>null</code>を返します。
     * 
     * @param path
     * @return ストリーム
     * @see #getResourceAsStreamNoException(String, String)
     */
    public static InputStream getResourceAsStreamNoException(String path) {
        return getResourceAsStreamNoException(path, null);
    }

    /**
     * リソースをストリームとして返します。リソースが見つからなかった場合は<code>null</code>を返します。
     * 
     * @param path
     * @param extension
     * @return ストリーム
     * @see #getResourceNoException(String, String)
     */
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

    /**
     * プロパティファイルを返します。
     * 
     * @param path
     * @return プロパティファイル
     * @throws IORuntimeException
     *             {@link IOException}が発生した場合
     */
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

    /**
     * 拡張子を返します。
     * 
     * @param path
     * @return 拡張子
     */
    public static String getExtension(String path) {
        int extPos = path.lastIndexOf(".");
        if (extPos >= 0) {
            return path.substring(extPos + 1);
        }
        return null;
    }

    /**
     * 拡張子を取り除きます。
     * 
     * @param path
     * @return 取り除いた後の結果
     */
    public static String removeExtension(String path) {
        int extPos = path.lastIndexOf(".");
        if (extPos >= 0) {
            return path.substring(0, extPos);
        }
        return path;
    }

    /**
     * クラスファイルが置かれているルートディレクトリを返します。
     * 
     * @param clazz
     * @return ルートディレクトリ
     * @see #getBuildDir(String)
     */
    public static File getBuildDir(Class clazz) {
        return getBuildDir(getResourcePath(clazz));
    }

    /**
     * クラスファイルが置かれているルートディレクトリを返します。
     * 
     * @param path
     * @return ルートディレクトリ
     */
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

    /**
     * 外部形式に変換します。
     * 
     * @param url
     * @return 外部形式
     */
    public static String toExternalForm(URL url) {
        String s = url.toExternalForm();
        return LdiURLUtil.decode(s, "UTF8");
    }

    /**
     * ファイル名を返します。
     * 
     * @param url
     * @return ファイル名
     */
    public static String getFileName(URL url) {
        String s = url.getFile();
        return LdiURLUtil.decode(s, "UTF8");
    }

    /**
     * ファイルを返します。
     * 
     * @param url
     * @return ファイル
     */
    public static File getFile(URL url) {
        File file = new File(getFileName(url));
        if (file != null && file.exists()) {
            return file;
        }
        return null;
    }

    /**
     * リソースをファイルとして返します。
     * 
     * @param path
     * @return ファイル
     * @see #getResourceAsFile(String, String)
     */
    public static File getResourceAsFile(String path) {
        return getResourceAsFile(path, null);
    }

    /**
     * リソースをファイルとして返します。
     * 
     * @param path
     * @param extension
     * @return ファイル
     * @see #getFile(URL)
     */
    public static File getResourceAsFile(String path, String extension) {
        return getFile(getResource(path, extension));
    }

    /**
     * リソースをファイルとして返します。リソースが見つからない場合は<code>null</code>を返します。
     * 
     * @param clazz
     * @return ファイル
     * @see #getResourceAsFileNoException(String)
     */
    public static File getResourceAsFileNoException(Class clazz) {
        return getResourceAsFileNoException(getResourcePath(clazz));
    }

    /**
     * リソースをファイルとして返します。リソースが見つからない場合は<code>null</code>を返します。
     * 
     * @param path
     * @return ファイル
     * @see #getResourceNoException(String)
     */
    public static File getResourceAsFileNoException(String path) {
        URL url = getResourceNoException(path);
        if (url == null) {
            return null;
        }
        return getFile(url);
    }

    /**
     * パスを変換します。
     * 
     * @param path
     * @param clazz
     * @return 変換された結果
     */
    public static String convertPath(String path, Class clazz) {
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