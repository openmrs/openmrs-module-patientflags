package org.openmrs.module.patientflags.web.rest.util;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.webservices.rest.web.RequestContext;

public class WebUtils {
	
	public static final SimpleDateFormat GLOBAL_JAVA_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getStringFilter(String param, RequestContext req) {
		return (StringUtils.isNotBlank(req.getParameter(param))) ? req.getParameter(param) : null;
	}

	public static Boolean getBooleanFilter(String param, RequestContext req) {
		String strval = getStringFilter(param, req);
		return strval == null ? null : Boolean.parseBoolean(strval);
	}
}
