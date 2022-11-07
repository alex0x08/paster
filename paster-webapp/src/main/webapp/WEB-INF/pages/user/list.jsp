<%--

    Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class="row">
    <div class="col-md-8">
        
        <h4 class="f-h4">
            <fmt:message key="user.list.title"/> 
            <a href="<c:url value='/main/user/new'/>" title="<fmt:message key="user.create.new"/>">
                <span style="font-size: larger;" class="i">+</span>
            </a>
        </h4>
    </div>
</div>
<div class="row">
                
    <div class="col-md-8">
  
        
        <table class="table table-bordered table-striped table-condensed" 
               data-behavior="HtmlTable" data-htmltable-sortable="true">
  <thead>
    <tr>
      <th>
            ID
      </th>
      <th>
            Login
      </th>
      <th>
            Name
      </th>
      <th>
            Last modified
      </th>
     
    </tr>
  </thead>
  <tbody>
      
         <c:forEach var="user" items="${items}" varStatus="status">
             <tr>
                 <td>
                         <a href="<c:url value='/main/user/edit/${user.id}'/>"><c:out value="${user.id}"/></a>
                 </td>
                 <td>
                     <a href="<c:url value='/main/user/edit/${user.id}'/>"><c:out value="${user.username}"/></a>
                 </td>
                 <td>
                     <a href="<c:url value='/main/user/edit/${user.id}'/>"><c:out value="${user.name}"/></a>
                     
                 </td>
                 <td>
                     <c:out value="${user.lastModifiedDt}"/>
                 </td>
             </tr>
         
   </c:forEach>
     
  </tbody>
</table>
        
      
        
    </div>
    
    
</div>

