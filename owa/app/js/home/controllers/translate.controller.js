class TranslateController {
  constructor($translate) {
    "ngInject";
    var vm = this;

    vm.changeLanguage = (langKey) => {
      return $translate.use(langKey);
    };
  }
}

export default TranslateController;
