/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

// Because of the "import *" in redux-store each exported function will control a its own field in the global state

// For example this placeholder function will set your redux state to be { replaceThis: {} } upon the first action
import 'regenerator-runtime/runtime'
import { combineReducers } from "redux";
import priorities from "./reducers/priorityReducer";
import tags from "./reducers/tagReducer";
import flags from "./reducers/flagReducer";
import search from "./reducers/searchReducer";
import session from "./reducers/sessionReducer";
import  {reducers as openmrsReducer} from '@openmrs/react-components';
import {reducer as reduxFormReducer} from 'redux-form';

const rootReducer = combineReducers({
  priorities, tags,flags,search,session,
  openmrs:openmrsReducer,
  form:reduxFormReducer
});

export default rootReducer;