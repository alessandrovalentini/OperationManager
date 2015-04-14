package entities;

import java.util.HashSet;
import java.util.Set;

public class Machine {
	private String serial;
	private Customer customers;
	private String producer;
	private String model;
	private String note;
	private Boolean dismissed;
	private Set<Intervention> interventionses = new HashSet<Intervention>(0);


	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public Customer getCustomers() {
		return customers;
	}
	public void setCustomers(Customer customers) {
		this.customers = customers;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Boolean getDismissed() {
		return dismissed;
	}
	public void setDismissed(Boolean dismissed) {
		this.dismissed = dismissed;
	}
	public Set<Intervention> getInterventionses() {
		return interventionses;
	}
	public void setInterventionses(Set<Intervention> interventionses) {
		this.interventionses = interventionses;
	}


}
