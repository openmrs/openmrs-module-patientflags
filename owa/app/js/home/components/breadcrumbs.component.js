import template from '../breadcrumbs.html';
import controller from '../controllers/breadcrumbs.controller.js';

let breadcrumbsComponent = {
  restrict: 'E',
  bindings: {},
  template: template,
  controller: controller,
  controllerAs: 'vm'
};

export default breadcrumbsComponent;
