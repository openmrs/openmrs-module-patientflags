package org.openmrs.module.patientflags;

import java.util.List;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;

/**
 * This class reprents the flag status for a patient after evaluation
 * 
 * @author MAIMOONA
 */
public class EvaluatedFlag extends BaseOpenmrsData {
	
	private Boolean isFlagged;
	
	private Patient patient;
	
	private Flag flag;
	
	private String flagMessage;
	
	private List<Object> data;
	
	public EvaluatedFlag(Boolean isFlagged, Patient patient, Flag flag, String flagMessage, List<Object> list) {
		this.isFlagged = isFlagged;
		this.patient = patient;
		this.flag = flag;
		this.flagMessage = flagMessage;
		this.data = list;
	}
	
	public Boolean getIsFlagged() {
		return isFlagged;
	}
	
	public void setIsFlagged(Boolean isFlagged) {
		this.isFlagged = isFlagged;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public Flag getFlag() {
		return flag;
	}
	
	public void setFlag(Flag flag) {
		this.flag = flag;
	}
	
	public String getFlagMessage() {
		return flagMessage;
	}
	
	public void setFlagMessage(String flagMessage) {
		this.flagMessage = flagMessage;
	}
	
	public List<Object> getData() {
		return data;
	}
	
	public void setData(List<Object> data) {
		this.data = data;
	}
	
	@Override
	public Integer getId() {
		return flag.getFlagId();
	}
	
	@Override
	public void setId(Integer arg0) {
		
	}
	
}
