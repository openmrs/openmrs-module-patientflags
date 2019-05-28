package org.openmrs.module.patientflags.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Patient;
import org.openmrs.module.patientflags.EvaluatedFlag;

public class PatientFlags extends Patient {
	
	private static final long serialVersionUID = 1L;
	
	private List<Map<String, Object>> flags;
	
	public PatientFlags() {
	}
	
	public PatientFlags(Patient patient, List<EvaluatedFlag> flags) {
		super(patient);
		this.setFlags(new ArrayList<>());
		
		for (EvaluatedFlag evaluatedFlag : flags) {
			Map<String, Object> map = new HashMap<>();
			map.put("flagMessage", evaluatedFlag.getFlagMessage());
			map.put("data", evaluatedFlag.getData());
			map.put("isFlagged", evaluatedFlag.getIsFlagged());
			map.put("flag", evaluatedFlag.getFlag());
			map.put("style", evaluatedFlag.getFlag().getPriority().getStyle());
			
			this.flags.add(map);
		}
	}
	
	public List<Map<String, Object>> getFlags() {
		return flags;
	}
	
	public void setFlags(List<Map<String, Object>> flags) {
		this.flags = flags;
	}
}
