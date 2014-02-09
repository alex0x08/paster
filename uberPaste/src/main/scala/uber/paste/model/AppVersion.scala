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

import com.jcabi.manifests.Manifests
import java.util.regex.Pattern
import javax.persistence.Embeddable
import uber.paste.base.Loggered

object AppVersion {
  
  val UNDEFINED = "UNDEFINED"
  val MF_IMPLEMENTATION_VERSION="Paster-Implementation-Version"
  val MF_IMPLEMENTATION_BUILD="Paster-Implementation-Build"
  val MF_IMPLEMENTATION_BUILD_DATE="Paster-Implementation-Build-Time"
 
  val PT_VER = Pattern.compile("^([0-9]+)\\.([0-9]+)-([a-zA-Z]+)$")
 
}

@Embeddable
class AppVersion {

   /**
    * app version
    */
   private var implVer:String = AppVersion.UNDEFINED
   private var implBuildNum:String = AppVersion.UNDEFINED
   private var implBuildTime:String = AppVersion.UNDEFINED
   //private var implVersionFull:String = null
   
  def toDbString():String =  {
    return implVer + "|" + implBuildNum + "|" + implBuildTime   
  }
  
  def getFull():String = {
    return implVer + "." + implBuildNum + " " + implBuildTime
  }
  
  def fillFromConfigProperty(ver:ConfigProperty):AppVersion = {
  
      if (ver==null) {
        return null
      }
      
    //System.out.println("_parsing value "+ver.getValue)
    
      val parts =ver.getValue.split('|')
    
        implVer = parts(0)

        implBuildNum = parts(1)

        implBuildTime = parts(2)

    
    return this;
  }
  
   def fillFromManifest():AppVersion = {
        
        implVer = getManifestValue(AppVersion.MF_IMPLEMENTATION_VERSION)

        implBuildNum = getManifestValue(AppVersion.MF_IMPLEMENTATION_BUILD)

        implBuildTime = getManifestValue(AppVersion.MF_IMPLEMENTATION_BUILD_DATE)
        
        return this;
    }

    def getImplVersion() =  implVer
    

    def getImplBuildNum()= implBuildNum
    
    def getImplBuildTime()= implBuildTime

    
    def getManifestValue(key:String ):String = {
     return if (Manifests.exists(key)) Manifests.read(key) else AppVersion.UNDEFINED
    }
    
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
