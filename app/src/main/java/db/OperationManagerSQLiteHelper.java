package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/*
 * 1 Table: Interventions (customers = phonebook, no machine, no users)
 * Columns: | id | title | descriptions | deadline | lookup_key | selected |
 * 
 * */
public class OperationManagerSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_INTERVENTIONS = "interventions";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DEADLINE = "deadline";
	public static final String COLUMN_LOOKUP_KEY = "lookupkey";
	public static final String COLUMN_SELECTED = "selected";

	private static final String DATABASE_NAME = "operationManager.db";
	private static final int DATABASE_VERSION = 2; //incremented
	
	public OperationManagerSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Database creation sql statement
	private static final String DATABASE_CREATE = 
			  "create table "
			+ TABLE_INTERVENTIONS + "(" 
			+ COLUMN_ID	+ " integer primary key autoincrement, " 
			+ COLUMN_TITLE + " text not null, "
			+ COLUMN_DESCRIPTION + ","
			+ COLUMN_DEADLINE + " text not null, "
			+ COLUMN_LOOKUP_KEY + " text not null, "
			+ COLUMN_SELECTED + " INTEGER DEFAULT 0 "
			+ ");";

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(OperationManagerSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERVENTIONS);
		onCreate(db);
	}

}

/* REFERENCES
 * http://www.vogella.com/tutorials/AndroidSQLite/article.html#android_requisites
 * http://developer.android.com/training/basics/data-storage/databases.html
 * */
