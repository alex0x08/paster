<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>
<html >
    <head>
        <meta charset="utf-8">
        <title><fmt:message key="site.title"/></title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="<fmt:message key="site.title"/>">
        <meta name="author" content="Alex">

        <link href="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/css/bootstrap.min.css'/>" rel="stylesheet"/>
        <link href="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/css/bootstrap-theme.min.css'/>" rel="stylesheet"/>
        <style type="text/css">
           
            .sidebar-nav {
                padding: 9px 0;
            }
            .dropdown-menu {
             min-width: 0px;
            }

        </style>

        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/main/static/${appVersion}/css/main.css'/>" />
        <link href="<c:url value='/main/static/${appVersion}/libs/zoombox/zoombox.css'/>" rel="stylesheet" type="text/css" media="screen" />
      
        <link rel="shortcut icon" href="<c:url value='/favicon.png'/>">
    
      <script src="<c:url value='/main/assets/${appVersion}/jquery/2.0.3/jquery.js'/>"></script>
      <script src="<c:url value='/main/assets/${appVersion}/jquery-ui/1.10.2/ui/minified/jquery-ui.min.js'/>"></script>
      
      <script src="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/js/bootstrap.min.js'/>"></script>
      <script src="<c:url value='/main/static/${appVersion}/libs/zoombox/zoombox.js'/>"></script>

    </head>

    <body class='container'>

         <div class="navbar navbar-static-top">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
            
                 <tiles:insertAttribute name="menu" />
      
        </div>
        <div class="navbar-collapse collapse">
            
            <ul class="nav navbar-nav">
            <li>
                  <tiles:insertAttribute name="header" />   
            </li>
                    
            </ul>   
                 <ul class='nav navbar-nav navbar-right'>    
                               <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
                                   
                                   <li>
                             <div class="btn-group box">

                                <a class="btn dropdown-toggle btn-danger" data-toggle="dropdown" href="#">
                                    <fmt:message key="login.title"/>
                                    <span class="caret"></span>
                                </a>

                                <ul class="dropdown-menu" id="auth-dropdown" role='menu'>

                                    <li >
                                        <div style="width:12em;" class='box'>
                                       
                                             <form  action="<c:url value='/j_spring_security_check' />" role='form'  method="POST" class="form-inline">

                                            <div class="input-group">
                                                <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span></span>
                                                <input class="form-control"  name="j_username" type="text" placeholder="Username">
                                            </div>
                                                 <div class="input-group" style="padding-top:0.5em;padding-bottom: 0.5em;">
                                                <span class="input-group-addon"><span class="glyphicon glyphicon-lock"></span></span>
                                                <input class="form-control"  name="j_password" type="password" placeholder="Password">
                                            </div>  
                                            <button type="submit" class="btn" ><fmt:message key="button.login"/></button>
                                        </form>
                                            
                                        </div>
                                        
                                       
                                    </li>
                                </ul>
                            </div>
           
                                   </li>
                           

                        </sec:authorize>

                            
                                  <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
                                      <li>
                                          <div class="btn-group">

                                              <a class="btn dropdown-toggle btn-default" data-toggle="dropdown" href="#">
                                                  <img  src="<c:out value='http://www.gravatar.com/avatar/${currentUser.avatarHash}?s=16'/>"/>
                                                  <sec:authentication property="principal.username" />
                                                  <span class="caret"></span>
                                              </a>

                                              <ul class="dropdown-menu ">
                                                  <li >

                                                      <a class="profile" href="<c:url value="/main/profile"/>">
                                                          <span class="glyphicon glyphicon-user"></span> <fmt:message key="profile.title"/></a> 
                                                  </li>

                                                  <li>  <a href="<c:url value="/act/doLogout"/>">
                                                          <span class="glyphicon glyphicon-log-out"></span>
                                                          <fmt:message key="button.logout"/></a>
                                                  </li>
                                                  <!-- dropdown menu links -->
                                              </ul>
                                          </div>                              
                                      </li>


                        </sec:authorize>

                             <li>
                   <div class="btn-group" >
                                
                            <a class="btn dropdown-toggle " data-toggle="dropdown" href="#">
                                <img style="display: inline; vertical-align:middle;" 
                                     title="<fmt:message key="locale.${pageContext.response.locale.language}"/>" 
                                     src="<c:url value='/main/static/${appVersion}/images/flags/flag_${pageContext.response.locale.language}.png'/>"/>
                                <span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu">

                                <c:forTokens items="ru,en" delims="," var="locale" >
                                    <c:if test="${pageContext.response.locale.language ne locale}">

                                        <li>
                                           
                                            <a href="<c:url value="${request.requestURL}">
                                                   <c:param name="locale" value="${locale}" />
                                                    <c:forEach items="${param}" var="currentParam">
                                                        <c:if test="${currentParam.key ne 'locale'}">
                                                            <c:param name="${currentParam.key}" value="${currentParam.value}"/>
                                                        </c:if>
                                                    </c:forEach>
                                                   
                                               </c:url>">
                                                   <img style="display: inline; vertical-align:middle;" 
                                                        title="<fmt:message key="locale.${locale}"/>" 
                                                   src="<c:url value='/main/static/${appVersion}/images/flags/flag_${locale}.png'/>"/>
                                               <fmt:message key="locale.${locale}"/>
                                            </a>
                                        </li>

                                    </c:if>

                                </c:forTokens>   

                            </ul>
                        </div>
                         
            </li>
                      
                                      
                     </ul>        
            
                         
                            
                        </div>
                
            </li>        
               

        </div>
        
                                
                       
        <div class="container-fluid">

            <div class="row-fluid">
               
                <div class="span9">
                    
                    <c:if test="${not empty statusMessageKey}">
                        <div class="alert alert-block alert-warning" style="width:20em;">
                            <a class="close" data-dismiss="alert" href="#">×</a>
                            <p><fmt:message key="${statusMessageKey}"/></p>
                        </div>
                    </c:if>
               
                    <tiles:insertAttribute name="content" />
                    <!--/span-->
                </div><!--/row-->
            </div><!--/span-->
        </div><!--/row-->

        <hr>

        <footer>
            <tiles:insertAttribute name="footer" />


        </footer>

    </div><!--/.fluid-container-->

    
   


    <!-- Boostrap modal dialog -->
    <div id="delete_confirmation" class="modal fade" aria-labelledby="myModalLabel" aria-hidden="true">
        
        <div class="modal-dialog">
            <div class="modal-content">
                
         
        <div class="modal-header">
            <a href="#" class="close" data-dismiss="modal">x</a>
            <h3><fmt:message key="dialog.confirm"/></h3>
        </div>
        <div class="modal-body">
            <div class="paddingT15 paddingB15" id="modal_text">    
                <div>
                    <img id="deleteTargetImg" src="<c:url value='/images/file.png'/>" />
                    <span id="deleteTargetTitle">

                    </span>
                </div>
                <fmt:message key="dialog.body.delete.message"/>
            </div>
        </div>
        <div class="modal-footer">
            <a href="#" class="btn btn-danger btn-large confirm_delete_the_item no_return"><fmt:message key="button.delete"/></a>
            <a href="#" class="btn btn-secondary " data-dismiss="modal"><fmt:message key="button.cancel"/></a>
        </div>
           </div>
       
        </div>    
    </div>
        
      <div id="paste_preview" class="modal fade" >
          
           <div class="modal-dialog">
               <div class="modal-content">
                   
        <div class="modal-header">
            <a href="#" class="close" data-dismiss="modal">x</a>
                  <div>
                    <img id="targetImg" src="<c:url value='/images/file.png'/>" />
                    <span id="targetTitle">

                    </span>
                </div>
        </div>
        <div class="modal-body">
            <div class="paddingT15 paddingB15" id="modal_text">    
              
                <div id="pasteContent" >
                </div>
            </div>
        </div>
               </div></div>
    </div>
    


           


    <script type="text/javascript" src="<c:url value='/main/static/${appVersion}/libs/flowplayer/flowplayer-3.2.12.min.js'/>"></script>

    <script type="text/javascript">
        
           $('#auth-dropdown').click(function(event){
                event.stopPropagation();
                }
            );
        
        $(document).ready(function() {
    
         $('a.zoombox').zoombox({
                theme       : 'simple',        //available themes : zoombox,lightbox, prettyphoto, darkprettyphoto, simple
                opacity     : 0.3,              // Black overlay opacity
                duration    : 800,              // Animation duration
                animation   : true,             // Do we have to animate the box ?
                width       : 1024,              // Default width
                height      : 768,              // Default height
                gallery     : true,             // Allow gallery thumb view
                autoplay : true,                // Autoplay for video
                overflow: true
            });
     
        flowplayer("a.embedPlayer", "<c:url value='/main/static/${appVersion}/libs/flowplayer/flowplayer-3.2.16.swf'/>", {
      clip:  {
          autoPlay: false,
          autoBuffering: true
      }
  });
     
        var commentsUrl = '<c:url value="/main/file/raw/comments"/>';

            $("[rel='tooltip']").tooltip();
  
               $(".commentsBtn").bind('click', function(e) {
               
                 var thisTab = e.target; 
                  var pageTarget = $(thisTab).attr('href');
                  
                  $(pageTarget).load(commentsUrl+'?id='+$(thisTab).attr('modelId'));
                });
        
              
         $(".pastePreviewBtn").bind('click', function() {
                var link=$(this);
           
           
                $('#targetImg').attr('src',link.attr('targetIcon'));
                $('#targetTitle').html(link.attr('targetTitle'));
           
           
                //${pasteUrl} ${externalUrl}
            
                var clink = '<iframe src="${pasteUrl}/main/paste/loadFrom?url=${externalUrl}/act/download?id=';
                clink+= link.attr('targetId');
                clink+= '" scrolling="auto" frameborder="0" style="width:640px;height:320px; "  allowTransparency="true" > </iframe>';
                
                $('#pasteContent').html(clink);
               $('#paste_preview').modal({backdrop: false}, "show");
              
                return false;

            });
   
   
            $(".fileDeleteBtn").click(function() {
                var link=$(this);           
            
                $('#deleteTargetImg').attr('src',link.attr('targetIcon'));
                $('#deleteTargetTitle').html(link.attr('targetTitle'));
            
                $('#delete_confirmation').modal({backdrop: false}, "show");
                
                $('.confirm_delete_the_item').click(function(e) {
                    $('#delete_confirmation').modal("hide");
                    e.preventDefault();
                    location.href=link.attr('deleteLink');
                    return true;
   
                });

                return false;

            });

        });
    </script>



</body>
</html>
