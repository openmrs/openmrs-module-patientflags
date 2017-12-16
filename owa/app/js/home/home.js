import angular from 'angular';
import uiRouter from 'angular-ui-router';
import breadcrumbsComponent from './components/breadcrumbs.component.js';
import headerComponent from './components/header.component.js';
import flagsComponent from '../flags/flags.component';
import flagsCreateComponent from '../flags/flagsCreate.component';
import uicommons from 'openmrs-contrib-uicommons';

let homeModule = angular.module('home', [ uiRouter, 'openmrs-contrib-uicommons'])
    .config(($stateProvider, $urlRouterProvider) => {
        "ngInject";
        $urlRouterProvider.otherwise('/');

        $stateProvider.state('home', {
            url: '/',
            template: require('./home.html')
        });

        $stateProvider.state('createFlag', {
            url: '/create-flag',
            template: require('../flags/flagCreatePage.html')
        });
    })
    .config(['$qProvider', function ($qProvider) {
      $qProvider.errorOnUnhandledRejections(false);
    }])

    // To prevent adding Hash bangs(#!/) instead of simple hash(#/) in Angular >1.5

    .config(['$locationProvider', function($locationProvider) {
      $locationProvider.hashPrefix('');
    }])

    .component('breadcrumbsComponent', breadcrumbsComponent)
    .component('headerComponent', headerComponent)
    .component('flags', flagsComponent)
    .component('flagsCreateComponent', flagsCreateComponent);

export default homeModule;
