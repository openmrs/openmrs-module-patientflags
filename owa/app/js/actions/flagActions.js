import {API_CONTEXT_PATH} from '../apiContext';

var tableDataList=[]
var dataLoaded=false

export const GET_FLAGS_BEGIN   = 'GET_FLAGS_BEGIN';
export const GET_FLAGS_SUCCESS = 'GET_FLAGS_SUCCESS';
export const GET_FLAGS_FAILURE = 'GET_FLAGS_FAILURE';
export const UPDATE_FLAGS  = 'UPDATE_FLAGS';

export const getFlagsBegin = () => ({
  type: GET_FLAGS_BEGIN 
});

export const getFlagsSuccess = tableDataList => ({
  type: GET_FLAGS_SUCCESS,
  payload: { tableDataList }
});

export const getFlagsFailure = error => ({
  type: GET_FLAGS_FAILURE,
  payload: { error }
});
export const updateFlagsSuccess = tableDataList => ({
    type: UPDATE_FLAGS,
    payload: {tableDataList}
});

export function getFlags(){
    return dispatch => {
        if(dataLoaded)
            return;
        dataLoaded=true;
        var url= API_CONTEXT_PATH+'/patientflags/flag?v=full';
        var auth='Basic YWRtaW46QWRtaW4xMjM='; // TODO: pick up from user login credentials 
        console.log("Successful Entry");
        dispatch(getFlagsBegin());
        fetch(url, {
            method: 'GET',
            withCredentials: true,
            credentials: 'include',
            headers: {
                'Authorization': auth,
                'Content-Type': 'application/json'
            }
        }).then(res => res.json())
        .then((data) => {
            var resultData = data['results'];
            for (var property in resultData){
                if(resultData.hasOwnProperty(property)){
                    console.log('Property',resultData[property]);
                    //Write Tags logic here - > tags -> 0,1,2 ... -> display 
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
                        //resultData[property]["tags"]=resultData[property]["tags"]["display"];
                    tableDataList.push(resultData[property]);
                }     
            }
            dispatch(getFlagsSuccess(tableDataList));
        })
        .catch(error => dispatch(getFlagsFailure(error)));
    };
}

export function updateTableData(newtableDataList){
    console.log('Sent Data',newtableDataList);
    tableDataList= newtableDataList;
    return dispatch => {
        dispatch(getFlagsSuccess(tableDataList));
    };
}
export function deleteFlag(uuid){
    var url= API_CONTEXT_PATH+'/patientflags/flag/'+encodeURI(uuid); // TODO: pick up base URL from - Dynamic Path ${Origin}
        var auth='Basic YWRtaW46QWRtaW4xMjM='; // TODO: pick up from user login credentials 
        console.log("Successful Entry");
        fetch(url, {
            method: 'DELETE',
            withCredentials: true,
            credentials: 'include',
            headers: {
                'Authorization': auth,
                'Content-Type': 'application/json'
            }
        }).then(res => res.json())
        .then((data) => {
            console.log(JSON.stringify(data));
        })
        .catch(error => this.setState({
            isLoading: false,
            message: 'Unable to Delete Flag' + error
        }));
}
export function updateFlag(destString,updateData){
    return dispatch =>{
        console.log(updateData);
        var url='';
        if(destString==='')
            url = API_CONTEXT_PATH+'/patientflags/flag/';
        else 
            url = API_CONTEXT_PATH+'/patientflags/flag/'+encodeURI(destString); // TODO: pick up base URL from {Origin}
        console.log(url);
        var auth='Basic YWRtaW46QWRtaW4xMjM='; // TODO: pick up from user login credentials 
        console.log("Successful Entry");
        fetch(url, {
            method: 'POST',
            withCredentials: true,
            credentials: 'include',
            headers: {
                'Authorization': auth,
                'Content-Type': 'application/json'
            },
            body:JSON.stringify(updateData)
        }).then(res => res.json())
        .then((data) => {
            console.log(data);
        });
            dispatch(updateFlagsSuccess(tableDataList));
        }
}