import {
    GET_PRIORITIES_BEGIN,
    GET_PRIORITIES_SUCCESS,
    GET_PRIORITIES_FAILURE,
    UPDATE_PRIORITIES,
  } from '../actions/priorityActions';
  
  const initialState = {
    tableDataList:[],
    loading: false,
    error: null
  };
  
  export default function priorityReducer(state = initialState, action) {
    switch(action.type) {
      case GET_PRIORITIES_BEGIN:
        // Mark the state as "loading" so we can show a spinner
        return {
          ...state,
          loading: true,
          error: null
        };
  
      case GET_PRIORITIES_SUCCESS:
        // All done: set loading "false".
        return {
          ...state,
          loading: false,
          tableDataList: action.payload.tableDataList
        };
  
      case GET_PRIORITIES_FAILURE:
        // The request failed. It's done. So set loading to "false".
        // Since it failed, we don't have tableData to display anymore, so set `tableDatalist` to empty.
        return {
          ...state,
          loading: false,
          error: action.payload.error,
          tableDataList: []
        };

        case UPDATE_PRIORITIES:
        return{
            ...state,
            loading: false,
            error:action.payload.error, 
            tableDataList: action.payload.tableDataList
        };
  
      default:
        return state;
    }
  }