<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="springform" uri="resources/spring-form.tld"%>

<openmrs:require privilege="Test Flags" otherwise="/login.htm"
	redirect="/module/patientflags/fingFlaggedPatients.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>


<h2><spring:message code="patientflags.admin.title" /></h2>

<div><b class="boxHeader"><spring:message
	code="patientflags.findFlaggedPatients" /></b>
<div class="box"><springform:form modelAttribute="flag" method="get">
	<springform:errors path="*" cssClass="error" />
	<table>
		<tr>
			<td><spring:message
				code="patientflags.findFlaggedPatients.selectFlag" />:</td>
			<td><springform:select path="flagId">
				<springform:options items="${flags}" itemValue="flagId" itemLabel="name" />
			</springform:select></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit"
				value="<spring:message code='patientflags.submit'/>" /></td>
		</tr>
	</table>
</springform:form></div>
</div>

<br />
&nbsp;
<c:if test="${!empty tags}">
<spring:message code="patientflags.or" />
<br />
<br />

<div><b class="boxHeader"><spring:message
	code="patientflags.findFlaggedPatients.byTagSet" /></b>
<div class="box"><springform:form modelAttribute="filter" method="get">
	<springform:errors path="*" cssClass="error" />
	<table>
		<tr>
			<td valign="top"><nobr><spring:message
				code="patientflags.findFlaggedPatients.selectTag" />:</nobr></td>
			<td><springform:select path="tags" multiple="true">
				<springform:options items="${tags}" itemValue="tagId" itemLabel="name" />
			</springform:select></td>
			<td align="left" valign="top"><nobr><springform:radiobutton
				path="type" value="ANYTAG" /> <spring:message
				code='patientflags.findFlaggedPatients.anyTag' /></nobr><br />
			<nobr><springform:radiobutton path="type" value="ALLTAGS" /> <spring:message
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
</springform:form></div>
</div>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>