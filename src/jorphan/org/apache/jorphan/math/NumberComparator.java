/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */

/*
 * Created on May 25, 2004
 */
package org.apache.jorphan.math;

import java.util.Comparator;

public class NumberComparator implements Comparator {

    /**
     *
     */
    public NumberComparator() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object val1, Object val2) {
        Number[] n1 = (Number[]) val1;
        Number[] n2 = (Number[]) val2;
        if (n1[0].longValue() < n2[0].longValue()) {
            return -1;
        } else if (n1[0].longValue() == n2[0].longValue()) {
            return 0;
        } else {
            return 1;
        }
    }

}
