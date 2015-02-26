<%@page import="org.openmrs.module.patientflags.Flag"%>
<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Patients" otherwise="/login.htm"
	redirect="/index.htm" />

<c:forEach items="${model.flags}" var="flag">
	<span ${flag.priority.style}> 
	<c:set var="flag" value="${flag}"/>
	<c:set var="patient_id" value="${param.patientId}"/>
	 <%
	 try{
	 Flag flag = (Flag)pageContext.getAttribute("flag");
	 Integer patient_id = Integer.parseInt(pageContext.getAttribute("patient_id").toString());
	 out.println(flag.evalMessage(patient_id));
	 }
	 catch(Exception e){
		 e.printStackTrace();
	 }
	  %>
	</span><br />
</c:forEach>