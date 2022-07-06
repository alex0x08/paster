/*
 * Copyright 2011 Ubersoft, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.Ox08.paster.webapp.model

import jakarta.persistence.Entity

import java.beans.PropertyEditorSupport
import java.util.Collection

/**
 * Brush aliases	File name
 * ActionScript3	as3, actionscript3	shBrushAS3.js
 * Bash/shell	bash, shell	shBrushBash.js
 * ColdFusion	cf, coldfusion	shBrushColdFusion.js
 * C#	c-sharp, csharp	shBrushCSharp.js
 * C++	cpp, c	shBrushCpp.js
 * CSS	css	shBrushCss.js
 * Delphi	delphi, pas, pascal	shBrushDelphi.js
 * Diff	diff, patch	shBrushDiff.js
 * Erlang	erl, erlang	shBrushErlang.js
 * Groovy	groovy	shBrushGroovy.js
 * JavaScript	js, jscript, javascript	shBrushJScript.js
 * Java	java	shBrushJava.js
 * JavaFX	jfx, javafx	shBrushJavaFX.js
 * Perl	perl, pl	shBrushPerl.js
 * PHP	php	shBrushPhp.js
 * Plain Text	plain, text	shBrushPlain.js
 * PowerShell	ps, powershell	shBrushPowerShell.js
 * Python	py, python	shBrushPython.js
 * Ruby	rails, ror, ruby	shBrushRuby.js
 * Scala	scala	shBrushScala.js
 * SQL	sql	shBrushSql.js
 * Visual Basic	vb, vbnet	shBrushVb.js
 * XML	xml, xhtml, xslt, html, xhtml
object CodeType {

  val Bash = new CodeType("code.type.bash", "bash", "sh", "shBrushBash.js", "sh")
  val CSharp = new CodeType("code.type.csharp", "csharp", "csharp", "shBrushCSharp.js", "cs")
  val CPP = new CodeType("code.type.cpp", "cpp", "c_cpp", "shBrushCpp.js", "cpp")
  val CSS = new CodeType("code.type.css", "css", "css", "shBrushCss.js", "css")
  val Diff = new CodeType("code.type.diff", "diff", "diff", "shBrushDiff.js", "diff")
  val Erlang = new CodeType("code.type.erlang", "erlang", "text", "shBrushErlang.js", "erl")
  val JavaScript = new CodeType("code.type.js", "js", "javascript", "shBrushJScript.js", "js")
  val Java = new CodeType("code.type.java", "java", "java", "shBrushJava.js", "java")
  val Perl = new CodeType("code.type.perl", "perl", "perl", "shBrushPerl.js", "pl")
  val Php = new CodeType("code.type.php", "php", "php", "shBrushPhp.js", "php")
  val Plain = new CodeType("code.type.plain", "plain", "text", "shBrushPlain.js", "txt")
  val PowerShell = new CodeType("code.type.powershell", "powershell", "powershell", "shBrushPowerShell.js", "ps")
  val Python = new CodeType("code.type.py", "py", "python", "shBrushPython.js", "py")
  val Ruby = new CodeType("code.type.ruby", "ruby", "ruby", "shBrushRuby.js", "rb")
  val Scala = new CodeType("code.type.scala", "scala", "scala", "shBrushScala.js", "scala")
  val Sql = new CodeType("code.type.sql", "sql", "sql", "shBrushSql.js", "sql")
  val VisualBasic = new CodeType("code.type.vb", "vb", "vbscript", "shBrushVb.js", "vb")
  val Xml = new CodeType("code.type.xml", "xml", "xml", "shBrushXml.js", "xml")

  val map = new java.util.LinkedHashMap[String, CodeType]

  add(Plain)
  add(Java)
  add(JavaScript)
  add(Xml)
  add(CSS)
  add(Sql)
  add(Php)
  add(Scala)
  add(Bash)
  add(CSharp)
  add(CPP)
  add(Diff)
  add(Erlang)
  add(Perl)
  add(PowerShell)
  add(Python)
  add(Ruby)
  add(VisualBasic)

  private def add(c: CodeType): Unit = {
    map.put(c.getCode(), c)
  }

  def list: Collection[CodeType] = map.values

  def valueOf(key: String): CodeType = if (map.containsKey(key)) map.get(key) else null
}
 */


class CodeType(code:String)
  extends java.io.Serializable {

  def getCode() = code

  def getName() = s"code.type.$code"

  def getEditType() = code

}

