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

package uber.paste.model


import java.text.SimpleDateFormat
import java.util.Properties
import java.util.regex.Pattern
import javax.persistence.Embeddable
import uber.paste.base.Loggered

/**
 * Contants related to app version
 */
object AppVersion {
  
  /**
   *  undefined string, will display when version info cannot be obtained
   */
  val UNDEFINED = "UNDEFINED"
  
  val MF_IMPLEMENTATION_VERSION="Paster-Implementation-Version"
  val MF_IMPLEMENTATION_BUILD="Paster-Implementation-Build"
  val MF_IMPLEMENTATION_BUILD_DATE="Paster-Implementation-Build-Time"
 
  val PT_VER = Pattern.compile("^([0-9]+)\\.([0-9]+)-([a-zA-Z]+)$")
 
  val MAVEN_TS_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm")
  
}

/**
 *  Application version
 */
@Embeddable
class AppVersion {

   /**
    * app version
    */
   private var implVer:String = AppVersion.UNDEFINED
   private var implBuildNum:String = AppVersion.UNDEFINED
   private var implBuildTime:String = AppVersion.UNDEFINED
   
  /**
   * @return app version to store in database
   */
  def toDbString():String =  {
    return implVer + "|" + implBuildNum + "|" + implBuildTime   
  }
  
  /**
   * @return full version string
   */
  def getFull():String = {
    return implVer + "." + implBuildNum + " " + implBuildTime
  }
  
  /**
   * load & parse version from database
   *
   * @param ver property from database
   */
  def fillFromConfigProperty(ver:ConfigProperty):AppVersion = {
  
      if (ver==null) {
        return null
      }
   
      val parts =ver.getValue.split('|')
    
        implVer = parts(0)
        implBuildNum = parts(1)
        implBuildTime = parts(2)
    
    return this
  }
  
  /**
   * @return appversion object loaded from application manifest
   */
  def fillFromParams(ver:String, time:String):AppVersion = {
    
     implVer = ver; implBuildTime = time        
     implBuildNum = AppVersion.MAVEN_TS_FORMAT.parse(time)
                                        .getTime.toString
                              
    return this
  }
  
  
    /**
     * @return version number (ex. 1.0-SNAPSHOT)
     */
    def getImplVersion() =  implVer    
    /**
     * @return build number (changeset)
     */    
    def getImplBuildNum()= implBuildNum
    
    /**
     * @return build date and time 
     */
    def getImplBuildTime()= implBuildTime

    
    
   /**
    * Compares this version object to another
    * @param o version object to compare
    */
    def compareTo(o:AppVersion): Int = {
        if (o==null) return -2
        
        val current = new MavenVersion(implVer)
        val compare = new MavenVersion(o.implVer)
        
    if ( !current.getMod.equals(compare.getMod) || current.getMajor != compare.getMajor ) {
            return -2
        }  
   
    return if (current.getMinor > compare.getMinor)  -1 else 1           
    }
    
}


 class MavenVersion(raw:String) extends Loggered{
    
    private var minor:Int =0
    private var major:Int = 0
    private var mod:String=""
        
    val m = AppVersion.PT_VER.matcher(raw)
        
        if (!m.matches()) {
          logger.warn("version pattern not match: "+raw)
        } else {
          
          major   = Integer.parseInt(m.group(1))
          minor = Integer.parseInt(m.group(2))
          mod  = m.group(3)
        }
  
    def getMinor() = minor

    def getMajor() = major

    def getMod() = mod
    
}
