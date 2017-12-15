"use strict";

import '../app/js/home/home';

describe('component: observationComponent', () => {
  beforeEach(angular.mock.module('home'));

  let component, scope, $componentController, $httpBackend;

  let observationList = {
    "results":[{
      "uuid": "f2b553a0-8e33-4396-a19f-3b434934a0c5",
      "display": "Diagnosis order: Primary",
      "links": [
        {
          "rel": "self",
          "uri": "http://localhost:8081/openmrs-standalone/ws/rest/v1/obs/f2b553a0-8e33-4396-a19f-3b434934a0c5"
        }
      ]
    }]
  };

  beforeEach(inject((_$httpBackend_, $rootScope, _$componentController_) => {
    $httpBackend = _$httpBackend_;
    $httpBackend.whenGET(/translation.*/).respond();
    $httpBackend.whenGET('manifest.webapp').respond(500, "");
    $httpBackend.whenGET('/ws/rest/v1/obs?includeAll=true&q=mark&v=full').respond(observationList);
    $httpBackend.whenGET('/ws/rest/v1/obs?q=mark').respond(observationList);

    scope = $rootScope.$new();
    $componentController = _$componentController_;

  }));

  it('Rest call should return the required json file', () => {

    component = $componentController('observationComponent',
      {
        $scope: scope
      }
    );

    component.query = 'mark';
    component.onsearch();
    $httpBackend.flush();
    expect(component.observations).toEqual(observationList.results);

  })
});







