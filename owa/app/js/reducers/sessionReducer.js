import {
    GET_SESSION_BEGIN,
    GET_SESSION_SUCCESS,
    GET_SESSION_FAILURE,
    UPDATE_SESSION,
  } from '../actions/flagActions';
  
  const initialState = {
    session:[],
    loading: false,
    error: null,
    loginLocations:[],
    header:{
        headerLogoLinks:[]
    }
  };
  
  export default function sessionReducer(state = initialState, action) {
    switch(action.type) {
      case GET_SESSION_BEGIN:
        // Mark the state as "loading" so we can show a spinner
        return {
          ...state,
          loading: true,
          error: null
        };
  
      case GET_SESSION_SUCCESS:
        // All done: set loading "false".
        return {
          ...state,
          loginLocations: [],
          session: action.payload.session
        };
  
      case GET_SESSION_FAILURE:
        // The request failed. It's done. So set loading to "false".
        // Since it failed, we don't have tableData to display anymore, so set `session` to empty.
        return {
          ...state,
          loading: false,
          error: action.payload.error,
          session: []
        };

        case UPDATE_SESSION:
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