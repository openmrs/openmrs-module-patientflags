import React, { Component } from 'react';
import ReactTable from 'react-table';
import 'react-table/react-table.css';
import {connect} from 'react-redux';
import {getFlags} from '../actions/flagActions';
import {getFlaggedPatients} from '../actions/searchActions';

class FindFlaggedPatients extends Component{
    listLoaded=false;
    cols=[{Header:'Patient',accessor:'patient',Cell: row => <div style={{ 'text-align':'center' }}>{row.value}</div>},{Header:'Tags', accessor:'flags'},{Header:'Flag', accessor:'flag'},{Header:'Status', accessor:'enabled'}]
    
    state={
        testVar:'test',
        tableData:[{name:'Flag A',message:'message',priority:'priority'}
        ],
        searchData:[],
        flags:[],
        selectedOption:'',
        tableDataList:[],
        isLoading: true,
        message:'',
        evaluator:'showall',
        style: {visibility:'hidden'}
    };

    componentDidMount(){
        this.props.dispatch(getFlags());
        this.setState({
            flags:[...this.state.flags,this.props.flagList]
        });
    }
    componentDidUpdate(prevProps){
        if(!this.listLoaded || prevProps.flagList!==this.props.flagList){
            this.setState({
                flags:this.props.flagList,
            });
            this.listLoaded=true;
        }
    }


    handleOptionChangeFlags(name, event) {
        var result = event.target.value;
          this.setState({
                selectedOptions:result
          })
      }
      handleSubmit(event){
        this.setState((prevState) =>({
            style: Object.assign({}, prevState.style, {
              visibility:'visible'
             })
            }));
        this.props.dispatch(getFlaggedPatients(this.state.selectedOption));
        event.preventDefault();
      }

        render(){
            const optionItemsFlags = this.state.flags.map((d) => {
                 return  <option key={d.uuid} value={d.name}>{d.name}</option>
        });
            return (
            <div className="tab-div">
                <h2><i className="icon-search"></i>&nbsp;Search</h2>
            <form className="container" onSubmit={this.handleSubmit.bind(this)} style={{'overflow-y':'hidden', 'height':'auto','border':'0.5px solid #D3D3D3','display':'flex','flex-flow':'row wrap','align-items':'center','margin-top':'10px','margin-bottom':'10px','padding':'10px'}}> 
                <h4><i className="icon-user"></i>&nbsp;Find Patients By Flag</h4>
                <div className="form-group" style={{'margin-left':'10%'}}>
                    <select className="form-control"  onChange = {this.handleOptionChangeFlags.bind(this,'flags')} >
                        {optionItemsFlags}
                    </select>
                </div>
                <input type="submit" value="Search" className="button" style={{'margin-left':'10%'}}/>
              </form>
                <ReactTable  className="displayTable" style={{'margin-top':'5px'}} style={this.state.style} columns={this.cols} data={this.state.searchData} defaultPageSize='5'/>
            </div>
            );
        }
    }
    const mapStateToProps = state => ({
        loading: state.flags.loading,
        error: state.flags.error,
        flagList: state.flags.tableDataList,
        searchData: state.search.tableDataList
      });
      
      export default connect(mapStateToProps)(FindFlaggedPatients);