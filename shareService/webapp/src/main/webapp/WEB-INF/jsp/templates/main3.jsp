<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>
<html >
    <head>
        <meta charset="utf-8">
        <title><fmt:message key="site.title"/></title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="alex">

        <link href="<c:url value='/css/metro-bootstrap.css'/>" rel="stylesheet"/>
        <style type="text/css">
            body {
                padding-top: 60px;
                padding-bottom: 40px;
            }
            .sidebar-nav {
                padding: 9px 0;
            }
            .dropdown-menu {
             min-width: 0px;
            }

        </style>

        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->


        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/css/main.css'/>" />
        <link href="<c:url value='/libs/zoombox/zoombox.css'/>" rel="stylesheet" type="text/css" media="screen" />
        <link href="<c:url value='/libs/jasny-bootstrap/css/jasny-bootstrap.css'/>" rel="stylesheet" type="text/css" media="screen" />
        <link href="<c:url value='/libs/jasny-bootstrap/css/jasny-bootstrap-responsive.css'/>" rel="stylesheet" type="text/css" media="screen" />

        <!-- Le fav and touch icons -->
        <link rel="shortcut icon" href="<c:url value='/favicon.png'/>">
        <link rel="apple-touch-icon-precomposed" sizes="144x144" 
              href="<c:url value='/assets/ico/apple-touch-icon-144-precomposed.png'/>">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" 
              href="<c:url value='/assets/ico/apple-touch-icon-114-precomposed.png'/>">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" 
              href="<c:url value='/assets/ico/apple-touch-icon-72-precomposed.png'/>">
        <link rel="apple-touch-icon-precomposed" 
              href="<c:url value='/assets/ico/apple-touch-icon-57-precomposed.png'/>">

        <script src="<c:url value='/js/jquery-1.8.0.js'/>"></script>
        <script src="<c:url value='/libs/zoombox/zoombox.js'/>"></script>


    </head>

    <body data-spy="scroll" data-target=".subnav" data-offset="50" screen_capture_injected="true">


        <div class="navbar  navbar-fixed-top ">
            <div class="navbar-inner">
                <div class="container-fluid">
                    <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </a>

                    <a class="brand" href="<c:url value='/'/>"><img style="width:35px;height:35px;" src="<c:url value='/images/file.png'/>"/></a>    
                    <div class="nav-collapse collapse navbar-responsive-collapse">

                        <tiles:insertAttribute name="header" />

                        <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">

                            <div class="btn-group pull-right">

                                <a class="btn dropdown-toggle btn-danger" data-toggle="dropdown" href="#">
                                    <fmt:message key="login.title"/>
                                    <span class="caret"></span>
                                </a>

                                <ul class="dropdown-menu" id="auth-dropdown">


                                    <li style="padding-right:5px;padding-left: 5px;" >


                                        <form  action="<c:url value='/j_spring_security_check' />"  method="POST">

                                            <div class="input-prepend">
                                                <span class="add-on"><i class="icon-user"></i></span>
                                                <input class="span2"  name="j_username" type="text" placeholder="Username">
                                            </div>

                                            <div class="input-prepend">
                                                <span class="add-on"><i class="icon-password"></i></span>
                                                <input class="span2"  name="j_password" type="password" placeholder="Password">
                                            </div>


                                            <label class="checkbox">
                                                <input type="checkbox" name="_spring_security_remember_me"> <fmt:message key="login.rememberMe"/>
                                            </label>

                                            <button type="submit" class="btn">Sign in</button>

                                        </form>
                                    </li>
                                </ul>
                            </div>


                        </sec:authorize>

                        <div class="btn-group pull-right">
                            <a class="btn dropdown-toggle " data-toggle="dropdown" href="#">
                                <img style="display: inline; vertical-align:middle;" 
                                     title="<fmt:message key="locale.${pageContext.response.locale.language}"/>" 
                                     src="<c:url value='/images/flags/flag_${pageContext.response.locale.language}.png'/>"/>
                                <span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu">

                                <c:forTokens items="ru,en" delims="," var="locale" >
                                    <c:if test="${pageContext.response.locale.language ne locale}">

                                        <li>
                                            <a href="<c:url value="${request.requestURL}">
                                                   <c:param name="locale" value="${locale}" /></c:url>">
                                                   <img style="display: inline; vertical-align:middle;" 
                                                        title="<fmt:message key="locale.${locale}"/>" 
                                                   src="<c:url value='/images/flags/flag_${locale}.png'/>"/>
                                               <fmt:message key="locale.${locale}"/>
                                            </a>
                                        </li>

                                    </c:if>

                                </c:forTokens>   

                            </ul>
                        </div>

                        <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">

                            <div class="btn-group pull-right">

                                <a class="btn dropdown-toggle btn-primary" data-toggle="dropdown" href="#">
                                    <img  src="<c:out value='http://www.gravatar.com/avatar/${currentUser.avatarHash}?s=32'/>"/>
                                    <sec:authentication property="principal.username" />
                                    <span class="caret"></span>
                                </a>

                                <ul class="dropdown-menu">
                                    <li >

                                        <a class="profile" href="<c:url value="/main/profile"/>">
                                            <i class="icon-user"></i><fmt:message key="profile.title"/></a> 
                                    </li>

                                    <li>  <a href="<c:url value="/act/doLogout"/>">
                                            <i class="icon-off"></i>
                                            <fmt:message key="button.logout"/></a>
                                    </li>
                                    <!-- dropdown menu links -->
                                </ul>
                            </div>

                        </sec:authorize>

                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>

        <div class="container-fluid">

            <div class="row-fluid">
                <div class="span3">
                    <div class="well sidebar-nav">
                        <tiles:insertAttribute name="menu" />

                    </div><!--/.well -->
                </div><!--/span-->
                <div class="span9">
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

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="<c:url value='/js/bootstrap-transition.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-alert.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-modal.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-dropdown.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-scrollspy.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-tab.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-tooltip.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-popover.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-button.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-collapse.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-carousel.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-typeahead.js'/>"></script>



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
                gallery     : false,             // Allow gallery thumb view
                autoplay : false                // Autoplay for video
            });
              
        });
        
    </script>


    <!-- Boostrap modal dialog -->
    <div id="delete_confirmation" class="modal hide fade" style="display: none; ">
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
        
      <div id="paste_preview" class="modal hide fade" style="display: none;width:660px;height: 400px; ">
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
    </div>
    
        
    <script type="text/javascript">

        $(document).ready(function() {

            $("[rel='tooltip']").tooltip();
   
              
         $(".pastePreviewBtn").live('click', function() {
                var link=$(this);
           
           
                $('#targetImg').attr('src',link.attr('targetIcon'));
                $('#targetTitle').html(link.attr('targetTitle'));
           
           
                //${pasteUrl} ${externalUrl}
            
                var clink = '<iframe src="${pasteUrl}/main/paste/loadFrom?url=${externalUrl}/act/download?id=';
                clink+= link.attr('targetId');
                clink+= '" scrolling="auto" frameborder="0" style="width:640px;height:320px; "  allowTransparency="true" > </iframe>';
                
                $('#pasteContent')
                .html(clink);
                
                
                $('#paste_preview').modal({backdrop: false}, "show");
                
              
                return false;

            });


   
   
            $(".fileDeleteBtn").live('click', function() {
                var link=$(this);
            
                $('#deleteTargetImg').attr('src',link.attr('targetIcon'));
                $('#deleteTargetTitle').html(link.attr('targetTitle'));
            
                $('#delete_confirmation').modal({backdrop: false}, "show");
                $('.confirm_delete_the_item').live('click', function(e) {
                    $('#delete_confirmation').modal("hide");
                    e.preventDefault();
                    // alert(link.attr('href'));
                    location.href=link.attr('deleteLink');
                    return true;
   
                });

                return false;

            });

        });
    </script>



</body>
</html>