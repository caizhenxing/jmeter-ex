/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jmeter;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Provides JMeter the ability to use proxy servers that require username and
 * password.
 *
 * @version $Revision: 674362 $
 */
public class ProxyAuthenticator extends Authenticator {
    /** The username to authenticate with. */
    private String userName;

    /** The password to authenticate with. */
    private char password[];

    /**
     * Create a ProxyAuthenticator with the specified username and password.
     *
     * @param userName
     *            the username to authenticate with
     * @param password
     *            the password to authenticate with
     */
    public ProxyAuthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password.toCharArray();
    }

    /**
     * Return a PasswordAuthentication instance using the userName and password
     * specified in the constructor.
     *
     * @return a PasswordAuthentication instance to use for authenticating with
     *         the proxy
     */
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
