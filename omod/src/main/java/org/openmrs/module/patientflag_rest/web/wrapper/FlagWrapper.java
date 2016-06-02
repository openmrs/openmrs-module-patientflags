package org.openmrs.module.patientflag_rest.web.wrapper;

import java.io.Serializable;
import java.util.List;

import org.openmrs.BaseOpenmrsData;

/**
 * @author Stélio Moiane
 *
 */
public class FlagWrapper extends BaseOpenmrsData implements Serializable {

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	private List<FlagBean> flags;

	private PatientBean patient;

	public FlagWrapper(final List<FlagBean> flags) {
		this.flags = flags;
	}

	@Override
	public Integer getId() {
		return this.patient.getId();
	}

	@Override
	public void setId(final Integer id) {
		this.patient.setId(id);
	}

	public PatientBean getPatient() {
		return this.patient;
	}

	public void setPatient(final PatientBean patient) {
		this.patient = patient;
	}

	public List<FlagBean> getFlags() {
		return this.flags;
	}
}
