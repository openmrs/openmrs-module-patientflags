import template from '../notification.html';
import controller from '../controllers/notification.controller.js';

let notificationComponent = {
  restrict: 'E',
  bindings: {},
  template: template,
  controller: controller,
  controllerAs: 'vm'
};

export default notificationComponent;
