import React, { Component } from 'react';
import {CirclePicker} from 'react-color';

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
          console.log(this.props.dataFromChild);
          var str = this.state.urlSuffix=this.props.dataFromChild.name;
          console.log(str);
          //REST Call 
          var url='http://localhost:8081/openmrs/ws/rest/v1/patientflags/priority/'+encodeURI(str)+'?v=full'; // TODO: pick up base URL from {Origin}
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
      postPriority(){
        console.log(JSON.stringify(this.state.editData));
        var url='http://localhost:8081/openmrs/ws/rest/v1/patientflags/priority/'+encodeURI(this.state.urlSuffix); // TODO: pick up base URL from {Origin}
        var auth='Basic YWRtaW46QWRtaW4xMjM='; // TODO: pick up from user login credentials 
        console.log("Successful Entry");
        fetch(url, {
            method: 'POST',
            withCredentials: true,
            credentials: 'include',
            headers: {
                'Authorization': auth,
                'Content-Type': 'application/json'
            },
            body:JSON.stringify(this.state.editData)
        }).then(res => res.json())
        .then((data) => {
            console.log(data);
            console.log(data['results']['uuid']);
        })
        .catch(error => this.setState({
            isLoading: false,
            message: 'Something bad happened ' + error
        }));
      }

      handleChange(name, event) {
        console.log("Event Called");
        const target = event.target;
        const value = target.value;
        //const name = target.name;
        console.log(name+" "+value);
        this.setState((prevState) =>({
            editData: Object.assign({}, prevState.editData, {
              [name]: value
             })
            }));
      }
      handleColorChange = (color) => {
        console.log(color.hex);
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
        this.props.callBackFromParent(this.state.editData,this.props.index);
        alert('A Priority name was submitted: ' + this.state.editData.name);
        event.preventDefault();
      }
      render() {
        return (
            <div>
                <h3>Edit Priority</h3>
                <br/><br/>
              <form className="container" onSubmit={this.handleSubmit} onReset={this.handleReset}> 
                <div className="form-group">
                    <label htmlFor="flg">Name:</label>
                        <input type="text" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'name')} value={this.state.editData.name}/>
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
                <input type="submit" value="Save" className="btn btn-primary"/>
                &nbsp;<input type="reset" value="Reset" className="btn btn"/>
              </form>
             </div>
            );
      }
}

export default EditPriorities;
