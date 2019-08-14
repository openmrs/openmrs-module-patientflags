import {
    GET_FLAGS_BEGIN,
    GET_FLAGS_SUCCESS,
    GET_FLAGS_FAILURE,
    UPDATE_FLAGS,
  } from '../actions/flagActions';
  
  const initialState = {
    tableDataList:[],
    loading: false,
    error: null
  };
  
  export default function flagReducer(state = initialState, action) {
    switch(action.type) {
      case GET_FLAGS_BEGIN:
        // Mark the state as "loading" so we can show a spinner
        return {
          ...state,
          loading: true,
          error: null
        };
  
      case GET_FLAGS_SUCCESS:
        // All done: set loading "false".
        return {
          ...state,
          loading: false,
          tableDataList: action.payload.tableDataList
        };
  
      case GET_FLAGS_FAILURE:
        // The request failed. It's done. So set loading to "false".
        // Since it failed, we don't have tableData to display anymore, so set `tableDatalist` to empty.
        return {
          ...state,
          loading: false,
          error: action.payload.error,
          tableDataList: []
        };

        case UPDATE_FLAGS:
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