<%@page import="org.openmrs.module.patientflags.Flag"%>
<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Patients" otherwise="/login.htm"
	redirect="/index.htm" />

<c:forEach items="${model.flaglist}" var="fm">
	<span ${fm.flag.priority.style}>${fm.flagMessage}</span><br />
</c:forEach>