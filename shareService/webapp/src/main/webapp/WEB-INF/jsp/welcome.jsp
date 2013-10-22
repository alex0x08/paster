<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<div class="hero-unit">
  <h1><fmt:message key="index.title"/></h1>
  <p><fmt:message key="index.message"/></p>
  <p>
         
      <c:url var="url" value='/main/file/list' />

         <jsp:include
             page="/WEB-INF/jsp/templates/file-search-header.jsp">
             <jsp:param name="filesBtn" value="LIST"/>
         </jsp:include>
            
  </p>
</div>


