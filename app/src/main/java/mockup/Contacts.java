package mockup;

import java.util.ArrayList;

import android.util.Log;

import entities.Contact;

public final class Contacts {
	private String[] names = {
			"VFD Ufficio Service",
			"Fabio Valentini",
			"Comune di Taio",
			"Comune di Romeno", 
			"Comune di Nanno"
			};
	
	private String[] addresses = {
			"Cles, via Dallafior 30", 
			"Tassullo, via Glavasi 32", 
			"Taio, via Simone Barbacovi 4", 
			"Romeno, Piazza Lampi 3", 
			"Nanno, Via dei Madruzzo 36"
			};
	private String[] phones = {
			"1111 111111", 
			"2222 222222", 
			"3333 333333", 
			"4444 444444", 
			"5555 555555"
			};
	
	//public static final ArrayList<Contact> contacts = new ArrayList<Contact>();
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	
	private void checkForDataMissing(){
		boolean match = (names.length == addresses.length) &&
				(addresses.length == phones.length);
		if (! match)
			Log.e("INTERVENTIONS","Some data is missing, check String Arrays!");
	}
	
	public Contacts(){
		checkForDataMissing();
		
		for (int i = 0; i < names.length; i++) {
			contacts.add(
            		new Contact("i", names[i], addresses[i], phones[i])
            		);
        }
	}
	
	public ArrayList<Contact> getCotacts(){
		return contacts;
	}
	
}
