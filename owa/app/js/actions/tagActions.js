import {API_CONTEXT_PATH} from '../apiContext';

var tableDataList=[]
var dataLoaded=false

export const GET_TAGS_BEGIN   = 'GET_TAGS_BEGIN';
export const GET_TAGS_SUCCESS = 'GET_TAGS_SUCCESS';
export const GET_TAGS_FAILURE = 'GET_TAGS_FAILURE';
export const UPDATE_TAGS  = 'UPDATE_TAGS';

export const getTagsBegin = () => ({
  type: GET_TAGS_BEGIN 
});

export const getTagsSuccess = tableDataList => ({
  type: GET_TAGS_SUCCESS,
  payload: { tableDataList }
});

export const getTagsFailure = error => ({
  type: GET_TAGS_FAILURE,
  payload: { error }
});
export const updateTagsSuccess = tableDataList => ({
    type: UPDATE_TAGS,
    payload: {tableDataList}
});

export function getTags(){
    return dispatch => {
        if(dataLoaded)
            return dispatch(getTagsSuccess(tableDataList));
        dataLoaded=true;
        console.log("API CALLED");
        tableDataList=[]
        dispatch(getTagsBegin());
        var url=API_CONTEXT_PATH+'/patientflags/tag'; // TODO: pick up base URL from {Origin}
        var auth='Basic YWRtaW46QWRtaW4xMjM='; // TODO: pick up from user login credentials 
        console.log("Successful Entry");
        return fetch(url, {
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
            var index=0;
            for (var property in resultData){
                if(resultData.hasOwnProperty(property)){       
                    tableDataList.push(resultData[property]);
                }     
            }
            dispatch(getTagsSuccess(tableDataList));
        })
        .catch(error => dispatch(getTagsFailure(error)));
        // End of Service 
    };
}

export function updateTableData(newtableDataList){
    console.log('Sent Data',newtableDataList);
    tableDataList= newtableDataList;
    return dispatch => {
        dispatch(getTagsSuccess(tableDataList));
    };
}
export function deleteTag(uuid){
    var url=API_CONTEXT_PATH+'/patientflags/tag/'+uuid; // TODO: pick up base URL from - Dynamic Path ${Origin}
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
            message: 'Unable to Delete Tag' + error
        }));
}
export function updateTag(destString,updateData){
    return dispatch =>{
        console.log(updateData);
        var url='';
        if(destString==='')
            url = API_CONTEXT_PATH+'/patientflags/tag/';
        else 
            url=API_CONTEXT_PATH+'/patientflags/tag/'+encodeURI(destString); // TODO: pick up base URL from {Origin}
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
            dispatch(updateTagsSuccess(tableDataList));
        }
}