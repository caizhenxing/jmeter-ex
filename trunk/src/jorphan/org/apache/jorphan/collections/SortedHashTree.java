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

package org.apache.jorphan.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * SortedHashTree is a different implementation of the {@link HashTree}
 * collection class. In the SortedHashTree, the ordering of values in the tree
 * is made explicit via the compare() function of objects added to the tree.
 * This works in exactly the same fashion as it does for a SortedSet.
 *
 * @see HashTree
 * @see HashTreeTraverser
 *
 */
public class SortedHashTree extends HashTree implements Serializable {

    private static final long serialVersionUID = 233L;

    public SortedHashTree() {
        super(new TreeMap()); // equivalent to new TreeMap((Comparator)null);
    }

    // non-null Comparators don't appear to be used at present
    public SortedHashTree(Comparator comper) {
        super(new TreeMap(comper));
    }

    public SortedHashTree(Object key) {
        this();
        data.put(key, new SortedHashTree());
    }

    public SortedHashTree(Object key, Comparator comper) {
        this(comper);
        data.put(key, new SortedHashTree(comper));
    }

    public SortedHashTree(Collection keys) {
        this();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            data.put(it.next(), new SortedHashTree());
        }
    }

    public SortedHashTree(Collection keys, Comparator comper) {
        this(comper);
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            data.put(it.next(), new SortedHashTree(comper));
        }
    }

    public SortedHashTree(Object[] keys) {
        this();
        for (int x = 0; x < keys.length; x++) {
            data.put(keys[x], new SortedHashTree());
        }
    }

    public SortedHashTree(Object[] keys, Comparator comper) {
        this(comper);
        for (int x = 0; x < keys.length; x++) {
            data.put(keys[x], new SortedHashTree(comper));
        }
    }

    protected HashTree createNewTree() {
        Comparator comparator = ((TreeMap)data).comparator();
        return new SortedHashTree(comparator);
    }

    protected HashTree createNewTree(Object key) {
        Comparator comparator = ((TreeMap)data).comparator();
        return new SortedHashTree(key, comparator);
    }

    protected HashTree createNewTree(Collection values) {
        Comparator comparator = ((TreeMap)data).comparator();
        return new SortedHashTree(values, comparator);
    }

}
