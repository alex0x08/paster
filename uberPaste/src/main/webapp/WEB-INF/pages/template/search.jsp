<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="url" value='/main/paste/list/search' />

<form:form action="${url}" id="searchForm"
           modelAttribute="query"
           method="POST" >
<div class="row">
    <div class="column grid-9" style="padding-top: 0.5em;margin-right: 0;">
        <fmt:message key='paste.search.placeholder' var="searchPlaceHolder"/>
        <form:input path="query" name="query" id="pquery" placeholder="${searchPlaceHolder}" 
                    cssStyle="height:1em;width:97%;margin-right:0.5em;min-width: 5em;" autocomplete="true"  />
     
    </div>
        <div class="column grid-4" style="text-align:left;padding-left: 0;margin-left: 0;margin-top: 0.3em;" >
        
        <button class="sbtn p-btn-save  " id="doSearchBtn" type="submit">
                 <span id="btnCaption"><fmt:message key="button.search"/></span>
                 <i id="btnIcon" class="fa fa-spinner" style="display:none;"></i>
                 </button>
       
        <form:errors path="query" cssClass="error" element="div"/>
    </div>
</div>
<div class="row">
    <div class="column grid-10">

            <span style="font-size: 12px;display:block;padding-bottom: 10px;" >
            <fmt:message key="search.query.samples"/>:
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
                                   this.getElementById('btnCaption').set('text',transmitText).disabled = true;
                                   this.getElementById('btnIcon').setStyle('display','');
                                   $("searchForm").submit();
                               });       
        
    });
    
</script>