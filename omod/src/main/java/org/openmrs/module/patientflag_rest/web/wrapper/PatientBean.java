/*
 * MozView Technologies, Lda. 2010 - 2016
 */
package org.openmrs.module.patientflag_rest.web.wrapper;

import java.io.Serializable;

/**
 * @author Stélio Moiane
 *
 */
public class PatientBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String uuid;

	private String name;

	public PatientBean(final String uuid, final String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public String getUuid() {
		return this.uuid;
	}

	public String getName() {
		return this.name;
	}
}
