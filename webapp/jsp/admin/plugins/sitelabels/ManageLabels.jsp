<jsp:useBean id="managesitelabelsLabel" scope="session" class="fr.paris.lutece.plugins.sitelabels.web.LabelJspBean" />
<% String strContent = managesitelabelsLabel.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
