class PatientSearchController {
  /* @ngInject */
  constructor(openmrsRest) {
    var vm = this;
    vm.patients = [];
    vm.IsHidden = true;
    vm.loading = true;
    vm.notFound = false;
    vm.onsearch = () => {
      if (vm.query.length > 2) {
        vm.loading = false;
        vm.notFound = true;
        vm.IsHidden = true;
        openmrsRest.getFull('patient', {q: vm.query, includeAll: true}).then(function (response) {
          if (response.results.length > 0) {
            vm.loading = true;
            vm.IsHidden = false;
            vm.notFound = true;
            vm.patients = response.results;
          }
          else {
            vm.loading = true;
            vm.IsHidden = true;
            vm.notFound = false;
          }
        })
      }
      else {
        vm.IsHidden = true;
        vm.loading = true;
        vm.notFound = false;
      }
    }

  }
}
export default PatientSearchController;
