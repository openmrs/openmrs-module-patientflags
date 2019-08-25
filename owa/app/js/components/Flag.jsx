import React, { Component } from 'react';
import ReactTable from 'react-table';
import 'react-table/react-table.css';
import Popup from 'reactjs-popup';
import EditFlags from './Modals/EditFlags';
import {connect} from 'react-redux';
import {getTags} from '../actions/tagActions';
import {getFlags,updateTableData} from '../actions/flagActions';
import {getSession} from '../actions/sessionActions';

class Flag extends Component{
    listLoaded=false;
    cols=[{Header:'Name',accessor:'name',Cell: row => <div style={{ 'text-align':'center' }}>{row.value}</div>},{Header:'Tags', accessor:'tagList',Cell: row => <div>{row.value.toString()}</div>},{Header:'Priority', accessor:'priority'},{Header:'Status', accessor:'enabled'},{Header:'Actions',accessor:'deleteFlag',Cell: row => <div style={{ 'margin': "0 auto", 'padding': "10px" }}>{row.value}</div>}]
    
    state={
        testVar:'test',
        tableData:[{name:'Flag A',message:'message',priority:'priority'}
        ],
        tags:[],
        selectedOptions:[],
        filterData:[],
        tableDataList:[],
        isLoading: true,
        message:'',
        evaluator:'showall',
        visibilityModal:{
            display:'none'
        }
    };

    componentDidMount(){
        this.props.dispatch(getSession());
        this.props.dispatch(getFlags());
        this.setState({
            tableDataList:this.props.tableDataList,
            filterData: this.props.tableDataList
        });
        this.props.dispatch(getTags());
        this.setState({
            tags:[...this.state.tags,this.props.tagList]
        })
    }
    componentDidUpdate(prevProps){
        if(prevProps.tableDataList!== this.props.tableDataList){
            
            var index=0;
            this.setState({
                tableDataList:this.props.tableDataList
            })
            var localProp = Object.create(this.props.tableDataList);
            for (var property in localProp){
                localProp[property]['deleteFlag']=this.buttonGenerator(++index,localProp[property]);
                localProp[property]['tagList'] = this.tagListGenerator(localProp[property].tags);
            }
            this.setState({
                filterData: localProp
            });
        }
        if(!this.listLoaded || prevProps.tagList!==this.props.tagList){
            this.setState({
                tags:this.props.tagList,
            });
            this.listLoaded=true;
        }
    }
    
    getData(){
        
    }
    deleteFlag(rowIndex){
        
        let tableData=this.state.tableDataList;
        
        //Delete Flag Service 
        this.props.dispatch(deleteTag(tableData[rowIndex].name));
        //End of Service 
        tableData.splice(rowIndex,1);
        this.setState({
            tableDataList: [...this.state.tableDataList, tableData]
          })
    }
    buttonGenerator(index,passedData){
        return (
        <div>
            <Popup trigger={<button className="iconButton edit-action" title="Edit"><i class="icon-pencil"></i></button>} modal closeOnDocumentClick>{close=>(<EditFlags dataFromChild={passedData} callBackFromParent={this.editCallback.bind(this)} index={index} closeButton={close}/>)}</Popup>
            <button onClick={()=>this.deleteTag(index)} className="iconButton delete-action" title="Delete"><i class="icon-remove "></i></button>
        </div>
        );
    }
    tagListGenerator(passedData){
        
        var tagList=[];
        for(var property in passedData){
            
             tagList.push(passedData[property].display)
        }
        return tagList
    }

    
    updateState(){
        
        this.props.dispatch(updateTableData(this.state.tableDataList));
        
        this.setState({
            tableDataList:this.props.tableDataList
        })
    }

    handleOptionChangeTags(name, event) {
        var options = event.target.options;
        var result=[];
        for (var i = 0, l = options.length; i < l; i++) {
            if (options[i].selected) {
                var value= options[i].value;
                
                result.push(value);
              }
          }
          this.setState({
                selectedOptions:result
          })
      }
      handleEvaluatorChange(event){
        
        this.setState({
            evaluator:event.target.value
        })
      }
      handleSubmit(event){
        var filterData=[];
        var evaluatorCheck= this.state.evaluator;
        var matchArr=this.state.selectedOptions;
        var tableData= this.state.tableDataList;
        if(evaluatorCheck === 'showall'){
            this.setState({
                filterData:tableData
            })
            return;
        }
        matchArr.sort();
        var trigger=false;
        for(var property in tableData){
            trigger=false;
            var tempArr = tableData[property]['tags'];
            tempArr.sort();
            
            if(evaluatorCheck==='true'){
                if(_.isEqual(matchArr,tempArr))
                    trigger=true;
            }
            else{
                trigger = matchArr.map((d) => {
                    
                    if(tempArr.indexOf(d)!== -1){
                    
                        return true;
                    }
                    else 
                        return false;
                })
                trigger=trigger[0];
            }
            
            if(trigger)
                filterData.push(tableData[property]);

        }
        
            this.setState({
                filterData:filterData
            })
        
            event.preventDefault();
      }

    onFilteredChangeCustom = (value, accessor) => {
        if(accessor === 'tags')
            value=value[0]
        
        let filtered = this.state.filtered;
        let insertNewFilter = 1;
    
        if (filtered.length) {
            
          filtered.forEach((filter, i) => {
            if (filter["id"] === accessor) {
              if (value === "" || !value.length) filtered.splice(i, 1);
              else filter["value"] = value;
    
              insertNewFilter = 0;
            }
          });
        }
    
        if (insertNewFilter) {
          filtered.push({ id: accessor, value: value });
        }
        
        this.setState({ filtered: filtered });
      };
    
    editCallback= (dataFromChild,index) => {
        
        var localData= Object.create(dataFromChild);
        localData['enabled'] = localData.enabled ? 'Enabled' : 'Disabled';
        if(index!=null){
            this.setState({
                tableDataList: this.state.tableDataList.map(el => (el.name === localData.name ? Object.assign({}, el,  localData ) : el))
              },()=> this.updateState());
        }
        else {
            localData["deleteFlag"]=this.buttonGenerator(this.state.tableDataList.length,localData);
            this.setState({
                tableDataList: [...this.state.tableDataList, localData]
              }, ()=> this.updateState());    
        }
    }
        render(){
            const optionItemsTags = this.state.tags.map((d) => {
                 return  <option key={d.uuid} value={d.display}>{d.display}</option>
        });
            return (
            <div className="tab-div">
                <h2>Manage Flags</h2>
                <Popup trigger={<button className="button confirm"> Add a Flag </button>} modal closeOnDocumentClick className="dialog">
                    {close => (
                    <div>
                        <EditFlags callBackFromParent={this.editCallback.bind(this)} index={null} closeButton={close}/>
                    </div>
                    )}
                </Popup>
                
            <form className="container" onSubmit={this.handleSubmit.bind(this)} style={{'overflow-y':'hidden', 'height':'auto','border':'0.5px solid #D3D3D3','display':'flex','flex-flow':'row wrap','align-items':'center','margin-top':'10px','margin-bottom':'10px','padding':'10px'}}> 
                <h4><i className="icon-search"></i>&nbsp;Filter By Tags</h4>
                <div className="form-group" style={{'margin-left':'10%'}}>
                    <select multiple className="form-control"  onChange = {this.handleOptionChangeTags.bind(this,'tags')} >
                        {optionItemsTags}
                    </select>
                </div>
                <div className="form-group">
                <input type="radio" className="form-control" id="flg" onChange = {this.handleEvaluatorChange.bind(this)} value={'showall'} checked={this.state.evaluator === 'showall'} /> show all flags<br/>
                        <input type="radio" className="form-control" id="flg" onChange = {this.handleEvaluatorChange.bind(this)} value={false} checked={this.state.evaluator === 'false'} /> flags that contain any selected tags <br/>
                        <input type="radio" className="form-control" id="flg" onChange = {this.handleEvaluatorChange.bind(this)} value={true} checked={this.state.evaluator === 'true'}/> flags that contain all selected tags <br/>
                </div>
                <input type="submit" value="Filter" className="button" style={{'margin-left':'10%'}}/>
              </form>

                <ReactTable  className="displayTable" style={{'margin-top':'5px'}} columns={this.cols} data={this.state.filterData} defaultPageSize='5'/>
            </div>
            );
        }
    }
    const mapStateToProps = state => ({
        tableDataList: state.flags.tableDataList,
        loading: state.flags.loading,
        error: state.flags.error,
        tagList: state.tags.tableDataList,
        session: state.openmrs.session
      });
      
      export default connect(mapStateToProps)(Flag);