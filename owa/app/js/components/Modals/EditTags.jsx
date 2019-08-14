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
        var url=API_CONTEXT_PATH+'/role'; // TODO: pick up base URL from {Origin}
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
            console.log(data);
            var resultData = data['results'];
            for (var property in resultData){
                if(resultData.hasOwnProperty(property)){
                    this.setState({
                        roles: [...this.state.roles, resultData[property]]
                      })
                }     
            }
            console.log(resultData['0']);
            console.log(Object.keys(resultData['0']));
            
            console.log(this.state.roles);
        })
        .catch(error => this.setState({
            isLoading: false,
            message: 'Something bad happened ' + error
        }));
        /// REST Call for getting list of displayPoints
         url=API_CONTEXT_PATH+'/patientflags/displaypoint'; // TODO: pick up base URL from {Origin}
         auth='Basic YWRtaW46QWRtaW4xMjM='; // TODO: pick up from user login credentials 
        console.log("Successful Entry API Call 2");
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
          console.log(data);
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
            console.log(resultData['0']);
            console.log(Object.keys(resultData['0']));
            
            console.log(this.state.displayPoints);
        })
        .catch(error => this.setState({
            isLoading: false,
            message: 'Something bad happened ' + error
        }));
        /// REST Call for Tag data if UUID is available 
        if((this.props.dataFromChild)!= null){
          console.log("Entered");
          var str = this.state.urlSuffix=this.props.dataFromChild.display;
          //REST Call 
          var url=API_CONTEXT_PATH+'/patientflags/tag/'+encodeURI(str); // TODO: pick up base URL from {Origin}
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
              console.log('URI Data',data.name);
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
      displayPointReferenceStore(displayName){
        console.log(displayName);
        switch(displayName){
          case 'Patient Dashboard Header': return 1;
          case 'Patient Dashboard Overview': return 2;
          default: return 0;
        }
      }
      selectionMapping(data){
        console.log('Got Data',data);
        for(var property in data.roles){
          console.log('Display',data.roles[property]['display']);
          this.setState({
            selectedRoles: [...this.state.selectedRoles, data.roles[property]['display']]
          });
        }
        console.log('Selected Roles',this.state.selectedRoles);
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
        console.log("Event Called");
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        //const name = target.name;
        console.log(name+" "+value);
        this.setState((prevState) =>({
            editData: Object.assign({}, prevState.editData, {
              [name]: value
             })
            }));
      }
      handleOptionChangeRoles(name, event) {
        console.log("Event Called", name);
        var options = event.target.options;
        var result=[];
        for (var i = 0, l = options.length; i < l; i++) {
            if (options[i].selected) {
                var value= options[i].value;
                console.log(value);
                result.push(this.state.roles[value]);
              }
              console.log("EditData Log",this.state.editData); 
          }
          this.setState((prevState) =>({
            editData: Object.assign({}, prevState.editData, {
              [name]: result
             })
            }));
      }
      handleOptionChangeDisplayPoints(name, event) {
        console.log("Event Called", name);
        const target = event.target;
        var options = event.target.options;
        console.log('Resultant Array',this.state.editData.displayPoints);
        var result=[]
        for (var i = 0, l = options.length; i < l; i++) {
            if (options[i].selected) {
                console.log('Selected Option',options[i]);
                var value= options[i].value;
                console.log(value);
                result.push(this.state.displayPoints[value]);
              }
          }
          this.setState((prevState) =>({
            editData: Object.assign({}, prevState.editData, {
              [name]:result 
             })
            }));
        console.log("EditData Log",this.state.editData);
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
        this.props.callBackFromParent(this.state.editData,this.props.index);
        alert('A Tag name was submitted: ' + this.state.editData.name);
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
        return (
            <div>
                <h3>Edit Tag</h3>
              <form className="container" onSubmit={this.handleSubmit} onReset={this.handleReset}> 
                <div className="form-group">
                    <label htmlFor="flg">Tag:</label>
                        <input type="text" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'name')} value={this.state.editData.name}/>
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
                &nbsp;<input type="reset" value="Reset" className="button"/>
              </form>
             </div>
            );
      }
}

const mapStateToProps = state => ({
  loading: state.priorities.loading,
  error: state.priorities.error
});

export default connect(mapStateToProps)(EditTags);
