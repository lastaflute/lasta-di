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

import org.lastaflute.di.exception.EmptyRuntimeException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class HotText {

    private String path;

    private String value;

    private File file;

    private long lastModified;

    public HotText() {
    }

    public HotText(String path) {
        setPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) throws EmptyRuntimeException {
        if (path == null) {
            throw new EmptyRuntimeException("path");
        }
        this.path = path;
        file = LdiResourceUtil.getResourceAsFileNoException(path);
        if (file != null) {
            updateValueByFile();
        } else {
            updateValueByPath();
        }
    }

    public String getValue() {
        if (isModified()) {
            updateValueByFile();
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isModified() {
        return file != null && file.lastModified() > lastModified;
    }

    protected void updateValueByFile() {
        value = LdiTextUtil.readUTF8(file);
        lastModified = file.lastModified();
    }

    protected void updateValueByPath() {
        value = LdiTextUtil.readUTF8(path);
    }
}
