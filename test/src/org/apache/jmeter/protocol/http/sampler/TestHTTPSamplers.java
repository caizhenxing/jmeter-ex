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

package org.apache.jmeter.protocol.http.sampler;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.http.util.HTTPFileArg;
import junit.framework.TestCase;

public class TestHTTPSamplers extends TestCase {

    public TestHTTPSamplers(String arg0) {
        super(arg0);
    }

    // Parse arguments singly
    public void testParseArguments(){
        HTTPSamplerBase sampler = new HTTPNullSampler();
        Arguments args;
        Argument arg;

        args = sampler.getArguments();
        assertEquals(0,args.getArgumentCount());
        assertEquals(0,sampler.getHTTPFileCount());

        sampler.parseArguments("");
        args = sampler.getArguments();
        assertEquals(0,args.getArgumentCount());
        assertEquals(0,sampler.getHTTPFileCount());

        sampler.parseArguments("name1");
        args = sampler.getArguments();
        assertEquals(1,args.getArgumentCount());
        arg=args.getArgument(0);
        assertEquals("name1",arg.getName());
        assertEquals("",arg.getMetaData());
        assertEquals("",arg.getValue());
        assertEquals(0,sampler.getHTTPFileCount());

        sampler.parseArguments("name2=");
        args = sampler.getArguments();
        assertEquals(2,args.getArgumentCount());
        arg=args.getArgument(1);
        assertEquals("name2",arg.getName());
        assertEquals("=",arg.getMetaData());
        assertEquals("",arg.getValue());
        assertEquals(0,sampler.getHTTPFileCount());

        sampler.parseArguments("name3=value3");
        args = sampler.getArguments();
        assertEquals(3,args.getArgumentCount());
        arg=args.getArgument(2);
        assertEquals("name3",arg.getName());
        assertEquals("=",arg.getMetaData());
        assertEquals("value3",arg.getValue());
        assertEquals(0,sampler.getHTTPFileCount());
    }

    // Parse arguments all at once
    public void testParseArguments2(){
        HTTPSamplerBase sampler = new HTTPNullSampler();
        Arguments args;
        Argument arg;

        args = sampler.getArguments();
        assertEquals(0,args.getArgumentCount());
        assertEquals(0,sampler.getHTTPFileCount());

        sampler.parseArguments("&name1&name2=&name3=value3");
        args = sampler.getArguments();
        assertEquals(3,args.getArgumentCount());
        assertEquals(0,sampler.getHTTPFileCount());

        arg=args.getArgument(0);
        assertEquals("name1",arg.getName());
        assertEquals("",arg.getMetaData());
        assertEquals("",arg.getValue());
        assertEquals(0,sampler.getHTTPFileCount());

        arg=args.getArgument(1);
        assertEquals("name2",arg.getName());
        assertEquals("=",arg.getMetaData());
        assertEquals("",arg.getValue());
        assertEquals(0,sampler.getHTTPFileCount());

        arg=args.getArgument(2);
        assertEquals("name3",arg.getName());
        assertEquals("=",arg.getMetaData());
        assertEquals("value3",arg.getValue());
        assertEquals(0,sampler.getHTTPFileCount());
    }

        public void testArgumentWithoutEquals() throws Exception {
            HTTPSamplerBase sampler = new HTTPNullSampler();
            sampler.setProtocol("http");
            sampler.setMethod(HTTPSamplerBase.GET);
            sampler.setPath("/index.html?pear");
            sampler.setDomain("www.apache.org");
            assertEquals("http://www.apache.org/index.html?pear", sampler.getUrl().toString());
        }

        public void testMakingUrl() throws Exception {
            HTTPSamplerBase config = new HTTPNullSampler();
            config.setProtocol("http");
            config.setMethod(HTTPSamplerBase.GET);
            config.addArgument("param1", "value1");
            config.setPath("/index.html");
            config.setDomain("www.apache.org");
            assertEquals("http://www.apache.org/index.html?param1=value1", config.getUrl().toString());
        }

        public void testMakingUrl2() throws Exception {
            HTTPSamplerBase config = new HTTPNullSampler();
            config.setProtocol("http");
            config.setMethod(HTTPSamplerBase.GET);
            config.addArgument("param1", "value1");
            config.setPath("/index.html?p1=p2");
            config.setDomain("www.apache.org");
            assertEquals("http://www.apache.org/index.html?param1=value1&p1=p2", config.getUrl().toString());
        }

        public void testMakingUrl3() throws Exception {
            HTTPSamplerBase config = new HTTPNullSampler();
            config.setProtocol("http");
            config.setMethod(HTTPSamplerBase.POST);
            config.addArgument("param1", "value1");
            config.setPath("/index.html?p1=p2");
            config.setDomain("www.apache.org");
            assertEquals("http://www.apache.org/index.html?p1=p2", config.getUrl().toString());
        }

        // test cases for making Url, and exercise method
        // addArgument(String name,String value,String metadata)

        public void testMakingUrl4() throws Exception {
            HTTPSamplerBase config = new HTTPNullSampler();
            config.setProtocol("http");
            config.setMethod(HTTPSamplerBase.GET);
            config.addArgument("param1", "value1", "=");
            config.setPath("/index.html");
            config.setDomain("www.apache.org");
            assertEquals("http://www.apache.org/index.html?param1=value1", config.getUrl().toString());
        }

        public void testMakingUrl5() throws Exception {
            HTTPSamplerBase config = new HTTPNullSampler();
            config.setProtocol("http");
            config.setMethod(HTTPSamplerBase.GET);
            config.addArgument("param1", "", "=");
            config.setPath("/index.html");
            config.setDomain("www.apache.org");
            assertEquals("http://www.apache.org/index.html?param1=", config.getUrl().toString());
        }

        public void testMakingUrl6() throws Exception {
            HTTPSamplerBase config = new HTTPNullSampler();
            config.setProtocol("http");
            config.setMethod(HTTPSamplerBase.GET);
            config.addArgument("param1", "", "");
            config.setPath("/index.html");
            config.setDomain("www.apache.org");
            assertEquals("http://www.apache.org/index.html?param1", config.getUrl().toString());
        }

        // test cases for making Url, and exercise method
        // parseArguments(String queryString)

        public void testMakingUrl7() throws Exception {
            HTTPSamplerBase config = new HTTPNullSampler();
            config.setProtocol("http");
            config.setMethod(HTTPSamplerBase.GET);
            config.parseArguments("param1=value1");
            config.setPath("/index.html");
            config.setDomain("www.apache.org");
            assertEquals("http://www.apache.org/index.html?param1=value1", config.getUrl().toString());
        }

        public void testMakingUrl8() throws Exception {
            HTTPSamplerBase config = new HTTPNullSampler();
            config.setProtocol("http");
            config.setMethod(HTTPSamplerBase.GET);
            config.parseArguments("param1=");
            config.setPath("/index.html");
            config.setDomain("www.apache.org");
            assertEquals("http://www.apache.org/index.html?param1=", config.getUrl().toString());
        }

        public void testMakingUrl9() throws Exception {
            HTTPSamplerBase config = new HTTPNullSampler();
            config.setProtocol("http");
            config.setMethod(HTTPSamplerBase.GET);
            config.parseArguments("param1");
            config.setPath("/index.html");
            config.setDomain("www.apache.org");
            assertEquals("http://www.apache.org/index.html?param1", config.getUrl().toString());
        }

        public void testMakingUrl10() throws Exception {
            HTTPSamplerBase config = new HTTPNullSampler();
            config.setProtocol("http");
            config.setMethod(HTTPSamplerBase.GET);
            config.parseArguments("");
            config.setPath("/index.html");
            config.setDomain("www.apache.org");
            assertEquals("http://www.apache.org/index.html", config.getUrl().toString());
        }
        
        public void testFileList(){
            HTTPSamplerBase config = new HTTPNullSampler();
            HTTPFileArg[] arg;
            arg = config.getHTTPFiles();
            assertNotNull(arg);
            assertEquals(0,arg.length);

            config.setFileField("");
            config.setFilename("");
            config.setMimetype("");
            arg = config.getHTTPFiles();
            assertNotNull(arg);
            assertEquals(0,arg.length);
            
            config.setMimetype("text/plain");            
            arg = config.getHTTPFiles();
            assertNotNull(arg);
            assertEquals(1,arg.length);
            assertEquals("text/plain",arg[0].getMimeType());
            assertEquals("",arg[0].getPath());
            assertEquals("",arg[0].getParamName());
            
            config.setFileField("test123.tmp");
            config.setFilename("/tmp/test123.tmp");
            arg = config.getHTTPFiles();
            assertNotNull(arg);
            assertEquals(1,arg.length);
            assertEquals("text/plain",arg[0].getMimeType());
            assertEquals("/tmp/test123.tmp",arg[0].getPath());
            assertEquals("test123.tmp",arg[0].getParamName());
            
            HTTPFileArg[] files = {};
            
            // Ignore empty file specs
            config.setHTTPFiles(files);
            arg = config.getHTTPFiles();
            assertNotNull(arg);
            assertEquals(0,arg.length);
            files = new HTTPFileArg[]{
                    new HTTPFileArg(),
                    new HTTPFileArg(),
                    };
            config.setHTTPFiles(files);
            arg = config.getHTTPFiles();
            assertNotNull(arg);
            assertEquals(0,arg.length);

            // Ignore trailing empty spec
            files = new HTTPFileArg[]{
                    new HTTPFileArg("file"),
                    new HTTPFileArg(),
                    };
            config.setHTTPFiles(files);
            arg = config.getHTTPFiles();
            assertNotNull(arg);
            assertEquals(1,arg.length);

            // Ignore leading empty spec
            files = new HTTPFileArg[]{
                    new HTTPFileArg(),
                    new HTTPFileArg("file1"),
                    new HTTPFileArg(),
                    new HTTPFileArg("file2"),
                    new HTTPFileArg(),
                    };
            config.setHTTPFiles(files);
            arg = config.getHTTPFiles();
            assertNotNull(arg);
            assertEquals(2,arg.length);
     }

    public void testSetAndGetFileField() {
        HTTPSamplerBase sampler = new HTTPNullSampler();
        sampler.setFileField("param");
        assertEquals("param", sampler.getFileField());
        HTTPFileArg file = sampler.getHTTPFiles()[0];
        assertEquals("param", file.getParamName());

        sampler.setFileField("param2");
        assertEquals("param2", sampler.getFileField());
        file = sampler.getHTTPFiles()[0];
        assertEquals("param2", file.getParamName());
    }

    public void testSetAndGetFilename() {
        HTTPSamplerBase sampler = new HTTPNullSampler();
        sampler.setFilename("name");
        assertEquals("name", sampler.getFilename());
        HTTPFileArg file = sampler.getHTTPFiles()[0];
        assertEquals("name", file.getPath());

        sampler.setFilename("name2");
        assertEquals("name2", sampler.getFilename());
        file = sampler.getHTTPFiles()[0];
        assertEquals("name2", file.getPath());
    }

    public void testSetAndGetMimetype() {
        HTTPSamplerBase sampler = new HTTPNullSampler();
        sampler.setMimetype("mime");
        assertEquals("mime", sampler.getMimetype());
        HTTPFileArg file = sampler.getHTTPFiles()[0];
        assertEquals("mime", file.getMimeType());

        sampler.setMimetype("mime2");
        assertEquals("mime2", sampler.getMimetype());
        file = sampler.getHTTPFiles()[0];
        assertEquals("mime2", file.getMimeType());
    }
}
