/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.patientflags.aop;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.Patient;

/**
 * Tracks transaction state to ensure generatePatientFlags is only called once per "transaction".
 * When a high-level method like saveEncounter is called, it marks the start of a transaction.
 * Lower-level methods (like saveObs) that are called within that transaction will defer
 * flag generation until the transaction completes.
 */
public class FlagGenerationTransactionTracker {
	
	private static final ThreadLocal<Boolean> inTransaction = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};
	
	private static final ThreadLocal<Set<Patient>> pendingPatients = new ThreadLocal<Set<Patient>>() {
		@Override
		protected Set<Patient> initialValue() {
			return ConcurrentHashMap.newKeySet();
		}
	};
	
	/**
	 * Marks the start of a transaction. Should be called when a high-level method
	 * like saveEncounter begins.
	 */
	public static void startTransaction() {
		inTransaction.set(true);
		pendingPatients.get().clear();
	}
	
	/**
	 * Marks the end of a transaction and returns the set of patients that need
	 * flag generation. Should be called when the high-level method completes.
	 * 
	 * @return the set of patients that need flag generation
	 */
	public static Set<Patient> endTransaction() {
		Set<Patient> patients = pendingPatients.get();
		inTransaction.set(false);
		pendingPatients.remove();
		return patients;
	}
	
	/**
	 * Checks if we're currently in a transaction.
	 * 
	 * @return true if in a transaction, false otherwise
	 */
	public static boolean isInTransaction() {
		return inTransaction.get() != null && inTransaction.get();
	}
	
	/**
	 * Adds a patient to the pending set if we're in a transaction, or returns
	 * the patient immediately if not in a transaction.
	 * 
	 * @param patient the patient that needs flag generation
	 * @return the patient if not in transaction (should generate flags now),
	 *         null if in transaction (flags will be generated later)
	 */
	public static Patient handlePatient(Patient patient) {
		if (patient == null) {
			return null;
		}
		
		if (isInTransaction()) {
			pendingPatients.get().add(patient);
			return null; // Defer flag generation
		} else {
			return patient; // Generate flags immediately
		}
	}
	
	/**
	 * Clears the transaction state. Should be called in a finally block to ensure
	 * cleanup even if an exception occurs.
	 */
	public static void clear() {
		inTransaction.remove();
		pendingPatients.remove();
	}
}

