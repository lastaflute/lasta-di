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

import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ClassTraversal {

    private static final String CLASS_SUFFIX = ".class";
    private static final String WAR_FILE_EXTENSION = ".war";
    private static final String WEB_INF_CLASSES_PATH = "WEB-INF/classes/";

    public interface ClassHandler {
        void processClass(String packageName, String shortClassName);
    }

    public static void forEach(final File rootDir, final ClassHandler handler) {
        forEach(rootDir, null, handler);
    }

    public static void forEach(final File rootDir, final String rootPackage, final ClassHandler handler) {
        final File packageDir = getPackageDir(rootDir, rootPackage);
        if (packageDir.exists()) {
            traverseFileSystem(packageDir, rootPackage, handler);
        }
    }

    public static void forEach(final JarFile jarFile, final ClassHandler handler) {
        if (jarFile.getName().toLowerCase().endsWith(WAR_FILE_EXTENSION)) {
            forEach(jarFile, WEB_INF_CLASSES_PATH, handler);
        } else {
            forEach(jarFile, "", handler);
        }
    }

    public static void forEach(final JarFile jarFile, final String prefix, final ClassHandler handler) {
        final int startPos = prefix.length();
        final Enumeration<JarEntry> enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            final JarEntry entry = (JarEntry) enumeration.nextElement();
            final String entryName = entry.getName().replace('\\', '/');
            if (entryName.startsWith(prefix) && entryName.endsWith(CLASS_SUFFIX)) {
                final String className = entryName.substring(startPos, entryName.length() - CLASS_SUFFIX.length()).replace('/', '.');
                final int pos = className.lastIndexOf('.');
                final String packageName = (pos == -1) ? null : className.substring(0, pos);
                final String shortClassName = (pos == -1) ? className : className.substring(pos + 1);
                handler.processClass(packageName, shortClassName);
            }
        }
    }

    public static void forEach(final ZipInputStream zipInputStream, final ClassHandler handler) {
        forEach(zipInputStream, "", handler);
    }

    public static void forEach(final ZipInputStream zipInputStream, final String prefix, final ClassHandler handler) {
        final int startPos = prefix.length();
        ZipEntry entry = null;
        while ((entry = LdiZipInputStreamUtil.getNextEntry(zipInputStream)) != null) {
            try {
                final String entryName = entry.getName().replace('\\', '/');
                if (entryName.startsWith(prefix) && entryName.endsWith(CLASS_SUFFIX)) {
                    final String className = entryName.substring(startPos, entryName.length() - CLASS_SUFFIX.length()).replace('/', '.');
                    final int pos = className.lastIndexOf('.');
                    final String packageName = (pos == -1) ? null : className.substring(0, pos);
                    final String shortClassName = (pos == -1) ? className : className.substring(pos + 1);
                    handler.processClass(packageName, shortClassName);
                }
            } finally {
                LdiZipInputStreamUtil.closeEntry(zipInputStream);
            }
        }
    }

    private static void traverseFileSystem(final File dir, final String packageName, final ClassHandler handler) {
        final File[] files = dir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            final File file = files[i];
            final String fileName = file.getName();
            if (file.isDirectory()) {
                traverseFileSystem(file, LdiClassUtil.concatName(packageName, fileName), handler);
            } else if (fileName.endsWith(".class")) {
                final String shortClassName = fileName.substring(0, fileName.length() - CLASS_SUFFIX.length());
                handler.processClass(packageName, shortClassName);
            }
        }
    }

    private static File getPackageDir(final File rootDir, final String rootPackage) {
        File packageDir = rootDir;
        if (rootPackage != null) {
            final String[] names = rootPackage.split("\\.");
            for (int i = 0; i < names.length; i++) {
                packageDir = new File(packageDir, names[i]);
            }
        }
        return packageDir;
    }
}
