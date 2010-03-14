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

package org.apache.jmeter.protocol.jms.sampler;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueSender;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * Request/reply executor with a fixed reply queue. <br>
 * 
 * Used by JMS Sampler (Point to Point)
 * 
 * Created on: October 28, 2004
 *
 */
public class FixedQueueExecutor implements QueueExecutor {

    private static final Logger log = LoggingManager.getLoggerForClass();

    /** Sender. */
    private final QueueSender producer;

    /** Timeout used for waiting on message. */
    private final int timeout;
    
    private final boolean useReqMsgIdAsCorrelId;

    /**
     * Constructor.
     *
     * @param producer
     *            the queue to send the message on
     * @param timeout
     *            timeout to use for the return message
     */
    public FixedQueueExecutor(QueueSender producer, int timeout) {
        this.producer = producer;
        this.timeout = timeout;
        this.useReqMsgIdAsCorrelId = false;
    }

    /**
     * Constructor.
     *
     * @param producer
     *            the queue to send the message on
     * @param timeout
     *            timeout to use for the return message
     * @param useReqMsgIdAsCorrelId
     *            whether to use the request message id as the correlation id
     */
    public FixedQueueExecutor(QueueSender producer, int timeout, boolean useReqMsgIdAsCorrelId) {
        this.producer = producer;
        this.timeout = timeout;
        this.useReqMsgIdAsCorrelId = useReqMsgIdAsCorrelId;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.jmeter.protocol.jms.sampler.QueueExecutor#sendAndReceive(javax.jms.Message)
     */
    public Message sendAndReceive(Message request) throws JMSException {
        String id = request.getJMSCorrelationID();
        if(id == null && !useReqMsgIdAsCorrelId){
            log.error("Correlation id is null. Set the JMSCorrelationID header");
            return null;
        }
        producer.send(request);
        
        if(useReqMsgIdAsCorrelId) {
            id = request.getJMSMessageID();
        }

        MessageAdmin.getAdmin().putRequest(id, request);
        try {
            if (log.isDebugEnabled()) {
                log.debug("wait for reply " + id + " started on " + System.currentTimeMillis());
            }
            synchronized (request) {
                request.wait(timeout);
            }
            if (log.isDebugEnabled()) {
                log.debug("done waiting for " + id + " ended on " + System.currentTimeMillis());
            }

        } catch (InterruptedException e) {
            log.warn("Interrupt exception caught", e);
        }
        return MessageAdmin.getAdmin().get(id);
    }
}