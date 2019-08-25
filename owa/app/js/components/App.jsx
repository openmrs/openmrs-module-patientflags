/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import React from 'react';
import {Switch, Route, Link,BrowserRouter} from 'react-router-dom';
import Tag from './Tag';
import Flag from './Flag';
import Priority from './Priority';
import FindFlaggedPatients from './FindFlaggedPatients';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import {Tabs as OpenMRSTabs} from "@openmrs/react-components";


export default class App extends React.Component {
  render() {
    return (
      <div>
        <h1>Patient Flags</h1>
        <OpenMRSTabs>
          <div label="Manage Flags">
            <Flag/>
          </div>
          <div label="Manage Tags">
            <Tag/>
          </div>
          <div label="Manage Priorities">
            <Priority/>
          </div>
          <div label="Find Flagged Patients">
            <FindFlaggedPatients/>
          </div>
        </OpenMRSTabs>
      </div>
    )
  }
}
