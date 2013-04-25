<%@ tag import="org.ocpsoft.prettytime.PrettyTime"  import="java.util.Date"%>
<%@ attribute name="date" required="true" type="java.util.Date" %>
<%@ attribute name="locale" required="true" type="java.util.Locale" %>

<%
    PrettyTime p = new PrettyTime();
    p.setLocale(locale);
    if (date!=null) {
        String prettier = p.format(date);
        out.println(prettier);
    } else {
        out.println("No date");
    }
 %>