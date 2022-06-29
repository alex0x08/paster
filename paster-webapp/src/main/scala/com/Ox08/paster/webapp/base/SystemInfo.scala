/*
 * Copyright 2013 Ubersoft, LLC.
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

package com.Ox08.paster.webapp.base

import com.Ox08.paster.webapp.model.{AppVersion, Key, KeyObj, Project}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


object AppProfile extends KeyObj[AppProfile] {

  val DEVELOPMENT = new AppProfile("DEVELOPMENT", "Development mode")
  val PRODUCTION = new AppProfile("PRODUCTION", "Prod")

  add(DEVELOPMENT);
  add(PRODUCTION)

}


class AppProfile(code: String, desc: String) extends Key(code, desc) {
  override def create(code: String) = new AppProfile(code, null)
}


@Component
class SystemInfo() {

  private var runtimeVersion: AppVersion = _

  private var dateStart: java.util.Date = null

  private var dateInstall: java.util.Date = null

  private var project: Project = null

  private var profile: AppProfile = AppProfile.DEVELOPMENT

  private var lock: Boolean = false

  @Autowired
  def this(@Value("${build.version}") buildVersion: String,
           @Value("${build.time}") buildTime: String) = {
    this();
    runtimeVersion = new AppVersion().fillFromParams(buildVersion, buildTime)
  }

  def getRuntimeVersion() = runtimeVersion

  def getAppProfile() = profile

  def setAppProfile(profile: AppProfile): Unit = {
    checkLock(); this.profile = profile
  }

  def getDateStart() = dateStart

  def setDateStart(date: java.util.Date) : Unit = {
    checkLock(); this.dateStart = date
  }

  def getDateInstall() = dateInstall

  def setDateInstall(date: java.util.Date) : Unit = {
    checkLock(); this.dateInstall = date
  }


  def getProject() = project

  def setProject(p: Project) : Unit = {
    checkLock(); project = p
  }

  def doLock() : Unit = {
    this.lock = true
  }

  private def checkLock() : Unit = {
    if (lock) {
      throw new IllegalStateException("Object locked.")
    }
  }
}
