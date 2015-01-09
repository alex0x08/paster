<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<c:set var="pageTitle" value="404: Not found"/>


<div class="jumbotron">
    <img src="<c:url  value='/prayer.png'/>" style="width: 128px;height:128px;" 
         class="img-responsive img-rounded" alt="Reponsive image"/>    
  <h1>Page not found</h1>
  <p>You step in the stream,
but the water has moved on.
This page is not here.</p>
  <p>
        
                   <a class="btn btn-primary btn-lg" href="<c:url value='/'/>" title="<fmt:message key="index.title"/>">
                           <i class="fa fa-home"></i> <fmt:message key="index.title"/>
                    </a>
      </p>
</div>

        