import React, { Component } from 'react';
import {connect} from 'react-redux';
import {updateTag} from '../../actions/tagActions';
import {API_CONTEXT_PATH} from '../../apiContext';


class EditTags extends Component {  
    constructor(props) {
        super(props);
        this.state = {
            editData:{
                name: '',
                roles:[],
                displayPoints:[]
            },
            displayPoints:[],
            roles:[],
            selectedDp:[],
            selectedRoles:[],
            isUpdate:false,
            urlSuffix:''

        };
        this.handleChange = this.handleChange.bind(this);
        this.handleOptionChangeRoles=this.handleOptionChangeRoles.bind(this);
        this.handleOptionChangeDisplayPoints=this.handleOptionChangeDisplayPoints.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleReset= this.handleReset.bind(this);
      }
      componentDidMount(){
        /// REST Call for getting list of roles 
        var url=API_CONTEXT_PATH+'/role'; 
        
        fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(res => res.json())
        .then((data) => {
            
            var resultData = data['results'];
            for (var property in resultData){
                if(resultData.hasOwnProperty(property)){
                    this.setState({
                        roles: [...this.state.roles, resultData[property]]
                      })
                }     
            }
            
            
            
            
        })
        .catch(error => this.setState({
            isLoading: false,
            message: 'Something bad happened ' + error
        }));
        /// REST Call for getting list of displayPoints
         url=API_CONTEXT_PATH+'/patientflags/displaypoint'; 
        
        fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(res => res.json())
        .then((data) => {
          
            var resultData = data['results'];
            var displayPointCounter=1;
            for (var property in resultData){
                if(resultData.hasOwnProperty(property)){
                   resultData[property]['displayPointId']=displayPointCounter++;
                    this.setState({
                        displayPoints: [...this.state.displayPoints, resultData[property]]
                      })
                }     
            }
            
            
            
            
        })
        .catch(error => this.setState({
            isLoading: false,
            message: 'Something bad happened ' + error
        }));
        /// REST Call for Tag data if UUID is available 
        if((this.props.dataFromChild)!= null){
          
          var str = this.state.urlSuffix=this.props.dataFromChild.display;
          //REST Call 
          var url=API_CONTEXT_PATH+'/patientflags/tag/'+encodeURI(str); 
          
          fetch(url, {
              method: 'GET',
              headers: {
                  'Content-Type': 'application/json'
              }
          }).then(res => res.json())
          .then((data) => {
              
              this.selectionMapping(data);
              for (var property in data){
                if(data.hasOwnProperty(property)){
                  if(property==='displayPoints'){
                        for(var propertySelf in data[property]){
                        data[property][propertySelf]['displayPointId']=this.displayPointReferenceStore(data[property][propertySelf]['display']);
                        }
                  }
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
      displayPointReferenceStore(displayName){
        
        switch(displayName){
          case 'Patient Dashboard Header': return 1;
          case 'Patient Dashboard Overview': return 2;
          default: return 0;
        }
      }
      selectionMapping(data){
        
        for(var property in data.roles){
          
          this.setState({
            selectedRoles: [...this.state.selectedRoles, data.roles[property]['display']]
          });
        }
        
        for(var property in data.displayPoints){
          this.setState({
            selectedDp: [...this.state.selectedDp, data.displayPoints[property]['display']]
          });
        }
      }
      postTag(){
        this.props.dispatch(updateTag(this.state.urlSuffix,this.state.editData));
      }

      handleChange(name, event) {
        
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        
        this.setState((prevState) =>({
            editData: Object.assign({}, prevState.editData, {
              [name]: value
             })
            }));
      }
      handleOptionChangeRoles(name, event) {
        
        var options = event.target.options;
        var result=[];
        for (var i = 0, l = options.length; i < l; i++) {
            if (options[i].selected) {
                var value= options[i].value;
                
                result.push(this.state.roles[value]);
              }
               
          }
          this.setState((prevState) =>({
            editData: Object.assign({}, prevState.editData, {
              [name]: result
             })
            }));
      }
      handleOptionChangeDisplayPoints(name, event) {
        
        const target = event.target;
        var options = event.target.options;
        
        var result=[]
        for (var i = 0, l = options.length; i < l; i++) {
            if (options[i].selected) {
                
                var value= options[i].value;
                
                result.push(this.state.displayPoints[value]);
              }
          }
          this.setState((prevState) =>({
            editData: Object.assign({}, prevState.editData, {
              [name]:result 
             })
            }));
        
      }
      
      handleReset(event){
        this.setState({
            editData:{
              name: '',
              roles:[],
              displayPoints:[]
            }
            
        });
        event.preventDefault();
      }
    
      handleSubmit(event) {
        this.postTag();
        if(this.props.error==null){
            this.props.callBackFromParent(this.state.editData,this.props.index);
            alert("Tag Saved Successfully!");
        }
        else{
            alert(this.props.error);
        }
        event.preventDefault();
      }
      render() {
        const optionItemsRoles = this.state.roles.map((d,index) => {
                if(this.state.selectedRoles.indexOf(d.display)!== -1){
                  return <option selected key={d.uuid} value={index}>{d.display}</option>
                }
                else 
                 return  <option key={d.uuid} value={index}>{d.display}</option>
        });
        const optionItemsDisplayPoints = this.state.displayPoints.map((d,index) => {
          if(this.state.selectedDp.indexOf(d.display)!== -1)
               return <option selected key={d.uuid} value={index}>{d.display}</option>
          else 
               return <option key={d.uuid} value={index}>{d.display}</option>
        });
        var nameField= null;
          if(this.props.dataFromChild!=null)
              nameField = <input readOnly type="text" className="form-control" id="flg" value={this.state.editData.name}/>
          else  
              nameField = <input type="text" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'name')} value={this.state.editData.name}/>
        return (
            <div>
                <div className="dialog-header">
                      <i class="icon-folder-open"></i>&nbsp;<h3>Edit Tags</h3>
                </div>
              <form className="container" onSubmit={this.handleSubmit}> 
                <div className="form-group">
                    <label htmlFor="flg">Tag:</label>
                        {nameField}
                </div>

                <div className="form-group">
                <label htmlFor="uuid">Visible To:</label>
                  <select multiple className="form-control"  onChange = {this.handleOptionChangeRoles.bind(this,'roles')} >
                    {optionItemsRoles}
                </select>
             </div>

             <div className="form-group">
                <label htmlFor="uuid">Shown In:</label>
                  <select multiple className="form-control" onChange = {this.handleOptionChangeDisplayPoints.bind(this,'displayPoints')}>
                    {optionItemsDisplayPoints}
                </select>
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
  error: state.tags.error
});

export default connect(mapStateToProps)(EditTags);
