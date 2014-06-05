<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<div class="row">
    <div class="col-xs-2 col-md-2">

        <ul class="nav nav-pills nav-stacked">

            <c:url var="listModeUrl" value="/main/file/list" />
            <sec:authorize access="isAnonymous()">

                <li class="active"><a href="${listModeUrl}/all">
                        <span class="badge pull-right"><c:out value="${pageItems.nrOfElements}"/></span>
                        <fmt:message key="level.all"/>
                    </a></li>


            </sec:authorize>      

            <sec:authorize access="isAuthenticated()">

                <c:forEach var="mode" items="${availableModes}" >
                    <c:choose>
                        <c:when test="${mode.sourceCode eq pageItems.sourceCode}">

                            <li class="active"><a href="${listModeUrl}/${mode.codeLowerCase}">
                                    <span class="badge pull-right"><c:out value="${pageItems.nrOfElements}"/></span>
                                    <fmt:message key="${mode.desc}"/>
                                </a></li>

                        </c:when>
                        <c:otherwise>
                            <li ><a href="${listModeUrl}/${mode.codeLowerCase}">
                                    <fmt:message key="${mode.desc}"/>
                                </a></li>

                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                                <c:if test="${pageItems.sourceCode eq 'search'}">

                                 <li class="active"><a href="${listModeUrl}/all">
                                    <span class="badge pull-right"><c:out value="${pageItems.nrOfElements}"/></span>
                                    <fmt:message key="button.search"/>
                                </a></li>
                                    
                                </c:if>                

            </sec:authorize>
        </ul>




    </div>
    <div class="col-xs-12 col-sm-6 col-md-10">


        <c:if test="${pageItems.nrOfElements > 0}">
            <div class="row" >                
                <div class="col-md-6"  > 
                    <c:if test="${pageItems.pageCount > 1}">  
                        <jsp:include
                            page="/WEB-INF/jsp/templates/common/pagination.jsp">
                            <jsp:param name="modelName" value="file" />
                        </jsp:include>
                    </c:if>
                </div>
                <div class="col-md-6"   >
                    <jsp:include
                        page="/WEB-INF/jsp/templates/common/sort.jsp">
                        <jsp:param name="modelName" value="file" />
                    </jsp:include>
                    <jsp:include
                        page="/WEB-INF/jsp/templates/common/page-size.jsp">
                        <jsp:param name="modelName" value="file" />
                    </jsp:include>
                </div>
            </div>

        </c:if>

        <div class="row">
            <div id="gallery" class="col-md-12">      
                <c:forEach var="obj" items="${pageItems.pageList}" varStatus="status">
                    <c:set var="model" value="${obj}" scope="request"></c:set>
                    <jsp:include page="/WEB-INF/jsp/templates/common/preview.jsp" />
                </c:forEach>

                <c:if test="${pageItems.nrOfElements == 0}">
                    <jsp:include page="/WEB-INF/jsp/templates/common/noFiles.jsp" />
                </c:if>
            </div>
        </div>
    </div>
</div>


