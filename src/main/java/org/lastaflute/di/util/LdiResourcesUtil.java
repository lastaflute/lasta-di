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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.lastaflute.di.helper.log.LaLogger;
import org.lastaflute.di.util.ClassTraversal.ClassHandler;
import org.lastaflute.di.util.ResourceTraversal.ResourceHandler;

/**
 * file, jar, wsjar, zip, code-source, vfsfile, vfszip
 * @author modified by jflute (originated in Seasar)
 */
public class LdiResourcesUtil {

    protected static final Resources[] EMPTY_ARRAY = new Resources[0];
    private static final LaLogger logger = LaLogger.getLogger(LdiResourcesUtil.class);
    protected static final Map<String, ResourcesFactory> resourcesTypeFactories = new HashMap<String, ResourcesFactory>();

    static {
        addResourcesFactory("file", new ResourcesFactory() {
            public Resources create(final URL url, final String rootPackage, final String rootDir) {
                return new FileSystemResources(getBaseDir(url, rootDir), rootPackage, rootDir);
            }
        });
        addResourcesFactory("jar", new ResourcesFactory() {
            public Resources create(final URL url, final String rootPackage, final String rootDir) {
                return new JarFileResources(url, rootPackage, rootDir);
            }
        });
        addResourcesFactory("zip", new ResourcesFactory() {
            public Resources create(final URL url, final String rootPackage, final String rootDir) {
                return new JarFileResources(LdiJarFileUtil.create(new File(LdiZipFileUtil.toZipFilePath(url))), rootPackage, rootDir);
            }
        });
        addResourcesFactory("code-source", new ResourcesFactory() {
            public Resources create(final URL url, final String rootPackage, final String rootDir) {
                return new JarFileResources(LdiURLUtil.create("jar:file:" + url.getPath()), rootPackage, rootDir);
            }
        });
        addResourcesFactory("vfszip", new ResourcesFactory() {
            public Resources create(final URL url, final String rootPackage, final String rootDir) {
                return new VfsZipResources(url, rootPackage, rootDir);
            }
        });
    }

    public static void addResourcesFactory(final String protocol, ResourcesFactory factory) {
        resourcesTypeFactories.put(protocol, factory);
    }

    public static Resources getResourcesType(final Class<?> referenceClass) {
        final URL url = LdiResourceUtil.getResource(toClassFile(referenceClass.getName()));
        final String path[] = referenceClass.getName().split("\\.");
        String baseUrl = url.toExternalForm();
        for (int i = 0; i < path.length; ++i) {
            int pos = baseUrl.lastIndexOf('/');
            baseUrl = baseUrl.substring(0, pos);
        }
        return getResourcesType(LdiURLUtil.create(baseUrl + '/'), null, null);
    }

    public static Resources getResourcesType(final String rootDir) {
        final URL url = LdiResourceUtil.getResource(rootDir.endsWith("/") ? rootDir : rootDir + '/');
        return getResourcesType(url, null, rootDir);
    }

    public static Resources[] getResourcesTypes(final String rootPackage) {
        if (LdiStringUtil.isEmpty(rootPackage)) {
            return EMPTY_ARRAY;
        }

        final String baseName = toDirectoryName(rootPackage);
        final List<Resources> list = new ArrayList<Resources>();
        for (final Iterator<URL> it = LdiClassLoaderUtil.getResources(baseName); it.hasNext();) {
            final URL url = it.next();
            final Resources resourcesType = getResourcesType(url, rootPackage, baseName);
            if (resourcesType != null) {
                list.add(resourcesType);
            }
        }
        if (list.isEmpty()) {
            logger.log("WSSR0014", new Object[] { rootPackage });
            return EMPTY_ARRAY;
        }
        return (Resources[]) list.toArray(new Resources[list.size()]);
    }

    protected static Resources getResourcesType(final URL url, final String rootPackage, final String rootDir) {
        final ResourcesFactory factory = (ResourcesFactory) resourcesTypeFactories.get(LdiURLUtil.toCanonicalProtocol(url.getProtocol()));
        if (factory != null) {
            return factory.create(url, rootPackage, rootDir);
        }
        logger.log("WSSR0013", new Object[] { rootPackage, url });
        return null;
    }

    protected static String toDirectoryName(final String packageName) {
        if (LdiStringUtil.isEmpty(packageName)) {
            return null;
        }
        return packageName.replace('.', '/') + '/';
    }

    protected static String toClassFile(final String className) {
        return className.replace('.', '/') + ".class";
    }

    protected static File getBaseDir(final URL url, final String baseName) {
        File file = LdiURLUtil.toFile(url);
        final String[] paths = LdiStringUtil.split(baseName, "/");
        for (int i = 0; i < paths.length; ++i) {
            file = file.getParentFile();
        }
        return file;
    }

    public interface ResourcesFactory {
        Resources create(URL url, String rootPackage, String rootDir);
    }

    public interface Resources {

        boolean isExistClass(final String className);

        void forEach(ClassHandler handler);

        void forEach(ResourceHandler handler);

        void close();
    }

    /**
     * @author modified by jflute (originated in Seasar)
     */
    public static class FileSystemResources implements Resources {

        protected final File baseDir;
        protected final String rootPackage;
        protected final String rootDir;

        public FileSystemResources(final File baseDir, final String rootPackage, final String rootDir) {
            this.baseDir = baseDir;
            this.rootPackage = rootPackage;
            this.rootDir = rootDir;
        }

        public FileSystemResources(final URL url, final String rootPackage, final String rootDir) {
            this(LdiURLUtil.toFile(url), rootPackage, rootDir);
        }

        public boolean isExistClass(final String className) {
            final File file = new File(baseDir, toClassFile(LdiClassUtil.concatName(rootPackage, className)));
            return file.exists();
        }

        public void forEach(final ClassHandler handler) {
            ClassTraversal.forEach(baseDir, rootPackage, handler);
        }

        public void forEach(final ResourceHandler handler) {
            ResourceTraversal.forEach(baseDir, rootDir, handler);
        }

        public void close() {
        }
    }

    /**
     * @author modified by jflute (originated in Seasar)
     */
    public static class JarFileResources implements Resources {

        protected final JarFile jarFile;
        protected final String rootPackage;
        final protected String rootDir;

        public JarFileResources(final JarFile jarFile, final String rootPackage, final String rootDir) {
            this.jarFile = jarFile;
            this.rootPackage = rootPackage;
            this.rootDir = rootDir;
        }

        public JarFileResources(final URL url, final String rootPackage, final String rootDir) {
            this(LdiJarFileUtil.toJarFile(url), rootPackage, rootDir);
        }

        public boolean isExistClass(final String className) {
            return jarFile.getEntry(toClassFile(LdiClassUtil.concatName(rootPackage, className))) != null;
        }

        public void forEach(final ClassHandler handler) {
            ClassTraversal.forEach(jarFile, new ClassHandler() {
                public void processClass(String packageName, String shortClassName) {
                    if (rootPackage == null || (packageName != null && packageName.startsWith(rootPackage))) {
                        handler.processClass(packageName, shortClassName);
                    }
                }
            });
        }

        public void forEach(final ResourceHandler handler) {
            ResourceTraversal.forEach(jarFile, new ResourceHandler() {
                public void processResource(String path, InputStream is) {
                    if (rootDir == null || path.startsWith(rootDir)) {
                        handler.processResource(path, is);
                    }
                }
            });
        }

        public void close() {
            LdiJarFileUtil.close(jarFile);
        }
    }

    /**
     * @author modified by jflute (originated in Seasar)
     */
    public static class VfsZipResources implements Resources {

        protected static final String WAR_CLASSES_PREFIX = "/WEB-INF/CLASSES/";

        protected final String rootPackage;
        protected final String rootDir;
        protected final URL zipUrl;
        protected final String prefix;
        protected final Set<String> entryNames = new HashSet<String>();

        public VfsZipResources(final URL url, final String rootPackage, final String rootDir) {
            URL zipUrl = url;
            String prefix = "";
            if (rootPackage != null) {
                final String[] paths = rootPackage.split("\\.");
                for (int i = 0; i < paths.length; ++i) {
                    zipUrl = LdiURLUtil.create(zipUrl, "..");
                }
            }
            loadFromZip(zipUrl);
            if (entryNames.isEmpty()) {
                final String zipUrlString = zipUrl.toExternalForm();
                if (zipUrlString.toUpperCase().endsWith(WAR_CLASSES_PREFIX)) {
                    final URL warUrl = LdiURLUtil.create(zipUrl, "../..");
                    final String path = warUrl.getPath();
                    zipUrl = LdiFileUtil.toURL(new File(path.substring(0, path.length() - 1)));
                    prefix = zipUrlString.substring(warUrl.toExternalForm().length());
                    loadFromZip(zipUrl);
                }
            }

            this.rootPackage = rootPackage;
            this.rootDir = rootDir;
            this.zipUrl = zipUrl;
            this.prefix = prefix;
        }

        private void loadFromZip(final URL zipUrl) {
            final ZipInputStream zis = new ZipInputStream(LdiURLUtil.openStream(zipUrl));
            try {
                ZipEntry entry = null;
                while ((entry = LdiZipInputStreamUtil.getNextEntry(zis)) != null) {
                    entryNames.add(entry.getName());
                    LdiZipInputStreamUtil.closeEntry(zis);
                }
            } finally {
                LdiInputStreamUtil.close(zis);
            }
        }

        public boolean isExistClass(final String className) {
            final String entryName = prefix + toClassFile(LdiClassUtil.concatName(rootPackage, className));
            return entryNames.contains(entryName);
        }

        public void forEach(final ClassHandler handler) {
            final ZipInputStream zis = new ZipInputStream(LdiURLUtil.openStream(zipUrl));
            try {
                ClassTraversal.forEach(zis, prefix, new ClassHandler() {
                    public void processClass(String packageName, String shortClassName) {
                        if (rootPackage == null || (packageName != null && packageName.startsWith(rootPackage))) {
                            handler.processClass(packageName, shortClassName);
                        }
                    }
                });
            } finally {
                LdiInputStreamUtil.close(zis);
            }
        }

        public void forEach(final ResourceHandler handler) {
            final ZipInputStream zis = new ZipInputStream(LdiURLUtil.openStream(zipUrl));
            try {
                ResourceTraversal.forEach(zis, prefix, new ResourceHandler() {
                    public void processResource(String path, InputStream is) {
                        if (rootDir == null || path.startsWith(rootDir)) {
                            handler.processResource(path, is);
                        }
                    }
                });
            } finally {
                LdiInputStreamUtil.close(zis);
            }
        }

        public void close() {
        }
    }
}
