"use strict";

import '../app/js/home/home';

describe('component: encounterComponent', () => {
  beforeEach(angular.mock.module('home'));

  let component, scope, $componentController, $httpBackend;

  let encounterList = {
    "results":[{
      "uuid": "6ff4d871-a529-4b88-9cb9-761cf3c35136",
      "display": "Vitals 31/12/2015",
      "links": [
        {
          "rel": "self",
          "uri": "http://localhost:8081/openmrs-standalone/ws/rest/v1/encounter/6ff4d871-a529-4b88-9cb9-761cf3c35136"
        }
      ]
    }]
  };

  beforeEach(inject((_$httpBackend_, $rootScope, _$componentController_) => {
    $httpBackend = _$httpBackend_;
    $httpBackend.whenGET(/translation.*/).respond();
    $httpBackend.whenGET('manifest.webapp').respond(500, "");
    $httpBackend.whenGET('/ws/rest/v1/encounter?includeAll=true&q=john&v=full').respond(encounterList);
    $httpBackend.whenGET('/ws/rest/v1/encounter?q=john').respond(encounterList);

    scope = $rootScope.$new();
    $componentController = _$componentController_;

  }));

  it('Rest call should return the required json file', () => {

    component = $componentController('encounterComponent',
      {
        $scope: scope
      }
    );

    component.query = 'john';
    component.onsearch();
    $httpBackend.flush();
    expect(component.encounters).toEqual(encounterList.results);

  })
});







