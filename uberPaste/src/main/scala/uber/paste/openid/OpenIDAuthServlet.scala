package uber.paste.openid

import javax.servlet.http.{HttpServletResponse, HttpSession, HttpServletRequest, HttpServlet}
import uber.paste.base.{SessionStore, Loggered}
import com.dyuproject.openid.{OpenIdServletFilter, OpenIdUser, RelyingParty}
import com.dyuproject.openid.ext.{AxSchemaExtension, SRegExtension}
import com.dyuproject.util.http.UrlEncodedParameterMap
import java.lang.String
import scala.collection.JavaConversions._
import uber.paste.build.UserBuilder
import uber.paste.model.User
import org.springframework.security.core.AuthenticationException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import java.io.{FileNotFoundException, IOException}
import javax.servlet.ServletException
import uber.paste.manager.{UserRememberMeService, UserManager}
import org.springframework.web.context.support.WebApplicationContextUtils
import java.net.UnknownHostException
import scala.Predef._


/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 13.02.13
 * Time: 13:59
 */
class OpenIDAuthServlet extends HttpServlet with Loggered{


    private var userManager:UserManager = null

    private var rememberMe:UserRememberMeService = null

    private val _relyingParty = RelyingParty.getInstance() .addListener(new SRegExtension()
                    .addExchange("email")
                    .addExchange("country")
                    .addExchange("language")
                    .addExchange("fullname")
    ).addListener(new AxSchemaExtension()
    .addExchange("email")
    .addExchange("country")
    .addExchange("language")
    .addExchange("firstname")
    .addExchange("lastname")
  )
    .addListener(new RelyingParty.Listener()
  {
    override def onDiscovery(user:OpenIdUser, request:HttpServletRequest)
    {
      logger.info("discovered user: " + user.getClaimedId());
    }
    override def onPreAuthenticate(user:OpenIdUser, request:HttpServletRequest,
       params:UrlEncodedParameterMap)
    {
      logger.info("pre-authenticate user: " + user.getClaimedId())
    }
    override def onAuthenticate(user:OpenIdUser, request:HttpServletRequest)
    {
      logger.info("newly authenticated user: " + user.getIdentity())

      for (e<-user.getAttributes().entrySet()) {
          logger.debug("key="+e.getKey()+" value="+e.getValue())
      }


      val  sreg:java.util.Map[String,String] = SRegExtension.remove(user)
      val axschema:java.util.Map[String,String] = AxSchemaExtension.remove(user)

      if(sreg!=null && !sreg.isEmpty())
      {
        logger.info("sreg: " + sreg);
        user.setAttribute("info", sreg);
      }
      else if(axschema!=null && !axschema.isEmpty())
      {
        logger.info("axschema: " + axschema);
        user.setAttribute("info", axschema);
      }
      fillPerson(user,request);
    }
    override def onAccess(user:OpenIdUser, request:HttpServletRequest)
    {
      logger.info("user access: " + user.getIdentity());
      logger.info("info: " + user.getAttribute("info"));

      fillPerson(user,request);
    }
  })



  private def fillPerson(user:OpenIdUser, request:HttpServletRequest) {

    logger.info("_fillPerson..");

    var luser = UserBuilder.createNew().fillFromOpenIdUser(user).get();


    val exist = userManager.getUserByOpenID(luser.getOpenID())
    if (exist != null) {
      doSpringAuth(exist,request.getSession());

    } else {
      luser =userManager.save(luser);
      doSpringAuth(luser,request.getSession());

    }

  }

  @throws(classOf[ServletException])
  override def init()  {
    super.init()

    val appContext =  WebApplicationContextUtils.getWebApplicationContext(getServletContext())

    userManager = appContext.getBean("userManager").asInstanceOf[UserManager]

    rememberMe =  appContext.getBean("umsRememberMeServices").asInstanceOf[UserRememberMeService]

  }

  @throws(classOf[IOException])
  @throws(classOf[ServletException])
  override def doGet(request:HttpServletRequest, response:HttpServletResponse)
  {
    doPost(request, response);
  }



  @throws(classOf[IOException])
  @throws(classOf[ServletException])
  override def doPost(request:HttpServletRequest, response:HttpServletResponse)
  {

    var errorMsg = OpenIdServletFilter.DEFAULT_ERROR_MSG
    try
    {
      val  user:OpenIdUser = _relyingParty.discover(request)
      if(user==null)
      {
        if(RelyingParty.isAuthResponse(request))
        {
          // authentication timeout
          response.sendRedirect(request.getRequestURI());
        }
        else
        {
          // set error msg if the openid_identifier is not resolved.
          if(request.getParameter(_relyingParty.getIdentifierParameter())!=null)
            request.setAttribute(OpenIdServletFilter.ERROR_MSG_ATTR, errorMsg);

          // new user
          //  request.getRequestDispatcher(PAGE_AFTER_AUTH).forward(request, response);
          response.sendRedirect(request.getContextPath() + "/")
        }
        return;
      }

      if(user.isAuthenticated())
      {
        // user already authenticated
        // request.getRequestDispatcher(PAGE_AFTER_AUTH).forward(request, response);
        response.sendRedirect(request.getContextPath() + "/");
        return;
      }

      if(user.isAssociated() && RelyingParty.isAuthResponse(request))
      {
        // verify authentication
        if(_relyingParty.verifyAuth(user, request, response))
        {

          if (user.getIdentity()!=null && UserManager.getCookieValue(request, rememberMe.getSSOCookieName())==null)  {

            val suser:User = userManager.getUserByOpenID(user.getIdentity())

            response.addCookie(UserManager.createNewSSOCookie(rememberMe.getSSOCookieName,
              userManager.createSession(suser.getId())))
            logger.debug("sso cookie was added for user .."+suser)
          }

          // authenticated
          // redirect to home to remove the query params instead of doing:
          // request.getRequestDispatcher("/home.jsp").forward(request, response);
          response.sendRedirect(request.getContextPath() + "/act/openid-login");
        }
        else
        {
          // failed verification
          // request.getRequestDispatcher(PAGE_AFTER_AUTH).forward(request, response);
          response.sendRedirect(request.getContextPath() + "/")
        }
        return
      }

      // associate and authenticate user
      val url = request.getRequestURL()
      logger.info("url="+url)

      val trustRoot = url.substring(0, url.indexOf("/", 9))

      logger.info("trustRoot="+trustRoot)

      val realm = url.substring(0, url.lastIndexOf("/"))

      logger.info("realm="+realm)

      val returnTo = url.toString()

      logger.info("returnto="+returnTo)

      if(_relyingParty.associateAndAuthenticate(user, request, response, trustRoot, realm,
        returnTo))
      {

        // successful association
        return;
      }
    }
    catch
    {
      case e @ (_ : UnknownHostException | _ : FileNotFoundException | _ :Exception) => {
        logger.error(e.getLocalizedMessage(),e)
        errorMsg = OpenIdServletFilter.DEFAULT_ERROR_MSG
      }

    }

    request.setAttribute(OpenIdServletFilter.ERROR_MSG_ATTR, errorMsg);
    // request.getRequestDispatcher(PAGE_AFTER_AUTH).forward(request, response);
    response.sendRedirect(request.getContextPath() + "/");
  }


  @throws(classOf[AuthenticationException])
  def doSpringAuth(user:User, s:HttpSession)  {


    // log user in automatically
    val  auth = new UsernamePasswordAuthenticationToken(
      user,
      user.getPassword(),
      user.getAuthorities())
    auth.setDetails(user)
  
    SecurityContextHolder.getContext().setAuthentication(auth)


    SessionStore.instance.add(s.getId(), user)

    //s.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext())


  }
}
