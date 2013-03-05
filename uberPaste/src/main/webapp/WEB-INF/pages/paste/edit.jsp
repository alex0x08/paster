<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="url" value='/main/paste/save' />

<fieldset>
    <legend><span class="i" style="font-size:2em;">/</span><c:out value="${requestScope.title}" escapeXml="true"/></legend>

<form:form action="${url}" 
           modelAttribute="model"
           method="POST" >

    <div class="row">
        <div class="column grid-14">
            <form:errors  cssClass="errorblock" element="div" />
        </div>
    </div>

    <div class="row">
        <div class="column grid-9">

    <c:choose>
        <c:when test="${model.blank}">
        </c:when>
        <c:otherwise>
            <form:hidden path="id"  />
        </c:otherwise>
    </c:choose>
            <form:label path="name"><fmt:message key="paste.title"/>:</form:label>
                <form:input cssClass="notice" cssErrorClass="error" path="name" name="title" id="pname" cssStyle="width:97%;" maxlength="255" title="Paste title"  />
                <form:errors path="name" cssClass="error" />
    </div>

    <div class="column grid-1" >
        <div style="padding-top: 1em;vertical-align: middle;">
            <span class="i" >]</span>
            <form:checkbox path="sticked" style="display:inline;" title="Stick paste"/>
        </div>

        </div>
    </div>

    <div class="row">
        <div class="column grid-6">

        <form:label path="tagsAsString"><fmt:message key="paste.tags"/>:</form:label>
    <form:input path="tagsAsString" maxlength="255" cssStyle="width:97%;" autocomplete="true" placeholder="enter space-separated tags here"  />
    <form:errors path="tagsAsString" cssClass="error" />

        </div>
        <div class="column grid-3">


        <form:label path="codeType">Hightlight like:</form:label>
    <form:select path="codeType" multiple="false" id="ptype">
                    
                    <c:forEach items="${availableCodeTypes}" var="codeType">
				    <form:option value="${codeType.code}" >
                                    <fmt:message key="${codeType.name}"/>
                                </form:option>
                    </c:forEach>
                </form:select> 
                <form:errors path="codeType" cssClass="error" />

        </div>

        <div class="column grid-2">

            <form:label path="priority"><fmt:message key="paste.priority"/>:</form:label>
            <form:select path="priority" multiple="false" id="pprior">
                <c:forEach items="${availablePriorities}" var="prior">
                    <form:option value="${prior.code}" >
                        <fmt:message key="${prior.name}"/>
                    </form:option>
                </c:forEach>
            </form:select>
            <form:errors path="priority" cssClass="error" />


        </div>
    </div>

    <div class="row">
        <div class="column grid-16">

        <form:label path="text"><fmt:message key="paste.text"/>:</form:label>
    <form:textarea path="text" cssErrorClass="error" cssClass="notice" cssStyle="width:97%;" name="text" id="ptext"  cols="120" rows="10" />
    <form:errors path="text" cssClass="error" />


    <div class="form-buttons">
            <div class="button">
                <c:if test="${!model.blank}">
                    <input name="cancel" type="submit" value="<fmt:message key="button.cancel"/>" />
                </c:if>
                <fmt:message var="submit_button_text" key="button.save"/>

                <form:button name="submit" id="submitBtn"   >
                    <c:out value="${submit_button_text}"/>
                </form:button>

                <c:if test="${!model.blank}">
                    <div style="float:right;margin-top: 2em;">
                        <kc:prettyTime date="${model.lastModified}" locale="${pageContext.response.locale}"/>
                    </div>

                </c:if>

            </div>
        </div>

        </div>
    </div>

</form:form>
     </fieldset>


<a href="<c:url value="/main/paste/list"/>">&#8592;<fmt:message key="paste.list.title"/></a>
