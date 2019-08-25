import {API_CONTEXT_PATH} from '../apiContext';

var tableDataList=[]
var dataLoaded=false

export const GET_PRIORITIES_BEGIN   = 'GET_PRIORITIES_BEGIN';
export const GET_PRIORITIES_SUCCESS = 'GET_PRIORITIES_SUCCESS';
export const GET_PRIORITIES_FAILURE = 'GET_PRIORITIES_FAILURE';
export const UPDATE_PRIORITIES  = 'UPDATE_PRIORITIES';

export const getPrioritiesBegin = () => ({
  type: GET_PRIORITIES_BEGIN 
});

export const getPrioritiesSuccess = tableDataList => ({
  type: GET_PRIORITIES_SUCCESS,
  payload: { tableDataList }
});

export const getPrioritiesFailure = error => ({
  type: GET_PRIORITIES_FAILURE,
  payload: { error }
});
export const updatePrioritiesSuccess = tableDataList => ({
    type: UPDATE_PRIORITIES,
    payload: {tableDataList}
});

export function getPriorities(){
    return dispatch => {
        if(dataLoaded)
            return dispatch(getPrioritiesSuccess(tableDataList));
        dataLoaded=true;
        console.log("API CALLED");
        tableDataList=[]
        dispatch(getPrioritiesBegin());
        var url=API_CONTEXT_PATH+'/patientflags/priority/?v=full'; 
        console.log("Successful Entry");
        return fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(res => res.json())
        .then((data) => {
            var resultData = data['results'];
            for (var property in resultData){
                if(resultData.hasOwnProperty(property)){        
                    tableDataList.push(resultData[property]);
                }     
            }
            console.log('Table Data After Fetch',tableDataList);
            dispatch(getPrioritiesSuccess(tableDataList));
        })
        .catch(error => dispatch(getPrioritiesFailure(error)));
        // End of Service 
    };
}

export function updateTableData(newtableDataList){
    console.log('Sent Data',newtableDataList);
    tableDataList= newtableDataList;
    return dispatch => {
        dispatch(getPrioritiesSuccess(tableDataList));
    };
}

export function updatePriorities(destString,updateData){
    return dispatch =>{
        console.log(JSON.stringify(updateData));
        var url='';
        if(destString==='')
            url =API_CONTEXT_PATH+'/patientflags/priority?v=full';
        else 
            url=API_CONTEXT_PATH+'/patientflags/priority/'+encodeURI(destString)+'?v=full'; 
        console.log("Successful Entry");
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body:JSON.stringify(updateData)
        }).then(res => res.json())
        .then((data) => {
            console.log(data);
            if(data.hasOwnProperty('error')){
                dispatch(getFlagsFailure('Something Went Wrong',tableDataList));
                return;
            }
        });
            dispatch(updatePrioritiesSuccess(tableDataList));
        }
}