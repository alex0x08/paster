<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

 <c:if test="${not empty usersOnline}">

                            <div class="alert alert-block pull-left ">
                                <a class="close" data-dismiss="alert" href="#">�</a>
                                <h4 class="alert-heading">Online:</h4>
                                <c:forEach var="user" items="${usersOnline}">

                                    <img  src="<c:out value='http://www.gravatar.com/avatar/${user.avatarHash}?s=16'/>"/>
                                    <span>${user.name}</span>

                                </c:forEach>

                            </div>

                        </c:if>