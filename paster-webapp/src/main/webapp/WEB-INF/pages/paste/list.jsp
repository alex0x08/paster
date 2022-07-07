<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>




<div class="row justify-content-between">
    <div class="col-md-8">
        <a  href="<c:url value='/main/paste/list/${sourceType}'/>">
            <span class="i" style="font-size: 1.5em;">P</span></a>

        <sec:authorize access="hasRole('ROLE_ADMIN')">

            <c:forEach var="source" items="${availableSourceTypes}" varStatus="loopStatus">

                <c:choose>
                    <c:when test="${source eq sourceType}">
                        <span style="font-size: 14px; font-weight: bold; ">
                            <c:out value="${source}"/>
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value='/main/paste/list/${source}'/>">
                            <c:out value="${source}"/>
                        </a>
                    </c:otherwise>
                </c:choose>

                <c:if test="${!loopStatus.last}"> | </c:if>
            </c:forEach>

        </sec:authorize>

    </div>
    <div class="col-md-2 hidden-sm hidden-xs">
        
        <a class="img-map img-xml" href="<c:url value='/main/paste/list/body.xml'/>" title="xml" alt="xml" target="_blank">
        </a> |
        <a class="img-map img-json" href="<c:url value='/main/paste/list/body.json'/>" title="json" alt="json" target="_blank">
        </a> |
        <a class="img-map img-rss" href="<c:url value='/main/paste/list.rss'/>" title="rss" alt="rss" target="_blank">
        </a>
        |
        <a class="img-map img-atom" href="<c:url value='/main/paste/list.atom'/>" title="atom" alt="atom" target="_blank">
        </a> 

    </div>


    <div class="col-auto">
        <tiles:insertDefinition name="/common/pageList" >
            <tiles:putAttribute name="listMode" value="${listMode}"/>
            <tiles:putAttribute name="pageItems" value="${pageItems}"/>
            <tiles:putAttribute name="sourceType" value="${sourceType == null ? 'main' : sourceType }"/>
           
            <c:if test="${listMode eq 'search'}">
                <tiles:putAttribute name="result" value="${result}"/>
            </c:if>
        </tiles:insertDefinition>
    </div>     
</div>


<c:if test="${listMode eq 'search' }">
    <div class="row">
        <div class="col-md-12"  >

            <div class="paging" style="margin: auto; text-align: center;float: left;" >
                Found
                <c:forEach var="resultType" items="${availableResults}" varStatus="loopStatus">
                    <c:choose>
                        <c:when test="${result eq resultType.codeLowerCase}">
                            <span style="font-size: larger; "><fmt:message key="${resultType.name}"/> </span>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value='/main/paste/list/search/${resultType.codeLowerCase}/1'/>"><fmt:message key="${resultType.name}"/></a>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${!loopStatus.last}"> | </c:if>
                </c:forEach>
            </div>
        </div>
    </div>

</c:if>       



<div class="row">
    <div id="pastas" class="col-md-12" >

        <c:forEach var="paste" items="${pageItems.pageList}" varStatus="status">

            <c:choose>
                <c:when test="${paste['class'].name eq 'com.Ox08.paster.webapp.model.Paste'}">
                    <c:if test="${not paste.stick}">
                        <c:set property="curDate" value="${paste.lastModified}" target="${splitHelper}"/>
                    </c:if>

                    <c:set var="priorTitle"><fmt:message key="${paste.priority}"/></c:set>

                  
                    <div class="row justify-content-start" >

                        <div class="col-md-2" style="padding-bottom: 0.5em;">

                            <c:url value='/main/resources/${appId}/t/${paste.lastModified.time}/paste_content/${paste.thumbImage}' var="thumbUrl">
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
            
                                        <a class="i ${paste.priority}" style="font-size:2em;"
                                           title="<c:out value="${paste.id}"/>: ${priorTitle}. Click to search with same priority."
                                           href="<c:url value='/main/paste/list/search?query=priority:${paste.priority}'/>">/</a>
            

                                        <a class="listLinkLine" href="<c:url value='/${paste.id}'></c:url>" 
                                           pasteId="${paste.id}" title="Click to view paste vol. ${paste.id}">
                                            <c:out value="${paste.title}"  escapeXml="true"/></a>
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
                                        ,<kc:prettyTime date="${paste.lastModified}" locale="${pageContext.response.locale}"/>
                                    </small>

                                

                                </div>
                            </div>
                           
                        </div>
                    </div>

                </c:when>
                <c:when test="${paste['class'].name eq 'com.Ox08.paster.webapp.Comment'}">

                    <c:set property="curDate" value="${paste.lastModified}" target="${splitHelper}"/>

                    <a href="<c:url value='/${paste.id}'></c:url>" title="Click to view paste vol. ${paste.id}">
                        <span  class="pasteTitle"><c:out value="${paste.text}" escapeXml="true"  /></span>
                    </a>

                    <span  class="pasteTitle"><c:out value="${paste.text}" escapeXml="true"  /></span>

                    <small>
                        <tiles:insertDefinition name="/common/owner" >
                            <tiles:putAttribute name="model" value="${paste}"/>
                            <tiles:putAttribute name="modelName" value="paste"/>
                        </tiles:insertDefinition>

                        ,<kc:prettyTime date="${paste.lastModified}" locale="${pageContext.response.locale}"/>
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

<c:if test="${pageItems.nrOfElements > 5 and pageItems.page < pageItems.pageCount-1}">

    <c:choose>
        <c:when test="${listMode eq 'search'}">
            <c:url var="rawPageUrl" value="/main/paste/raw/list/search/${result}"/>
            <c:url var="userPageUrl" value="/main/paste/list/search/${result}"/>
        </c:when>
        <c:otherwise>
            <c:url var="rawPageUrl" value="/main/paste/raw/list/form"/>
            <c:url var="userPageUrl" value="/main/paste/list/form"/>
        </c:otherwise>
    </c:choose>

    
    <div id="pageLoadSpinner" style="display:none;">
        <i  class="fa fa-spinner fa-spin"></i>       
        <fmt:message key="action.loading"/>
    </div>
    <div id="morePages"></div>
</c:if>



