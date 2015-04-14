package entities;

import java.util.ArrayList;
import java.util.Date;

import utils.Logger;
import android.content.Context;
import db.InterventionData;

public class Intervention{
    private Long id;
    private Contact contact;
    private Date deadline;
    private String title;
    private String description;
    private boolean selected;
    
	private InterventionData data;
    private Context context;
    
    public Intervention(Context context){
    	this.context = context;
    	data = new InterventionData(context);
    }
    
    public Intervention(Context context, String title, String description, Contact contact, Date deadlines){
    	this.context = context;
    	data = new InterventionData(context);
    	
    	this.contact = contact;
    	this.deadline = deadlines;
    	this.title = title;
    	this.description = description;
    	this.selected = false;
    }
    
    //only for mockup
    public Intervention(Context context, long id, String title, String description, Contact contact, Date deadlines){
    	this.context = context;
    	data = new InterventionData(context);
    	
    	this.id = id;
    	this.contact = contact;
    	this.deadline = deadlines;
    	this.title = title;
    	this.description = description;
    	this.selected = false;
    }
    
    public boolean add(){
    	Logger.debug(this, "Add: called");
    	if (validate()){
    		Logger.debug(this, "Add: validated");
    		Intervention i = data.createIntervention(title, description, deadline, contact.getLookupKey(), selected);
    		this.id = i.getId();
    		Logger.debug(this, "Add: internvention added, id="+this.id);
    		return true;
    	}
    	else{
    		Logger.debug(this, "Add: not validated");
    		return false;
    	}
	}
    
    public boolean delete(){
    	if (this.id != null){
    		data.deleteIntervention(this);
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    public boolean validate(){
    	return (
    			title != null &&
    			deadline != null &&
    			contact != null);
    }
    
    public void toogleSelection(){
    	this.selected = !this.selected;
    	data.editIntervention(this);
    }
    
    public ArrayList<Intervention> getAll(){
    	Logger.debug(this, "called getAll");
    	return (ArrayList<Intervention>) data.getAllInterventions();
    }
    
    public ArrayList<Intervention> getSelectedOnly(){
    	Logger.debug(this, "called getSelectedOnly");
    	return (ArrayList<Intervention>) data.getSelectedInterventions();
    }
    
    public ArrayList<Intervention> getNotSelectedOnly(){
    	Logger.debug(this, "called getNotSelectedOnly");
    	return (ArrayList<Intervention>) data.getNotSelectedInterventions();
    }
    
    
    //GETTER AND SETTER
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String descriprion) {
		this.description = descriprion;
	}
    public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}