<%-- 
    Document   : lang-select
    Created on : 02.10.2013, 19:45:50
    Author     : aachernyshev
--%>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<div class="btn-group" >

    <a class="btn dropdown-toggle " data-toggle="dropdown" href="#">
        <img style="display: inline; vertical-align:middle;" 
             title="<c:out value="${pageContext.response.locale.displayName}"/>" 
             src="<c:url value='/main/static/${appVersion}/images/flags/flag_${pageContext.response.locale.language}_${pageContext.response.locale.country}.png'/>"/>
        <span class="caret"></span>
    </a>
    <ul class="dropdown-menu">

      
        
        <c:forEach items="${availableLocales}" var="locale" >
            <c:if test="${pageContext.response.locale ne locale}">

                <li>                                           
                    <a href="<c:url value="${request.requestURL}">
                           <c:param name="locale" value="${locale.language}_${locale.country}" />
                           <c:forEach items="${param}" var="currentParam">
                               <c:if test="${currentParam.key ne 'locale'}">
                                   <c:param name="${currentParam.key}" value="${currentParam.value}"/>
                               </c:if>
                           </c:forEach>

                       </c:url>">
                        <img style="display: inline; vertical-align:middle;" 
                             title="<c:out value='${locale.displayName}'/>" 
                             src="<c:url value='/main/static/${appVersion}/images/flags/flag_${locale.language}_${locale.country}.png'/>"/>
                       <c:out value='${locale.displayLanguage}'/>
                    </a>
                </li>

            </c:if>

        </c:forEach>   

    </ul>
</div>