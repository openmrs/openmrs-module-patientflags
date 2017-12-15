import angular from 'angular';
import uiRouter from 'angular-ui-router';
import breadcrumbsComponent from './components/breadcrumbs.component.js';
import breadcrumbsPatientCreateComponent from './components/breadcrumbsPatientCreate.component';
import translateComponent from './components/translate.component.js';
import headerComponent from './components/header.component.js';
import notificationComponent from './components/notification.component.js';
import patientSearchComponent from './components/patientSearch.component.js';
import encounterComponent from './components/encounter.component.js';
import observationComponent from './components/observation.component.js';
import providerComponent from './components/provider.component.js';
import patientCreateComponent from './components/patientCreate.component.js';
import uicommons from 'openmrs-contrib-uicommons';

let homeModule = angular.module('home', [ uiRouter, 'openmrs-contrib-uicommons'])
    .config(($stateProvider, $urlRouterProvider) => {
        "ngInject";
        $urlRouterProvider.otherwise('/');

        $stateProvider.state('home', {
            url: '/',
            template: require('./home.html')
        });

        $stateProvider.state('createPatient', {
        url: '/create-patient',
        template: require('./patientCreatePage.html')
        })
    })
    .config(['$qProvider', function ($qProvider) {
      $qProvider.errorOnUnhandledRejections(false);
    }])

    // To prevent adding Hash bangs(#!/) instead of simple hash(#/) in Angular >1.5

    .config(['$locationProvider', function($locationProvider) {
      $locationProvider.hashPrefix('');
    }])

    .component('breadcrumbsComponent', breadcrumbsComponent)
    .component('breadcrumbsPatientCreateComponent', breadcrumbsPatientCreateComponent)
    .component('translateComponent', translateComponent)
    .component('headerComponent', headerComponent)
    .component('notificationComponent', notificationComponent)
    .component('patientSearchComponent', patientSearchComponent )
    .component('encounterComponent', encounterComponent)
    .component('observationComponent', observationComponent)
    .component('providerComponent', providerComponent)
    .component('patientCreateComponent', patientCreateComponent );

export default homeModule;
