/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import React from 'react';
import {Route, Router} from 'react-router';
//import App from './components/App';
import Tag from './components/Tag';
import EditTags from './components/Modals/EditTags';
import Priority from './components/Priority';
import EditPriorities from './components/Modals/EditPriorities';
import App from './components/App'


export default (store) => {
  // combine store and onEnter if you need to fire an action when going to a route. Example:
  //   onEnter={ (nextState) => {store.dispatch(loadPatientAction(nextState.params.patientUuid)} }

  return (
     <Router>
       <Route path="/" component={App}></Route>
        <Route path="/Tags" component={Tag}></Route>
        <Route path="/Priority" component= {Priority}></Route>
        <Route path='/EditTags' component={EditTags}></Route>
        <Route path='/EditPriorities' component={EditPriorities}></Route>
      </Router>  
  );
}
