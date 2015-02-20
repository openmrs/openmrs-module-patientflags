<%@page import="org.openmrs.module.patientflags.Flag"%>
<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Test Flags" otherwise="/login.htm"
	redirect="/module/patientflags/fingFlaggedPatientsResults.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<h2><spring:message code="patientflags.admin.title" /></h2>

<!--  display the results -->
<div>
	<b class="boxHeader">
		${flag.name}: ${fn:length(flaggedPatients)} / ${allPatients.size}
	</b>
	<table cellpadding="2" cellspacing="0" class="box">
		<c:set var="i" value="0" />
		<c:forEach items="${flaggedPatients}" var="patientId">
			<tr class="${i % 2 == 0 ? 'evenRow' : 'oddRow'}">
				<td><a
					href="${pageContext.request.contextPath}${patientLink}patientId=${patientId}"><openmrs:patientWidget
					patientId="${patientId}" size="full" /></a></td>
				<td>
				<c:set var="flag" value="${flag}"/>
				<c:set var="patient_id" value="${patientId}"/>
				 <%
				 Flag flag = (Flag)pageContext.getAttribute("flag");
				 System.out.println(flag+"::FLAG JSP");
				 Integer patient_id = (Integer)pageContext.getAttribute("patient_id");
				 System.out.println(patient_id+"::patient_id JSP");
				 out.println(flag.evalMessage(patient_id));
				  %>
  				</td>
			</tr>
			<c:set var="i" value="${i + 1}" />
		</c:forEach>
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>