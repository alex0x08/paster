<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<c:set var="pageTitle" value="500: Internal error"/>


<div class="jumbotron">
    <img src="<c:url  value='/main/assets/${appId}/paster/static/big/backstab.png'/>" 
         style="width: 128px;height:128px;" 
         class="img-responsive img-rounded" alt="<fmt:message key="error.500.haiku"/>"/>    
    <h1><fmt:message key="error.500.title"/></h1>
    <p>
        <fmt:message key="error.500.haiku"/>
    </p>
    <p>

        <a class="btn btn-primary btn-lg" href="<c:url value='/'/>" title="<fmt:message key="index.title"/>">
            <i class="fa fa-home"></i> <fmt:message key="button.obey"/>
        </a>
    </p>
</div>

