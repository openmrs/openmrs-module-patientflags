class PatientCreateController {
  /* @ngInject */
  constructor(openmrsRest, $http, openmrsContext, $window) {
    var vm = this;
    vm.locations = [];
    vm.identifierTypes = [];
    vm.loading = true;
    vm.errorPerson = true;
    vm.blankPerson = true;
    vm.errorAge = true;
    vm.statusPatient = true;
    vm.errorPatient = true;
    vm.loadingPatient = true;
    vm.loadingIdentifier = true;
    vm.isHiddenPerson = false;
    vm.isHiddenPatient = true;
    vm.addressHidden = true;
    vm.invalidGivenName = false;
    vm.invalidFamilyName = false;
    vm.invalidAge = false;
    vm.blankIdentifier = false;
    vm.blankOpenmrsIdentifier = false;
    vm.blankOldIdentifier = false;
    vm.personData = {
      "gender": "Male",
      "age": "",
      "names": [
        {
          "givenName":"",
          "middleName": "",
          "familyName":""
        }],
      "addresses": [
        {
          "address1": "",
          "address2": "",
          "cityVillage": "",
          "stateProvince": "",
          "country": ""
        }
      ]
    };

    // for old identification number and old identification number as identifier type
    vm.oldPatientData = {

      "identifiers": [{
        "identifierType": "", // OpenMRS ID
        "identifier": "", // valid Luhn mod 30 check digit
        "location": "" // Default Location
      },
      {
        "identifierType": "", // Old OpenMRS Identifier
        "identifier": "", // valid according to https://psb.re/luhn
        "location": "" // Default Location
      }],
      "person": ""
    };

    // for OpenMRS ID as the identifier type
    vm.patientData = {

      "identifiers": [{
        "identifierType": "", // OpenMRS ID
        "identifier": "", // valid Luhn mod 30 check digit
        "location": "" // Default Location
      }],
      "person": ""
    };

    openmrsRest.getFull('location').then(function (response) {
      vm.locations = response.results;
      // for the default select options
      vm.selectedLocation = vm.locations[0].uuid;
      vm.patientData.identifiers[0].location = vm.locations[0].uuid;
      vm.oldPatientData.identifiers[0].location = vm.locations[0].uuid;
      vm.oldPatientData.identifiers[1].location = vm.locations[0].uuid;
    });

    openmrsRest.getFull('patientidentifiertype').then(function (response) {
      vm.identifierTypes = response.results;
      // for the default select options
      vm.selectedIdentifier = vm.identifierTypes[0].uuid;
      vm.patientData.identifiers[0].identifierType = vm.identifierTypes[0].uuid;
      vm.oldPatientData.identifiers[0].identifierType = vm.identifierTypes[0].uuid;
    });

    vm.validations = () => {
      vm.givenName = () =>{
        if (vm.personData.names[0].givenName.length > 0)
          return true;
      };
      vm.familyName = () =>{
        if (vm.personData.names[0].familyName.length > 0)
          return true;
      };
      vm.age = () =>{
        if (isNaN(vm.personData.age) || vm.personData.age < 1 || vm.personData.age > 100)
          return true;
      };
      vm.identifier = () => {
        if(vm.patientData.identifiers[0].identifier.length >0) {
          return true;
        }
      };
      vm.openmrsIdentifier = () => {
        if(vm.oldPatientData.identifiers[0].identifier.length >0) {
          return true;
        }
      };
      vm.oldIdentifier = () => {
        if(vm.oldPatientData.identifiers[1].identifier.length >0) {
          return true;
        }
      };
    };

    vm.oncreatePerson = () => {
      vm.errorPerson = true;
      vm.blankPerson = true;
      vm.errorAge = true;
      vm.loading = false;
      vm.errorPatient = true;
      vm.statusPatient = true;
      vm.patientData.identifiers[0].identifier = "";
      vm.oldPatientData.identifiers[0].identifier = "";
      vm.oldPatientData.identifiers[1].identifier = "";
      vm.selectedIdentifier = vm.identifierTypes[0].uuid;
      vm.patientData.identifiers[0].identifierType = vm.identifierTypes[0].uuid;
      vm.validations();
      if(!vm.givenName()) {
        vm.invalidGivenName = true;
      }
      if(!vm.familyName()) {
        vm.invalidFamilyName = true;
      }
      if(vm.age()) {
        vm.invalidAge = true;
      }
      if(vm.givenName() && vm.familyName()) {
        if(vm.age()) {
          vm.loading = true;
          vm.errorAge = false;
        }
        else {
          openmrsRest.create('person', vm.personData).then(function successCallback(response) {
            vm.uuidPerson = response.uuid;
            vm.patientData.person = vm.uuidPerson;
            vm.oldPatientData.person = vm.uuidPerson;
            vm.loading = true;
            vm.isHiddenPerson = true;
            vm.isHiddenPatient = false;
            vm.identifiersPart = false;
            vm.oldIdentifiersPart = true;
          }, function errorCallback(response) {
            vm.loading = true;
            vm.errorPerson = false;
          });
        }
      }
      else {
        vm.loading = true;
        vm.blankPerson = false;
      }
    };

    vm.addressFields = () => {
      if(vm.addressHidden) {
        vm.addressHidden = false;
      }
      else {
        vm.addressHidden = true;
      }
    };

    vm.generateIdentifier = () => {
      vm.platform = openmrsContext.getConfig().href;
      vm.getContextPath = $window.location.origin;
      vm.loadingIdentifier = false;
      $http.get(vm.getContextPath + vm.platform +'/module/idgen/generateIdentifier.form?source=1').
      then(function successCallback(response) {
        vm.generate_identifier = response.data;
        vm.patientData.identifiers[0].identifier = vm.generate_identifier.identifiers[0];
        vm.oldPatientData.identifiers[0].identifier = vm.generate_identifier.identifiers[0];
        vm.loadingIdentifier = true;
        vm.blankIdentifier = false;
        vm.blankOpenmrsIdentifier = false;
      }, function errorCallback(response) {
        console.log('An Error occurred with status : ' + response.status);
        vm.loadingIdentifier = true;
      });
    };

    vm.back = () => {
      vm.isHiddenPatient = true;
      vm.isHiddenPerson = false;
    };

    vm.newPatient = () => {
      vm.personData.names[0].givenName = "";
      vm.personData.names[0].middleName = "";
      vm.personData.names[0].familyName = "";
      vm.personData.age = "";
      vm.personData.addresses[0].address1 = "";
      vm.personData.addresses[0].address2 = "";
      vm.personData.addresses[0].cityVillage = "";
      vm.personData.addresses[0].stateProvince = "";
      vm.personData.addresses[0].country = "";
      vm.isHiddenPatient = true;
      vm.isHiddenPerson = false;
    };

    vm.oncreatePatient = () => {
      vm.statusPatient = true;
      vm.errorPatient = true;
      vm.loadingPatient = false;
      vm.query = [];

      if(!vm.identifiersPart) {
        vm.query = vm.patientData;
        if(!vm.identifier()) {
          vm.blankIdentifier = true;
        }
      }
      else {
        vm.query = vm.oldPatientData;
        if(!vm.openmrsIdentifier()) {
          vm.blankOpenmrsIdentifier = true;
        }
        if(!vm.oldIdentifier()) {
          vm.blankOldIdentifier = true;
        }
      }

      openmrsRest.create('patient', vm.query).then(function successCallback(response) {
        vm.loadingPatient = true;
        vm.statusPatient = false;
       }, function errorCallback(response) {
        vm.loadingPatient = true;
        vm.errorMessage = response.data.error.message;
        vm.globalErrorMessage = response.data.error.globalErrors[0].message;
        vm.errorPatient = false;
      });
    };

    vm.onSelectLocation = () => {
      vm.patientData.identifiers[0].location = vm.selectedLocation;
      vm.oldPatientData.identifiers[0].location = vm.selectedLocation;
      vm.oldPatientData.identifiers[1].location = vm.selectedLocation;
    };
    vm.onSelectIdentifier = () => {
      if(vm.selectedIdentifier == vm.identifierTypes[0].uuid) {
        vm.identifiersPart = false;
        vm.oldIdentifiersPart = true;
      }
      else {
        vm.identifiersPart = true;
        vm.oldIdentifiersPart = false;
      }
      vm.patientData.identifiers[0].identifierType = vm.selectedIdentifier;
      vm.oldPatientData.identifiers[1].identifierType = vm.selectedIdentifier;
    };
    vm.onSelectGender = () => {
      vm.personData.gender = vm.selectedGender;
    }
  }
}

export default PatientCreateController;
