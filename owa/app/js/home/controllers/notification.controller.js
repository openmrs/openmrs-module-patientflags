class NotificationController {
  constructor(openmrsNotification) {
    "ngInject";
    var vm = this;
    vm.title = "";
    vm.content = "";
    vm.success = () => {
      openmrsNotification.success(vm.content, vm.title);
    }
    vm.error = () => {
      openmrsNotification.error(vm.content, vm.title);
    }
    vm.warning = () => {
      openmrsNotification.warning(vm.content, vm.title);
    }
    vm.info = () => {
      openmrsNotification.info(vm.content, vm.title);
    }
  }
}

export default NotificationController;
