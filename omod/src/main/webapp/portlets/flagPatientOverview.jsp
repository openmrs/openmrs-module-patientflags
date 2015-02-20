<%@page import="org.openmrs.module.patientflags.Flag"%>
<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Patients" otherwise="/login.htm"
	redirect="/index.htm" />

<c:forEach items="${model.flags}" var="flag">
	<span ${flag.priority != null ? flag.priority.style : ''}>
	<c:set var="flag" value="${flag}"/>
	<c:set var="patient_id" value="${param.patientId}"/>
	 <%
	 Flag flag = (Flag)pageContext.getAttribute("flag");
	 System.out.println(flag+"::FLAG JSP");
	 Integer patient_id = Integer.parseInt(pageContext.getAttribute("patient_id").toString());
	 System.out.println(patient_id+"::patient_id JSP");
	 out.println(flag.evalMessage(patient_id));
	  %>
	</span><br />
</c:forEach>