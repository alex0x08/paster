<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


   <script type="text/javascript">
             
             var currentSortColumn = '${pageItems.sort.property}';
             var sortAsc = ${pageItems.sort.ascending};
             
             var direction = sortAsc ? 'down' : 'up';
             
                       $(document).ready(function() {                                                           
                           
                              $('.sortColumnKey').each(function(index, obj) {
                                    if ( obj.getAttribute('sortColumn') == currentSortColumn) {
                                       if (sortAsc) {
                                        $(this).find("span").addClass('glyphicon-arrow-down').removeClass('glyphicon-arrow-up');
                                       } else {
                                        $(this).find("span").addClass('glyphicon-arrow-up').removeClass('glyphicon-arrow-down');
                                       }
                                    }
                                   $(this).click(function() {
                                       direction = sortAsc ? 'up' : 'down';
                                        window.location = "<c:url value='/main/${param.modelName}/list/sort/'/>"+obj.getAttribute('sortColumn')+'/'+direction;
                                    });
                              });
                    });
                      
                  </script>