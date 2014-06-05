
package uber.paste.plugin.hello.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import uber.paste.controller.AbstractController
import uber.paste.openid.OpenIDServer

@Controller
@RequestMapping(Array("/plugin/hello"))
class HelloController extends AbstractController{

  @RequestMapping(Array("/hello"))
  def login(model:Model):Unit = {
    model.addAttribute("availableServers",OpenIDServer.list)
  }
  
}
