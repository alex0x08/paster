<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


  <jsp:include page="/WEB-INF/pages/common/paste-update-poll.jsp"/>
<div class="row">
    <div class="column grid-1" style="text-align:right;padding-right: 0;margin-top: -1em;" >
        <a class="mainLinkLine" href="<c:url value="/main/paste/new"></c:url>" title="<fmt:message key='paste.create.new'/>"><span class="i" style="font-size: 4em;">/</span></a>
    </div>
    <div class="column grid-12" style="padding-left: 0;margin-left: -1em;" >
         <jsp:include page="/WEB-INF/pages/template/search.jsp"/>
     </div>
</div>

<div class="row">
    <div class="column grid-10">
   
             <a  href="<c:url value='/main/paste/list/${sourceType}'/>"><span class="i" style="font-size: 1.5em;">P</span></a>
    <c:forEach var="source" items="${availableSourceTypes}" varStatus="loopStatus">

        <c:choose>
            <c:when test="${source.codeLowerCase eq sourceType}">
                <span style="font-size: 14px; font-weight: bold; "><fmt:message key="${source.name}"/> </span>
            </c:when>
            <c:otherwise>
                <a href="<c:url value="/main/paste/list/${source.codeLowerCase}"/>"><fmt:message key="${source.name}"/></a>
            </c:otherwise>
        </c:choose>

        <c:if test="${!loopStatus.last}"> | </c:if>
    </c:forEach>
   
    </div>
    <div class="column grid-2">
        <a href="<c:url value="/main/paste/list/body.xml"/>">
            <img src="<c:url value='/main/static/${appVersion}/images/xml.png'/>" title="xml" alt="xml"/></a> |
        <a href="<c:url value="/main/paste/list/body.json"/>">
            <img src="<c:url value='/main/static/${appVersion}/images/json.png'/>" title="json" alt="json"/></a> |
        <a href="<c:url value="/main/paste/list.rss"/>"><img src="<c:url value='/main/static/${appVersion}/images/rss.png'/>" title="rss" alt="rss"/></a> |
        <a href="<c:url value="/main/paste/list.atom"/>"><img src="<c:url value='/main/static/${appVersion}/images/atom.png'/>" title="atom" alt="atom"/></a> |
        <a href="<c:url value="/main/paste/list.xls"/>"><img src="<c:url value='/main/static/${appVersion}/images/xls.gif'/>" title="xls" alt="xls"/></a> |
        <a href="<c:url value="/ws/paste?wsdl"/>"><img src="<c:url value='/main/static/${appVersion}/images/wsdl_icon.png'/>" title="wsdl" alt="wsdl"/></a>

    </div>
 </div>


      <c:if test="${listMode eq 'search' }">
<div class="row">
    <div class="column grid-12"  >

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
    <div class="column grid-12" >
   
        
    <div id="pastas">
        
    <c:forEach var="paste" items="${pageItems.pageList}" varStatus="status">

        <c:choose>
            <c:when test="${paste['class'].name eq 'uber.paste.model.Paste'}">                
                 <c:if test="${not paste.sticked}"> 
                    <c:set property="curDate" value="${paste.lastModified}" target="${splitHelper}"/>
                </c:if>
                
                <c:set var="priorTitle"><fmt:message key="${paste.priority.name}"/></c:set>

        <div class="row">

            <div class="column grid-16">

                <c:if test="${paste.sticked}">
                    <span class="i" title="Paste sticked">]</span>
                </c:if>

                <a class="i ${paste.priority.cssClass}" style="font-size:2em;"
                   title="<c:out value="${paste.id}"/>: ${priorTitle}. Click to search with same priority."
                   href="<c:url value='/main/paste/list/search?query=priority:${paste.priority.code}'/>">/</a>

                <a href="<c:url value="/${paste.id}"></c:url>" title="Click to view paste vol. ${paste.id}">
                    <span  class="pasteTitle"><c:out value="${paste.name}" escapeXml="true"  /></span>
                </a>
            </div>
        </div>

        <div class="row" >

                    <a class="pastePreviewLink" href="<c:url value="/${paste.id}"></c:url>" pasteId="${paste.id}" 
                       title="Click to view paste vol. ${paste.id}">

                       <c:choose>
                           <c:when test="${not empty paste.thumbImage}">
                <div class="column grid-4" >
                    
                   
                               <img src="${paste.thumbImageRead}" />
                    </div>
                               <c:set var="currentRowSize" value="12"/>
                           </c:when>
                           <c:otherwise>
                             <c:set var="currentRowSize" value="16"/>
                           </c:otherwise>
                       </c:choose>
                    </a>
                    
                <div class="column grid-${currentRowSize}" >
                    <div class="row">
                        <div class="pasteTitle column grid-14">
                            <div class="pasteTitle" style="padding: 1em;">
                                <a class="listLinkLine" href="<c:url value="/${paste.id}"></c:url>" 
                                   pasteId="${paste.id}" title="Click to view paste vol. ${paste.id}">
                                    <c:out value="${paste.title}"  escapeXml="true"/></a>
                            </div>
                        </div>
                        </div>

                    <div class="row">
                    <div class="column grid-10">
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
                                
                                <a href="<c:url value='/main/paste/list/search?query=codeType:${paste.codeType.code}'/>">
                                    <fmt:message key='${paste.codeType.name}'/></a>                               
                                 
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

                        <tiles:insertDefinition name="/common/deleteLink" >
                            <tiles:putAttribute name="model" value="${paste}"/>
                            <tiles:putAttribute name="modelName" value="paste"/>
                            <tiles:putAttribute name="currentUser" value="${currentUser}"/>
                        </tiles:insertDefinition>

                        </div>
                    </div>
                </div>
                </div>

            </c:when>
            <c:when test="${paste['class'].name eq 'uber.paste.model.Comment'}">

                <c:set property="curDate" value="${paste.lastModified}" target="${splitHelper}"/>

                <a href="<c:url value="/${paste.id}"></c:url>" title="Click to view paste vol. ${paste.id}">
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

          </div>
         

<c:if test="${pageItems.nrOfElements == 0}">
    <center>
        <fmt:message key='common.list.empty'/>
    </center>
</c:if>

    </div>    
    <div class="column grid-3">
        <tiles:insertDefinition name="/common/pageList" >
            <tiles:putAttribute name="listMode" value="${listMode}"/>
            <tiles:putAttribute name="pageItems" value="${pageItems}"/>

            <c:if test="${listMode eq 'search'}">
                <tiles:putAttribute name="result" value="${result}"/>
            </c:if>
        </tiles:insertDefinition>
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
    
    <script type="text/javascript">

        var userPageUrl = '${userPageUrl}';

        var pageUrl = '${rawPageUrl}';

          window.addEvent('domready',function(){
                var lazy = new LazyPagination(document,{
		url: pageUrl,
		method: 'get',
		maxRequests: ${pageItems.pageCount-(pageItems.page+1)},
		buffer: 250,
		pageDataIndex: 'page',
		data: {
			page: ${pageItems.page}+2
		},
		inject: {
			element: 'morePages',
			where: 'before'
                },beforeLoad: function() {
                    $('pageLoadSpinner').setStyle('display','');
                },afterAppend: function(block,page) {
                   // alert(page);
                    try {
                        history.pushState({page: page}, "Page "+page, userPageUrl+"/"+page);
                    } catch (e) {}
                    
                     $('pageLoadSpinner').setStyle('display','none');
		    parseSearchResults(block);
                }
	
            });
    });

    </script>
    <div id="pageLoadSpinner" style="display:none;">
             <img src="<c:url value='/main/static/${appVersion}/images/gear_sml.gif'/>"/>   
             <fmt:message key="action.loading"/>
          </div>
        <div id="morePages"></div>
 </c:if>
  


<script type="text/javascript">

    var viewUrl = '${ctx}/main/paste';

     function parseSearchResults(parent) {
        parent.getElements('.pasteTitle').each(function(el, i)
        {
            el.set(
                    'html', el.get('html').replace(/\[result[^\]]*\]([\s\S]*?)\[\/result\]/gi,"<span style='background-color: #e3e658; '>$1</span>")
            );

        }); 
     } 
    
    window.addEvent('domready', function() {

        parseSearchResults($('pastas'));
   

    });
</script>
