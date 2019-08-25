import {
    GET_FLAGGEDPATIENTS_BEGIN,
    GET_FLAGGEDPATIENTS_SUCCESS,
    GET_FLAGGEDPATIENTS_FAILURE,
    UPDATE_FLAGGEDPATIENTS,
  } from '../actions/searchActions';
  
  const initialState = {
    session:'',
    loading: false,
    error: null
  };
  
  export default function searchReducer(state = initialState, action) {
    switch(action.type) {
      case GET_FLAGGEDPATIENTS_BEGIN:
        // Mark the state as "loading" so we can show a spinner
        return {
          ...state,
          loading: true,
          error: null
        };
  
      case GET_FLAGGEDPATIENTS_SUCCESS:
        // All done: set loading "false".
        return {
          ...state,
          loading: false,
          session: action.payload.session
        };
  
      case GET_FLAGGEDPATIENTS_FAILURE:
        // The request failed. It's done. So set loading to "false".
        // Since it failed, we don't have tableData to display anymore, so set `session` to empty.
        return {
          ...state,
          loading: false,
          error: action.payload.error,
          session: []
        };

        case UPDATE_FLAGGEDPATIENTS:
        return{
            ...state,
            loading: false,
            error:action.payload.error, 
            session: action.payload.session
        };
  
      default:
        return state;
    }
  }