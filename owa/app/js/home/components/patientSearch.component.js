import template from '../patientSearch.html';
import controller from '../controllers/patientSearch.controller';

let patientSearchComponent = {
  restrict: 'E',
  bindings: {},
  controller: controller,
  controllerAs: 'vm',
  template: template
};

export default patientSearchComponent;
