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

package org.apache.jmeter.protocol.http.config;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.junit.JMeterTestCase;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.sampler.HTTPNullSampler;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.NullProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;

public class UrlConfigTest extends JMeterTestCase {
    HTTPSamplerBase config;

    HTTPSamplerBase defaultConfig;

    HTTPSamplerBase partialConfig;

    public UrlConfigTest(String name) {
        super(name);
    }

    protected void setUp() {
        Arguments args = new Arguments();
        args.addArgument("username", "mstover");
        args.addArgument("password", "pass");
        args.addArgument("action", "login");
        config = new HTTPNullSampler();
        config.setName("Full Config");
        config.setProperty(HTTPSamplerBase.DOMAIN, "www.lazer.com");
        config.setProperty(HTTPSamplerBase.PATH, "login.jsp");
        config.setProperty(HTTPSamplerBase.METHOD, HTTPSamplerBase.POST);
        config.setProperty(new TestElementProperty(HTTPSamplerBase.ARGUMENTS, args));
        defaultConfig = new HTTPNullSampler();
        defaultConfig.setName("default");
        defaultConfig.setProperty(HTTPSamplerBase.DOMAIN, "www.xerox.com");
        defaultConfig.setProperty(HTTPSamplerBase.PATH, "default.html");
        partialConfig = new HTTPNullSampler();
        partialConfig.setProperty(HTTPSamplerBase.PATH, "main.jsp");
        partialConfig.setProperty(HTTPSamplerBase.METHOD, HTTPSamplerBase.GET);
    }

    public void testSimpleConfig() {
        assertTrue(config.getName().equals("Full Config"));
        assertEquals(config.getDomain(), "www.lazer.com");
    }

    public void testOverRide() {
        JMeterProperty jmp = partialConfig.getProperty(HTTPSamplerBase.DOMAIN);
        assertTrue(jmp instanceof NullProperty);
        assertTrue(new NullProperty(HTTPSamplerBase.DOMAIN).equals(jmp));
        partialConfig.addTestElement(defaultConfig);
        assertEquals(partialConfig.getPropertyAsString(HTTPSamplerBase.DOMAIN), "www.xerox.com");
        assertEquals(partialConfig.getPropertyAsString(HTTPSamplerBase.PATH), "main.jsp");
    }
}