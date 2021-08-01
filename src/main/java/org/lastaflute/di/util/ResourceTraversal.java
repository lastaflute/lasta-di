/*
 * Copyright 2015-2021 the original author or authors.
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
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class ResourceTraversal {

    public interface ResourceHandler {
        void processResource(String path, InputStream is);
    }

    protected ResourceTraversal() {
    }

    public static void forEach(final File rootDir, final ResourceHandler handler) {
        forEach(rootDir, null, handler);
    }

    public static void forEach(final File rootDir, final String baseDirectory, final ResourceHandler handler) {
        final File baseDir = getBaseDir(rootDir, baseDirectory);
        if (baseDir.exists()) {
            traverseFileSystem(rootDir, baseDir, handler);
        }
    }

    public static void forEach(final JarFile jarFile, final ResourceHandler handler) {
        forEach(jarFile, "", handler);
    }

    public static void forEach(final JarFile jarFile, final String prefix, final ResourceHandler handler) {
        final int pos = prefix.length();
        final Enumeration<JarEntry> enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            final JarEntry entry = enumeration.nextElement();
            if (!entry.isDirectory()) {
                final String entryName = entry.getName().replace('\\', '/');
                if (!entryName.startsWith(prefix)) {
                    continue;
                }
                final InputStream is = LdiJarFileUtil.getInputStream(jarFile, entry);
                try {
                    handler.processResource(entryName.substring(pos), is);
                } finally {
                    LdiInputStreamUtil.close(is);
                }
            }
        }
    }

    public static void forEach(final ZipInputStream zipInputStream, final ResourceHandler handler) {
        forEach(zipInputStream, "", handler);
    }

    public static void forEach(final ZipInputStream zipInputStream, final String prefix, final ResourceHandler handler) {
        final int pos = prefix.length();
        ZipEntry entry = null;
        while ((entry = LdiZipInputStreamUtil.getNextEntry(zipInputStream)) != null) {
            if (!entry.isDirectory()) {
                final String entryName = entry.getName().replace('\\', '/');
                if (!entryName.startsWith(prefix)) {
                    continue;
                }
                handler.processResource(entryName.substring(pos), new FilterInputStream(zipInputStream) {
                    public void close() throws IOException {
                        LdiZipInputStreamUtil.closeEntry(zipInputStream);
                    }
                });
            }
        }
    }

    private static void traverseFileSystem(final File rootDir, final File baseDir, final ResourceHandler handler) {
        final File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            final File file = files[i];
            if (file.isDirectory()) {
                traverseFileSystem(rootDir, file, handler);
            } else {
                final int pos = LdiFileUtil.getCanonicalPath(rootDir).length();
                final String filePath = LdiFileUtil.getCanonicalPath(file);
                final String resourcePath = filePath.substring(pos + 1).replace('\\', '/');
                final InputStream is = LdiFileInputStreamUtil.create(file);
                try {
                    handler.processResource(resourcePath, is);
                } finally {
                    LdiInputStreamUtil.close(is);
                }
            }
        }
    }

    private static File getBaseDir(final File rootDir, final String baseDirectory) {
        File baseDir = rootDir;
        if (baseDirectory != null) {
            final String[] names = baseDirectory.split("/");
            for (int i = 0; i < names.length; i++) {
                baseDir = new File(baseDir, names[i]);
            }
        }
        return baseDir;
    }
}
