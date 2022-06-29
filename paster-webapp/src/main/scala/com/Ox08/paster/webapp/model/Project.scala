/*
 * Copyright 2014 Ubersoft, LLC.
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

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.search.annotations.{Field, Indexed}
import org.springframework.web.multipart.MultipartFile
import javax.persistence.{Column, Entity, Lob}
import javax.xml.bind.annotation.{XmlRootElement, XmlTransient}

@Entity
@Indexed(index = "indexes/projects")
@XmlRootElement(name="project")
class Project(name:String) extends Named(name) with java.io.Serializable{

  @Lob
  @XmlTransient
  private var iconImage:String =null
  
  @Field
  private var description:String = null
  
  @Lob
  @Column(length = Integer.MAX_VALUE)
  @XmlTransient
  private var clientImage: String = null
  
  @transient
  @XmlTransient
  private var clientImageFile:MultipartFile = null
 
  @transient
  @XmlTransient
  private var iconImageFile:MultipartFile = null

  def this() = this(null)
  
  def getDescription() = description
  def setDescription(d:String) {description = d}
  
  @JsonIgnore
  def getIconImage() = iconImage
  def setIconImage(img:String) {iconImage = img}

  @JsonIgnore
  def getClientImage() = clientImage
  def setClientImage(img:String) {clientImage = img}

  
  @JsonIgnore
  def getIconImageFile() = iconImageFile
  def setIconImageFile(img:MultipartFile) {iconImageFile = img}

  @JsonIgnore
  def getClientImageFile() = clientImageFile
  def setClientImageFile(img:MultipartFile) {clientImageFile = img}

}
