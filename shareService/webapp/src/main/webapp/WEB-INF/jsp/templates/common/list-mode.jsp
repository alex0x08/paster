<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

        <c:set var="listUrl" value="/main/${param.modelName}/list" />

        <div class="btn-group" data-toggle="buttons">
                <c:forEach items="${availableModes}" var="mode">
                    <label class="btn btn-default" >
                        <fmt:message key="${sortColumn.name}" />
                        <form:radiobutton cssClass="sortColumnKey"  path="pageItems.sort.property" value="${sortColumn.property}"   />
                    </label>
                </c:forEach>
            </div>    
            
              <div class="btn-group" data-toggle="buttons">                 
                  <label class="btn btn-default" title="<fmt:message key="sort.asc" />" >
                             <span class="glyphicon glyphicon-arrow-up"></span>
                             <input class="sortColumnAsc" type="radio" value="up" ${pageItems.sort.ascending == false ? 'checked' : '' }  />
                    </label>             
                    <label class="btn btn-default" title="<fmt:message key="sort.desc" />" >
                             <span class="glyphicon glyphicon-arrow-down"></span>
                             <input class="sortColumnAsc" type="radio" value="down" ${pageItems.sort.ascending == true ? 'checked' : '' }/>
                    </label>
            </div>    
        
                             
        <script type="text/javascript">
            $(document).ready(function() {
             
                $('.sortColumnKey').change(function() {
                    window.location = "<c:url value='${listUrl}/sort/'/>"+$(this).val();
                });
      
                $('.sortColumnAsc').change(function() {
                    window.location = "<c:url value='${listUrl}/sort/${pageItems.sort.property}'/>/"+$(this).val();
                });            
            });
        </script>  
                        