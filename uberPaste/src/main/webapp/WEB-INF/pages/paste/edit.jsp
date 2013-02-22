<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="url" value='/main/paste/save' />

<fieldset>
    <legend><span class="i" style="font-size:2em;">/</span><c:out value="${requestScope.title}" escapeXml="true"/></legend>

<form:form action="${url}" 
           modelAttribute="model"
           method="POST" >

    <form:errors  cssClass="errorblock" element="div" />

    <c:choose>
        <c:when test="${model.blank}">
        </c:when>
        <c:otherwise>
            <form:hidden path="id"  />
        </c:otherwise>
    </c:choose>
            <form:label path="name"><fmt:message key="paste.title"/>:</form:label>
                <form:input path="name" name="title" id="pname" size="100em;"  />
                <form:errors path="name" cssClass="error" />


    <form:label path="tagsAsString"><fmt:message key="paste.tags"/>:</form:label>
    <form:input path="tagsAsString"  />
    <form:errors path="tagsAsString" cssClass="error" />



    <form:label path="codeType"><fmt:message key="paste.types"/>:</form:label>
    <form:select path="codeType" multiple="false" id="ptype">
                    
                    <c:forEach items="${availableCodeTypes}" var="codeType">
				    <form:option value="${codeType.code}" >
                                    <fmt:message key="${codeType.name}"/>
                                </form:option>
                    </c:forEach>
                </form:select> 
                <form:errors path="codeType" cssClass="error" />

    <form:label path="text"><fmt:message key="paste.text"/>:</form:label>
    <form:textarea path="text" name="text" id="ptext"  cols="120" rows="10" />
    <form:errors path="text" cssClass="error" />

    <form:label path="priority"><fmt:message key="paste.priority"/>:</form:label>
    <form:select path="priority" multiple="false" id="pprior">
        <c:forEach items="${availablePriorities}" var="prior">
            <form:option value="${prior.code}" >
                <fmt:message key="${prior.name}"/>
            </form:option>
        </c:forEach>
    </form:select>
    <form:errors path="priority" cssClass="error" />

    <div class="form-buttons">
            <div class="button">
                <input name="cancel" type="submit" value="<fmt:message key="button.cancel"/>" />
                <fmt:message var="submit_button_text" key="button.save"/>

                <form:button name="submit" id="submitBtn"  >
                    <c:out value="${submit_button_text}"/>
                </form:button>

                <c:if test="${!model.blank}">
                    <div style="float:right;margin-top: 2em;">
                        <kc:prettyTime date="${model.lastModified}" locale="${pageContext.response.locale}"/>
                    </div>

                </c:if>

            </div>
        </div>

</form:form>
     </fieldset>


<a href="<c:url value="/main/paste/list"/>"><fmt:message key="paste.list.title"/></a>
