<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm"
	redirect="/module/patientflags/managePriorities.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>


<h2><spring:message code="patientflags.admin.title" /></h2>
<a
	href="${pageContext.request.contextPath}/module/patientflags/editPriority.form"><spring:message
	code="patientflags.managePriorities.addPriority" /></a>
<br />
<br />

<!--  display the priorities -->
<div><b class="boxHeader"><spring:message
	code="patientflags.priorities" /></b>
<table cellpadding="2" cellspacing="0" class="box">
	<tr>
		<th><spring:message code="patientflags.name" /></th>
		<th><spring:message code="patientflags.style" /></th>
		<th><spring:message code="patientflags.rank" /></th>
		<th>&nbsp;</th>
	</tr>
	<c:set var="i" value="0" />
	<c:forEach items="${priorities}" var="priority">
		<tr class="${i % 2 == 0 ? 'evenRow' : 'oddRow'}">
			<td><a
				href="${pageContext.request.contextPath}/module/patientflags/editPriority.form?priorityId=${priority.priorityId}">${priority.name}</a></td>
			<td>${priority.style}</td>
			<td>${priority.rank}</td>
			<td>
				<a href="${pageContext.request.contextPath}/module/patientflags/deletePriority.form?priorityId=${priority.priorityId}"
				   onclick="return confirm('<spring:message code="patientflags.managePriorities.deletePriorityConfirm"/>');">
				   <spring:message code="patientflags.managePriorities.deletePriority" />
				</a>
			</td>
		</tr>
		<c:set var="i" value="${i + 1}" />
	</c:forEach>
</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>