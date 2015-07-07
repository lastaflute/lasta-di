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
package org.lastaflute.jta.util;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import org.lastaflute.jta.exception.LjtIllegalStateException;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class LjtDriverManagerUtil {

    public static void registerDriver(final String driverClassName) {
        registerDriver(forName(driverClassName));
    }

    protected static Class<?> forName(String className) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            return Class.forName(className, true, loader);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to find class: " + className + " loader=" + loader, e);
        }
    }

    public static void registerDriver(final Class<?> driverClass) {
        registerDriver((Driver) newInstance(driverClass));
    }

    protected static Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to instantiate the class: " + clazz, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Illegal access to the constructor of the class: " + clazz, e);
        }
    }

    public static void registerDriver(final Driver driver) {
        try {
            DriverManager.registerDriver(driver);
        } catch (final SQLException e) {
            throw new LjtIllegalStateException("Failed to register the driver: " + driver, e);
        }
    }

    public static void deregisterDriver(final Driver driver) {
        try {
            DriverManager.deregisterDriver(driver);
        } catch (final SQLException e) {
            throw new LjtIllegalStateException("Failed to deregister the driver: " + driver, e);
        }
    }

    public static synchronized void deregisterAllDrivers() {
        for (final Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
            deregisterDriver((Driver) e.nextElement());
        }
    }
}
