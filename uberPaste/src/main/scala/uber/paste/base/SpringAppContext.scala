/*
 * Copyright 2011 WorldWide Conferencing, LLC.
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

package uber.paste.base


import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

object SpringAppContext {
  
  def instance = new SpringAppContext
  
}
class SpringAppContext extends ApplicationContextAware{

  private var applicationContext:ApplicationContext = null

  /**
   * Loaded during Spring initialization.
   */
  @Override
  @throws(classOf[BeansException])
  def setApplicationContext(ctx:ApplicationContext)
  {
    applicationContext = ctx;
  }

  /**
   * Get access to the Spring ApplicationContext from anywhere in application.
   * @return Spring ApplicationContext
   */
  def getApplicationContext():ApplicationContext = {
    return applicationContext;
  }
  
}
