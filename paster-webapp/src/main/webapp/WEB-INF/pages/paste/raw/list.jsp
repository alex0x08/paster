<%--

    Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<%--
    Render paged list of pastas without template (raw)
--%>

<div id="paste_list_${param.page}" class="page">
    <h4 class="f-h4">
        <fmt:formatDate pattern="${dateTimePattern}"
                var="fromDate" value="${pageItems.firstElement.lastModifiedDt}" />
        <fmt:formatDate pattern="${dateTimePattern}"
                var="toDate" value="${pageItems.lastElement.lastModifiedDt}" />
        <c:url var='listBySourceTypeUrl' value='/main/paste/list/${sourceType}/${param.page}'/>
        <a href="${listBySourceTypeUrl}">
            <fmt:message key='common.list.page'>
                <fmt:param value="${param.page}"/>
                <fmt:param value="${fromDate}"/>
                <fmt:param value="${toDate}"/>
                <fmt:param value="${pageItems.elementsOnPage}"/>
            </fmt:message>
        </a>
    </h4>

    <div class="row">
        <div id="pastas" class="col-md-12" >
            <c:forEach var="paste" items="${pageItems.pageList}" varStatus="status">
                <c:choose>
                    <c:when test="${paste['class'].name eq 'com.Ox08.paster.webapp.model.Paste'}">
                        <c:if test="${not paste.stick}">
                            <c:set property="curDate" value="${paste.lastModifiedDt}" target="${splitHelper}"/>
                        </c:if>
                        <c:set var="priorTitle"><fmt:message key="${paste.priority}"/></c:set>
                        <div class="row justify-content-start" >
                            <div class="col-md-2" style="padding-bottom: 0.5em;">
                                <c:url value='/main/paste-resources/${appId}/t/${paste.lastModifiedDt.time}/paste_content/${paste.thumbImage}' var="thumbUrl">
                                </c:url>
                                <a class="pastePreviewLink" href="<c:url value='/${paste.id}'></c:url>" pasteId="${paste.id}"
                                   title="Click to view paste vol. ${paste.id}">
                                    <img src="${thumbUrl}"
                                         class="img-thumbnail img-responsive p-comment"  />
                                </a>
                            </div>
                            <div class="col-md-10" >
                                <div class="row">
                                    <div class="pasteTitle col-lg-10 col-md-12 col-xs-12">
                                        <div class="pasteTitle" style="padding: 1em;">
                                            <c:if test="${paste.stick}">
                                                <span class="i" title="Paste sticked">]</span>
                                            </c:if>
                                            <a class="i priority_${paste.priority}" style="font-size:2em;"
                                               title="<c:out value="${paste.id}"/>: ${priorTitle}. Click to search with same priority."
                                               href="<c:url value='/main/paste/list/search?query=priority:${paste.priority}'/>">/</a>

                                            <a class="listLinkLine" href="<c:url value='/${paste.id}'></c:url>"
                                               pasteId="${paste.id}" title="Click to view paste vol. ${paste.id}">
                                                <c:out value="${paste.title}"  escapeXml="true"/>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-12">
                                        <tiles:insertDefinition name="/common/tags" >
                                            <tiles:putAttribute name="model" value="${paste}"/>
                                            <tiles:putAttribute name="modelName" value="paste"/>
                                        </tiles:insertDefinition>
                                        <tiles:insertDefinition name="/common/commentCount" >
                                            <tiles:putAttribute name="model" value="${paste}"/>
                                            <tiles:putAttribute name="modelName" value="paste"/>
                                        </tiles:insertDefinition>
                                        <small>
                                            <tiles:insertDefinition name="/common/owner" >
                                                <tiles:putAttribute name="model" value="${paste}"/>
                                                <tiles:putAttribute name="modelName" value="paste"/>
                                            </tiles:insertDefinition>,
                                            <a href="<c:url value='/main/paste/list/search?query=codeType:${paste.codeType}'/>">
                                                <fmt:message key='${"code.type.".concat(paste.codeType)}'/></a>
                                            (
                                            <c:if test="${not empty paste.wordsCount}">
                                                <c:out value="${paste.wordsCount} "/>
                                                <fmt:message key="paste.edit.word.counter.wordText"/>
                                            </c:if>
                                            ,
                                            <c:if test="${not empty paste.symbolsCount}">
                                                <c:out value="${paste.symbolsCount} "/>
                                                <fmt:message key="paste.edit.word.counter.charText"/>
                                            </c:if>
                                            )
                                            ,<kc:prettyTime date="${paste.lastModifiedDt}"
                                            locale="${pageContext.response.locale}"/>
                                        </small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:when test="${paste['class'].name eq 'com.Ox08.paster.webapp.model.Comment'}">
                        <c:set property="curDate" value="${paste.lastModifiedDt}" target="${splitHelper}"/>
                        <a href="<c:url value='/${paste.id}'></c:url>" title="Click to view paste vol. ${paste.id}">
                            <span  class="pasteTitle"><c:out value="${paste.text}" escapeXml="true"  /></span>
                        </a>
                        <span  class="pasteTitle"><c:out value="${paste.text}" escapeXml="true"  /></span>
                        <small>
                            <tiles:insertDefinition name="/common/owner" >
                                <tiles:putAttribute name="model" value="${paste}"/>
                                <tiles:putAttribute name="modelName" value="paste"/>
                            </tiles:insertDefinition>
                            ,<kc:prettyTime date="${paste.lastModifiedDt}" locale="${pageContext.response.locale}"/>
                        </small>
                    </c:when>
                </c:choose>
                <c:if test="${splitHelper.split}">
                    <c:out value="${splitHelper.splitTitle}"/>
                    <hr/>
                </c:if>
            </c:forEach>
            <c:if test="${pageItems.nrOfElements == 0}">
                <center>
                    <fmt:message key='common.list.empty'/>
                </center>
            </c:if>
        </div>
    </div>


</div>


