import {API_CONTEXT_PATH} from '../apiContext';

var tableDataList=[]
var dataLoaded=false

export const GET_FLAGGEDPATIENTS_BEGIN   = 'GET_FLAGGEDPATIENTS_BEGIN';
export const GET_FLAGGEDPATIENTS_SUCCESS = 'GET_FLAGGEDPATIENTS_SUCCESS';
export const GET_FLAGGEDPATIENTS_FAILURE = 'GET_FLAGGEDPATIENTS_FAILURE';
export const UPDATE_FLAGGEDPATIENTS  = 'UPDATE_FLAGGEDPATIENTS';

export const getFlaggedPatientsBegin = () => ({
  type: GET_FLAGGEDPATIENTS_BEGIN 
});

export const getFlaggedPatientsSuccess = tableDataList => ({
  type: GET_FLAGGEDPATIENTS_SUCCESS,
  payload: { tableDataList }
});

export const getFlaggedPatientsFailure = error => ({
  type: GET_FLAGGEDPATIENTS_FAILURE,
  payload: { error }
});
export const updateFlaggedPatientsSuccess = tableDataList => ({
    type: UPDATE_FLAGGEDPATIENTS,
    payload: {tableDataList}
});

export function getFlaggedPatients(flag){
    return dispatch => {
        if(dataLoaded)
            return;
        dataLoaded=true;
        var url= API_CONTEXT_PATH+'/patientflags/eval?flag='+flag;
        console.log("Successful Entry");
        dispatch(getFlaggedPatientsBegin());
        fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(res => res.json())
        .then((data) => {
            var resultData = data['results'];
            for (var property in resultData){
                if(resultData.hasOwnProperty(property)){
                    console.log('Property',resultData[property]);
                    var priority= resultData[property].priority;
                    if(priority!==null)
                        resultData[property]["priority"]=resultData[property]["priority"]["name"];
                    else 
                        resultData[property]["priority"]="NONE";
                    var enabled = resultData[property].enabled;
                    if(enabled)
                        resultData[property]["enabled"]='Enabled';
                    else 
                        resultData[property]["enabled"]= 'Disabled';
                    tableDataList.push(resultData[property]);
                }     
            }
            dispatch(getFlaggedPatientsSuccess(tableDataList));
        })
        .catch(error => dispatch(getFlaggedPatientsFailure(error)));
    };
}