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
package org.lastaflute.di.core.factory.exception;

import java.util.Collection;
import java.util.Iterator;

import org.lastaflute.di.exception.SRuntimeException;

/**
 * <pre>
 * aaa.dicon --include--&gt; bbb.dicon --include--&gt; ccc.dicon --include--&gt; aaa.dicon
 * </pre>
 * @author modified by jflute (originated in Seasar)
 */
public class CircularIncludeRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = -8674493688526055877L;

    protected String path;
    protected Collection<String> paths;

    public CircularIncludeRuntimeException(final String path, final Collection<String> paths) {
        super("ESSR0076", new Object[] { path, toString(path, paths) });
        this.path = path;
        this.paths = paths;
    }

    public String getPath() {
        return path;
    }

    public Collection<String> getPaths() {
        return paths;
    }

    protected static String toString(final String path, final Collection<String> paths) {
        final StringBuffer buf = new StringBuffer(200);
        for (final Iterator<String> it = paths.iterator(); it.hasNext();) {
            buf.append("\"").append(it.next()).append("\" includes ");
        }
        buf.append("\"").append(path).append("\"");
        return new String(buf);
    }
}
