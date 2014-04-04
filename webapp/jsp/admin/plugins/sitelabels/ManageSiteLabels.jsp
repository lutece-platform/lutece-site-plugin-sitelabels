<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="managesitelabels" scope="session" class="fr.paris.lutece.plugins.sitelabels.web.ManageSiteLabelsJspBean" />

<% managesitelabels.init( request, managesitelabels.RIGHT_MANAGESITELABELS ); %>
<%= managesitelabels.getManageSiteLabelsHome ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
