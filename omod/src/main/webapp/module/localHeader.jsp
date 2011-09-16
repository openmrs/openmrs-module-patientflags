<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
		code="admin.title.short" /></a></li>
	<openmrs:hasPrivilege privilege="Manage Flags">
		<li
			<c:if test='<%= request.getRequestURI().contains("manageFlags") %>'>class="active"</c:if>>
		<a
			href="${pageContext.request.contextPath}/module/patientflags/manageFlags.form"><spring:message
			code="patientflags.manageFlags" /></a></li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Manage Flags">
		<li
			<c:if test='<%= request.getRequestURI().contains("manageTags") %>'>class="active"</c:if>>
		<a
			href="${pageContext.request.contextPath}/module/patientflags/manageTags.list"><spring:message
			code="patientflags.manageTags" /></a></li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Manage Priorities">
		<li
			<c:if test='<%= request.getRequestURI().contains("managePriorities") %>'>class="active"</c:if>>
		<a
			href="${pageContext.request.contextPath}/module/patientflags/managePriorities.list"><spring:message
			code="patientflags.managePriorities" /></a></li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Manage Flags">
		<li
			<c:if test='<%= request.getRequestURI().contains("managePatientFlagsProperties") %>'>class="active"</c:if>>
		<a
			href="${pageContext.request.contextPath}/module/patientflags/managePatientFlagsProperties.form"><spring:message
			code="patientflags.managePatientFlagsProperties" /></a></li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Test Flags">
		<li
			<c:if test='<%= request.getRequestURI().contains("findFlaggedPatients") %>'>class="active"</c:if>>
		<a
			href="${pageContext.request.contextPath}/module/patientflags/findFlaggedPatients.form"><spring:message
			code="patientflags.findFlaggedPatients" /></a></li>
	</openmrs:hasPrivilege>
</ul>