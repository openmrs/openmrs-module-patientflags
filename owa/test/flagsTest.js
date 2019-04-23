'use strict';

import '../app/js/flags/flags';

describe('component: flagsComponent', () => {

    let component, $componentController, $httpBackend;

    let patientFlags = {
        "results": [
            {
                "uuid": "a1e42c2c-a0d1-4386-8e1b-b936461c0d1d",
                "display": "All",
                "links": [
                    {
                        "rel": "self",
                        "uri": "http://localhost:8081/openmrs/ws/rest/v1/patientflags/flag/a1e42c2c-a0d1-4386-8e1b-b936461c0d1d"
                    }
                ],
                "tags": [
                    {
                        "name": "Le Tag",
                    },
                    {
                        "name": "Tag 2",
                    }
                ],
                "enabled": true
            }
        ]
    };

    beforeEach(angular.mock.module('flags'));

    beforeEach(inject((_$httpBackend_, _$componentController_) => {
        $componentController = _$componentController_;
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('manifest.webapp').respond(500, "");
        $httpBackend.whenGET('/ws/rest/v1/patientflags/flag?v=full').respond(patientFlags);
    }));

    it('should load patient flagsComponent', () => {

        component = $componentController('flagsComponent');
        $httpBackend.flush();

        expect(component.flags).toEqual([
            {
                "uuid": "a1e42c2c-a0d1-4386-8e1b-b936461c0d1d",
                "display": "All",
                "links": [
                    {
                        "rel": "self",
                        "uri": "http://localhost:8081/openmrs/ws/rest/v1/patientflags/flag/a1e42c2c-a0d1-4386-8e1b-b936461c0d1d"
                    }
                ],
                "tags": "Le Tag Tag 2",
                "status": "Enabled",
                "enabled": true
            }
        ])
    });
});
