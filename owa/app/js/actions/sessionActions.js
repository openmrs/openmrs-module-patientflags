import {API_CONTEXT_PATH} from '../apiContext';

var session;
var dataLoaded=false

export const GET_SESSION_BEGIN   = 'GET_SESSION_BEGIN';
export const GET_SESSION_SUCCESS = 'GET_SESSION_SUCCESS';
export const GET_SESSION_FAILURE = 'GET_SESSION_FAILURE';
export const UPDATE_SESSION  = 'UPDATE_SESSION';

export const getSessionBegin = () => ({
  type: GET_SESSION_BEGIN 
});

export const getSessionSuccess = session => ({
  type: GET_SESSION_SUCCESS,
  payload: { session }
});

export const getSessionFailure = error => ({
  type: GET_SESSION_FAILURE,
  payload: { error }
});


export function getSession(){
    return dispatch => {
        if(dataLoaded)
            return;
        dataLoaded=true;
        var url= API_CONTEXT_PATH+'/appui/session';
        console.log("Successful Entry");
        dispatch(getSessionBegin());
        fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(res => res.json())
        .then((data) => {
            console.log(data);
            session=data;
            dispatch(getSessionSuccess(session));
        })
        .catch(error => dispatch(getSessionFailure(error)));
    };
}