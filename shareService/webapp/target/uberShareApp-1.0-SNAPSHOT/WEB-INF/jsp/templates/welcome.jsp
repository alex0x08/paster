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

 <style>

    /* GLOBAL STYLES
    -------------------------------------------------- */
    /* Padding below the footer and lighter body text */

    body {
      padding-bottom: 40px;
      color: #5a5a5a;
    }



    /* CUSTOMIZE THE NAVBAR
    -------------------------------------------------- */

    /* Special class on .container surrounding .navbar, used for positioning it into place. */
    .navbar-wrapper {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      z-index: 10;
      margin-top: 20px;
      margin-bottom: -90px; /* Negative margin to pull up carousel. 90px is roughly margins and height of navbar. */
    }
    .navbar-wrapper .navbar {

    }

    /* Remove border and change up box shadow for more contrast */
    .navbar .navbar-inner {
      border: 0;
      -webkit-box-shadow: 0 2px 10px rgba(0,0,0,.25);
         -moz-box-shadow: 0 2px 10px rgba(0,0,0,.25);
              box-shadow: 0 2px 10px rgba(0,0,0,.25);
    }

    /* Downsize the brand/project name a bit */
    .navbar .brand {
      padding: 14px 20px 16px; /* Increase vertical padding to match navbar links */
      font-size: 16px;
      font-weight: bold;
      text-shadow: 0 -1px 0 rgba(0,0,0,.5);
    }

    /* Navbar links: increase padding for taller navbar */
    .navbar .nav > li > a {
      padding: 15px 20px;
    }

    /* Offset the responsive button for proper vertical alignment */
    .navbar .btn-navbar {
      margin-top: 10px;
    }



    /* CUSTOMIZE THE CAROUSEL
    -------------------------------------------------- */

    /* Carousel base class */
    .carousel {
      margin-bottom: 60px;
    }

    .carousel .container {
      position: relative;
      z-index: 9;
    }

    .carousel-control {
      height: 80px;
      margin-top: 0;
      font-size: 120px;
      text-shadow: 0 1px 1px rgba(0,0,0,.4);
      background-color: transparent;
      border: 0;
      z-index: 10;
    }

    .carousel .item {
      height: 500px;
    }
    .carousel img {
      position: absolute;
      top: 0;
      left: 0;
      min-width: 100%;
      height: 500px;
    }

    .carousel-caption {
      background-color: transparent;
      position: static;
      max-width: 550px;
      padding: 0 20px;
      margin-top: 200px;
    }
    .carousel-caption h1,
    .carousel-caption .lead {
      margin: 0;
      line-height: 1.25;
      color: #fff;
      text-shadow: 0 1px 1px rgba(0,0,0,.4);
    }
    .carousel-caption .btn {
      margin-top: 10px;
    }



    /* MARKETING CONTENT
    -------------------------------------------------- */

    /* Center align the text within the three columns below the carousel */
    .marketing .span4 {
      text-align: center;
    }
    .marketing h2 {
      font-weight: normal;
    }
    .marketing .span4 p {
      margin-left: 10px;
      margin-right: 10px;
    }


    /* Featurettes
    ------------------------- */

    .featurette-divider {
      margin: 80px 0; /* Space out the Bootstrap <hr> more */
    }
    .featurette {
      padding-top: 120px; /* Vertically center images part 1: add padding above and below text. */
      overflow: hidden; /* Vertically center images part 2: clear their floats. */
    }
    .featurette-image {
      margin-top: -120px; /* Vertically center images part 3: negative margin up the image the same amount of the padding to center it. */
    }

    /* Give some space on the sides of the floated elements so text doesn't run right into it. */
    .featurette-image.pull-left {
      margin-right: 40px;
    }
    .featurette-image.pull-right {
      margin-left: 40px;
    }

    /* Thin out the marketing headings */
    .featurette-heading {
      font-size: 50px;
      font-weight: 300;
      line-height: 1;
      letter-spacing: -1px;
    }



    /* RESPONSIVE CSS
    -------------------------------------------------- */

    @media (max-width: 979px) {

      .container.navbar-wrapper {
        margin-bottom: 0;
        width: auto;
      }
      .navbar-inner {
        border-radius: 0;
        margin: -20px 0;
      }

      .carousel .item {
        height: 500px;
      }
      .carousel img {
        width: auto;
        height: 500px;
      }

      .featurette {
        height: auto;
        padding: 0;
      }
      .featurette-image.pull-left,
      .featurette-image.pull-right {
        display: block;
        float: none;
        max-width: 40%;
        margin: 0 auto 20px;
      }
    }


    @media (max-width: 767px) {

      .navbar-inner {
        margin: -20px;
      }

      .carousel {
        margin-left: -20px;
        margin-right: -20px;
      }
      .carousel .container {

      }
      .carousel .item {
        height: 300px;
      }
      .carousel img {
        height: 300px;
      }
      .carousel-caption {
        width: 65%;
        padding: 0 70px;
        margin-top: 100px;
      }
      .carousel-caption h1 {
        font-size: 30px;
      }
      .carousel-caption .lead,
      .carousel-caption .btn {
        font-size: 18px;
      }

      .marketing .span4 + .span4 {
        margin-top: 40px;
      }

      .featurette-heading {
        font-size: 30px;
      }
      .featurette .lead {
        font-size: 18px;
        line-height: 1.5;
      }

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

    
     <!-- NAVBAR
    ================================================== -->
    <div class="navbar-wrapper">
      <!-- Wrap the .navbar in .container to center it within the absolutely positioned parent. -->
      <div class="container">

        <div class="navbar navbar-inverse">
          <div class="navbar-inner">
              
              
              
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
          </div><!-- /.navbar-inner -->
        </div><!-- /.navbar -->

      </div> <!-- /.container -->
    </div><!-- /.navbar-wrapper -->



    <!-- Carousel
    ================================================== -->
    <div id="myCarousel" class="carousel slide">
      <div class="carousel-inner">
        <div class="item active">
            <img src="<c:url value='/assets/img/examples/slide-01.jpg'/>" alt="">
          <div class="container">
            <div class="carousel-caption">
              <h1>Example headline.</h1>
              <p class="lead">Cras justo odio, dapibus ac facilisis in, egestas eget quam. Donec id elit non mi porta gravida at eget metus. Nullam id dolor id nibh ultricies vehicula ut id elit.</p>
              <a class="btn btn-large btn-primary" href="#">Sign up today</a>
            </div>
          </div>
        </div>
        <div class="item">
            <img src="<c:url value='/assets/img/examples/slide-02.jpg'/>" alt="">
          <div class="container">
            <div class="carousel-caption">
              <h1>Another example headline.</h1>
              <p class="lead">Cras justo odio, dapibus ac facilisis in, egestas eget quam. Donec id elit non mi porta gravida at eget metus. Nullam id dolor id nibh ultricies vehicula ut id elit.</p>
              <a class="btn btn-large btn-primary" href="#">Learn more</a>
            </div>
          </div>
        </div>
        <div class="item">
            <img src="<c:url value='/assets/img/examples/slide-03.jpg'/>" alt="">
          <div class="container">
            <div class="carousel-caption">
              <h1>One more for good measure.</h1>
              <p class="lead">Cras justo odio, dapibus ac facilisis in, egestas eget quam. Donec id elit non mi porta gravida at eget metus. Nullam id dolor id nibh ultricies vehicula ut id elit.</p>
              <a class="btn btn-large btn-primary" href="#">Browse gallery</a>
            </div>
          </div>
        </div>
      </div>
      <a class="left carousel-control" href="#myCarousel" data-slide="prev">&lsaquo;</a>
      <a class="right carousel-control" href="#myCarousel" data-slide="next">&rsaquo;</a>
    </div><!-- /.carousel -->



    <!-- Marketing messaging and featurettes
    ================================================== -->
    <!-- Wrap the rest of the page in another container to center all the content. -->

    <div class="container marketing">

      <!-- Three columns of text below the carousel -->
      <div class="row">
        <div class="span4">
          <img class="img-circle" data-src="holder.js/140x140">
          <h2>Heading</h2>
          <p>Donec sed odio dui. Etiam porta sem malesuada magna mollis euismod. Nullam id dolor id nibh ultricies vehicula ut id elit. Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Praesent commodo cursus magna, vel scelerisque nisl consectetur et.</p>
          <p><a class="btn" href="#">View details &raquo;</a></p>
        </div><!-- /.span4 -->
        <div class="span4">
          <img class="img-circle" data-src="holder.js/140x140">
          <h2>Heading</h2>
          <p>Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem nec elit. Cras mattis consectetur purus sit amet fermentum. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</p>
          <p><a class="btn" href="#">View details &raquo;</a></p>
        </div><!-- /.span4 -->
        <div class="span4">
          <img class="img-circle" data-src="holder.js/140x140">
          <h2>Heading</h2>
          <p>Donec sed odio dui. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Vestibulum id ligula porta felis euismod semper. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</p>
          <p><a class="btn" href="#">View details &raquo;</a></p>
        </div><!-- /.span4 -->
      </div><!-- /.row -->


      <!-- START THE FEATURETTES -->

      <hr class="featurette-divider">

      <div class="featurette">
          <img class="featurette-image pull-right" src="<c:url value='/assets/img/examples/browser-icon-chrome.png'/>">
        <h2 class="featurette-heading">First featurette headling. <span class="muted">It'll blow your mind.</span></h2>
        <p class="lead">Donec ullamcorper nulla non metus auctor fringilla. Vestibulum id ligula porta felis euismod semper. Praesent commodo cursus magna, vel scelerisque nisl consectetur. Fusce dapibus, tellus ac cursus commodo.</p>
      </div>

      <hr class="featurette-divider">

      <div class="featurette">
          <img class="featurette-image pull-left" src="<c:url value='/assets/img/examples/browser-icon-firefox.png'/>">
        <h2 class="featurette-heading">Oh yeah, it's that good. <span class="muted">See for yourself.</span></h2>
        <p class="lead">Donec ullamcorper nulla non metus auctor fringilla. Vestibulum id ligula porta felis euismod semper. Praesent commodo cursus magna, vel scelerisque nisl consectetur. Fusce dapibus, tellus ac cursus commodo.</p>
      </div>

      <hr class="featurette-divider">

      <div class="featurette">
          <img class="featurette-image pull-right" src="<c:url value='/assets/img/examples/browser-icon-safari.png'/>">
        <h2 class="featurette-heading">And lastly, this one. <span class="muted">Checkmate.</span></h2>
        <p class="lead">Donec ullamcorper nulla non metus auctor fringilla. Vestibulum id ligula porta felis euismod semper. Praesent commodo cursus magna, vel scelerisque nisl consectetur. Fusce dapibus, tellus ac cursus commodo.</p>
      </div>

      <hr class="featurette-divider">

      <!-- /END THE FEATURETTES -->


      <!-- FOOTER -->
      <footer>
        <p class="pull-right"><a href="#">Back to top</a></p>
        <p>&copy; 2013 Company, Inc. &middot; <a href="#">Privacy</a> &middot; <a href="#">Terms</a></p>
      </footer>

    </div><!-- /.container -->



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
    <script>
      !function ($) {
        $(function(){
          // carousel demo
          $('#myCarousel').carousel()
        })
      }(window.jQuery)
    </script>
    <script src="<c:url value='/js/holder/holder.js'/>"></script>
 
    
    
    <body data-spy="scroll" data-target=".subnav" data-offset="50" screen_capture_injected="true">


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
        <a href="#" class="btn confirm_delete_the_item no_return"><fmt:message key="button.delete"/></a>
        <a href="#" class="btn btn-secondary " data-dismiss="modal"><fmt:message key="button.cancel"/></a>
     </div>
</div>

<script type="text/javascript">

    $(document).ready(function() {

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
