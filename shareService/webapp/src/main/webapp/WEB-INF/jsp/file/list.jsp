<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<script src="<c:url value='/main/static/${appVersion}/libs/bootstrap-paginator/bootstrap-paginator.min.js'/>"></script>


<c:url var="url" value='/main/file/list' />


<div class="row" style="margin: 0; padding: 0;">                

    <div class="col-md-10 centered text-center"  >        
        <c:if test="${pageItems.pageCount > 1}">       
            <ul id="file-pagination" style="" ></ul>  
        </c:if>   
    </div>     

    
    <div class="col-md-2 centered" style="padding-top: 1em;"  >

        <div class="btn-group">
            <button type="button" class="btn btn-danger"><c:out value="${pageItems.pageSize}"/></button>
            <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
                <span class="caret"></span>
            </button>
            <ul class="dropdown-menu" role="menu">

                <c:forEach items="${fn:split('5,10,50,100,500',',')}" var="pg">
                    <li >
                        <a href="<c:url value="/main/file/list/limit/${pg}">
                           </c:url>"><c:out value="${pg}"/></a>
                    </li>
                </c:forEach>   
            </ul>
        </div>   
    </div>         
    
</div>

<div class="row">

    <div id="gallery" class="col-md-12">  
    
    <c:forEach var="obj" items="${pageItems.pageList}" varStatus="status">

        <c:set var="model" value="${obj}" scope="request"></c:set>

        <jsp:include page="/WEB-INF/jsp/templates/common/preview.jsp" />

    </c:forEach>



<c:if test="${pageItems.nrOfElements == 0}">
    <jsp:include page="/WEB-INF/jsp/templates/common/noFiles.jsp" />
</c:if>

    </div>
</div>
     <c:url value="/main/file/list/" var="listUrl"/>
            
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
            },
                size:'normal'
                
        }

        $('#file-pagination').bootstrapPaginator(options);
        
      
    });
    </script>          