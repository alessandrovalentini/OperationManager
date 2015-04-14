package com.example.operationmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import entities.Contact;
import entities.Intervention;
import utils.Logger;


public class AddActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private TextView contactName, selectedDate, selectedTime;
        private ImageButton selectContact,chooseDate, chooseTime;
        private Button submit;
        private EditText editTitle, editDescription;
        private Context context;

        private String key = null;

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = 23;
        int mMinute = 59;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_add, container, false);
            context = this.getActivity();
            contactName = (TextView) rootView.findViewById(R.id.selectedContactText);
            selectedDate = (TextView) rootView.findViewById(R.id.dateAppointement);
            selectedTime = (TextView) rootView.findViewById(R.id.timeAppointement);
            editTitle = (EditText) rootView.findViewById(R.id.titleField);
            editDescription = (EditText)rootView.findViewById(R.id.descriptionField);
            selectContact = (ImageButton) rootView.findViewById(R.id.selectContactButton);
            chooseDate = (ImageButton) rootView.findViewById(R.id.chooseDate);
            chooseTime = (ImageButton) rootView.findViewById(R.id.chooseTime);
            submit = (Button) rootView.findViewById(R.id.submitNewIntervention);

            selectContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI); //select
                    //Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI); //add new
                    startActivityForResult(intent, 1);
                }
            });

            //pick date
            final DatePickerDialog dpd = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    updateDate(year, monthOfYear, dayOfMonth);
                }
            }, mYear, mMonth, mDay);

            chooseDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) { dpd.show();}
            });

            //pick time
            final TimePickerDialog tpd = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    updateTime(hourOfDay, minute);
                }
            }, mHour, mMinute, false);

            chooseTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {tpd.show();}
            });

            //add new intervention
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    String title = "";
                    String description = "";

                    title = editTitle.getText().toString();
                    description = editDescription.getText().toString();

                    Logger.debug(this, "submit button pressed");

                    if(key != null && title != ""){
                        //String newDate = mDay + "-" + (mMonth + 1) + "-" + mYear +" "+ mHour + ":" + mMinute;
                        //Logger.debug(this, "new date: "+newDate);
                        Date deadline = extractDate(mYear, mMonth, mDay, mHour, mMinute);
                        Logger.debug(this, "Deadline: "+deadline);
                        Logger.debug(this, "selecting contact");
                        Contact c = new Contact(context, key);
                        Logger.debug(this, "adding new intervention");
                        Intervention i = new Intervention(context, title, description, c, deadline);
                        Logger.debug(this, "internvetion added, i.getTitle="+i.getTitle());
                        i.add();

                        editTitle.setText("");
                        editDescription.setText("");
                        contactName.setText(R.string.selectContact);
                        selectedDate.setText(R.string.dateFormat);
                        selectedTime.setText(R.string.dateFormat);

                        //Return to MainActivity
                        Intent intent;
                        intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Logger.warn(this, "Date submitted but contactID or Title missing");
                        errorDialog();
                    }
                }
            });

            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (data != null){
                Uri contact = data.getData();

                if (contact != null)
                    Logger.debug(this, "Contact = "+contact );
                else
                    Logger.debug(this, "Contact is null!");

                ContentResolver resolvr = context.getContentResolver();
                Cursor c = resolvr.query(contact, null, null, null, null);

                Logger.warn(this, "Retrieving contact from list, count is " + c.getCount());
                while(c.moveToNext()){
                    key = c.getString(c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

                    Contact cnt = new Contact(context, key);
                    //contactName.setText(cnt.getName() + " Phone:" + cnt.getPhone() + "Address:" + cnt.getAddress());
                    contactName.setText(cnt.getName());
                }
            }
        }

        private void updateDate(int year, int month, int day){
            mDay = day;
            mMonth = month;
            mYear = year;

            String newDate = mDay + "-" + (mMonth + 1) + "-" + mYear;
            selectedDate.setText(newDate);
            Logger.debug(this,"Dater updated: "+newDate);
        }

        private void updateTime(int hour, int minute){
            mMinute = minute;
            mHour = hour;

            String newTime = mHour + ":" + mMinute;
            selectedTime.setText(newTime);
            Logger.debug(this,"Time updated: "+newTime);
        }

        public Date extractDate(int year, int month, int day, int hour, int minute){

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH,day);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.YEAR, year );
            cal.set(Calendar.HOUR_OF_DAY, hour); //24h
            cal.set(Calendar.MINUTE, minute);

            return new Date(cal.getTimeInMillis());
        }

        private void errorDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setMessage(R.string.dataMissing).setTitle(R.string.error).setPositiveButton(
                    "Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
