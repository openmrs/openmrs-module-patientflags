import angular from 'angular';
import uiRouter from 'angular-ui-router';
import flagsComponent from './flags.component.js';
import uicommons from 'openmrs-contrib-uicommons';

let flagsModule = angular.module('flags', [ uiRouter, 'openmrs-contrib-uicommons'])
    .config(($stateProvider) => {
        $stateProvider.state('createFlag', {
            url: '/create-flag',
            template: require('./flagCreatePage.html')
        });
    })
    .config(['$qProvider', function ($qProvider) {
        $qProvider.errorOnUnhandledRejections(false);
    }])
    .component('flagsComponent', flagsComponent);

export default flagsModule;
