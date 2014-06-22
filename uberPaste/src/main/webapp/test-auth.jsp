<%@page import="org.pac4j.core.exception.RequiresHttpAction"%>
<%@page import="org.pac4j.springframework.security.authentication.*"%>
<%@page import="org.springframework.security.core.*"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="org.pac4j.core.context.*"%>
<%@page import="org.pac4j.core.profile.*"%>
<%@page import="org.pac4j.oauth.client.*"%>
<%
	WebContext context = new J2EContext(request, response); 
	FacebookClient fbClient = (FacebookClient) application.getAttribute("FacebookClient");
	TwitterClient twClient = (TwitterClient) application.getAttribute("TwitterClient");
%>
<h1>index</h1>
<a href="facebook/index.jsp">Protected url by Facebook : facebook/index.jsp</a><br />
<a href="twitter/index.jsp">Protected url by Twitter : twitter/index.jsp</a><br />
<a href="google/index.jsp">Protected url by Google : google/index.jsp</a><br />
<br />
<a href="/j_spring_security_logout">logout</a>
<br /><br />
<%Authentication auth = (Authentication) SecurityContextHolder.getContext().getAuthentication();
UserProfile profile = null;
if (auth != null && auth instanceof ClientAuthenticationToken) {
    ClientAuthenticationToken token = (ClientAuthenticationToken) auth; 
    profile = token.getUserProfile();
}
%>
profile : <%=profile%>
<br /><br />
<hr />
<%
try {
%>
<a href="<%=fbClient.getRedirectAction(context, false, false).getLocation()%>">Authenticate with Facebook</a><br />
<a href="<%=twClient.getRedirectAction(context, false, false).getLocation()%>">Authenticate with Twitter</a><br />
<%
} catch (RequiresHttpAction e) {
	// should not happen
}
%>
