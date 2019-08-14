import React, { Component } from 'react';
import ReactTable from 'react-table';
import 'react-table/react-table.css';
import Popup from 'reactjs-popup';
import EditFlags from './Modals/EditFlags';
import {connect} from 'react-redux';
import {getTags} from '../actions/tagActions';
import {getFlags,updateTableData} from '../actions/flagActions';

class Flag extends Component{
    listLoaded=false;
    cols=[{Header:'Name',accessor:'name',Cell: row => <div style={{ 'text-align':'center' }}>{row.value}</div>},{Header:'Tags', accessor:'tags'},{Header:'Priority', accessor:'priority'},{Header:'Status', accessor:'enabled'},{Header:'Actions',accessor:'deleteFlag',Cell: row => <div style={{ 'margin': "0 auto", 'padding': "10px" }}>{row.value}</div>}]
    
    state={
        testVar:'test',
        tableData:[{name:'Flag A',message:'message',priority:'priority'}
        ],
        tags:[],
        selectedOptions:[],
        filterData:[],
        tableDataList:[],
        isLoading: true,
        message:''
    };

    componentDidMount(){
        this.props.dispatch(getFlags());
        this.setState({
            tableDataList:this.props.tableDataList,
            filterData: this.props.tableDataList
        });
        this.props.dispatch(getTags());
        this.setState({
            tags:[...this.state.tags,this.props.tagList]
        },()=> console.log(this.state.tags));
    }
    componentDidUpdate(prevProps){
        if(prevProps.tableDataList!== this.props.tableDataList){
            var index=0;
            for (var property in this.props.tableDataList){
                this.props.tableDataList[property]['deleteFlag']=this.buttonGenerator(++index,this.props.tableDataList[property]);
                this.props.tableDataList[property]['tags'] = this.tagListGenerator(this.props.tableDataList[property].tags);
            }
            this.setState({
                tableDataList:this.props.tableDataList,
                filterData: this.props.tableDataList
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
        return this.state.tableDataList;
    }
    deleteFlag(rowIndex){
        console.log(rowIndex);
        let tableData=this.state.tableDataList;
        console.log(tableData[rowIndex].name);
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
            <Popup trigger={<button className="iconButton edit-action"><i class="icon-pencil"></i></button>} modal closeOnDocumentClick><a className="close">x</a><EditFlags dataFromChild={passedData} callBackFromParent={this.editCallback.bind(this)} index={index}/></Popup>
            <button onClick={()=>this.deleteTag(index)} className="iconButton delete-action"><i class="icon-remove "></i></button>
        </div>
        );
    }
    tagListGenerator(passedData){
        var tagList=[];
        for(var property in passedData){
            console.log('Tag Property',passedData[property]);
             tagList.push(passedData[property].display)
        }
        return tagList
    }

    
    updateState(){
        console.log(this.state.tableDataList);
        this.props.dispatch(updateTableData(this.state.tableDataList));
        console.log(this.props.tableDataList);
        this.setState({
            tableDataList:this.props.tableDataList
        })
    }

    handleOptionChangeTags(name, event) {
        console.log("Event Called", name);
        var options = event.target.options;
        var result=[];
        for (var i = 0, l = options.length; i < l; i++) {
            if (options[i].selected) {
                var value= options[i].value;
                console.log(value);
                result.push(value);
              }
          }
          this.setState({
                selectedOptions:result
          })
      }
      handleSubmit(event){
        var filterData=[];
        var matchArr=this.state.selectedOptions;
        console.log(matchArr);
        var tableData= this.state.tableDataList;
        var trigger=true;
        for(var property in tableData){
            trigger=true;
            var tempArr = tableData[property]['tags'];
            console.log(tempArr);
            for(var i=0;i<tempArr.length; i++){
                if(matchArr[i]!== tempArr[i]){
                    trigger=false;
                    break;
                }
            }
            if(trigger)
                filterData.push(tableData[property]);

        }
        if(filterData.length!==0){
            this.setState({
                filterData:filterData
            })
        }
            event.preventDefault();
      }

    onFilteredChangeCustom = (value, accessor) => {
        if(accessor === 'tags')
            value=value[0]
        console.log('value accessor pair',value,accessor);
        let filtered = this.state.filtered;
        let insertNewFilter = 1;
    
        if (filtered.length) {
            console.log("Entered");
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
        console.log('Filter Data',filtered);
        this.setState({ filtered: filtered });
      };
    
    editCallback= (dataFromChild,index) => {
        if(index!=null){
            this.setState({
                tableDataList: this.state.tableDataList.map(el => (el.name === dataFromChild.name ? Object.assign({}, el,  dataFromChild ) : el))
              },()=> this.updateState());
        }
        else {
            dataFromChild["deleteFlag"]=this.buttonGenerator(this.state.tableDataList.length,dataFromChild);
            dataFromChild['tags']=this.tagListGenerator(dataFromChild['tags']);
            this.setState({
                tableDataList: [...this.state.tableDataList, dataFromChild]
              }, ()=> this.updateState());    
        }
    }
        render(){
            const optionItemsTags = this.state.tags.map((d) => {
                 return  <option key={d.uuid} value={d.display}>{d.display}</option>
        });
            return (
            <div>
                <h2>Manage Flags</h2>
                <Popup trigger={<button className="button confirm"> Add a Flag </button>} modal closeOnDocumentClick>
                    <a className="close">x</a>
                    <EditFlags callBackFromParent={this.editCallback.bind(this)} index={null}/>
                </Popup>
                
            <form className="container" onSubmit={this.handleSubmit.bind(this)} style={{'overflow-y':'hidden', 'height':'auto'}}> 
                <div className="form-group">
                    <label htmlFor="uuid">Associated Tags:</label>
                    <select multiple className="form-control"  onChange = {this.handleOptionChangeTags.bind(this,'tags')} >
                        {optionItemsTags}
                    </select>
                </div>
                <input type="submit" value="Filter" className="button confirm"/>
              </form>

                <ReactTable  className="displayTable" style={{'margin-top':'5px'}} columns={this.cols} data={this.state.filterData} filterable filtered={this.state.filtered} 
                onFilteredChange={filtered => {this.setState({ filtered });}}
                   defaultPageSize='5'/>
            </div>
            );
        }
    }
    const mapStateToProps = state => ({
        tableDataList: state.flags.tableDataList,
        loading: state.flags.loading,
        error: state.flags.error,
        tagList: state.tags.tableDataList,
      });
      
      export default connect(mapStateToProps)(Flag);