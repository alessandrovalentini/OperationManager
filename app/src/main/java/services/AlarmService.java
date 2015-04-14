package services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.example.operationmanager.MainActivity;
import com.example.operationmanager.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import entities.Intervention;
import utils.Logger;

public class AlarmService extends Service {
    private Thread triggerService;
    private LocationManager lm;
    private MyLocationListener locationListener = new MyLocationListener();
    private Context context;
    String bestProvider;

    ArrayList<Intervention> allIntervnetions = new ArrayList<Intervention>();
    ArrayList<Intervention> notifiedInterventions = new ArrayList<Intervention>();

    private int minTime = 60 * 1000;// milliseconds
    private int minDistance = 1000;    // meters
    private float range = minDistance; // meters
    int period = 10000;
    int delay = 5000;

    public AlarmService() {}

    @Override
    public IBinder onBind(Intent intent) {
        Logger.warn(this, "Alarm service bounded");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.warn(this, "Alarm service started");
        int res;
        res = super.onStartCommand(intent, flags, startId);
        context = getApplicationContext();

        addLocationListener();

        periodicThread(delay, period);

        return res;
    }

    private void periodicThread(int delay, int period){
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        Logger.error(this, "Periodic thread, size: " + allIntervnetions.size());
                    }
                },
                delay,
                period
                );
    }

    class MyLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location){
            Logger.warn(this, "Device location is: " + location.getLatitude() + ", " + location.getLongitude() + "Accuracy: " + location.getAccuracy());

            refreshInterventions();
            loadPreferences();
            ArrayList<Intervention> nearInterventions = checkForProximity(location, allIntervnetions);
            notifyProximity(nearInterventions);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        public void onProviderEnabled(String s) {
            Logger.debug(this, "Location provider is enable");
        }

        @Override
        public void onProviderDisabled(String s) {
            Logger.error(this,"Location provider is disable");
        }
    }

    //reload all appointments
    private void refreshInterventions(){
        allIntervnetions.clear();
        allIntervnetions = new ArrayList<Intervention>();
        Intervention intervention = new Intervention(this);
        allIntervnetions.addAll(intervention.getSelectedOnly());
        allIntervnetions.addAll(intervention.getNotSelectedOnly());

        Logger.warn(this,"Refreshing: " + allIntervnetions.size() + " interventions retrieved");
    }

    //look for an appointment inside the range, if any
    private ArrayList<Intervention> checkForProximity(Location currentLocation, ArrayList<Intervention> ls){
        Logger.warn(this,"Checking "+ ls.size()+" interventions for proximity");
        ArrayList<Intervention> nearInterventions = new ArrayList<Intervention>();

        for(Intervention i : ls){
            if (i.getContact().hasAddress()) {
                LatLng coords = getLocationFromAddress(i.getContact().getAddress());
                Location destLocation = new Location(bestProvider);
                destLocation.setLatitude(coords.latitude);
                destLocation.setLongitude(coords.longitude);


                Logger.warn(this,
                        "Get coordinates intervention for " + i.getContact().getAddress()
                        + "Distance is " + currentLocation.distanceTo(destLocation)
                        + "Logitude: " + coords.longitude + " Latitude: " + coords.latitude
                );

                if (currentLocation.distanceTo(destLocation) < range)
                    nearInterventions.add(i);
            }
        }

        return nearInterventions;
    }

    /*private ArrayList<Intervention> checkForDeadline(Location currentLocation, ArrayList<Intervention> ls){
        Logger.warn(this,"Checking "+ ls.size()+" interventions for proximity");
        ArrayList<Intervention> dueInterventions = new ArrayList<Intervention>();
        Date now = new Date();

        for(Intervention i : ls){
            if (minutesLeft(i.getDeadline()) < interval) {
                dueInterventions.add(i);
            }
        }

        return dueInterventions;
    }*/


    private void addLocationListener(){
        triggerService = new Thread(new Runnable(){
            public void run(){
                try{
                    Looper.prepare();
                    lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    locationListener = new MyLocationListener();

                    Criteria c = new Criteria();
                    c.setAccuracy(Criteria.ACCURACY_COARSE);
                    bestProvider = lm.getBestProvider(c, true);

                    lm.requestLocationUpdates(bestProvider, minTime, minDistance, locationListener);
                    Looper.loop();
                }
                catch(Exception ex){
                    Logger.error(this, "EXCEPTION " + ex.getMessage());
                }
            }
        }, "LocationThread");
        triggerService.start();
    }


    public LatLng getLocationFromAddress(String address){
        List<Address> addresses;
        double latitude;
        double longitude;
        Geocoder gc = new Geocoder(context);

        try {
            addresses = gc.getFromLocationName(address, 1);
        } catch (IOException e) {
            Logger.error(this, "EXCEPTION " + e.getLocalizedMessage());
            return null;
        }

        if(addresses.size() > 0) {
            latitude= addresses.get(0).getLatitude();
            longitude= addresses.get(0).getLongitude();
            return new LatLng(latitude, longitude);
        }
        else{
            Logger.warn(this,"Failed to retrieving position, returning null");
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void notifyProximity(ArrayList<Intervention> ls ) {
        Logger.warn(this,"notify proximity for " + ls.size() + " elements");

        if (ls.size() > 0) {
            for (Intervention i : ls) {
                Logger.debug(this, "Near :" + i.getContact().getAddress());
                Intent intent = new Intent(this, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

                Notification n = new Notification.Builder(this)
                        .setContentTitle(i.getTitle())
                        .setContentText(i.getContact().getAddress())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(pIntent)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setAutoCancel(true)
                        .build();

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(i.getId().intValue(), n); //don't notify two time the same event if not cancelled

            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void notifyDeadline(ArrayList<Intervention> ls ) {
        Logger.warn(this,"notify proximity");

        if (ls.size() > 0)
            for (Intervention i : ls) {
                Logger.debug(this, "Near :" + i.getContact().getAddress());
                Intent intent = new Intent(this, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

                Notification n = new Notification.Builder(this)
                        .setContentTitle(i.getTitle())
                        .setContentText(i.getContact().getAddress())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(pIntent)
                        .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI)
                        .setAutoCancel(true)
                        .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(i.getId().intValue(), n); //don't notify two time the same event if not cancelled
        }
    }


    private void loadPreferences(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String rangeString = sharedPref.getString("alarm_range", "1000");

        range = Integer.parseInt(rangeString);
        minDistance = Integer.parseInt(rangeString);

        if (range > 1000)
            minTime = (minDistance / 1000) * 60 * 1000;
        else
            minTime = 60 * 1000;

        Logger.warn("this", "Alarm range set to " + range + "minTime: " + minTime + "minDistance: "+ minDistance);

    }

    private int minutesLeft(Date d){
        Calendar today = Calendar.getInstance();
        Calendar date  = Calendar.getInstance();
        date.setTime(d);


        long timediff = date.getTimeInMillis() - today.getTimeInMillis();
        final long day = 1000*60;
        long days = timediff%day;

        Integer diff = Integer.valueOf(""+days);

        return diff;
    }
}
