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
        
        tableDataList=[]
        dispatch(getTagsBegin());
        var url=API_CONTEXT_PATH+'/patientflags/tag'; 
        
        return fetch(url, {
            method: 'GET',
            headers: {
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
    
    tableDataList= newtableDataList;
    return dispatch => {
        dispatch(getTagsSuccess(tableDataList));
    };
}
export function deleteTag(uuid){
    var url=API_CONTEXT_PATH+'/patientflags/tag/'+uuid; 
        
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
            message: 'Unable to Delete Tag' + error
        }));
}
export function updateTag(destString,updateData){
    return dispatch =>{
        
        var url='';
        if(destString==='')
            url = API_CONTEXT_PATH+'/patientflags/tag/';
        else 
            url=API_CONTEXT_PATH+'/patientflags/tag/'+encodeURI(destString);
        
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body:JSON.stringify(updateData)
        }).then(res => res.json())
        .then((data) => {
            
            if(data.hasOwnProperty('error')){
                dispatch(getFlagsFailure('Something Went Wrong',tableDataList));
                return;
            }
        });
            dispatch(updateTagsSuccess(tableDataList));
        }
}