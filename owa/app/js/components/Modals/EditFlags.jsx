import React, { Component } from 'react';
import {connect} from 'react-redux';
import {updateFlag} from '../../actions/flagActions';
import { getPriorities } from '../../actions/priorityActions';
import { getTags } from '../../actions/tagActions';
import {API_CONTEXT_PATH} from '../../apiContext';


class EditFlags extends Component {  
    listLoaded=false;
    constructor(props) {
        super(props);
        this.state = {
            editData:{
                name: '',
                tags:[],
                evaluator:'',
                message:'',
                criteria:'',
                enabled:false,
                priority:''
            },
            tags:[],
            priorities:[],
            selectedTags:[],
            selectedPriority:" ",
            isUpdate:false,
            urlSuffix:'',
            evaluators:["org.openmrs.module.patientflags.evaluator.GroovyFlagEvaluator","org.openmrs.module.patientflags.evaluator.SQLFlagEvaluator","org.openmrs.module.patientflags.evaluator.CustomFlagEvaluator"]

        };
        this.handleChange = this.handleChange.bind(this);
        this.handleOptionChangePriority=this.handleOptionChangePriority.bind(this);
        this.handleOptionChangeTags=this.handleOptionChangeTags.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleReset= this.handleReset.bind(this);
      }
      componentDidMount(){
          
        /// REST Call for getting list of tag 
        this.props.dispatch(getTags());
        this.setState({
            tags:[...this.state.tags,this.props.tagList]
        });
        /// REST Call for getting list of priorities
         this.props.dispatch(getPriorities());
         this.setState({
             priorities:[...this.state.priorities,this.props.priorityList]
         });
        /// REST Call for Flag data if UUID is available 
        if((this.props.dataFromChild)!= null){
          
          var str = this.state.urlSuffix=this.props.dataFromChild.name;
          //REST Call 
          var url=API_CONTEXT_PATH+'/patientflags/flag/'+encodeURI(str); 
          
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
              message: 'Failed to Load Flag Data' + error
          })); 
          //End of Call
        }
      }
      componentDidUpdate(prevProps){
          
        if(!this.listLoaded || prevProps.tagList!==this.props.tagList || prevProps.priorityList!== this.props.priorityList){
            this.setState({
                tags:this.props.tagList,
                priorities:this.props.priorityList
            });
            this.listLoaded=true;
        }
    }
      selectionMapping(data){
        
        for(var property in data.tags){
          this.setState({
            selectedTags: [...this.state.selectedTags, data.tags[property]['name']]
          });
        }
        
        for(var property in data.priority){
          this.setState({
            selectedPriority: data.priority['name']
          });
        }
      }
      postFlag(){
        
        this.props.dispatch(updateFlag(this.state.urlSuffix,this.state.editData));
      }

      handleChange(name, event) {
        
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        //const name = target.name;
        
        this.setState((prevState) =>({
            editData: Object.assign({}, prevState.editData, {
              [name]: value
             })
            }));
      }
      handleOptionChangeTags(name, event) {
        
        var options = event.target.options;
        var result=[];
        for (var i = 0, l = options.length; i < l; i++) {
            if (options[i].selected) {
                var value= options[i].value;
                
                result.push(this.state.tags[value]);
              }
          }
          
          this.setState((prevState) =>({
            editData: Object.assign({}, prevState.editData, {
              [name]: result
             })
            }));
      }
      handleOptionChangePriority(name, event) {
       
        var value=event.target.value;
        this.setState((prevState) =>({
            editData: Object.assign({}, prevState.editData, {
              [name]: value
             })
            }));
      }
      
      handleReset(event){
        this.setState({
            editData:{
                name: '',
                tags:[],
                evaluator:'',
                message:'',
                criteria:'',
                enabled:false,
                priority:''
            }
            
        });
        event.preventDefault();
      }
    
      handleSubmit(event) {
        
        this.postFlag();
        if(this.props.error==null){
              this.props.callBackFromParent(this.state.editData,this.props.index);
              alert("Flag Saved Successfully!");
        }
        else{
              alert(this.props.error);
        }
        event.preventDefault();
      }
      render() {
        const optionItemsTags = this.state.tags.map((d,index) => {
                if(this.state.selectedTags.indexOf(d.display)!== -1){
                  return <option selected key={d.uuid} value={index}>{d.display}</option>
                }
                else 
                 return  <option key={d.uuid} value={index}>{d.display}</option>
        });
        const optionItemsPriorities = this.state.priorities.map((d,index) => {
          if(this.state.selectedPriority === d.name)
               return <option selected key={d.uuid} value={d.name}>{d.name}</option>
          else 
               return <option key={d.uuid} value={d.name}>{d.name}</option>
        });

        var nameField= null;
          if(this.props.dataFromChild!=null)
              nameField = <input readOnly type="text" className="form-control" id="flg" value={this.state.editData.name}/>
          else  
              nameField = <input type="text" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'name')} value={this.state.editData.name}/>

        return (
            <div className="Modal">
               <div className="dialog-header">
                      <i class="icon-folder-open"></i>&nbsp;<h3>Edit Flag</h3>
                </div>
              <form className="container" onSubmit={this.handleSubmit}>
                <div className="form-group">
                    <label htmlFor="flg">Name:</label>
                    {nameField}
                </div>
                <div className="form-group">
                    <label htmlFor="flg">Type:</label><br/>
                        <input type="radio" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'evaluator')} value={this.state.evaluators[0]} checked={this.state.editData.evaluator === this.state.evaluators[0]}/> Groovy Flag <br/>
                        <input type="radio" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'evaluator')} value={this.state.evaluators[1]} checked={this.state.editData.evaluator === this.state.evaluators[1]}/> SQL Flag <br/>
                        <input type="radio" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'evaluator')} value={this.state.evaluators[2]} checked={this.state.editData.evaluator === this.state.evaluators[2]}/> Custom Flag <br/>
                </div>

                <div className="form-group">
                    <label htmlFor="flg">Criteria:</label>
                        <input type="text" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'criteria')} value={this.state.editData.criteria}/>
                </div>

                <div className="form-group">
                    <label htmlFor="flg">Message:</label>
                        <input type="text" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'message')} value={this.state.editData.message}/>
                </div>

                <div className="form-group">
                <label htmlFor="uuid">Associated Tags:</label>
                  <select multiple className="form-control"  onChange = {this.handleOptionChangeTags.bind(this,'tags')} >
                    {optionItemsTags}
                </select>
             </div>

             <div className="form-group">
                <label htmlFor="uuid">Priority:</label>
                  <select className="form-control" onChange = {this.handleOptionChangePriority.bind(this,'priority')}>
                    {optionItemsPriorities}
                </select>
             </div>

             <div className="form-group">
                    <label htmlFor="flg">Enabled:</label>
                        <input type="checkbox" className="form-control" id="flg" onChange = {this.handleChange.bind(this,'enabled')} checked={this.state.editData.enabled}/>
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
    error: state.flags.error,
    tagList: state.tags.tableDataList,
    priorityList:state.priorities.tableDataList
});

export default connect(mapStateToProps)(EditFlags);
