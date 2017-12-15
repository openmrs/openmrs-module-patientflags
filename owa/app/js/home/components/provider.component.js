import template from '../provider.html';
import controller from '../controllers/provider.controller';

let providerComponent = {
  restrict: 'E',
  bindings: {},
  controller: controller,
  controllerAs: 'vm',
  template: template
};

export default providerComponent;
