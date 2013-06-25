<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<div id="notice"></div>


<c:url var="url" value='/main/file/integrated/list' />

<c:url var="uploadUrl" value='/main/file/save/integrated' />

<div class="row-fluid">
    <div class="span6">

        <div class="btn-group pull-left">
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

            <form:form action="${uploadUrl}" modelAttribute="model" 
                       method="POST" id="file_upload" cssClass="form-vertical"
                       enctype="multipart/form-data">

                <fieldset>
                    <form:errors path="*" cssClass="errorblock" element="div"/>

                    <c:out value="Using integration code: ${model.integrationCode}" escapeXml="true" />

                    <form:hidden path="url" />
                    <form:hidden path="name" />
                    <form:hidden path="integrationCode" />
                    <form:hidden path="accessLevel" />

                    <div class="control-group">

                        <form:label cssClass="control-label" path="file"><fmt:message key="file.file.new"/>:</form:label>

                            <div class="controls">
                            <form:input path="file" name="file" cssClass="input-file"
                                        type="file" /> 
                            <form:errors path="file" cssClass="error" />
                        </div>      
                    </div>
                    <div class="form-actions">
                        <fmt:message var="submit_button_text" key="button.upload" />
                        <input name="submit" class="btn btn-primary" type="submit" value="${submit_button_text}" />
                    </div>
                </fieldset>
            </form:form>

        </sec:authorize>

        <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">

            <div class="btn-group">

                <a class="btn dropdown-toggle btn-danger" data-toggle="dropdown" href="#">
                    <fmt:message key="login.title"/>
                    <span class="caret"></span>
                </a>

                <ul class="dropdown-menu" id="auth-dropdown">
                    <li style="padding-right:5px;padding-left: 5px;" >

                        <form  action="<c:url value='/j_spring_security_check' />"  method="POST">
                            <input type="hidden" name="integrationCode" value="${model.integrationCode}"/>
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

            <script type="text/javascript">
                $('#auth-dropdown').click(function(event){
                    event.stopPropagation();
                }
            );
            </script>                 

        </sec:authorize>


    </div>
</div>

<div class="row-fluid">
    <div class="span4">

        <c:if test="${pageItems.nrOfElements > 0}">

            <div class="btn-group pull-left" >

                <a class="btn dropdown-toggle btn-danger" data-toggle="dropdown" href="#">
                    <c:out value="${pageItems.pageSize}"/> 
                    <span class="caret"></span>
                </a>

                <ul class="dropdown-menu">

                    <c:forTokens items="5,10,50,100" delims="," var="pg" >
                        <li>
                            <a href="<c:url value="/main/file/integrated/list/${model.integrationCode}/limit/${pg}">
                               </c:url>"><c:out value="${pg}"/></a>
                        </li>
                    </c:forTokens>   

                </ul>

            </div>
        </c:if>

        <c:if test="${pageItems.pageCount > 1}">

            <div class="pagination ">
                <ul>
                    <c:if test="${!pageItems.firstPage}">
                        <li>
                            <a href="<c:url value="/main/file/integrated/list/${model.integrationCode}/prev"/>"><b>&larr; Prev</b></a>
                        </li>
                    </c:if>

                    <c:forEach begin="1" end="${pageItems.pageCount}" step="1" var="pnumber">
                        <c:choose>
                            <c:when test="${pnumber==pageItems.page+1}">
                                <li class="disabled"><a href="#">${pnumber}</a></li>                     
                            </c:when>
                            <c:otherwise>
                                <li >
                                    <a href="<c:url value="/main/file/integrated/list/${model.integrationCode}/${pnumber}">
                                       </c:url>">${pnumber}</a>
                                </li>

                            </c:otherwise>
                        </c:choose>

                    </c:forEach>

                    <c:if test="${!pageItems.lastPage}">
                        <li >
                            <a href="<c:url value="/main/file/integrated/list/${model.integrationCode}/next">
                               </c:url>"><b>Next &rarr;</b></a>
                        </li>
                    </c:if>

                </ul>
            </div>

        </c:if>


        <div id="gallery">
            <c:forEach var="obj" items="${pageItems.pageList}" varStatus="status">

                <c:set var="model" value="${obj}" scope="request"></c:set>

                <jsp:include page="/WEB-INF/jsp/templates/common/preview.jsp" >
                    <jsp:param name="integrationMode" value="integrated"/>
                    <jsp:param name="menuTargetNew" value="1"/>

                </jsp:include>
                <br/>
            </c:forEach>

        </div>


        <c:if test="${pageItems.nrOfElements == 0}">

            <jsp:include page="/WEB-INF/jsp/templates/common/noFiles.jsp" >
                <jsp:param name="integrationMode" value="1"/>
            </jsp:include>

        </c:if>

    </div></div>
