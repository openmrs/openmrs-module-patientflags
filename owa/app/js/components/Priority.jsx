import React, { Component } from 'react';
import ReactTable from 'react-table';
import 'react-table/react-table.css';
import Popup from 'reactjs-popup';
import EditPriorities from './Modals/EditPriorities';
// FontAwesome Icon Import - Import for Icon Rendering
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeartbeat } from '@fortawesome/free-solid-svg-icons';
import { faExclamation } from '@fortawesome/free-solid-svg-icons';
import { faWheelchair } from '@fortawesome/free-solid-svg-icons';
class Priority extends Component{
    cols=[{Header:'Name',accessor:'name',Cell: row => <div style={{ 'text-align':'center' }}><Popup trigger={<button className="btn btn-success"> {row.value} </button>} modal closeOnDocumentClick><a className="close">x</a><EditPriorities dataFromChild={this.state.tableDataList[row.index]} callBackFromParent={this.editCallback.bind(this)} index={row.index}/></Popup></div>},{Header:'Indicator',accessor:'style',Cell: row => <div style={{ 'margin': "0 auto", 'padding': "10px" }}>{row.value}</div>},{Header:'Rank',accessor:'rank',Cell: row => <div style={{ 'margin': "0 auto", 'padding': "10px" }}>{row.value}</div>},{Header:'Action',accessor:'deleteTag',Cell: row => <div style={{ 'margin': "0 auto", 'padding': "10px" }}>{row.value}</div>}]
    
    state={
        testVar:'test',
        tableData:[
        ],
        tableDataList:[],
        isLoading: true,
        message:'',
    };

    componentDidMount(){
        // Get Tag Details Service 
        var url='http://localhost:8081/openmrs/ws/rest/v1/patientflags/priority/?v=full'; // TODO: pick up base URL from {Origin}
        var auth='Basic YWRtaW46QWRtaW4xMjM='; // TODO: pick up from user login credentials 
        console.log("Successful Entry");
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
                    resultData[property]['style']=this.colorIndicator(resultData[property].style);
                    resultData[property]["deleteTag"]=this.buttonGenerator(this.state.tableDataList.length);
                    this.setState({
                        tableDataList: [...this.state.tableDataList, resultData[property]]
                      })
                }     
            }
            console.log(resultData['0']);
            console.log(Object.keys(resultData['0']));
            
            console.log(this.state.tableDataList);
        })
        .catch(error => this.setState({
            isLoading: false,
            message: 'Something bad happened ' + error
        }));
        console.log(this.state.message);
        // End of Service 
    }
    
    getData(){
        return this.tableDataList;
    }
    deleteTag(rowIndex){
        console.log(rowIndex);
        let tableData=this.state.tableDataList;
        console.log(tableData[rowIndex].uuid);
        //Delete Tag Service 
        var url='http://localhost:8081/openmrs/ws/rest/v1/patientflags/priority/'+tableData[rowIndex].uuid; // TODO: pick up base URL from - Dynamic Path ${Origin}
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
            message: 'Something bad happened ' + error
        }));
        console.log(this.state.message);
        //End of Service 
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
    buttonGenerator(index){
        return (<button onClick={()=>this.deleteTag(index)}>Delete</button>);
    }

    
    editCallback= (dataFromChild,index) => {
        dataFromChild['style']=this.colorIndicator(dataFromChild.style);
        if(index!=null){
            this.setState({
                tableDataList: this.state.tableDataList.map(el => (el.name === dataFromChild.name ? Object.assign({}, el,  dataFromChild ) : el))
              });
        }
        else {
            dataFromChild["deleteTag"]=this.buttonGenerator(this.state.tableDataList.length);
            this.setState({
                tableDataList: [...this.state.tableDataList, dataFromChild]
              })    
        }
    }
        render(){
            return (
            <div>
                <h2>Manage Priorities</h2>
                <Popup trigger={<button className="btn btn-success"> Add Priority </button>} modal closeOnDocumentClick>
                    <a className="close">x</a>
                    <EditPriorities callBackFromParent={this.editCallback.bind(this)} index={null}/>
                </Popup>
                <ReactTable columns={this.cols} data={this.state.tableDataList} defaultPageSize='5'/>
            </div>
            );
        }
    }
export default Priority;