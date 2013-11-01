<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>
<html >
    <head>
       <%@ include file="/WEB-INF/jsp/templates/common/template-common-head.jsp"%>
    </head>

    <body>
       <div class="navbar navbar-default" style="margin-bottom: 0.5em;" >
        <div class="navbar-header" >
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
                 <tiles:insertAttribute name="menu" />
        </div>
        <div class="navbar-collapse collapse" >
            
            <ul class="nav navbar-nav">
            <li>
                  <tiles:insertAttribute name="header" />   
            </li>
                    
            </ul>   
            <ul class='nav navbar-nav navbar-right' >    
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
                            <div class="btn-group box">

                                <a class="btn dropdown-toggle btn-default" data-toggle="dropdown" href="#">
                                    <img  src="<c:out value='http://www.gravatar.com/avatar/${currentUser.avatarHash}?s=16'/>"/>
                                    <sec:authentication property="principal.username" />
                                    <span class="caret"></span>
                                </a>

                                <ul class="dropdown-menu ">
                                    <li>
                                        <a class="profile" href="<c:url value="/main/profile"/>">
                                            <span class="glyphicon glyphicon-user"></span>
                                            <c:out value="${currentUser.name}"/>

                                        </a> 
                                    </li>

                                    <li>
                                        <a href="<c:url value="/act/doLogout"/>">
                                            <span class="glyphicon glyphicon-log-out"></span>
                                            <fmt:message key="button.logout"/></a>
                                    </li>

                                </ul>
                            </div>                              
                        </li>


                    </sec:authorize>

                             <li>
                 <jsp:include page="/WEB-INF/jsp/templates/common/lang-select.jsp"/>
                    </li>
                </ul>        
               </div>   

        </div>
                       
                 <div class="container">
                     <div class="row">
                         <div class="col-xs-12 col-md-12" >
                             <div id="jsErrors"></div>
                             <div id="notice"></div>

                             <c:if test="${not empty statusMessageKey}">
                                 <div class="alert alert-block alert-warning" style="width:20em;">
                                     <a class="close" data-dismiss="alert" href="#">×</a>
                                     <p><fmt:message key="${statusMessageKey}"/></p>
                                 </div>
                             </c:if>
                         </div>
                     </div>
                     <tiles:insertAttribute name="content" />

                 </div>
                    
        <hr>

        <footer>
            <tiles:insertAttribute name="footer" />
        </footer>
 


    <!-- Boostrap modal dialog -->
    <div id="delete_confirmation" class="modal fade" >
        
        <div class="modal-dialog">
            <div class="modal-content">
                
         
        <div class="modal-header">
            <a href="#" class="close" data-dismiss="modal">x</a>
            <h3><fmt:message key="dialog.confirm"/></h3>
        </div>
        <div class="modal-body">
            <div  id="modal_text">    
                <div>
                    <img id="deleteTargetImg" src="<c:url value='/main/static/${appVersion}/images/file.png'/>" />
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
                                <img id="targetImg" src="<c:url value='/main/static/${appVersion}/images/file.png'/>" />
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


        <%@ include file="/WEB-INF/jsp/templates/common/template-common-body.jsp"%>

</body>
</html>
