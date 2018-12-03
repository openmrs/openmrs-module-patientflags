package org.openmrs.module.patientflags.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.EvaluatedFlag;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.FlagValidationResult;

public class TestOverDueFlagEvaluator implements FlagEvaluator {
	
	public static void main(String[] args) {
		System.out.println(Duration.standardDays(3).toString());
	}
	
	@Override
	public EvaluatedFlag eval(Flag flag, Patient patient) {
		List<Object> data = new ArrayList<>();
		List<Obs> reviewObsL = Context.getObsService().getObservations(Arrays.asList(patient), null, 
				Arrays.asList(new Concept(7000001)), null, null, null, null, null, null, null, null, false);
		for (Obs o : reviewObsL) {
			// for all review types
			//if(o.getConcept().getConceptId() == 7000001) {
				Obs duration = null;
				Obs frequency = null;
				
				// if concepts are encapsulated under this obs otherwise those must be under parent obs
				if(o.hasGroupMembers()) {
					for (Obs ogm : o.getGroupMembers()) {
						if(ogm.getConcept().getConceptId() == 7000003) {
							frequency = ogm;
						}
						else if(ogm.getConcept().getConceptId() == 7000002) {
							duration = ogm;
						}
					}
				}
				else if(o.getObsGroup().hasGroupMembers()) {
					for (Obs ogm : o.getObsGroup().getGroupMembers()) {
						if(ogm.getConcept().getConceptId() == 7000003) {
							frequency = ogm;
						}
						else if(ogm.getConcept().getConceptId() == 7000002) {
							duration = ogm;
						}
					}
				}
				
				if(duration != null && frequency != null) {
					int dur = (int) (duration.getValueNumeric() * 12);
					int freq = frequency.getValueNumeric().intValue();
					
					Date startDate = o.getEncounter().getEncounterDatetime();//  TODO change to obs datetime
					DateTime endDate = new DateTime(startDate).plusMonths(dur);
					
					// init with first review date
					DateTime reviewDate = new DateTime(startDate).plusMonths(freq);
					DateTime reviewExpiryDate = new DateTime(reviewDate).plusMonths(freq);
					int reviewElapsedDays = Days.daysBetween(reviewDate, DateTime.now()).getDays();
					
					// review date has passed
					while (!reviewDate.isAfter(endDate) && reviewElapsedDays > 0) {
						List<Obs> reviewResult = Context.getObsService().getObservations(Arrays.asList(patient), null, 
								Arrays.asList(o.getValueCoded()), null, null, null, null, null, null, 
								reviewDate.toDate(), reviewExpiryDate.toDate(), false);
						// no result submitted for this within required period
						if(reviewResult.size() == 0) {
							// pushing data for each test for message evaluation
							String elapsedDuration = reviewElapsedDays <= 6
									?(reviewElapsedDays +" day(s)")
									:((reviewElapsedDays/7) +" week(s)");
							data.add(new Object[] {o.getValueCoded().getName().getName(), elapsedDuration});
						}
						
						// update review date for next iteration
						reviewDate = new DateTime(reviewDate).plusMonths(freq);
						reviewExpiryDate = new DateTime(reviewDate).plusMonths(freq);
						reviewElapsedDays = Days.daysBetween(reviewDate, DateTime.now()).getDays();
					}
				}
			//}
		}
		
		if(data.size() > 0) {
			return new EvaluatedFlag(true, patient, flag, flag.evalMessage(patient, data.toArray()), data);
		}
		else {
			return new EvaluatedFlag(false, patient, flag, "", data);
		}
	}
	
	@Override
	public Cohort evalCohort(Flag flag, Cohort cohort) {
		return null;
	}
	
	@Override
	public FlagValidationResult validate(Flag flag) {
		return new FlagValidationResult(true);
	}
	
	@Override
	public String evalMessage(Flag flag, int patientId) {
		return flag.getMessage();
	}
	
}
