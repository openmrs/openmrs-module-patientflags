<%@ include file="/WEB-INF/template/include.jsp"%>

<!--  THIS JSP PAGE IS CURRENTLY NOT USED, BUT WE'RE KEEPING IN IN PLACE -->
<!--  IN CASE WE WANT TO REVIVE IT IN THE FUTURE -->

<openmrs:require privilege="Test Flags" otherwise="/login.htm"
	redirect="/module/patientflags/fingFlaggedPatientsByTagResults.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<h2><spring:message code="patientflags.admin.title" /></h2>

<!--  display the results -->
<div><b class="boxHeader"><spring:message
	code="patientflags.flaggedPatients" /></b>
<table cellpadding="2" cellspacing="0" class="box">
	<tr>
		<th><spring:message code="patientflags.patient" /></th>
		<th><spring:message code="patientflags.flags" /></th>
	</tr>
	<c:set var="i" value="0" />
	<c:forEach items="${flaggedPatientsMap}" var="patientMap">
		<tr class="${i % 2 == 0 ? 'evenRow' : 'oddRow'}">
			<td><a
				href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${patientMap.key.patientId}"><openmrs:patientWidget
				patientId="${patientMap.key.patientId}" size="full" /></a></td>
			<td><c:forEach items="${patientMap.value}" var="flag">
					${flag.name};
				</c:forEach></td>
		</tr>
		<c:set var="i" value="${i + 1}" />
	</c:forEach>
</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>