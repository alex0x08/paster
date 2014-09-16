<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


  <jsp:include page="/WEB-INF/pages/common/paste-update-poll.jsp"/>
  

<div class="row">
    <div class="col-md-10">
   
             <a  href="<c:url value='/main/paste/list/${sourceType}'/>"><span class="i" style="font-size: 1.5em;">P</span></a>
    
             <sec:authorize ifAnyGranted="ROLE_ADMIN">
             
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
                 
             </sec:authorize>
      
    </div>
    <div class="col-md-2">
       
        <a class="img-map img-xml" href="<c:url value="/main/paste/list/body.xml"/>" title="xml" alt="xml">
        </a> |
        <a class="img-map img-json" href="<c:url value="/main/paste/list/body.json"/>" title="json" alt="json">
        </a> |
        <a class="img-map img-rss" href="<c:url value="/main/paste/list.rss"/>" title="rss" alt="rss">
        </a>
            |
        <a class="img-map img-atom" href="<c:url value="/main/paste/list.atom"/>" title="atom" alt="atom">
         </a> 
       
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
    <div class="col-md-10" >
   
        
    <div id="pastas">
        
    <c:forEach var="paste" items="${pageItems.pageList}" varStatus="status">

        <c:choose>
            <c:when test="${paste['class'].name eq 'uber.paste.model.Paste'}">                
                 <c:if test="${not paste.sticked}"> 
                    <c:set property="curDate" value="${paste.lastModified}" target="${splitHelper}"/>
                </c:if>
                
                <c:set var="priorTitle"><fmt:message key="${paste.priority.name}"/></c:set>

        <div class="row">

            <div class="col-md-12">

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

                   
                       <c:choose>
                           <c:when test="${not empty paste.thumbImage}">
                               <div class="col-md-3" >

                                   <c:url value='/main/resources/${appVersion}/t/${paste.lastModified.time}/${paste.thumbImage}.jpg' var="thumbUrl">
                                   </c:url>

                                   <a class="pastePreviewLink" href="<c:url value="/${paste.id}"></c:url>" pasteId="${paste.id}" 
                                      title="Click to view paste vol. ${paste.id}">
                                       <img src="${thumbUrl}" width="250" height="150" class="p-comment"  />

                                   </a>
                                   
                               </div>
                               <c:set var="currentRowSize" value="8"/>
                           </c:when>
                           <c:otherwise>
                             <c:set var="currentRowSize" value="10"/>
                           </c:otherwise>
                       </c:choose>
                    
                    
                <div class="col-md-${currentRowSize}" >
                    <div class="row">
                        <div class="pasteTitle col-md-12">
                            <div class="pasteTitle" style="padding: 1em;">
                                <a class="listLinkLine" href="<c:url value="/${paste.id}"></c:url>" 
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
    <div class="col-md-2">
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
                              $('paste_list_'+page).grab($('pageLoadSpinner'),"after");
              
                     $('pageLoadSpinner').setStyle('display','none');
		    parseSearchResults(block);
                         bindDeleteDlg(block);
       
                }
	
            });
    });

    </script>
    <div id="pageLoadSpinner" style="display:none;">
                  <i  class="fa fa-spinner fa-spin"></i>       
             <fmt:message key="action.loading"/>
          </div>
        <div id="morePages"></div>
 </c:if>
  


<script type="text/javascript">

  

    var viewUrl = '${ctx}/main/paste',createUrl = '${ctx}/main/paste/newFrame?frameMode=1';

     function parseSearchResults(parent) {
        parent.getElements('.pasteTitle').each(function(el, i)
        {
            el.set(
                    'html', el.get('html').replace(/\[result[^\]]*\]([\s\S]*?)\[\/result\]/gi,"<span style='background-color: #e3e658; '>$1</span>")
            );

        }); 
     } 
     
      var createNewPasteDlg;
        
    window.addEvent('domready', function() {


        createNewPasteDlg = new SimpleModal({
            "hideHeader":true, "closeButton":false,"hideFooter":true});
      

          $("createNewBtn").addEvent("click", function(e){
          e.stop();
    var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0),
    h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);

            
            if (w>1140) w=1140;
            createNewPasteDlg.initialize({"width":w * 0.9});
        createNewPasteDlg.show({
          
          "model":"modal",
          "contents":'<iframe src="'+createUrl+'" width="'+(w * 0.9 )+'" height="382" frameborder="0" style="padding:0;margin:0;" ></iframe>'
            });
        });


        parseSearchResults($('pastas'));
   

    });
</script>
