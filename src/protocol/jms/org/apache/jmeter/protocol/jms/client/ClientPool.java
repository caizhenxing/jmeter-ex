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
 */

package org.apache.jmeter.protocol.jms.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * ClientPool holds the client instances in an ArrayList. The main purpose of
 * this is to make it easier to clean up all the instances at the end of a test.
 * If we didn't do this, threads might become zombie.
 * 
 * N.B. This class needs to be fully synchronized as it is called from sample threads
 * and the thread that runs testEnded() methods.
 */
public class ClientPool {

    private static final ArrayList clients = new ArrayList();

    private static final HashMap client_map = new HashMap();

    /**
     * Add a ReceiveClient to the ClientPool. This is so that we can make sure
     * to close all clients and make sure all threads are destroyed.
     *
     * @param client
     */
    public static synchronized void addClient(ReceiveSubscriber client) {
        clients.add(client);
    }

    /**
     * Add a OnMessageClient to the ClientPool. This is so that we can make sure
     * to close all clients and make sure all threads are destroyed.
     *
     * @param client
     */
    public static synchronized void addClient(OnMessageSubscriber client) {
        clients.add(client);
    }

    /**
     * Add a Publisher to the ClientPool. This is so that we can make sure to
     * close all clients and make sure all threads are destroyed.
     *
     * @param client
     */
    public static synchronized void addClient(Publisher client) {
        clients.add(client);
    }

    /**
     * Clear all the clients created by either Publish or Subscribe sampler. We
     * need to do this to make sure all the threads creatd during the test are
     * destroyed and cleaned up. In some cases, the client provided by the
     * manufacturer of the JMS server may have bugs and some threads may become
     * zombie. In those cases, it is not the responsibility of JMeter for those
     * bugs.
     */
    public static synchronized void clearClient() {
        Iterator itr = clients.iterator();
        while (itr.hasNext()) {
            Object client = itr.next();
            if (client instanceof ReceiveSubscriber) {
                ReceiveSubscriber sub = (ReceiveSubscriber) client;
                sub.close();
                sub = null;
            } else if (client instanceof Publisher) {
                Publisher pub = (Publisher) client;
                pub.close();
                pub = null;
            } else if (client instanceof OnMessageSubscriber) {
                OnMessageSubscriber sub = (OnMessageSubscriber) client;
                sub.close();
                sub = null;
            }
        }
        clients.clear();
        client_map.clear();
    }

    public static synchronized void put(Object key, OnMessageSubscriber client) {
        client_map.put(key, client);
    }

    public static synchronized void put(Object key, Publisher client) {
        client_map.put(key, client);
    }

    public static synchronized Object get(Object key) {
        return client_map.get(key);
    }
}
