package entities;

import android.content.Context;

import db.ContactData;
import utils.Logger;

public class Contact {
	private String lookupKey;
	private String name;
	private String address;
	private String phone;
	
	//Methods
	public Contact(String lookupKey, String name, String address, String phone){
		this.lookupKey = lookupKey;
		this.name = name;
		this.address = address;
		this.phone = phone;
	}
	
	public Contact(Context context, String lookupKey){
		ContactData cd = new ContactData(context);
        Logger.debug(this, "lookupKey = "+lookupKey);
        this.lookupKey = lookupKey;
        Logger.debug(this, "Get Address");
		this.address = cd.getAddress(lookupKey);
		Logger.debug(this, "Get name");
		this.name = cd.getName(lookupKey);
		Logger.debug(this, "Get phone");
		this.phone = cd.getPhone(lookupKey);	
		Logger.debug(this, "Completed");
	}
	
	// Getter and setter
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLookupKey() {
		return lookupKey;
	}
	public void setLookupKey(String lookupKey) {
		this.lookupKey = lookupKey;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

    public boolean hasAddress(){
        if (address == null || address.equals(""))
            return false;
        else
            return true;
    }

    public boolean hasPhone(){
        if (phone == null || phone.equals(""))
            return false;
        else
            return true;
    }
	
	
}
