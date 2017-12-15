"use strict";

import '../app/js/home/home';

describe('component: patientSearchComponent', () => {
  beforeEach(angular.mock.module('home'));

  let component, scope, $componentController, $httpBackend;

  let patientList = {
  "results":[{
    "uuid": "c61195ff-0e93-4799-bd49-3c1738d9c34f",
    "display": "1002C4 - John Sánchez",
    "links": [
      {
        "rel": "self",
        "uri": "http://localhost:8081/openmrs-standalone/ws/rest/v1/patient/c61195ff-0e93-4799-bd49-3c1738d9c34f"
      }
    ]
  }]
  };

  beforeEach(inject((_$httpBackend_, $rootScope, _$componentController_) => {
    $httpBackend = _$httpBackend_;
    $httpBackend.whenGET(/translation.*/).respond();
    $httpBackend.whenGET('manifest.webapp').respond(500, "");
    $httpBackend.whenGET('/ws/rest/v1/patient?includeAll=true&q=john&v=full').respond(patientList);
    $httpBackend.whenGET('/ws/rest/v1/patient?q=john').respond(patientList);

    scope = $rootScope.$new();
    $componentController = _$componentController_;

  }));

  it('Rest call should return the required json file', () => {

    component = $componentController('patientSearchComponent',
      {
        $scope: scope
      }
      );

    component.query = 'john';
    component.onsearch();
    $httpBackend.flush();
    expect(component.patients).toEqual(patientList.results);

  })
});







