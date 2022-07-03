/*
 * $Id: HeaderValuesMap.java 1229087 2012-01-09 10:35:14Z mck $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tiles.request.attribute.EnumeratedValuesExtractor;


/**
 * Exposes an {@link EnumeratedValuesExtractor} object as a read-only map.
 *
 * @version $Rev: 1229087 $ $Date: 2012-01-09 21:35:14 +1100 (Mon, 09 Jan 2012) $
 */
public class HeaderValuesMap implements Map<String, String[]> {

    /**
     * The request.
     */
    private EnumeratedValuesExtractor request;

    /**
     * Constructor.
     *
     * @param request The request object to use.
     */
    public HeaderValuesMap(EnumeratedValuesExtractor request) {
        this.request = request;
    }


    /** {@inheritDoc} */
    public void clear() {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    public boolean containsKey(Object key) {
        return (request.getValue(key(key)) != null);
    }


    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        if (!(value instanceof String[])) {
            return (false);
        }
        String[] test = (String[]) value;
        Enumeration<String> names = request.getKeys();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (compareHeaders(name, array2set(test))) {
                return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    public Set<Map.Entry<String, String[]>> entrySet() {
        return new HeadersEntrySet();
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        EnumeratedValuesExtractor otherRequest = ((HeaderValuesMap) o).request;
        boolean retValue = true;
        for (Enumeration<String> attribs = request.getKeys(); attribs
                .hasMoreElements()
                && retValue;) {
            String parameterName = attribs.nextElement();
            Set<String> valueSet = enumeration2set(otherRequest.getValues(parameterName));
            retValue = compareHeaders(parameterName, valueSet);
        }

        return retValue;
    }


    /** {@inheritDoc} */
    public String[] get(Object key) {
        return getHeaderValues(key(key));
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int retValue = 0;
        for (Enumeration<String> attribs = request.getKeys(); attribs
                .hasMoreElements();) {
            String parameterName = attribs.nextElement();
            Enumeration<String> values = request.getValues(parameterName);
            int valueHash = 0;
            while (values.hasMoreElements()) {
                valueHash += values.nextElement().hashCode();
            }
            retValue += parameterName.hashCode() ^ valueHash;
        }
        return retValue;
    }


    /** {@inheritDoc} */
    public boolean isEmpty() {
        return !request.getKeys().hasMoreElements();
    }


    /** {@inheritDoc} */
    public Set<String> keySet() {
        return new KeySet(request);
    }


    /** {@inheritDoc} */
    public String[] put(String key, String[] value) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    public void putAll(Map<? extends String, ? extends String[]> map) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    public String[] remove(Object key) {
        throw new UnsupportedOperationException();
    }



    /** {@inheritDoc} */
    public int size() {
        return enumerationSize(request.getKeys());
    }


    /** {@inheritDoc} */
    public Collection<String[]> values() {
        return new HeaderValuesCollection();
    }

    /**
     * Extracts values enumeration of an attribute and returns the corresponding array of values.
     *
     * @param key The key of the attribute.
     * @return The values of the attribute.
     */
    private String[] getHeaderValues(String key) {
        List<String> list = new ArrayList<String>();
        Enumeration<String> values = request.getValues(key);
        while (values.hasMoreElements()) {
            list.add(values.nextElement());
        }
        String[] retValue = list.toArray(new String[list.size()]);
        return retValue;
    }

    /**
     * Converts the content of a string enumeration to an array of strings.
     *
     * @param enumeration The enumeration to convert.
     * @return The corresponding array.
     */
    private Set<String> enumeration2set(Enumeration<String> enumeration) {
        Set<String> retValue = new HashSet<String>();
        while (enumeration.hasMoreElements()) {
            retValue.add(enumeration.nextElement());
        }
        return retValue;
    }

    /**
     * Transforms a string array in a string set.
     *
     * @param valueArray The array to convert.
     * @return The corresponding set.
     */
    private Set<String> array2set(String[] valueArray) {
        Set<String> values = new HashSet<String>();
        for (int i = 0; i < valueArray.length; i++) {
            values.add(valueArray[i]);
        }
        return values;
    }

    /**
     * Checks if values of a header attribute are the same as the one passed in
     * the set.
     *
     * @param name The name of the header.
     * @param testSet The set of values it must contain.
     * @return <code>true</code> if all the values, and only them, are present
     * in the header values.
     */
    private boolean compareHeaders(String name, Set<String> testSet) {
        Enumeration<String> values = request.getValues(name);
        boolean matched = true;
        while (values.hasMoreElements() && matched) {
            String currentValue = values.nextElement();
            matched = testSet.remove(currentValue);
        }
        matched = matched && testSet.isEmpty();
        return matched;
    }

    /**
     * Entry set implementation for {@link HeaderValuesMap}.
     */
    private class HeadersEntrySet implements Set<Map.Entry<String, String[]>> {

        @Override
        public boolean add(java.util.Map.Entry<String, String[]> e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(
                Collection<? extends java.util.Map.Entry<String, String[]>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean contains(Object o) {
            return containsEntry((java.util.Map.Entry<String, String[]>) o);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean containsAll(Collection<?> c) {
            Collection<Map.Entry<String, String[]>> realCollection =
                (Collection<Map.Entry<String, String[]>>) c;
            for (Map.Entry<String, String[]> entry : realCollection) {
                if (!containsEntry(entry)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isEmpty() {
            return HeaderValuesMap.this.isEmpty();
        }

        @Override
        public Iterator<java.util.Map.Entry<String, String[]>> iterator() {
            return new HeadersEntrySetIterator();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return HeaderValuesMap.this.size();
        }

        @Override
        public Object[] toArray() {
            return toList().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return toList().toArray(a);
        }

        /**
         * Checks whether the given entry is present in the headers.
         *
         * @param entry The entry to check.
         * @return <code></code> if the key and the values of the entry are present.
         */
        private boolean containsEntry(Map.Entry<String, String[]> entry) {
            Enumeration<String> entryValues = request.getValues(key(entry.getKey()));
            String[] valueArray = entry.getValue();
            Set<String> values = array2set(valueArray);
            while (entryValues.hasMoreElements()) {
                if (!values.remove(entryValues.nextElement())) {
                    return false;
                }
            }
            return values.isEmpty();
        }

        /**
         * Turns this entry set into a list.
         *
         * @return The collection, turned into a list.
         */
        private List<Map.Entry<String, String[]>> toList() {
            List<Map.Entry<String, String[]>> entries = new ArrayList<Map.Entry<String, String[]>>();
            Enumeration<String> names = request.getKeys();
            while (names.hasMoreElements()) {
                entries.add(extractNextEntry(names));
            }
            return entries;
        }

        /**
         * Returns the next entry, by getting the next element in the given enumeration.
         *
         * @param names The enumeration to get the next name from..
         * @return The next map entry.
         */
        private MapEntry<String, String[]> extractNextEntry(
                Enumeration<String> names) {
            String name = names.nextElement();
            return new MapEntryArrayValues<String, String>(name, getHeaderValues(name), false);
        }

        /**
         * Iterates {@link HeadersEntrySet} elements.
         */
        private class HeadersEntrySetIterator implements Iterator<Map.Entry<String, String[]>> {

            /**
             * The enumeration to use.
             */
            private Enumeration<String> namesEnumeration = request.getKeys();

            @Override
            public boolean hasNext() {
                return namesEnumeration.hasMoreElements();
            }

            @Override
            public Map.Entry<String, String[]> next() {
                return extractNextEntry(namesEnumeration);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        }
    }

    /**
     * It is a collection of all values of the header. Each element is an array
     * of values of a single header.
     */
    private class HeaderValuesCollection implements Collection<String[]> {

        @Override
        public boolean add(String[] e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends String[]> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean containsAll(Collection<?> c) {
            Collection<String[]> realCollection = (Collection<String[]>) c;
            for (String[] value : realCollection) {
                if (!containsValue(value)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isEmpty() {
            return HeaderValuesMap.this.isEmpty();
        }

        @Override
        public Iterator<String[]> iterator() {
            return new HeaderValuesCollectionIterator();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return HeaderValuesMap.this.size();
        }

        @Override
        public Object[] toArray() {
            return toList().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return toList().toArray(a);
        }

        /**
         * Turns this collection into a list.
         *
         * @return The list.
         */
        private List<String[]> toList() {
            List<String[]> entries = new ArrayList<String[]>();
            Enumeration<String> names = request.getKeys();
            while (names.hasMoreElements()) {
                entries.add(enumeration2array(request.getValues(names.nextElement())));
            }
            return entries;
        }

        /**
         * Converts the content of a string enumeration to an array of strings.
         *
         * @param enumeration The enumeration to convert.
         * @return The corresponding array.
         */
        private String[] enumeration2array(Enumeration<String> enumeration) {
            List<String> list1 = new ArrayList<String>();
            while (enumeration.hasMoreElements()) {
                list1.add(enumeration.nextElement());
            }

            return list1.toArray(new String[list1.size()]);
        }

        /**
         * Iterates elements of {@link HeaderValuesCollection}.
         */
        private class HeaderValuesCollectionIterator implements Iterator<String[]> {

            /**
             * The enumeration of the name of header attributes.
             */
            private Enumeration<String> namesEnumeration = request.getKeys();

            @Override
            public boolean hasNext() {
                return namesEnumeration.hasMoreElements();
            }

            @Override
            public String[] next() {
                return enumeration2array(request.getValues(namesEnumeration.nextElement()));
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
