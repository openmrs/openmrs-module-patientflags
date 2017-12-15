import template from '../translate.html';
import controller from '../controllers/translate.controller.js';

let translateComponent = {
  restrict: 'E',
  bindings: {},
  template: template,
  controller: controller,
  controllerAs: 'vm'
};

export default translateComponent;
