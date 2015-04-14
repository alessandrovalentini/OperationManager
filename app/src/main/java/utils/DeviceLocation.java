package utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;

public class DeviceLocation extends AsyncTask<Void, Integer, Location> {
	Location deviceLocation;
	ProgressDialog progress;
	Context context;
	
	public DeviceLocation(Context context){
		this.context = context;
        this.myLocation(); //added
	}

	@Override
	protected void onPreExecute() {
		Logger.debug(this, "onPreExecute");
		super.onPreExecute();
		progress = new ProgressDialog(context);
	}
	
	@Override
	protected void onPostExecute(Location result) {
		super.onPostExecute(result);
		deviceLocation = result;
		Logger.debug(this, "onPostExecute");
		progress.dismiss();
	}

	@Override
	protected Location doInBackground(Void... params) {
		Logger.debug(this, "Retrieved context");
		Location myLocation = null;
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		Logger.debug(this, "GPS: "+isGPSEnabled+" Network: "+isNetworkEnabled);
		
		if (!isGPSEnabled && !isNetworkEnabled) {
			Intent locationServiceIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(locationServiceIntent);
			Logger.warn(this,"Location service is disable");
		}
		else if (isGPSEnabled) {
			lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);   
			Logger.debug(this,"GPS is enable");
		}
		else if (!isGPSEnabled || myLocation == null){
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			String provider = lm.getBestProvider(criteria, true);
			myLocation = lm.getLastKnownLocation(provider);
			Logger.warn(this,"GPS is disable, use network");
		}


		if (myLocation != null)
			Logger.debug(this,"myLocation: "+myLocation.toString());
		else
			Logger.warn(this,"myLocation: null");
		
		return myLocation;
	}
	
	public Location getDeviceLocation() {
		return deviceLocation;
	}

    private void myLocation(){
        Logger.debug(this, "Retrieved context");
        Location myLocation = null;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Logger.debug(this, "GPS: "+isGPSEnabled+" Network: "+isNetworkEnabled);

        if (!isGPSEnabled && !isNetworkEnabled) {
            Intent locationServiceIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(locationServiceIntent);
            Logger.warn(this,"Location service is disable");
        }
        else if (isGPSEnabled) {
            lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Logger.debug(this,"GPS is enable");
        }
        else if (!isGPSEnabled || myLocation == null){
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            myLocation = lm.getLastKnownLocation(provider);
            Logger.warn(this,"GPS is disable, use network");
        }


        if (myLocation != null)
            Logger.debug(this,"myLocation: "+myLocation.toString());
        else
            Logger.warn(this,"myLocation: null");

        this.deviceLocation = myLocation;
    }

}
