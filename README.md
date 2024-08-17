openmrs-module-patientflags
==========================

The OpenMRS Patient Flag Module provides the functionality to generate warning flags /
notifications in the patient dashboard. A flag is simply some criteria, and an associated
message string. Current module computes the flags for a patient (or a group of patients) when
in form submission. Or do this as AOP for data submitted outside of forms and save the
precomputed results in the database table. Whenever a Patient Dashboard is loaded, it loads the
precomputed patient flags in the system, and for all flags that evaluate true, the associated
message string is displayed.
A flag contains properties such as flag type, display point, priority and tags.

User Guide
--------------

On the Administration page, the `Patient Flags` section provides options to `manage fags, manage tags, manage priorities,
manage flag display, and find flagged patients`.

Use the `manage flags` option to create new flags, and to edit and remove existing flags.
The Patient Flag Module provides the options to create four types of patient flags.
1. `Groovy Flag` - Groovy flag uses groovy script as its criteria. This script returns a cohort
   after evaluation of all patients which are evaluated as “true”.
2. `SQL flag` - These types of flags take a SQL query as its criteria. This returns all patients
   that match the specific criteria.
   Eg : get patients who have drug allergy
   SELECT a.patient_id FROM allergy a where a.allergen_type = "DRUG";
3. `Logical flag` - These flags take logical strings that are compatible with OPENMRS logics.
4. `Custom flag` - These flags defined using a custom flag evaluator that custom evaluator
   class should be implemented with the FlagEvaluator interface.

`Flag Priority` - The flag priority determines the order of the display of the flags in the display
points. and flags can be displayed in different colors using display styles. The display style
should be a valid attribute that can be used with a HTML <span> tag.

[Patient Flag Module wiki page](https://openmrs.atlassian.net/wiki/spaces/Archives/pages/25467777/Patient+Flags+Module#User-Guide)

Developer Guide
---------------

**Prerequisite**

* Java version ( 8, 11 or 17)
* Maven
#### Setup Project

1. Clone the project

    `git clone https://github.com/<username>/openmrs-module-patientflags.git
`

2. Build the project 

   `mvn clean install` in the root module package

Components Of The Module
------------------------

#### Back-end (api-module)

The backend of the flag module is built using Java, containing all the services required to manage flags and the flag 
module's REST API. The current flag module backend consists of five major aspects.

`Evaluator` : A flag is a message with a specific criteria. If a patient’s details match with the specified criteria
   module will display the corresponding message on the patient’s profile. Users could write these
   criteria using different languages and evaluators are used to check whether the patient's
   condition matches these criteria.

   * SQL evaluator - evaluate flags which use SQL query as criteria

   * Groovy evaluator - Flags which are using Groovy scripts as their criteria are evaluated
   using this evaluator. It executes the Groovy script to determine if the patient meets the
   defined conditions

   * Custom evaluator - For flags with custom or logic-based criteria, use a custom evaluator.

   * CQL evaluator - evaluate the flags which are use CQL script ( but currently this is not in
   use)
   
   
`Patient flag task` : This component is responsible for handling the generation of flags for patients, generating
flagged patients based on given flags, and evaluating all flags. It operates as a thread, managing
these tasks concurrently and efficiently.

`Encounter Service Advice` : When an encounter occurs, this service is invoked, triggering the start of the patient flag task
thread and initiating the evaluation of the patient's flags. This is the operating use of Access
Oriented programming.

`Flag Service` : This class is responsible for handling all the logic within the flags module and interacting with
the DAO layer to communicate with the database

`Dao layer` : This component provides functionality for saving, retrieving, and deleting flag records from the
database

#### OMOD Module

This module contains all JSP bases user interface and controllers. When build the project, the common `patientflags-<module 
version>.omod` file can be found under `/target` folder in OMOD module.


#### OWA Module - [OWA wiki](https://openmrs.atlassian.net/wiki/spaces/Archives/pages/25468124/Patient+Flags+Module+OWA)

The Patient Flags Module OWA is an Open Web Application built using React & React-Redux in order to act as a front end for 
the Rest API supported by the Patient Flags Module. The web application consumes the Patient Flags Module Web API and has 7 Components.


Issues 
------------
[Patient Flag Module JIRA project](https://openmrs.atlassian.net/jira/software/c/projects/FLAG/list)
