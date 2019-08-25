/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import 'regenerator-runtime/runtime'
import {createStore, applyMiddleware} from 'redux'
import promiseMiddleware from 'redux-promise-middleware';
import thunkMiddleware from 'redux-thunk';
import rootReducer from './reducers'
import { sagas as openmrsSagas} from '@openmrs/react-components';
import createSagaMiddleware from 'redux-saga';

export default function () {
  const sagaMiddleware= createSagaMiddleware();
  const store = createStore(rootReducer, {}, applyMiddleware(
    thunkMiddleware,
    sagaMiddleware,
    promiseMiddleware()
  ));
  sagaMiddleware.run(openmrsSagas);
  return store;
}
