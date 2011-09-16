<%@ include file="/WEB-INF/template/include.jsp"%>

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
					href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${patientId}"><openmrs:patientWidget
					patientId="${patientId}" size="full" /></a></td>
			</tr>
			<c:set var="i" value="${i + 1}" />
		</c:forEach>
	</table>
</div>
