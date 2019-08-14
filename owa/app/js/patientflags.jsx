/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import React from 'react';
import {render} from 'react-dom';
import {Router, Route, hashHistory} from 'react-router'
import {Provider} from 'react-redux'
import createStore from './redux-store'
import routes from './routes'
import {Header} from '@openmrs/react-components'
let store = createStore();

render((
         <Provider store={store}>
           <Router history={hashHistory}>
             {routes(store)}
           </Router>
         </Provider>
       ), document.getElementById('app'));

       render((
        <Header store={store}/>
      ), document.getElementById('nav'));
