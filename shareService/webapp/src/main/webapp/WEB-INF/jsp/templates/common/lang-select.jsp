<%-- 
    Document   : lang-select
    Created on : 02.10.2013, 19:45:50
    Author     : aachernyshev
--%>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

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