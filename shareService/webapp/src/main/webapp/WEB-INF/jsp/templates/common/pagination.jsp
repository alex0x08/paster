<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<c:choose>
    <c:when test="${param.integrationCode ne null}">
        <c:url var="listUrl" value="/main/${param.modelName}/integrated/list/${param.integrationCode}/" />
    </c:when>
    <c:otherwise>
        <c:url var="listUrl" value="/main/${param.modelName}/list/" />
    </c:otherwise>
</c:choose>


<script src="<c:url value='/main/static/${appVersion}/libs/bootstrap-paginator/bootstrap-paginator.min.js'/>"></script>

  <script type="text/javascript">
    $(document).ready(function() {
         var options = {
            bootstrapMajorVersion: 3, 
            currentPage: ${pageItems.page+1},
            totalPages: ${pageItems.pageCount},
            itemContainerClass: function (type, page, current) {
                return (page === current) ? "active" : "pointer-cursor";
            },
            pageUrl: function(type, page, current){
                return "${listUrl}"+page;
            },
            useBootstrapTooltip:true,
            tooltipTitles: function (type, page, current) {
                switch (type) {
                case "first":
                    return "<fmt:message key="button.first"/> <i class='icon-fast-backward icon-white'></i>";
                case "prev":
                    return "<fmt:message key="button.prev"/> <i class='icon-backward icon-white'></i>";
                case "next":
                    return "<fmt:message key="button.next"/> <i class='icon-forward icon-white'></i>";
                case "last":
                    return "<fmt:message key="button.last"/> <i class='icon-fast-forward icon-white'></i>";
                case "page":
                    return "<fmt:message key='list.page.goto.no-param'/>"+page;
                }
            },
            bootstrapTooltipOptions: {
                html: true,
                placement: 'bottom'
            },  size:'normal'
                
        };

        $('#list-pagination').bootstrapPaginator(options);
      
     
      
    });
    </script>  
    
            <ul id="list-pagination" style="margin:0;" ></ul>  
       