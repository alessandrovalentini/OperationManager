package entities;


public class Customer{
	private String name;
	private String city;
	private int cap;
	private String address;
	private String phone;
	private String note;
	private Boolean deleted;
	//private Set<Machine> machineses = new HashSet<Machine>(0);
	//private Set<Intervention> interventionses = new HashSet<Intervention>(0);

	public Customer(){};

	public Customer(String name, String city, String address, String phone){
		this.name = name;
		this.city = city;
		this.address = address;
		this.phone = phone;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getCap() {
		return cap;
	}
	public void setCap(int cap) {
		this.cap = cap;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	/*public Set<Machine> getMachineses() {
		return machineses;
	}
	public void setMachineses(Set<Machine> machineses) {
		this.machineses = machineses;
	}
	public Set<Intervention> getInterventionses() {
		return interventionses;
	}
	public void setInterventionses(Set<Intervention> interventionses) {
		this.interventionses = interventionses;
	}*/


}