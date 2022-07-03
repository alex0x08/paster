/*
 * $Id: JspModelBody.java 1305546 2012-03-26 20:34:37Z nlebas $
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
package org.apache.tiles.request.jsp.autotag;

import java.io.IOException;
import java.io.Writer;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.JspFragment;

import org.apache.tiles.autotag.core.runtime.AbstractModelBody;

/**
 * The body abstraction in a JSP tag.
 *
 * @version $Rev: 1305546 $ $Date: 2012-03-27 07:34:37 +1100 (Tue, 27 Mar 2012) $
 */
public class JspModelBody extends AbstractModelBody {

    /**
     * The real body.
     */
    private JspFragment jspFragment;

    /**
     * Constructor.
     *
     * @param jspFragment The real body.
     * @param jspContext The page context.
     */
    public JspModelBody(JspFragment jspFragment, JspContext jspContext) {
        super(jspContext.getOut());
        this.jspFragment = jspFragment;
    }

    @Override
    public void evaluate(Writer writer) throws IOException {
        if (jspFragment == null) {
            return;
        }

        try {
            jspFragment.invoke(writer);
        } catch (JspException e) {
            throw new IOException("JspException when evaluating the body", e);
        }
    }

}
