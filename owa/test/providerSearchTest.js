"use strict";

import '../app/js/home/home';

describe('component: providerComponent', () => {
  beforeEach(angular.mock.module('home'));

  let component, scope, $componentController, $httpBackend;

  let providerList = {
    "results":[{
      "uuid": "b312bef3-f221-4189-ae4a-3bbc2e90a683",
      "display": "clerk - John Smith",
      "links": [
        {
          "rel": "self",
          "uri": "http://localhost:8081/openmrs-standalone/ws/rest/v1/provider/b312bef3-f221-4189-ae4a-3bbc2e90a683"
        }
      ]
    }]
  };

  beforeEach(inject((_$httpBackend_, $rootScope, _$componentController_) => {
    $httpBackend = _$httpBackend_;
    $httpBackend.whenGET(/translation.*/).respond();
    $httpBackend.whenGET('manifest.webapp').respond(500, "");
    $httpBackend.whenGET('/ws/rest/v1/provider?includeAll=true&q=john&v=full').respond(providerList);
    $httpBackend.whenGET('/ws/rest/v1/provider?q=john').respond(providerList);

    scope = $rootScope.$new();
    $componentController = _$componentController_;

  }));

  it('Rest call should return the required json file', () => {

    component = $componentController('providerComponent',
      {
        $scope: scope
      }
    );

    component.query = 'john';
    component.onsearch();
    $httpBackend.flush();
    expect(component.providers).toEqual(providerList.results);

  })
});







