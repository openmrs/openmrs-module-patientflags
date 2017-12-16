import template from './flags.html';
import controller from './flags.controller';

let flagsComponent = {
    restrict: 'E',
    bindings: {},
    template: template,
    controller: controller,
    controllerAs: 'vm'
};

export default flagsComponent;
