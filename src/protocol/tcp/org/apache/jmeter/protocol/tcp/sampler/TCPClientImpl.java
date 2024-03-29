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

/*
 * Basic TCP Sampler Client class
 *
 * Can be used to test the TCP Sampler against an HTTP server
 *
 * The protocol handler class name is defined by the property tcp.handler
 *
 */
package org.apache.jmeter.protocol.tcp.sampler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * Sample TCPClient implementation.
 * Reads data until the defined EOL byte is reached.
 * If there is no EOL byte defined, then reads until
 * the end of the stream is reached.
 * The EOL byte is defined by the property "tcp.eolByte".
 */
public class TCPClientImpl extends AbstractTCPClient {
    private static final Logger log = LoggingManager.getLoggerForClass();

    private int eolInt = JMeterUtils.getPropDefault("tcp.eolByte", 1000); // $NON-NLS-1$
    // default is not in range of a byte

    public TCPClientImpl() {
        super();
        setEolByte(eolInt);
        if (useEolByte) {
            log.info("Using eolByte=" + eolByte);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.jmeter.protocol.tcp.sampler.TCPClient#write(java.io.OutputStream,
     *      java.lang.String)
     */
    public void write(OutputStream os, String s) {
        try {
            os.write(s.getBytes());
            os.flush();
        } catch (IOException e) {
            log.warn("Write error", e);
        }
        log.debug("Wrote: " + s);
        return;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.jmeter.protocol.tcp.sampler.TCPClient#write(java.io.OutputStream,
     *      java.io.InputStream)
     */
    public void write(OutputStream os, InputStream is) {
        byte buff[]=new byte[512];
        try {
            while(is.read(buff) > 0){
                os.write(buff);
                os.flush();
            }
        } catch (IOException e) {
            log.warn("Write error", e);
        }
    }

    /**
     * Reads data until the defined EOL byte is reached.
     * If there is no EOL byte defined, then reads until
     * the end of the stream is reached.
     */
    public String read(InputStream is) {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream w = new ByteArrayOutputStream();
        int x = 0;
        try {
            while ((x = is.read(buffer)) > -1) {
                w.write(buffer, 0, x);
                if (useEolByte && (buffer[x - 1] == eolByte)) {
                    break;
                }
            }
        } catch (SocketTimeoutException e) {
            // drop out to handle buffer
        } catch (InterruptedIOException e) {
            // drop out to handle buffer
        } catch (IOException e) {
            log.warn("Read error:" + e);
            return "";
        }

        // do we need to close byte array (or flush it?)
        log.debug("Read: " + w.size() + "\n" + w.toString());
        return w.toString();
    }
}
