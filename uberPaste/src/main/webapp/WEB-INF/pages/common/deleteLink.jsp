<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />
<tiles:importAttribute name="currentUser" ignore="true" />


<sec:authorize access="${currentUser !=null and (currentUser.admin or ( model.hasOwner  and model.owner eq currentUser)) }">
    |  <a id="deleteBtn_${model.id}" href="<c:url value='/main/${modelName}/delete'><c:param name="id" value="${model.id}"/> </c:url>"
          title="<fmt:message key='button.delete'/>">
        <span style="font-size: larger;" class="i">d</span>
    </a>

          <fmt:message var="dialogConfirmMessage" key="dialog.confirm.paste.remove.message"><fmt:param value="${model.title}"/></fmt:message>
          
          
    <script  type="text/javascript">
        window.addEvent('domready', function() {

            $("deleteBtn_${model.id}").addEvent("click", function(e) {
                e.stop();
                        var source = e.target || e.srcElement;
                        showModal(source.parentElement.href,'<fmt:message key='button.delete'/>','<fmt:message key='dialog.confirm.remove'/>',
                                        '${escape:escapeJS(dialogConfirmMessage)}');
            });

        });

    </script>



</sec:authorize>

