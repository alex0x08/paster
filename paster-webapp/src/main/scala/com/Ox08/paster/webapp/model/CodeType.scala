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
 */
object CodeType {

  val ActionScript = new CodeType("code.type.as3", "actionscript3", "text", "shBrushAS3.js", "as3")
  val Bash = new CodeType("code.type.bash", "bash", "sh", "shBrushBash.js", "sh")
  val ColdFusion = new CodeType("code.type.coldfusion", "cf", "coldfusion", "shBrushColdFusion.js", "cf")
  val CSharp = new CodeType("code.type.csharp", "csharp", "csharp", "shBrushCSharp.js", "cs")
  val CPP = new CodeType("code.type.cpp", "cpp", "c_cpp", "shBrushCpp.js", "cpp")
  val CSS = new CodeType("code.type.css", "css", "css", "shBrushCss.js", "css")
  val Delphi = new CodeType("code.type.delphi", "delphi", "pascal", "shBrushDelphi.js", "pas")
  val Diff = new CodeType("code.type.diff", "diff", "diff", "shBrushDiff.js", "diff")
  val Erlang = new CodeType("code.type.erlang", "erlang", "text", "shBrushErlang.js", "erl")
  val Groovy = new CodeType("code.type.groovy", "groovy", "groovy", "shBrushGroovy.js", "groovy")
  val JavaScript = new CodeType("code.type.js", "js", "javascript", "shBrushJScript.js", "js")
  val Java = new CodeType("code.type.java", "java", "java", "shBrushJava.js", "java")
  val JavaFX = new CodeType("code.type.javafx", "javafx", "java", "shBrushJavaFX.js", "javafx")
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
  add(ActionScript)
  add(Bash)
  add(ColdFusion)
  add(CSharp)
  add(CPP)
  add(Delphi)
  add(Diff)
  add(Erlang)
  add(Groovy)
  add(JavaFX)
  add(Perl)
  add(PowerShell)
  add(Python)
  add(Ruby)
  add(VisualBasic)

  private def add(c: CodeType): Unit = {
    map.put(c.getCode, c)
  }

  def list: Collection[CodeType] = map.values

  def valueOf(key: String): CodeType = if (map.containsKey(key)) map.get(key) else null
}


@Entity
class CodeType(name: String, code: String, keditType: String, kbrushFile: String, kext: String)
  extends Key(code, name) with java.io.Serializable {

  private var editType: String = keditType
  private var brushFile: String = kbrushFile
  private var ext: String = kext

  def this() = this(null, null, null, null, null)

  def getExt() = ext

  def setExt(ext: String): Unit = {
    this.ext = ext
  }

  def getEditType() = editType

  def setEditType(e: String): Unit = {
    this.editType = editType
  }

  def getBrushFile() = brushFile

  def setBrushFile(e: String): Unit = {
    this.brushFile = brushFile
  }

  override def create(code: String) = new CodeType(null, code, null, null, null)

  // def getAvailableCodeTypes() :Collection[CodeType] = CodeType.list
}

class CodeTypeEditor extends PropertyEditorSupport {

  override def setAsText(text: String): Unit = {
    setValue(CodeType.valueOf(text.toLowerCase));
  }
}
