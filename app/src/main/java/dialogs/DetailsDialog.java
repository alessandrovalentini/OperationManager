package dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.operationmanager.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import entities.Contact;
import entities.Intervention;



public class DetailsDialog extends DialogFragment{
    ImageButton callButton, closeButton, navButton;
	TextView textIntervention;
	TextView textContact;
	TextView textDeadline;
	Intervention intervention;
	Contact contact;

	public DetailsDialog(Intervention i){
		this.intervention = i;
		this.contact = i.getContact();
	}

	@Override  
	public Dialog onCreateDialog(Bundle savedInstanceState) {  
		final Dialog dialog = new Dialog(getActivity());

		//Initialization
		dialog.setContentView(R.layout.dialog_details);
		callButton = (ImageButton) dialog.findViewById(R.id.button_call);
		closeButton = (ImageButton) dialog.findViewById(R.id.button_close);
		navButton = (ImageButton) dialog.findViewById(R.id.button_nav);
		textIntervention = (TextView) dialog.findViewById(R.id.text_interventionDescription);
		textDeadline = (TextView) dialog.findViewById(R.id.text_deadline);
		textContact = (TextView) dialog.findViewById(R.id.text_contactDetails); 
		
		//Setup
		dialog.setTitle(intervention.getTitle());
		textIntervention.setText(intervention.getDescription());
		textDeadline.setText(convertDate(intervention.getDeadline()));
		String s = contact.getName()+"\n"+contact.getAddress();
		
		textContact.setText(s);
		dialog.show();  
		
		/******************************************************************************************/
		if (contact.hasPhone()) {
            callButton.setEnabled(true);
        }
		else {
            callButton.setEnabled(false);
            callButton.setBackgroundColor(Color.GRAY);
        }
		
		if (contact.hasAddress()) {
            navButton.setEnabled(true);
        }
		else {
            navButton.setEnabled(false);
            navButton.setBackgroundColor(Color.GRAY);
        }
        /******************************************************************************************/

		callButton.setOnClickListener(new OnClickListener() {
			@Override //aggiunta
			public void onClick(View v) {
				String phone = intervention.getContact().getPhone();
				String uri = "tel:" +phone;
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse(uri));
				startActivity(intent);
			}
		});  
		
		
		closeButton.setOnClickListener(new OnClickListener() {  
			@Override  
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		

		navButton.setOnClickListener(new OnClickListener() {//edited

			public void onClick(View v) {
				String address = intervention.getContact().getAddress();
				//String uri = "google.navigation:q=" + address;
				String uri = "http://maps.google.com/maps?daddr=" + address;
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(uri));
				startActivity(intent);
			}
		});  	
		
		
		return dialog;  
	}

    private String convertDate(Date d){
        SimpleDateFormat dateFormat = new SimpleDateFormat("kk:mm - dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(d);
    }
	
}  