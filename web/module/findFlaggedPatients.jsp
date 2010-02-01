<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="resources/spring-form.tld"%>

<openmrs:require privilege="Test Flags" otherwise="/login.htm"
	redirect="/module/patientflags/fingFlaggedPatients.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>


<h2><spring:message code="patientflags.admin.title" /></h2>

<div><b class="boxHeader"><spring:message
	code="patientflags.findFlaggedPatients" /></b>
<div class="box"><form:form modelAttribute="flag" method="get">
	<form:errors path="*" cssClass="error" />
	<table>
		<tr>
			<td><spring:message
				code="patientflags.findFlaggedPatients.selectFlag" />:</td>
			<td><form:select path="flagId">
				<form:options items="${flags}" itemValue="flagId" itemLabel="name" />
			</form:select></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit"
				value="<spring:message code='patientflags.submit'/>" /></td>
		</tr>
	</table>
</form:form></div>
</div>

<br />
&nbsp;
<c:if test="${!empty tags}">
<spring:message code="patientflags.or" />
<br />
<br />

<div><b class="boxHeader"><spring:message
	code="patientflags.findFlaggedPatients.byTagSet" /></b>
<div class="box"><form:form modelAttribute="filter" method="get">
	<form:errors path="*" cssClass="error" />
	<table>
		<tr>
			<td valign="top"><nobr><spring:message
				code="patientflags.findFlaggedPatients.selectTag" />:</nobr></td>
			<td><form:select path="tags" multiple="true">
				<form:options items="${tags}" itemValue="tagId" itemLabel="name" />
			</form:select></td>
			<td align="left" valign="top"><nobr><form:radiobutton
				path="type" value="ANYTAG" /> <spring:message
				code='patientflags.findFlaggedPatients.anyTag' /></nobr><br />
			<nobr><form:radiobutton path="type" value="ALLTAGS" /> <spring:message
				code='patientflags.findFlaggedPatients.allTags' /></nobr></td>
			<td width="100%">&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit"
				value="<spring:message code='patientflags.submit'/>" /></td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
	</table>
</form:form></div>
</div>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>