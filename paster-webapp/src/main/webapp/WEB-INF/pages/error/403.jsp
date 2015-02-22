<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<c:set var="pageTitle" value="403: Access denied"/>


<div class="jumbotron">
    <img src="<c:url  value='/main/assets/${appVersion}/paster/static/big/paranoia.png'/>" style="width: 128px;height:128px;" 
         class="img-responsive img-rounded" alt="Reponsive image"/>    
    <h1><fmt:message key="error.403.title"/></h1>
    <p>
        <fmt:message key="error.403.haiku"/>
    </p>

    <p>

        <a class="btn btn-primary btn-lg" href="<c:url value='/'/>" title="<fmt:message key="index.title"/>">
            <i class="fa fa-home"></i> <fmt:message key="button.obey"/>
        </a>
    </p>
</div>

