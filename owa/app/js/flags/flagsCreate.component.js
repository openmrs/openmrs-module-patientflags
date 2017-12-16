import template from './flagsCreate.html';
import controller from './flags.controller';

let flagsCreateComponent = {
    restrict: 'E',
    bindings: {},
    template: template,
    controller: controller,
    controllerAs: 'vm'
};

export default flagsCreateComponent;
