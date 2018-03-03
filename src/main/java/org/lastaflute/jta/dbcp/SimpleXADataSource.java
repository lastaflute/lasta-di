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
package org.lastaflute.jta.dbcp;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.lastaflute.jta.dbcp.impl.XAConnectionImpl;
import org.lastaflute.jta.util.LjtDriverManagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SimpleXADataSource implements XADataSource {

    private static final Logger logger = LoggerFactory.getLogger(SimpleXADataSource.class);

    protected String driverClassName;
    protected String url;
    protected String user;
    protected String password;
    protected final Properties properties = new Properties();
    protected int loginTimeout;

    public SimpleXADataSource() {
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        if (driverClassName != null && driverClassName.length() > 0) {
            LjtDriverManagerUtil.registerDriver(driverClassName);
        }
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addProperty(String name, String value) {
        properties.put(name, value);
    }

    public XAConnection getXAConnection() throws SQLException {
        return getXAConnection(user, password);
    }

    public XAConnection getXAConnection(String user, String password) throws SQLException {
        final Properties info = new Properties();
        info.putAll(properties);
        if (!isEmpty(user)) {
            info.put("user", user);
        }
        if (!isEmpty(password)) {
            info.put("password", password);
        }
        int currentLoginTimeout = DriverManager.getLoginTimeout();
        try {
            DriverManager.setLoginTimeout(loginTimeout);
            final Connection con = DriverManager.getConnection(url, info);
            return new XAConnectionImpl(con);
        } finally {
            try {
                DriverManager.setLoginTimeout(currentLoginTimeout);
            } catch (Exception e) {
                logger.info("Failed to set login timeout: currentLoginTimeout=" + currentLoginTimeout, e);
            }
        }
    }

    protected boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(final PrintWriter logWriter) throws SQLException {
    }

    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    public void setLoginTimeout(final int loginTimeout) throws SQLException {
        this.loginTimeout = loginTimeout;
    }

    // #java8comp
    /* (non-Javadoc)
     * @see javax.sql.CommonDataSource#getParentLogger()
     */
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
