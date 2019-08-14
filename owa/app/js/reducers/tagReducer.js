import {
    GET_TAGS_BEGIN,
    GET_TAGS_SUCCESS,
    GET_TAGS_FAILURE,
    UPDATE_TAGS,
  } from '../actions/tagActions';
  
  const initialState = {
    tableDataList:[],
    loading: false,
    error: null
  };
  
  export default function tagReducer(state = initialState, action) {
    switch(action.type) {
      case GET_TAGS_BEGIN:
        // Mark the state as "loading" so we can show a spinner
        return {
          ...state,
          loading: true,
          error: null
        };
  
      case GET_TAGS_SUCCESS:
        // All done: set loading "false".
        return {
          ...state,
          loading: false,
          tableDataList: action.payload.tableDataList
        };
  
      case GET_TAGS_FAILURE:
        // The request failed. It's done. So set loading to "false".
        // Since it failed, we don't have tableData to display anymore, so set `tableDatalist` to empty.
        return {
          ...state,
          loading: false,
          error: action.payload.error,
          tableDataList: []
        };

        case UPDATE_TAGS:
        return{
            ...state,
            loading: false,
            tableDataList: action.payload.tableDataList
        };
  
      default:
        return state;
    }
  }