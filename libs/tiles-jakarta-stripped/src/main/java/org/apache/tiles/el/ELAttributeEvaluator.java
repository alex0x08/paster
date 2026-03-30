/*
 * $Id: ELAttributeEvaluator.java 891884 2009-12-17 20:43:12Z apetrelli $
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
import jakarta.el.*;
import org.apache.tiles.evaluator.AbstractAttributeEvaluator;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluates string expression with typical EL syntax.<br>
 * You can use normal EL syntax, knowing that the root objects are
 * {@link Request}, {@link ApplicationContext} and beans
 * contained in request, session and application scope.
 *
 * @version $Rev: 891884 $ $Date: 2009-12-18 07:43:12 +1100 (Fri, 18 Dec 2009) $
 * @since 2.2.1
 */
public class ELAttributeEvaluator extends AbstractAttributeEvaluator {
    /*
     * Initialization parameter to decide the implementation of
     * {@link ExpressionFactoryFactory}.
     *
     * @since 2.2.1
     */
    /**
     * The EL expression factory.
     *
     * @since 2.2.1
     */
    protected ExpressionFactory expressionFactory;
    /**
     * The EL resolver to use.
     *
     * @since 2.2.1
     */
    protected ELResolver resolver;
    /**
     * Constructor.
     *
     * @since 2.2.1
     */
    public ELAttributeEvaluator() {
    }
    /**
     * Sets the expression factory to use.
     *
     * @param expressionFactory The expression factory.
     * @since 2.2.1
     */
    public void setExpressionFactory(ExpressionFactory expressionFactory) {
        this.expressionFactory = expressionFactory;
    }
    /**
     * Sets the EL resolver to use.
     *
     * @param resolver The EL resolver.
     * @since 2.2.1
     */
    public void setResolver(ELResolver resolver) {
        this.resolver = resolver;
    }
    /**
     * {@inheritDoc}
     */
    public Object evaluate(String expression, Request request) {
        final ELContextImpl context = new ELContextImpl(resolver);
        context.putContext(Request.class, request);
        context.putContext(ApplicationContext.class,
                request.getApplicationContext());
        final ValueExpression valueExpression = expressionFactory
                .createValueExpression(context, expression, Object.class);
        return valueExpression.getValue(context);
    }
    /**
     * Implementation of ELContext.<br>
     * Copied from Apache Tomcat 6.0.16 source code.
     *
     * @since 2.2.1
     */
    public static class ELContextImpl extends ELContext {
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

}
