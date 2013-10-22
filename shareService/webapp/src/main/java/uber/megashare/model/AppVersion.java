/**
 * Copyright (C) 2011 aachernyshev <alex@0x08.tk>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uber.megashare.model;

import com.jcabi.manifests.Manifests;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Embeddable;
import uber.megashare.base.LoggedClass;



/**
 *
 * @author aachernyshev
 */


@Embeddable
public class AppVersion implements Serializable{

    public static final String UNDEFINED = "UNDEFINED",
            MF_IMPLEMENTATION_VERSION="Share-Implementation-Version",
            MF_IMPLEMENTATION_BUILD="Share-Implementation-Build",
            MF_IMPLEMENTATION_BUILD_DATE="Share-Implementation-Build-Time";
    
    private String implVer, //app version
                   implBuildNum, // build number
                   implBuildTime, // build date
                   implVersionFull; // full version string
      
    
    
    
    public AppVersion fillFromManifest() {
    
       
        
        implVer = getManifestValue(MF_IMPLEMENTATION_VERSION);

        implBuildNum = getManifestValue(MF_IMPLEMENTATION_BUILD);

        implBuildTime = getManifestValue(MF_IMPLEMENTATION_BUILD_DATE);

        implVersionFull = implVer + "." + implBuildNum + " " + implBuildTime;
        
        return this;
    }

    public String getImplVersion() {
        return implVer;
    }

    public String getImplBuildNum() {
        return implBuildNum;
    }

    public String getImplBuildTime() {
        return implBuildTime;
    }

    public String getImplVersionFull() {
        return implVersionFull;
    }
       
    
    private String getManifestValue(String key) {
        return Manifests.exists(key) ? Manifests.read(key) : UNDEFINED;
    }

   
    public int compareTo(AppVersion o) {
        if (o==null) return -2;
        
        MavenVersion current = new MavenVersion(implVer),
                     compare = new MavenVersion(o.implVer);
        
        if ( !current.mod.equals(compare.mod) || current.major != compare.major ) {
            return -2;
        }
        
        return  current.minor > compare.minor ? -1 : 1;   
        
    }

    private static final Pattern PT_VER = Pattern.compile("^([0-9]+)\\.([0-9]+)-([a-zA-Z]+)$");
    
    private class MavenVersion extends LoggedClass{
    
    private int minor,major;
    
    private String mod="";
    
    public MavenVersion(String raw) {
    
        Matcher m = PT_VER.matcher(raw);
        
        if (!m.matches()) {
            getLogger().warn("version pattern not match: "+raw);
            return;
        }
      
        major   = Integer.parseInt(m.group(1));
        minor = Integer.parseInt(m.group(2));
  
        mod  = m.group(3);
        
    }

    public int getMinor() {
        return minor;
    }

    public int getMajor() {
        return major;
    }

    public String getMod() {
        return mod;
    }    
    
}
  
}
