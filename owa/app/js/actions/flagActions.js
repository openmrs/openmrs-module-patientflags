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

export const getFlagsFailure = (error, tableDataList) => ({
  type: GET_FLAGS_FAILURE,
  payload: { error, tableDataList }
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
        
        dispatch(getFlagsBegin());
        fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(res => res.json())
        .then((data) => {
            if(data.hasOwnProperty('error')){
                dispatch(getFlagsFailure('Something Went Wrong'));
                return;
            }
            var resultData = data['results'];
            for (var property in resultData){
                if(resultData.hasOwnProperty(property)){
                    
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
            dispatch(getFlagsSuccess(tableDataList));
        })
        .catch(error => dispatch(getFlagsFailure(error)));
    };
}

export function updateTableData(newtableDataList){
    
    tableDataList= newtableDataList;
    return dispatch => {
        dispatch(getFlagsSuccess(tableDataList));
    };
}
export function deleteFlag(uuid){
    var url= API_CONTEXT_PATH+'/patientflags/flag/'+encodeURI(uuid); 
        
        fetch(url, {
            method: 'DELETE',
            headers: {
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
        
        var localData= updateData;
        for (var property in localData['tags'])
            delete localData['tags'][property]['deleteTag'];
    
        
        var url='';
        if(destString==='')
            url = API_CONTEXT_PATH+'/patientflags/flag/';
        else 
            url = API_CONTEXT_PATH+'/patientflags/flag/'+encodeURI(destString); 
        
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body:JSON.stringify(localData)
        }).then(res => res.json())
        .then((data) => {
            
            if(data.hasOwnProperty('error')){
                
                dispatch(getFlagsFailure('Something Went Wrong',tableDataList));
                return;
            }
        });
            
            dispatch(updateFlagsSuccess(tableDataList));
        }
}