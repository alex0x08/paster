<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />
<tiles:importAttribute name="currentUser" ignore="true" />


<sec:authorize access="${currentUser !=null and (currentUser.admin or ( model.hasOwner  and model.owner eq currentUser)) }">
    |  <a id="deleteBtn_${model.id}" href="<c:url value='/main/${modelName}/delete'><c:param name="id" value="${model.id}"/> </c:url>"
          title="<fmt:message key='button.delete'/>">
        <span style="font-size: larger;" class="i">d</span>
    </a>


    <script  type="text/javascript">
        window.addEvent('domready', function() {

            $("deleteBtn_${model.id}").addEvent("click", function(e) {
                e.stop();
                SM.show({
                    "model": "confirm",
                    "callback": function() {
                        var source = e.target || e.srcElement;
                       window.location.href = source.parentElement.href;
                     },
                    "title": "Confirm Modal Title",
                    "contents": "Lorem ipsum dolor sit amet..."
                });

            });

        });

    </script>



</sec:authorize>

