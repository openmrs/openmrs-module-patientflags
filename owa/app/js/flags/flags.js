import angular from 'angular';
import uiRouter from 'angular-ui-router';
import flagsComponent from './flags.component.js';
import uicommons from 'openmrs-contrib-uicommons';

let flagsModule = angular.module('flags', [ uiRouter, 'openmrs-contrib-uicommons'])
    .component('flags', flagsComponent);
