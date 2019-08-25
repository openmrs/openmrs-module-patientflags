import React, { Component } from 'react';
import ReactTable from 'react-table';
import 'react-table/react-table.css';
import Popup from 'reactjs-popup';
import EditTags from './Modals/EditTags';
import {connect} from 'react-redux';
import {getTags,updateTableData,deleteTag,updateTag} from '../actions/tagActions';

class Tag extends Component{
    cols=[{Header:'Tag Name',accessor:'display',Cell: row => <div style={{ 'text-align':'center' }}>{row.value}</div>},{Header:'Actions',accessor:'deleteTag',Cell: row => <div style={{ 'margin': "0 auto", 'padding': "10px" }}>{row.value}</div>}]
    
    state={
        testVar:'test',
        tableData:[{display:'Flag A',message:'message',priority:'priority'}
        ],
        tableDataList:[],
        isLoading: true,
        message:''
    };

    componentDidMount(){
        this.props.dispatch(getTags());
        if(this.props.tableDataList.length!==0){
            var index=0;
            var tempObj= this.props.tableDataList;
            for (var property in tempObj)
                tempObj[property]['deleteTag']=this.buttonGenerator(++index,tempObj[property]);
            this.setState({
                tableDataList:tempObj
            })
        }
    }
    componentDidUpdate(prevProps){
        if(prevProps.tableDataList!== this.props.tableDataList){
            var index=0;
            var tempObj = this.props.tableDataList;
            for (var property in tempObj){
                tempObj[property]['deleteTag']=this.buttonGenerator(++index,tempObj[property]);
            }
            this.setState({
                tableDataList:tempObj
            })
        }
    }
    updateState(){
        this.props.dispatch(updateTableData(this.state.tableDataList));
        
        this.setState({
            tableDataList:this.props.tableDataList
        })
    }
    
    getData(){
        return this.tableDataList;
    }
    deleteTag(rowIndex){
        
        let tableData=this.state.tableDataList;
        
        //Delete Tag Service 
        this.props.dispatch(deleteTag(tableData[rowIndex].uuid));
        //End of Service 
        tableData.splice(rowIndex,1);
        this.setState({
            tableDataList: [...this.state.tableDataList, tableData]
          })
          this.updateState();
    }
    buttonGenerator(index,passedData){
        return (
        <div>
            <Popup trigger={<button className="iconButton edit-action"><i class="icon-pencil" title="Edit"></i></button>} modal closeOnDocumentClick>{close=>(<EditTags dataFromChild={passedData} callBackFromParent={this.editCallback.bind(this)} index={index} closeButton={close}/>)}</Popup>
            <button onClick={()=>this.deleteTag(index)} className="iconButton delete-action" title="Delete"><i class="icon-remove"></i></button>
        </div>
        );
    }

    editCallback= (dataFromChild,index) => {
        dataFromChild['display']=dataFromChild['name'];
        if(index!=null){
            this.setState({
                tableDataList: this.state.tableDataList.map(el => (el.display === dataFromChild.display ? Object.assign({}, el,  dataFromChild ) : el))
              },()=>this.updateState());
        }
        else {
            this.setState({
                tableDataList: [...this.state.tableDataList, dataFromChild]
              },()=>this.updateState());    
        }
    }
        render(){
            return (
            <div className="tab-div">
                <h2>Manage Tags</h2>
                <Popup trigger={<button className="button confirm"> Add a Tag </button>} modal closeOnDocumentClick>
                   {close=>( 
                    <EditTags callBackFromParent={this.editCallback.bind(this)} index={null} closeButton={close}/>
                   )}
                </Popup>
                <ReactTable className="displayTable" style={{'margin-top':'5px'}} columns={this.cols} data={this.state.tableDataList} defaultPageSize='5'/>
            </div>
            );
        }
    }
    const mapStateToProps = state => ({
        tableDataList: state.tags.tableDataList,
        loading: state.tags.loading,
        error: state.tags.error
      });
      
      export default connect(mapStateToProps)(Tag);