package mainFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.operationmanager.MainActivity;
import com.example.operationmanager.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entities.Contact;
import entities.Intervention;
import utils.DeviceLocation;
import utils.JsonParser;
import utils.Logger;

public class MapsFragment extends Fragment {
    private GoogleMap map;
    Context context;
    SupportMapFragment fragment;
    MainActivity mainActivity;
    Button drawRoute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.debug(this,"Map: onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        context = this.getActivity();
        mainActivity = (MainActivity) this.getActivity();
        drawRoute = (Button) rootView.findViewById(R.id.drawRouteButton);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Logger.debug(this,"Map: onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.fragment_map);

        if (fragment == null) {
            Logger.debug(this,"Map: new Fragment committed");
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.fragment_map, fragment).commit(); //<- Slow down tab switch!
        }
    }

    @Override
    public void onResume() {
        Logger.debug(this,"Map: onResume");
        super.onResume();
        final LatLng myCoords;
        Location l;

        if (map == null)
            map = fragment.getMap();

        map.clear();

        //Add Markers
        Intervention intervention = new Intervention(context);
/**************************************************************************************************/
        final ArrayList<Intervention> selected;
        if (mainActivity.selectedInterventions == null)
            selected = intervention.getSelectedOnly();
        else
            selected = mainActivity.selectedInterventions;
/**************************************************************************************************/

        for(Intervention i : selected){
            Contact c;
            c = i.getContact();
            Logger.info(this, "Adding markers selected");
            //String address = c.getAddress();
            if (c.hasAddress()){
                MarkerOptions newMarker = new MarkerOptions()
                        .position(this.getLocationFromAddress(c.getAddress()))
                        .title(c.getName())
                        .snippet(i.getTitle());
                map.addMarker(newMarker);
            }
        }

        //Add Markers
        ArrayList<Intervention> notSelected = intervention.getNotSelectedOnly();
        for(Intervention i : notSelected){
            Contact c;
            c = i.getContact();
            Logger.info(this, "Adding markers unselected");
            //String address = c.getAddress();
            if (c.hasAddress()){
                Logger.info(this, "Adding markers at address"+c.getAddress());
                MarkerOptions newMarker = new MarkerOptions()
                        .position(this.getLocationFromAddress(c.getAddress()))
                        .title(c.getName())
                        .snippet(i.getTitle())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                map.addMarker(newMarker);
            }
        }

        //Zoom on my location
        DeviceLocation dl = new DeviceLocation(mainActivity);
        dl.execute();
        l = dl.getDeviceLocation();

        //l = getLocation();
        map.setMyLocationEnabled(true);
        if (l != null){
            Logger.debug(this,"Location retrieved is "+l.toString());
            myCoords = new LatLng(l.getLatitude(), l.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoords, mainActivity.getZoomLevel()));
        }
        else{
            Logger.warn(this,"Unable to retrieve a valid position, using: "+mainActivity.defaultLocation);
            myCoords = getLocationFromAddress(mainActivity.defaultLocation);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoords, mainActivity.getZoomLevel()));

            //DO SOMETHING IF NULL
        }

/**************************************************************************************************/
        // DRAW PATH ON MAP
        drawRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    // Checks, whether start and end locations are captured
                    ArrayList <LatLng> selectedCoords = new ArrayList<LatLng>();
                    if(selected.size() >= 1){
                        for(Intervention i : selected){
                            if(i.getContact().getAddress() != "")
                                Logger.warn(this, "No address is empty string");
                            if(i.getContact().getAddress() != null)
                                Logger.warn(this, "No address is null");

                            if (i.getContact().hasAddress())
                                selectedCoords.add(getLocationFromAddress(i.getContact().getAddress()));
                            else
                                Logger.warn(this,"This contact has no address, it will not be added to the map!");
                        }

                        // Getting URL to the Google Directions API
                        if (selected.size() >= 2) {
                            String url = getDirectionsUrl(myCoords, selectedCoords);
                            DownloadTask downloadTask = new DownloadTask();
                            downloadTask.execute(url);
                        }
                        else {
                            Logger.warn(this, "No enough point to draw a root!");

                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.warning)
                                    .setMessage(R.string.notEnoughPointsMessage)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();

                        }
                    }
                }
            }
        });
/**************************************************************************************************/
    }

    public LatLng getLocationFromAddress(String address){
        List<Address> addresses;
        double latitude;
        double longitude;
        Geocoder gc = new Geocoder(context);

        try {
            addresses = gc.getFromLocationName(address, 1);
        } catch (IOException e) {
            Log.d("WARNING", e.getLocalizedMessage());
            return null;
        }

        if(addresses.size() > 0) {
            latitude= addresses.get(0).getLatitude();
            longitude= addresses.get(0).getLongitude();
            return new LatLng(latitude, longitude);
        }
        else{
            return null;
        }
    }


    /***********************************************************************************************
     ************************************ DIRECTION API ACCESS *************************************
     **********************************************************************************************/

    private String getDirectionsUrl(LatLng origin, ArrayList<LatLng> locations){

        LatLng dest;
        dest = locations.get(locations.size()-1);
        locations.remove(locations.size()-1);

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints Mod
        String waypoints = "waypoints=";
        for(LatLng l : locations){
            waypoints += l.latitude + "," + l.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Logger.error(this, "Exception while downloading url");
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                JsonParser parser = new JsonParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }
}