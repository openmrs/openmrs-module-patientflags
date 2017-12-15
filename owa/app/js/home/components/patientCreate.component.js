import template from '../patientCreate.html';
import controller from '../controllers/patientCreate.controller';

let patientCreateComponent = {
  restrict: 'E',
  bindings: {},
  controller: controller,
  controllerAs: 'vm',
  template: template
};

export default patientCreateComponent;
