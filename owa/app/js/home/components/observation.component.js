import template from '../observation.html';
import controller from '../controllers/observation.controller';

let observationComponent = {
  restrict: 'E',
  bindings: {},
  controller: controller,
  controllerAs: 'vm',
  template: template
};

export default observationComponent;
