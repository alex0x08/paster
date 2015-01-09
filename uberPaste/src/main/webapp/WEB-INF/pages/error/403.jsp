<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<c:set var="pageTitle" value="403: Access denied"/>


<div class="jumbotron">
    <img src="<c:url  value='/paranoia.png'/>" style="width: 128px;height:128px;" 
         class="img-responsive img-rounded" alt="Reponsive image"/>    
  <h1>Access denied</h1>
  <p>Login incorrect.
       Only perfect spellers may
       enter this system.</p>
  <p>
        
                   <a class="btn btn-primary btn-lg" href="<c:url value='/'/>" title="<fmt:message key="index.title"/>">
                           <i class="fa fa-home"></i> <fmt:message key="index.title"/>
                    </a>
      </p>
</div>

        