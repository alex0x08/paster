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

package uber.paste.model

import java.util.HashMap
import java.util.Collection
import javax.persistence._
import java.beans.PropertyEditorSupport
import java.util

/**
 * Brush aliases	File name
ActionScript3	as3, actionscript3	shBrushAS3.js
Bash/shell	bash, shell	shBrushBash.js
ColdFusion	cf, coldfusion	shBrushColdFusion.js
C#	c-sharp, csharp	shBrushCSharp.js
C++	cpp, c	shBrushCpp.js
CSS	css	shBrushCss.js
Delphi	delphi, pas, pascal	shBrushDelphi.js
Diff	diff, patch	shBrushDiff.js
Erlang	erl, erlang	shBrushErlang.js
Groovy	groovy	shBrushGroovy.js
JavaScript	js, jscript, javascript	shBrushJScript.js
Java	java	shBrushJava.js
JavaFX	jfx, javafx	shBrushJavaFX.js
Perl	perl, pl	shBrushPerl.js
PHP	php	shBrushPhp.js
Plain Text	plain, text	shBrushPlain.js
PowerShell	ps, powershell	shBrushPowerShell.js
Python	py, python	shBrushPython.js
Ruby	rails, ror, ruby	shBrushRuby.js
Scala	scala	shBrushScala.js
SQL	sql	shBrushSql.js
Visual Basic	vb, vbnet	shBrushVb.js
XML	xml, xhtml, xslt, html, xhtml
 */
object CodeType {
  
  val ActionScript = new CodeType("code.type.as3","actionscript3","text")
  val Bash = new CodeType("code.type.bash","bash","sh")
  val ColdFusion = new CodeType("code.type.coldfusion","cf","coldfusion")
  val CSharp = new CodeType("code.type.csharp","csharp","csharp")
  val CPP = new CodeType("code.type.cpp","cpp","c_cpp")
  val CSS = new CodeType("code.type.css","css","css")
  val Delphi = new CodeType("code.type.delphi","delphi","pascal")
  val Diff = new CodeType("code.type.diff","diff","diff")
  val Erlang = new CodeType("code.type.erlang","erlang","text")
  val Groovy = new CodeType("code.type.groovy","groovy","groovy")
  val JavaScript = new CodeType("code.type.js","js","javascript")
  val Java = new CodeType("code.type.java","java","java")
  val JavaFX = new CodeType("code.type.javafx","javafx","java")
  val Perl = new CodeType("code.type.perl","perl","perl")
  val Php = new CodeType("code.type.php","php","php")
  val Plain = new CodeType("code.type.plain","plain","text")
  val PowerShell = new CodeType("code.type.powershell","powershell","powershell")
  val Python = new CodeType("code.type.py","py","python")
  val Ruby = new CodeType("code.type.ruby","ruby","ruby")
  val Scala = new CodeType("code.type.scala","scala","scala")
  val Sql = new CodeType("code.type.sql","sql","sql")
  val VisualBasic = new CodeType("code.type.vb","vb","vbscript")
  val Xml = new CodeType("code.type.xml","xml","xml")

  val map = new java.util.LinkedHashMap[String,CodeType]

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
  
  private def add(c:CodeType):Unit = {
    map.put(c.getCode,c)
  }
  
  def list:Collection[CodeType] = {    
    return map.values
  }
  def valueOf(key:String):CodeType = {
    return if (map.containsKey(key)) map.get(key) else null
  }
}


@Entity
@Table(name = "codeTypes")
class CodeType extends Key with java.io.Serializable{

  private var editType:String = null

def this(name:String,code:String,editType:String) = {
    this()
    setName(name)
    setCode(code)
    this.editType=editType;
  }

  def getEditType() = editType
  def setEditType(e:String) { this.editType=editType}

 // def getAvailableCodeTypes() :Collection[CodeType] = CodeType.list
}

class CodeTypeEditor extends PropertyEditorSupport{

  override def setAsText(text:String):Unit = {
    setValue(CodeType.valueOf(text.toLowerCase));
  }
}
