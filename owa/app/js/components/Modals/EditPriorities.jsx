import React, { Component } from 'react';
import {CirclePicker} from 'react-color';
import {connect} from 'react-redux';
import {updatePriorities} from '../../actions/priorityActions';
import {API_CONTEXT_PATH} from '../../apiContext';

class EditPriorities extends Component {  
    constructor(props) {
        super(props);
        this.state = {
            editData:{
                name: '',
                style:'',
                rank:'',
            },
            isUpdate:false,
            urlSuffix:''

        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleReset= this.handleReset.bind(this);
        this.handleColorChange=this.handleColorChange.bind(this);
      }
      componentDidMount(){
        /// REST Call for Tag data if UUID is available 
        if((this.props.dataFromChild)!= null){
          
          var str = this.state.urlSuffix=this.props.dataFromChild.name;
          
          //REST Call 
          var url=API_CONTEXT_PATH+'/patientflags/priority/'+encodeURI(str)+'?v=full'; 
          
          fetch(url, {
              method: 'GET',
              headers: {
                  'Content-Type': 'application/json'
              }
          }).then(res => res.json())
          .then((data) => {
              
              for (var property in data){
                if(data.hasOwnProperty(property)){
                  this.setState((prevState) =>({
                    editData: Object.assign({}, prevState.editData, {
                      [property]:data[property]
                     })
                    }));
                }     
            }
          })
          .catch(error => this.setState({
              isLoading: false,
              message: 'Something bad happened ' + error
          })); 
          //End of Call
        }
      }
      componentDidUpdate(){
      }
      postPriority(){
        //Simple API call with no effect on state change. State change reflected in callBack from Parent
        this.props.dispatch(updatePriorities(this.state.urlSuffix,this.state.editData));
      }

      handleChange(name, event) {
        
        const target = event.target;
        const value = target.value;
        //const name = target.name;
        
        this.setState((prevState) =>({
            editData: Object.assign({}, prevState.editData, {
              [name]: value
             })
            }));
      }
      handleColorChange = (color) => {
        
        this.setState((prevState) =>({
          editData: Object.assign({}, prevState.editData, {
            style:  color.hex
           })
          }));
      };

      handleReset(event){
        this.setState({
            editData:{
                name: '',
                style:'',
                rank:''
            }
            
        });
        event.preventDefault();
      }
    
      handleSubmit(event) {
        this.postPriority();
        if(this.props.error==null){
          this.props.callBackFromParent(this.state.editData,this.props.index);
          alert('Priority Saved Successfully!');
        }
        else{
          alert(this.props.error);
        }
        event.preventDefault();
      }
      render() {
        var nameField= null;
          if(this.props.dataFromChild!=null)
              nameField = <input readOnly type="text" className="form-control" id="flg" value={this.state.editData.name}/>
          else  
              nameField = <input type="text" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'name')} value={this.state.editData.name}/>
        return (
            <div>
                <div className="dialog-header">
                      <i class="icon-folder-open"></i>&nbsp;<h3>Edit Priorities</h3>
                </div>
              <form className="container" onSubmit={this.handleSubmit}> 
                <div className="form-group">
                    <label htmlFor="flg">Name:</label>
                        {nameField}
                </div>
                <br/><br/>
                <div className="form-group">
                    <label htmlFor="flg">Rank:</label>
                        <input type="text" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'rank')} value={this.state.editData.rank}/>
                </div>
                <br/><br/>
                <div className="form-group">
                    <label htmlFor="flg">Style:</label>
                    <CirclePicker className="stylePriority" color={this.state.editData.style} onChangeComplete={this.handleColorChange}/>
                </div>
             <br/><br/>
                <input type="submit" value="Save" className="button confirm"/>
                &nbsp;<input type="reset" value="Cancel" className="button cancel" onClick={this.props.closeButton}/>
              </form>
             </div>
            );
      }
}
const mapStateToProps = state => ({
        loading: state.priorities.loading,
        error: state.priorities.error
});

export default connect(mapStateToProps)(EditPriorities);
