<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Patients" otherwise="/login.htm"
	redirect="/index.htm" />

<c:forEach items="${model.flags}" var="flag">
	<span ${flag.priority != null ? flag.priority.style : ''}>${flag.localizedMessage}</span><br />
</c:forEach>