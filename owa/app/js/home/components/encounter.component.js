import template from '../encounter.html';
import controller from '../controllers/encounter.controller';

let encounterComponent = {
  restrict: 'E',
  bindings: {},
  controller: controller,
  controllerAs: 'vm',
  template: template
};

export default encounterComponent;
