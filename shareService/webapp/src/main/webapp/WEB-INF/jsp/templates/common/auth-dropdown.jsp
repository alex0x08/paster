<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


 <div class="btn-group box">
                            <a class="btn dropdown-toggle btn-danger" data-toggle="dropdown" href="#">
                                <fmt:message key="login.title"/>
                                <span class="caret"></span>
                            </a>

                            <ul class="dropdown-menu" id="auth-dropdown" role='menu'>
                                <li >
                                    <div style="width:12em;" class='box'>
                                        <form  action="<c:url value='/j_spring_security_check' />" role='form'  method="POST" class="form-inline">
                                            <c:if test="${param.integrationCode ne null}">
                                                <input type="hidden" name="integrationCode" value="${param.integrationCode}"/>
                                             </c:if>
                                            
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