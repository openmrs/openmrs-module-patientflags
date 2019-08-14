import React, { Component } from 'react';
import ReactTable from 'react-table';
import 'react-table/react-table.css';
import Popup from 'reactjs-popup';
import EditPriorities from './Modals/EditPriorities';
import {connect} from 'react-redux';
import {getPriorities,updateTableData} from '../actions/priorityActions';

class Priority extends Component{
    cols=[{Header:'Name',accessor:'name',Cell: row => <div style={{ 'text-align':'center' }}>{row.value}</div>},{Header:'Indicator',accessor:'style',Cell: row => <div style={{ 'margin': "0 auto", 'padding': "10px" }}>{row.value}</div>},{Header:'Rank',accessor:'rank',Cell: row => <div style={{ 'margin': "0 auto", 'padding': "10px" }}>{row.value}</div>},{Header:'Actions',accessor:'deletePriority',Cell: row => <div style={{ 'margin': "0 auto", 'padding': "10px" }}>{row.value}</div>}]
    
    state={
        testVar:'test',
        tableData:[],
        message:'',
        tableDataList:[]
    };

    componentDidMount(){
        this.props.dispatch(getPriorities());
        if(this.props.tableDataList.length!==0){
            var index=0;
            for (var property in this.props.tableDataList){
                this.props.tableDataList[property]['style']= this.colorIndicator(this.props.tableDataList[property].style);
                this.props.tableDataList[property]['deletePriority']=this.buttonGenerator(++index,this.props.tableDataList[property]);
            }
            this.setState({
                tableDataList:this.props.tableDataList
            })
        }
    }
    componentDidUpdate(prevProps){
        if(prevProps.tableDataList!== this.props.tableDataList){
            var index=0;
            for (var property in this.props.tableDataList){
                this.props.tableDataList[property]['style']= this.colorIndicator(this.props.tableDataList[property].style);
                this.props.tableDataList[property]['deletePriority']=this.buttonGenerator(++index,this.props.tableDataList[property]);
            }
            this.setState({
                tableDataList:this.props.tableDataList
            })
        }
    }

    getData(){
        return this.props.tableDataList;
    }
    deletePriority(rowIndex){
        console.log(rowIndex);
        let tableData=this.state.tableDataList;
        let uuid=tableData[rowIndex].uuid;
        console.log(uuid);
        this.props.dispatch(deletePriority(uuid));
        tableData.splice(rowIndex,1);
        this.setState({
            tableDataList: [...this.state.tableDataList, tableData]
          })
    }
    iconGenerator(iconName){
        switch(iconName){
            case heart:;
            case exclaim:;
            case wheelChair:;
        }
    }
    colorIndicator(hexCode){
        var inlineStyle={
            'width':  '25px',
            'height': '25px',
            'margin': '5px',
            'border-radius':'50%',
            'background': hexCode  
            }
        return(<div className="coloredBox" style={inlineStyle}></div>);
    }
    buttonGenerator(index,passedData){
        return (
        <div>
            <Popup trigger={<button className="iconButton edit-action"><i class="icon-pencil"></i></button>} modal closeOnDocumentClick><a className="close">x</a><EditPriorities dataFromChild={passedData} callBackFromParent={this.editCallback.bind(this)} index={index}/></Popup>
            <button onClick={()=>this.deleteTag(index)} className="iconButton delete-action"><i class="icon-remove"></i></button>
        </div>
        );
    }

    updateState(){
        console.log(this.state.tableDataList);
        this.props.dispatch(updateTableData(this.state.tableDataList));
        console.log(this.props.tableDataList);
        this.setState({
            tableDataList:this.props.tableDataList
        })
    }
    
    editCallback= (dataFromChild,index) => {
        console.log(index,dataFromChild);
        dataFromChild['style']=this.colorIndicator(dataFromChild.style);
        if(index!=null){
            this.setState({
                tableDataList: this.state.tableDataList.map(el => (el.name === dataFromChild.name ? Object.assign({}, el,  dataFromChild ) : el))
              },()=> this.updateState());
        }
        else {
            console.log("Previous Table Data",dataFromChild,this.state.tableDataList);
            dataFromChild["deletePriority"]=this.buttonGenerator(this.state.tableDataList.length,dataFromChild);
            this.setState({
                tableDataList: [...this.state.tableDataList, dataFromChild]
              }, ()=> this.updateState());    
        }
    }
        render(){
            return (
            <div>
                <h2>Manage Priorities</h2>
                <Popup trigger={<button className="button confirm"> Add Priority </button>} modal closeOnDocumentClick>
                    <a className="close">x</a>
                    <EditPriorities callBackFromParent={this.editCallback.bind(this)} index={null}/>
                </Popup>
                <ReactTable className="displayTable" columns={this.cols} style={{'margin-top':'5px'}} data={this.state.tableDataList} defaultPageSize='5'/>
            </div>
            );
        }
    }
    const mapStateToProps = state => ({
        tableDataList: state.priorities.tableDataList,
        loading: state.priorities.loading,
        error: state.priorities.error
      });
      
      export default connect(mapStateToProps)(Priority);