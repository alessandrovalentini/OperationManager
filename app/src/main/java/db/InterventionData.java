package db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utils.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import entities.Contact;
import entities.Intervention;

public class InterventionData {

	// Database fields
	private SQLiteDatabase database;
	private OperationManagerSQLiteHelper dbHelper;
	private String[] allColumns = { 
			OperationManagerSQLiteHelper.COLUMN_ID,
			OperationManagerSQLiteHelper.COLUMN_TITLE,
			OperationManagerSQLiteHelper.COLUMN_DESCRIPTION,
			OperationManagerSQLiteHelper.COLUMN_DEADLINE,
			OperationManagerSQLiteHelper.COLUMN_LOOKUP_KEY,
			OperationManagerSQLiteHelper.COLUMN_SELECTED
	};
	Context context;

	public InterventionData(Context context) {
		dbHelper = new OperationManagerSQLiteHelper(context);
		this.context = context;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Intervention createIntervention(String title, String description, Date deadline, String lookupKey, boolean selected) {
		
		//Create values
		Logger.debug(this, "creating values");
		ContentValues values = new ContentValues();
		values.put(OperationManagerSQLiteHelper.COLUMN_TITLE, title);
		values.put(OperationManagerSQLiteHelper.COLUMN_DESCRIPTION, description);
		values.put(OperationManagerSQLiteHelper.COLUMN_DEADLINE, deadline.getTime());
		values.put(OperationManagerSQLiteHelper.COLUMN_LOOKUP_KEY, lookupKey);
		if (selected)
			values.put(OperationManagerSQLiteHelper.COLUMN_SELECTED, 1);
		else
			values.put(OperationManagerSQLiteHelper.COLUMN_SELECTED, 0);
		
		//Insert and retrieve db ID
		open();
		Logger.debug(this, "inserting intervention");
		long insertId = database.insert(OperationManagerSQLiteHelper.TABLE_INTERVENTIONS, null, values);
		Cursor cursor = database.query(OperationManagerSQLiteHelper.TABLE_INTERVENTIONS,
				allColumns, OperationManagerSQLiteHelper.COLUMN_ID + " = " + insertId, 
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
		}
		//retrieve value
		//Logger.debug(this, "Intervention Added");
		Intervention newIntervention = cursorToIntervention(cursor);
		//Logger.warn(this, "Title: "+newIntervention.getTitle());
		//Logger.warn(this, "Description: "+newIntervention.getDescription());
		//Logger.warn(this, "Deadline: "+newIntervention.getDeadline());
		//Logger.warn(this, "Selected: "+newIntervention.isSelected());
		cursor.close();
		//Logger.debug(this, "returning");
		close();
		return newIntervention;
	}

	public void deleteIntervention(Intervention intervention) {
		long id = intervention.getId();
		System.out.println("Intervention deleted with id: " + id);
		open();
		database.delete(OperationManagerSQLiteHelper.TABLE_INTERVENTIONS, OperationManagerSQLiteHelper.COLUMN_ID+ " = " + id, null);
		close();
	}
	
	public void editIntervention(Intervention i) {
		String table = OperationManagerSQLiteHelper.TABLE_INTERVENTIONS;
		
		ContentValues values = new ContentValues();
		values.put(OperationManagerSQLiteHelper.COLUMN_TITLE, i.getTitle());
		values.put(OperationManagerSQLiteHelper.COLUMN_DESCRIPTION, i.getDescription());
		values.put(OperationManagerSQLiteHelper.COLUMN_DEADLINE, i.getDeadline().getTime());
		values.put(OperationManagerSQLiteHelper.COLUMN_LOOKUP_KEY, i.getContact().getLookupKey());
		if (i.isSelected()){
			Logger.debug(this, "Unselecting intervention");
			values.put(OperationManagerSQLiteHelper.COLUMN_SELECTED, 1);
		}
		else{
			Logger.debug(this, "Selecting intervention");
			values.put(OperationManagerSQLiteHelper.COLUMN_SELECTED, 0);
		}
		
		Logger.debug(this, "Updating intervention "+i.getTitle());
		
		String whereClause = OperationManagerSQLiteHelper.COLUMN_ID+ " = " + i.getId();
		open();
		database.update(table, values, whereClause, null);
		close();
		Logger.debug(this, "Done ");
	}

	public List<Intervention> getAllInterventions() {
		List<Intervention> interventions = new ArrayList<Intervention>();
		Logger.debug(this, "Retrieving all interventions");
		
		open();
		Cursor cursor = database.query(OperationManagerSQLiteHelper.TABLE_INTERVENTIONS,
				allColumns, null, null, null, null, null);
		
		if (cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Intervention intervention = cursorToIntervention(cursor);
				interventions.add(intervention);
				cursor.moveToNext();
			}
		}
		// make sure to close the cursor
		cursor.close();
		close();
		return interventions;
	}
	
	public List<Intervention> getSelectedInterventions() {
		List<Intervention> interventions = new ArrayList<Intervention>();
		Logger.debug(this, "Retrieving selected interventions");
		
		String selection = OperationManagerSQLiteHelper.COLUMN_SELECTED+ " = " + 1;
		
		open();
		Cursor cursor = database.query(OperationManagerSQLiteHelper.TABLE_INTERVENTIONS,
				allColumns, selection, null, null, null, null);
		
		if (cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Intervention intervention = cursorToIntervention(cursor);
				interventions.add(intervention);
				cursor.moveToNext();
			}
		}
		// make sure to close the cursor
		cursor.close();
		close();
		return interventions;
	}
	
	public List<Intervention> getNotSelectedInterventions() {
		List<Intervention> interventions = new ArrayList<Intervention>();
		Logger.debug(this, "Retrieving not selected interventions");
		
		String selection = OperationManagerSQLiteHelper.COLUMN_SELECTED+ " = " + 0;
		
		open();
		Cursor cursor = database.query(OperationManagerSQLiteHelper.TABLE_INTERVENTIONS,
				allColumns, selection, null, null, null, null);
		
		if (cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Intervention intervention = cursorToIntervention(cursor);
				interventions.add(intervention);
				cursor.moveToNext();
			}
		}
		// make sure to close the cursor
		cursor.close();
		close();
		return interventions;
	}

	private Intervention cursorToIntervention(Cursor cursor) {
		Intervention intervention = new Intervention(context);
		Logger.debug(this, "cursorToIntervention start");
		//Load Intervention values
		intervention.setId(cursor.getLong(0));
		intervention.setTitle(cursor.getString(1));
		intervention.setDescription(cursor.getString(2));
		intervention.setDeadline(new Date(cursor.getLong(3)));
		Logger.debug(this, "cursorToIntervention retreiving contact");
		intervention.setContact(new Contact(context, (cursor.getString(4))));
		Logger.debug(this, "cursorToIntervention retreiving wheter is selected");
		switch(cursor.getInt(5)){
		case 0: intervention.setSelected(false); break;
		case 1: intervention.setSelected(true); break;
		default: Logger.error(this, "COLUMN_SELECTED contains an invalid value!"); break;
		}
		/*if (cursor.getInt(5) == 1)
			intervention.setSelected(true);
		else if (cursor.getInt(5) == 1)
			intervention.setSelected(true);*/
		
		/*Logger.warn(this, "Raw Intervention");
		Logger.warn(this, "Title: "+cursor.getString(1));
		Logger.warn(this, "Description: "+cursor.getString(2));
		Logger.warn(this, "Deadline (ms): "+cursor.getLong(3));
		Logger.warn(this, "Selected: "+cursor.getString(5));
		
		Logger.debug(this, "cursorToIntervention completed");*/
		return intervention;
	}

}
