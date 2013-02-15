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

package uber.paste.controller

import uber.paste.base.Loggered
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.ExceptionHandler
import uber.paste.model.{Struct, User}
import uber.paste.base.SessionStore
import org.springframework.context.MessageSource
import javax.annotation.Resource
import java.util.Locale
import scala.collection.JavaConversions._

abstract class AbstractController extends Loggered{

  protected val page404= "404"

  protected val page403= "403"

  protected val page500= "500"


  @Resource(name = "messageSource")
   protected val messageSource:MessageSource = null

  def getResource(key:String,locale:Locale):String = messageSource.getMessage(key,new Array[java.lang.Object](0),locale)
  def getResource(key:String,args:Array[Any],locale:Locale):String = messageSource.getMessage(key,args.asInstanceOf[Array[java.lang.Object]],locale)
  
  /**
   * handles error throwed from spring dao level
   * @param ex
   * @return 
   */
  @ExceptionHandler(Array(classOf[ObjectRetrievalFailureException]))
  def handleDAOException(ex:ObjectRetrievalFailureException):String= {
    logger.error("Object not found: " + ex.getLocalizedMessage(), ex);
    return page404;
  }

  /**
   * handles error throwed from spring dao level
   * @param ex
   * @return
   */
  @ExceptionHandler(Array(classOf[Throwable]))
  def handleException(ex:Throwable):String= {
    logger.error("exception " + ex.getLocalizedMessage(), ex);
    return page500;
  }


  def isCurrentUserLoggedIn():Boolean = {
    return getCurrentUser() != null
  }

  def isCurrentUserAdmin():Boolean = {
    val u:User = getCurrentUser()
    return u != null && u.isAdmin()
  }

  def  getCurrentUser():User = {

    return SecurityContextHolder.getContext().getAuthentication().getPrincipal() match {
      case u:User => { 
          SessionStore.instance.getUserForLogin(
            (SecurityContextHolder.getContext().getAuthentication().getPrincipal()).asInstanceOf[User].getUsername())
        }
      case _ => {
          /**
           * this almost all time means that we got anonymous user
           */
          logger.debug("getCurrentUser ,unknown principal type: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString)
          return null;
        }
    }
    
    
       
  }



}
