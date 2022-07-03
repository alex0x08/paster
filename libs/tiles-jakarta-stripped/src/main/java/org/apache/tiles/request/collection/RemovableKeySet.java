/*
 * $Id: RemovableKeySet.java 1229087 2012-01-09 10:35:14Z mck $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.collection;

import static org.apache.tiles.request.collection.CollectionUtil.*;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.tiles.request.attribute.HasRemovableKeys;

/**
 * Wraps {@link HasRemovableKeys} keys as a set.
 *
 * @version $Rev: 1229087 $ $Date: 2012-01-09 21:35:14 +1100 (Mon, 09 Jan 2012) $
 */
public class RemovableKeySet extends KeySet {

    /**
     * The request.
     */
    private HasRemovableKeys<?> request;

    /**
     * Constructor.
     *
     * @param request The request.
     */
    public RemovableKeySet(HasRemovableKeys<?> request) {
        super(request);
        this.request = request;
    }

    @Override
    public boolean remove(Object o) {
        String skey = key(o);
        Object previous = request.getValue(skey);
        if (previous != null) {
            request.removeValue(skey);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeAll(Collection<?> c) {
        Collection<String> realCollection = (Collection<String>) c;
        boolean retValue = false;
        for (String entry : realCollection) {
            retValue |= remove(entry);
        }
        return retValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean retainAll(Collection<?> c) {
        Collection<String> realCollection = (Collection<String>) c;
        boolean retValue = false;
        Set<String> keysToRemove = new LinkedHashSet<String>();
        for (Enumeration<String> keys = request.getKeys(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            if (!realCollection.contains(key)) {
                retValue = true;
                keysToRemove.add(key);
            }
        }
        for (String key : keysToRemove) {
            request.removeValue(key);
        }
        return retValue;
    }

}
