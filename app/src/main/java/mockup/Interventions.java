package mockup;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.util.Log;
import entities.Contact;
import entities.Intervention;

public final class Interventions {
	private String[] machines = {"Computer","Fotocopiatrice","Registratore di cassa","Server"};
	private Date deadline = new Date(); //use the current date
	private String[] titles = {"Formattazione","Tamburo", "Visita Fiscale", "Upgrade RAM"};
	private String[] descriptions = {
			"Formattare il pc causa virus, eseguire il backup e reinstallare Autocad",
			"Procedere alla sostituzione del tambuto, intervento coperto da contratto",
			"Eseguire visita periodica, segnalata stampante malfunzionante",
			"Upgrade RAM aggiungendo 2x4GB DDR3, ECC, marca HP"};
	
	//public static final ArrayList<Intervention> all = new ArrayList<Intervention>();
	//public static final ArrayList<Intervention> selected = new ArrayList<Intervention>();
	private ArrayList<Intervention> all = new ArrayList<Intervention>();
	private ArrayList<Intervention> selected = new ArrayList<Intervention>();
	
	private void checkForDataMissing(){
		boolean match = (machines.length == titles.length) &&
				(titles.length == descriptions.length);
		if (! match)
			Log.e("INTERVENTIONS","Some data is missing, check String Arrays!");
	}
	
	public Interventions(Context context){
		checkForDataMissing();
		Contacts c = new Contacts();
		ArrayList<Contact> cl = c.getCotacts();
		
		for (int i = 0; i < machines.length; i++) {
            all.add(
            		//new Intervention(c.contacts.get(i), deadline, titles[i], descriptions[i])
            		new Intervention(context, titles[i], descriptions[i], cl.get(i),  deadline)
            		);
        }
		
		selected.add(
				//new Intervention(c.contacts.get(4), deadline, "BSOD", "BSOD all'avvio del pc")
				new Intervention(context, "BSOD", "BSOD all'avvio del pc", cl.get(4), deadline)
				);
	}
	
	public ArrayList<Intervention> getAll(){
		return this.all;
	}
	
	public ArrayList<Intervention> getSelected(){
		return this.selected;
	}
}
