/*
 * $Id: ELContextImpl.java 836180 2009-11-14 14:00:02Z apetrelli $
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
package org.apache.tiles.el;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.FunctionMapper;
import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
/**
 * Implementation of ELContext.<br>
 * Copied from Apache Tomcat 6.0.16 source code.
 *
 * @since 2.2.1
 */
public class ELContextImpl extends ELContext {
    /**
     * A null function mapper.
     */
    private static final FunctionMapper NULL_FUNCTION_MAPPER = new FunctionMapper() {
        @Override
        public Method resolveFunction(String prefix, String localName) {
            return null;
        }
    };
    /**
     * Default implementation for the variable mapper.
     */
    private static final class VariableMapperImpl extends VariableMapper {
        /**
         * The mapped variables.
         */
        private Map<String, ValueExpression> vars;
        /**
         * {@inheritDoc}
         */
        @Override
        public ValueExpression resolveVariable(String variable) {
            return vars == null ? null : vars.get(variable);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public ValueExpression setVariable(String variable,
                                           ValueExpression expression) {
            if (vars != null) {
                return vars.put(variable, expression);
            }
            vars = new HashMap<>();
            return vars.put(variable, expression);
        }
    }
    /**
     * The EL resolver to use.
     */
    private final ELResolver resolver;
    /**
     * The function mapper to use.
     */
    private final FunctionMapper functionMapper = NULL_FUNCTION_MAPPER;
    /**
     * The variable mapper to use.
     */
    private VariableMapper variableMapper;
    /**
     * Constructor.
     *
     * @param resolver The resolver to use.
     */
    public ELContextImpl(ELResolver resolver) {
        this.resolver = resolver;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ELResolver getELResolver() {
        return this.resolver;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionMapper getFunctionMapper() {
        return this.functionMapper;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public VariableMapper getVariableMapper() {
        if (this.variableMapper == null)
            this.variableMapper = new VariableMapperImpl();

        return this.variableMapper;
    }
}
