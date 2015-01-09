<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<c:set var="pageTitle" value="500: Internal error"/>


<div class="jumbotron">
    <img src="<c:url  value='/backstab.png'/>" style="width: 128px;height:128px;" 
         class="img-responsive img-rounded" alt="Reponsive image"/>    
  <h1>Internal error</h1>
  <p>Errors have occurred.
       We won't tell you where or why.
       Lazy programmers.</p>
  <p>
        
                   <a class="btn btn-primary btn-lg" href="<c:url value='/'/>" title="<fmt:message key="index.title"/>">
                           <i class="fa fa-home"></i> <fmt:message key="index.title"/>
                    </a>
      </p>
</div>

        