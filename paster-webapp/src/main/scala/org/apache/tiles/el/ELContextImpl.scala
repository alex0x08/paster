package org.apache.tiles.el

import jakarta.el.{ELContext, ELResolver, FunctionMapper, ValueExpression, VariableMapper}
import org.apache.tiles.el.ELContextImpl.{NULL_FUNCTION_MAPPER, VariableMapperImpl}

import java.lang.reflect.Method
import java.util

object ELContextImpl {

  /**
   * A null function mapper.
   */
  private val NULL_FUNCTION_MAPPER = new FunctionMapper() {
    def resolveFunction(prefix: String, localName: String): Method = null
  }


  /**
   * Default implementation for the variable mapper.
   */
  final private class VariableMapperImpl extends VariableMapper {
    /**
     * The mapped variables.
     */
    private var vars:java.util.Map[String,ValueExpression] = null

    /** {@inheritDoc } */
    override def resolveVariable(variable: String): ValueExpression = {
      if (vars == null) return null
      vars.get(variable)
    }

    def setVariable(variable: String, expression: ValueExpression): ValueExpression = {
      if (vars == null) vars = new util.HashMap[String,ValueExpression]()
      vars.put(variable, expression)
    }
  }

}

class ELContextImpl(resolver: ELResolver) extends ELContext{


  /**
   * The function mapper to use.
   */
  private var functionMapper:FunctionMapper = NULL_FUNCTION_MAPPER

  /**
   * The variable mapper to use.
   */
  private var variableMapper:VariableMapper  = null

  def getELResolver: ELResolver = resolver

  def getFunctionMapper: FunctionMapper = functionMapper

  def getVariableMapper: VariableMapper = {
    if (this.variableMapper == null)
      this.variableMapper = new VariableMapperImpl
    this.variableMapper
  }

  /**
   * Sets the function mapper to use.
   *
   * @param functionMapper The function mapper.
   * @since 2.2.1
   */
  def setFunctionMapper(functionMapper: FunctionMapper): Unit = {
    this.functionMapper = functionMapper
  }

  /**
   * Sets the variable mapper to use.
   *
   * @param variableMapper The variable mapper.
   * @since 2.2.1
   */
  def setVariableMapper(variableMapper: VariableMapper): Unit = {
    this.variableMapper = variableMapper
  }
}
