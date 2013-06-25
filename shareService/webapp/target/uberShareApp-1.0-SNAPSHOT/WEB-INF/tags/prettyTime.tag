<%@ tag import="org.ocpsoft.prettytime.PrettyTime"  import="java.util.Date"%>
<%@ attribute name="date" required="true" type="java.util.Date" %>
<%@ attribute name="locale" required="true" type="java.util.Locale" %>
<%@ attribute name="var" required="true" type="java.lang.String" rtexprvalue="false"%>
<%@ variable alias="formattedDate" name-from-attribute="var" scope="AT_BEGIN" variable-class="java.lang.String"%>


<%
    PrettyTime p = new PrettyTime();
    p.setLocale(locale);

    String outputF = date!=null ? p.format(date): "No date";
 
    if (var!=null && var.trim().length()>0) {
        jspContext.setAttribute("formattedDate", outputF);
    } else {
        out.println(outputF);
    }
          
  
%>