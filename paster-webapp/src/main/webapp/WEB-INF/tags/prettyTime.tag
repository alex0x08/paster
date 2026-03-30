<%@ tag
import="org.ocpsoft.prettytime.PrettyTime"
import="java.text.SimpleDateFormat"
import="java.util.Date"%>

<%@ attribute name="date" required="true" type="java.util.Date" %>
<%@ attribute name="locale" required="true" type="java.util.Locale" %>
<%@ attribute name="format" required="true" type="java.lang.String" %>

<%

    if (date!=null) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        PrettyTime p = new PrettyTime();
        p.setLocale(locale);
        String prettier = p.format(date);
        out.println("<span title='");
        out.println(sdf.format(date));
        out.println("'>");
        out.println(prettier);
        out.println("</span>");
    } else {
        out.println("--");
    }
 %>
