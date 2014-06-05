package uber.paste.web

import javax.servlet._
import java.io.IOException
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 13.04.13
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
class CrossOriginResourceFilter extends Filter{

  @throws(classOf[IOException])
  @throws(classOf[ServletException])
  override def doFilter(req:ServletRequest, res:ServletResponse,
    chain:FilterChain)
   {

     val response = res.asInstanceOf[HttpServletResponse]


     if (!response.isCommitted) {
       response.setHeader("Access-Control-Allow-Origin", "*")
       response.setHeader("Access-Control-Allow-Credentials", "true")
       response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
       response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept")
         }
     chain.doFilter(req, res)
     }

  def init(filterConfig:FilterConfig) {
  }

 def destroy() {
  }
}
