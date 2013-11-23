<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


        
            <div class="btn-group" data-toggle="buttons">

                <c:forEach items="${availableSortColumns}" var="sortColumn">
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

        </div>
                             
                             
        <script type="text/javascript">
            $(document).ready(function() {
             
                $('.sortColumnKey').change(function() {
                    window.location = "<c:url value='/main/${param.modelName}/list/sort/'/>"+$(this).val();
                });
      
                $('.sortColumnAsc').change(function() {
                    window.location = "<c:url value='/main/${param.modelName}/list/sort/${pageItems.sort.property}'/>/"+$(this).val();
                });
            
            });
        </script>  
                        