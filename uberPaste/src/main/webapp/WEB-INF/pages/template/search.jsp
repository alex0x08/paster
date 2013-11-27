<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="url" value='/main/paste/list/search' />

<form:form action="${url}" id="searchForm"
           modelAttribute="query"
           method="POST" >
<div class="row">
    <div class="column grid-9" style="padding-top: 0.5em;margin-right: 0;">
        <form:input path="query" name="query" id="pquery" placeholder="Enter search string.." cssStyle="width:97%;margin-right:0.5em;min-width: 5em;" autocomplete="true"  />
     </div>
    <div class="column grid-3" style="">
        
        <button id="doSearchBtn" type="submit">
                 <img id="btnIcon" style="display:none;" src="<c:url value='/main/static/${appVersion}/images/gear_sml.gif'/>"/>
                 <span id="btnCaption"><fmt:message key="button.search"/></span>
             </button>

        <!--
        <input name="submit" type="submit" value="Search" style="text-align:left;min-width: 5em;max-width: 5em;"  />-->
        <form:errors path="query" cssClass="error" element="div"/>
    </div>
</div>
<div class="row">
    <div class="column grid-10">

            <span style="font-size: 12px;display:block;padding-bottom: 10px;" >
            Query samples:
         "<a href="<c:url value='/main/paste/list/search?query=id:1'/>">id:1</a>",
         "<a href="<c:url value='/main/paste/list/search?query=name:test'/>">name:test</a>" ,

        <c:url  var="lm_sample_url" value="/main/paste/list/search">
            <c:param name='query' value='lastModified:["30.01.2013" TO "31.01.2013"]'/>
        </c:url>
        "<a href='<c:out value="${lm_sample_url}"/>'> lastModified:["30.01.2013" TO "31.01.2013"]</a>"
 </span>

    </div>
</div>


</form:form>


<script type="text/javascript">

    
    window.addEvent('domready', function() {
    
          $('doSearchBtn').addEvent('click',function(){
                                   this.getElementById('btnCaption').set('text','Submitting...').disabled = true;
                                   this.getElementById('btnIcon').setStyle('display','');
                                   $("searchForm").submit();

                               });

        
        
    });
    
</script>