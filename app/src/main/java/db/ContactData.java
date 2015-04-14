package db;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;

import utils.Logger;

public class ContactData{
	Context context;

	public ContactData(Context context){
		this.context = context;		
	}
	
	public String getName(String lookupKey){
        String name = retrieveContactDetail(lookupKey, Phone.DISPLAY_NAME_PRIMARY);
        if (name == null) {
            Logger.warn(this, "Contact name is null, using lookupKey "+ lookupKey);
            name = lookupKey;
        }
        else
            Logger.debug(this, "Name retrieved is "+name);

		return name;
	}
	
	public String getPhone(String lookupKey){//edited
		if (hasPhone(lookupKey)){
			String phone = retrieveContactDetail(lookupKey, Phone.NUMBER);
            return phone;
        }
        else
            return "";
	}
	
	public String getAddress(String lookupKey){	//edited
		String address = retrieveAddress(lookupKey);
		if (address == null){
			Logger.warn(this, "Address is null");
			address = "";
		}
        else {
            Logger.debug(this, "Address = " + address);
        }
        return address;
	}
	
	public boolean hasPhone(String lookupKey){
        Logger.debug(this, "Has phone");
        String hasPhone = retrieveContactDetail(lookupKey, Phone.HAS_PHONE_NUMBER);
        if (hasPhone == null)
            return false;
        else
		    return (Integer.parseInt(hasPhone) > 0);
	}
	
	private String retrieveContactDetail(String lookupKey, String selectedColumn){
		Uri uri = Phone.CONTENT_URI;
		String[] projection = null;
		String selection = Phone.LOOKUP_KEY +" = ?";
		String[] selectionArgs = {lookupKey};
		String sortOrder = null;
        String detail = null;

        Logger.debug(this,"retrieve ");
		Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor.getCount() > 0) {
            Logger.debug(this,"Cursor count > 0 ");
            cursor.moveToNext();
            detail = cursor.getString(cursor.getColumnIndex(selectedColumn));
        }
        else
            Logger.warn(this,"Cursor count = 0 ");

        return detail;
	}

	private String retrieveAddress(String lookupKey){
		Uri uri = ContactsContract.Data.CONTENT_URI;
		String[] projection = null;
		String selection = ContactsContract.Data.LOOKUP_KEY + "=? AND " + StructuredPostal.MIMETYPE + "=?";
		String[] selectionArgs = {lookupKey, StructuredPostal.CONTENT_ITEM_TYPE};
		String sortOrder = null;

		Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        Logger.debug(this,"");
		if (cursor.getCount() > 0){//edited
			cursor.moveToNext();
			String street = cursor.getString(cursor.getColumnIndex(StructuredPostal.STREET));
			return street;
		}
		else{//edited
			return null;
		}
	}
}
