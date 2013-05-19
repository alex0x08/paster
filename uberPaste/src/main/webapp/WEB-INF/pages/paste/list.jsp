<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


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
       from
    <c:forEach var="source" items="${availableSourceTypes}" varStatus="loopStatus">

        <c:choose>
            <c:when test="${source.codeLowerCase eq sourceType}">
                <span style="font-size: 14px; "><c:out value="${source.code}"/> </span>
            </c:when>
            <c:otherwise>
                <a href="<c:url value="/main/paste/list/${source.codeLowerCase}"/>"><fmt:message key="${source.name}"/></a>
            </c:otherwise>
        </c:choose>

        <c:if test="${!loopStatus.last}"> | </c:if>
    </c:forEach>

    </div>
    <div class="column grid-2">
        <a href="<c:url value="/main/paste/list/body.xml"/>"><img src="<c:url value='/images/xml.png'/>" title="xml" alt="xml"/></a> |
        <a href="<c:url value="/main/paste/list/body.json"/>"><img src="<c:url value='/images/json.png'/>" title="json" alt="json"/></a> |
        <a href="<c:url value="/main/paste/list.rss"/>"><img src="<c:url value='/images/rss.png'/>" title="rss" alt="rss"/></a> |
        <a href="<c:url value="/main/paste/list.atom"/>"><img src="<c:url value='/images/atom.png'/>" title="atom" alt="atom"/></a> |
        <a href="<c:url value="/main/paste/list.xls"/>"><img src="<c:url value='/images/xls.gif'/>" title="xls" alt="xls"/></a> |
        <a href="<c:url value="/ws/paste?wsdl"/>"><img src="<c:url value='/images/wsdl_icon.png'/>" title="wsdl" alt="wsdl"/></a>

    </div>
 </div>

<div class="row">
    <div class="column grid-12"  >

      <c:if test="${listMode eq 'search' }">

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

      </c:if>

        <%-- processing page list --%>

        <tiles:insertDefinition name="/common/pageList" >
            <tiles:putAttribute name="listMode" value="${listMode}"/>
            <tiles:putAttribute name="pageItems" value="${pageItems}"/>
            <tiles:putAttribute name="sortDesc" value="${sortDesc}"/>

            <c:if test="${listMode eq 'search'}">
                <tiles:putAttribute name="result" value="${result}"/>
            </c:if>
        </tiles:insertDefinition>



        <%-- processing elements per page and sort setup --%>

        <div class="paging" style="margin: auto; text-align: center;float:right;  " >
    <c:choose>
        <c:when test="${listMode eq 'search' }">

            <c:forEach var="page" items="${pageSet}" varStatus="loopStatus">

                <c:choose>
                    <c:when test="${pageItems.pageSize eq page}">
                        <span style="font-size: larger; "><c:out value="${page}"/> </span>
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value="/main/paste/list/search/${result}/limit/${page}"/>">${page}</a>
                    </c:otherwise>
                </c:choose>

                <c:if test="${!loopStatus.last}"> | </c:if>
            </c:forEach>


        </c:when>
        <c:when test="${listMode eq 'list' }">

            <c:forEach var="page" items="${pageSet}" varStatus="loopStatus">

                <c:choose>
                    <c:when test="${pageItems.pageSize eq page}">
                        <span style="font-size: larger; "><c:out value="${page}"/> </span>
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value="/main/paste/list/${sourceType}/limit/${page}"/>">${page}</a>
                    </c:otherwise>
                </c:choose>

                <c:if test="${!loopStatus.last}"> | </c:if>
            </c:forEach>

                    <span style="padding-left: 3em;">
            <c:choose>
                <c:when test="${sortDesc}">
                    <span style="font-size: larger; "> &#x2191; </span>
                    <a href="<c:url value="/main/paste/list/${sourceType}/older"/>"> &#8595;</a>
                </c:when>
                <c:otherwise>
                    <span style="font-size: larger; "> &#8595; </span>
                    <a href="<c:url value="/main/paste/list/${sourceType}/earlier"/>">&#x2191; </a>
                </c:otherwise>
            </c:choose>
        </span>


        </c:when>

    </c:choose>


        </div>



    </div>
</div>


<div class="row">
    <div class="column grid-16" >


    <div id="pastas">


    <c:forEach var="paste" items="${pageItems.pageList}" varStatus="status">

      <c:set property="curDate" value="${paste.lastModified}" target="${splitHelper}"/>


        <c:choose>
            <c:when test="${paste['class'].name eq 'uber.paste.model.Paste'}">

                <c:set var="priorTitle"><fmt:message key="${paste.priority.name}"/></c:set>

        <div class="row">

            <div class="column grid-16">

                <c:if test="${paste.sticked}">
                    <span class="i" title="Paste sticked">]</span>
                </c:if>

                <a class="i ${paste.priority.cssClass}" style="font-size:2em;"
                   title="<c:out value="${paste.id}"/>: ${priorTitle}. Click to search with same priority."
                   href="<c:url value='/main/paste/list/search?query=priority:${paste.priority.code}'/>">/</a>



                <a href="<c:url value="/main/paste/${paste.id}"></c:url>" title="Click to view paste vol. ${paste.id}">
                    <span  class="pasteTitle"><c:out value="${paste.name}" escapeXml="true"  /></span>
                </a>


            </div>
        </div>

        <div class="row" >

                <div class="column grid-4">
                    <a class="pastePreviewLink" href="<c:url value="/main/paste/${paste.id}"></c:url>" pasteId="${paste.id}" title="Click to view paste vol. ${paste.id}">

                       <c:choose>
                           <c:when test="${not empty paste.thumbImage}">
                               <img src="${paste.thumbImage}" width="300" height="200"/>

                           </c:when>
                           <c:otherwise>
                               <img src="<c:url value='/images/nodata.png'/>" width="300" height="200"/>
                           </c:otherwise>
                       </c:choose>
                    </a>



                </div>

                <div class="column grid-12" >

                    <div class="row">

                        <div class="pasteTitle column grid-14">

                            <div class="pasteTitle" style="padding: 1em;">
                                <a class="listLinkLine" href="<c:url value="/main/paste/${paste.id}"></c:url>" pasteId="${paste.id}" title="Click to view paste vol. ${paste.id}"><c:out value="${paste.title}"  escapeXml="true"/></a>
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
                                </tiles:insertDefinition>


                                ,<kc:prettyTime date="${paste.lastModified}" locale="${pageContext.response.locale}"/>
                            </small>

                            <sec:authorize ifAnyGranted="ROLE_ADMIN">
                                |  <a href="<c:url value='/main/paste/delete'><c:param name="id" value="${paste.id}"/> </c:url>"><fmt:message key='button.delete'/></a>
                            </sec:authorize>



                        </div>

                    </div>






                </div>




                </div>

            </c:when>
            <c:when test="${paste['class'].name eq 'uber.paste.model.Comment'}">

                <a href="<c:url value="/main/paste/${paste.id}"></c:url>" title="Click to view paste vol. ${paste.id}">
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
        No pastas
    </center>
</c:if>

    </div>
</div>


<script src="<c:url value='/libs/lightface/Source/LightFace.js'/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value='/libs/lightface/Source/LightFace.IFrame.js'/>" type="text/javascript" charset="utf-8"></script>


<script type="text/javascript">

    var viewUrl = '${ctx}/main/paste';


    window.addEvent('domready', function() {

        document.body.getElements('.pasteTitle').each(function(el, i)
        {
           // alert(el);
            el.set(
                    'html', el.get('html').replace(/\[result[^\]]*\]([\s\S]*?)\[\/result\]/gi,"<span style='background-color: #e3e658; '>$1</span>")
            );

        });

        var pastePreview = new LightFace.IFrame(
                { height:400,
                  width:800,
                  fadeDuration: 100,
                  fadeDelay: 500,
                 draggable:true , url:  '', title: 'Google!' })
                .addButton('Close', function() { pastePreview.close(); },true);


        document.body.getElements('.pastePreviewLink').each(function(el, i)
        {

         /*   el.addEvent('mouseover',function() {

                 var pasteId = el.get('pasteId');

                setTimeout(function() {
                    pastePreview.load(viewUrl+ '/'+pasteId, 'Google!');
                    pastePreview.open();

                }, 1000);

            });
          */

            /* el.addEvent('mouseout',function() {
               pastePreview.close();

             });*/

        });

    });
</script>
