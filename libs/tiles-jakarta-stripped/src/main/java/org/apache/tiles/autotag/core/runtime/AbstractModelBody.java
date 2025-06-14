/*
 * $Id: AbstractModelBody.java 1692328 2015-07-22 21:16:35Z mck $
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
package org.apache.tiles.autotag.core.runtime;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Pattern;

import org.apache.tiles.autotag.core.runtime.util.NullWriter;

/**
 * Base class for the abstraction of the body.
 *
 * @version $Rev: 1692328 $ $Date: 2015-07-23 07:16:35 +1000 (Thu, 23 Jul 2015) $
 */
public abstract class AbstractModelBody implements ModelBody {

    // precompiled the pattern to avoid compiling on every method call
    private static final Pattern PATTERN = Pattern.compile("^\\s*|\\s*$");

    /**
     * The default writer to use.
     */
    private final Writer defaultWriter;

    /**
     * Constructor.
     *
     * @param defaultWriter The default writer to use.
     */
    public AbstractModelBody(Writer defaultWriter) {
        this.defaultWriter = defaultWriter;
    }

    @Override
    public void evaluate() throws IOException {
        evaluate(defaultWriter);
    }

    @Override
    public String evaluateAsString() throws IOException {
        StringWriter writer = new StringWriter();
        try (writer) {
            evaluate(writer);
        }
        String body = writer.toString();
        if (body != null) {
            body = PATTERN.matcher(body).replaceAll("");
            if (body.isEmpty())
                body = null;

        }
        return body;
    }

    @Override
    public void evaluateWithoutWriting() throws IOException {
        try (NullWriter writer = new NullWriter()) {
            evaluate(writer);
        }
    }

}
