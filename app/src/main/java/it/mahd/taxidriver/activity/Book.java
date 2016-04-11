package it.mahd.taxidriver.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
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

import it.mahd.taxidriver.Main;
import it.mahd.taxidriver.R;
import it.mahd.taxidriver.util.Controllers;
import it.mahd.taxidriver.util.DirectionMap;
import it.mahd.taxidriver.util.ServerRequest;
import it.mahd.taxidriver.util.SocketIO;

/**
 * Created by salem on 2/13/16.
 */
public class Book extends Fragment implements LocationListener {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();
    Socket socket = SocketIO.getInstance();

    MapView mMapView;
    Service service;
    private static Dialog bookDialog;
    private static Dialog validDialog;
    private GoogleMap googleMap;
    ArrayList<LatLng> markerPoints;
    protected LocationManager locationManager;// Declaring a Location Manager
    Location location; // location
    private CameraPosition cameraPosition;
    private CameraUpdate cameraUpdate;
    private double latitude, longitude;
    private Boolean isStart = false;
    boolean isGPSEnabled = false;// flag for GPS status
    boolean isNetworkEnabled = false;// flag for network status
    boolean canGetLocation = false;// flag for GPS status
    private String tokenOfClient, fnameOfClient;
    private boolean ioBook = false;
    private boolean ioValid = false;
    private boolean isClick = false;
    private boolean isRoute = false;
    private double pcourse, ptake, preturn;
    private double originLatitude, originLongitude, desLatitude, desLongitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 3;// The minimum distance to change Updates in meters // 3 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3 * 1;// The minimum time between updates in milliseconds // 3 seconds
    private FloatingActionButton Start_btn, Pause_btn, Course_btn, Valid_btn;
    private TextView DistanceDuration_txt;

    private TextInputLayout PriceCourse_input, PriceTake_input, PriceReturn_input;
    private EditText PriceCourse_etxt, PriceTake_etxt, PriceReturn_etxt;
    private Button Send_btn;

    public Book() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.book, container, false);
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);
        socket.connect();
        ioBook = true;
        ioValid = false; isClick = false; isRoute = false;
        socket.on(conf.io_preBook, handleIncomingPreBook);//listen in book now
        socket.on(conf.io_validRoute, handleIncomingValidRoute);//listen in valid route

        mMapView = (MapView) v.findViewById(R.id.mapView);
        DistanceDuration_txt = (TextView) v.findViewById(R.id.distance_time_txt);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        markerPoints = new ArrayList<LatLng>();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        getLocation();
        if(canGetLocation()){
            latitude = getLatitude();
            longitude = getLongitude();
        }else{
            showSettingsAlert();
            latitude = 0;
            longitude = 0;
        }
        cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                if (isClick) {
                    // Already two locations
                    if (markerPoints.size() > 1) {
                        markerPoints.clear();
                        googleMap.clear();
                    }
                    // Adding new item to the ArrayList
                    markerPoints.add(point);
                    // Creating MarkerOptions & Setting the position of the marker
                    MarkerOptions options = new MarkerOptions();
                    options.position(point);
                    //For the start location, the color of marker is BLUE and for the end location, the color of marker is RED.
                    if (markerPoints.size() == 1) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Start");
                    } else if (markerPoints.size() == 2) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title("End");
                    }
                    // Add new marker to the Google Map Android API V2
                    googleMap.addMarker(options);
                    // Checks, whether start and end locations are captured
                    if (markerPoints.size() >= 2) {
                        LatLng origin = markerPoints.get(0);
                        LatLng dest = markerPoints.get(1);
                        // To Client
                        JSONObject json = new JSONObject();
                        try{
                            json.put(conf.tag_originLatitude,origin.latitude);
                            json.put(conf.tag_originLongitude, origin.longitude);
                            json.put(conf.tag_desLatitude,dest.latitude);
                            json.put(conf.tag_desLongitude, dest.longitude);
                            json.put(conf.tag_token, pref.getString(conf.tag_token, ""));
                            socket.emit(conf.io_drawRoute, json);
                        }catch(JSONException e){ }
                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);
                        // Start downloading json data from Google Directions API
                        DownloadTask downloadTask = new DownloadTask();
                        downloadTask.execute(url);
                    }
                }
            }
        });

        Start_btn = (FloatingActionButton) v.findViewById(R.id.start_btn);
        Start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = true;
                sendToServer(latitude, longitude, isStart);
            }
        });

        Pause_btn = (FloatingActionButton) v.findViewById(R.id.pause_btn);
        Pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = false;
                sendToServer(0, 0, isStart);
            }
        });

        Course_btn = (FloatingActionButton) v.findViewById(R.id.course_btn);
        Course_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = false; ioBook = false;
                isClick = true;
                sendToServer(0, 0, isStart);
                /*if (!isClick)
                    Toast.makeText(getActivity(), "Other service !!", Toast.LENGTH_SHORT).show();*/
                googleMap.clear();
            }
        });

        Valid_btn = (FloatingActionButton) v.findViewById(R.id.valid_btn);
        Valid_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //save book
                validDialog = new Dialog(getActivity(), R.style.FullHeightDialog);
                validDialog.setContentView(R.layout.book_dialog_valid);
                validDialog.setCancelable(true);

                PriceCourse_input = (TextInputLayout) validDialog.findViewById(R.id.input_priceCourse);
                PriceCourse_etxt = (EditText) validDialog.findViewById(R.id.priceCourse_etxt);
                PriceTake_input = (TextInputLayout) validDialog.findViewById(R.id.input_priceTake);
                PriceTake_etxt = (EditText) validDialog.findViewById(R.id.priceTake_etxt);
                PriceReturn_input = (TextInputLayout) validDialog.findViewById(R.id.input_priceReturn);
                PriceReturn_etxt = (EditText) validDialog.findViewById(R.id.priceReturn_etxt);
                Send_btn = (Button) validDialog.findViewById(R.id.send_btn);
                Send_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (conf.NetworkIsAvailable(getActivity())) {
                            if (!validatePriceCourse()) { return; }
                            if (!validatePriceTake()) { return; }
                            if (!validatePriceReturn()) { return; }

                            pcourse = Double.parseDouble(PriceCourse_etxt.getText().toString());
                            ptake = Double.parseDouble(PriceTake_etxt.getText().toString());
                            preturn = Double.parseDouble(PriceReturn_etxt.getText().toString());
                            if (ptake >= pcourse) {
                                if (!(preturn == ptake - pcourse)) {
                                    Toast.makeText(getActivity(), "plz verify u return " + (ptake - pcourse), Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }

                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair(conf.tag_tokenDriver, pref.getString(conf.tag_token, "")));
                            params.add(new BasicNameValuePair(conf.tag_username, pref.getString(conf.tag_fname, "") + " " + pref.getString(conf.tag_lname, "")));
                            params.add(new BasicNameValuePair(conf.tag_tokenClient, tokenOfClient));
                            params.add(new BasicNameValuePair(conf.tag_fname, fnameOfClient));
                            params.add(new BasicNameValuePair(conf.tag_originLatitude, originLatitude + ""));
                            params.add(new BasicNameValuePair(conf.tag_originLongitude, originLongitude + ""));
                            params.add(new BasicNameValuePair(conf.tag_desLatitude, desLatitude + ""));
                            params.add(new BasicNameValuePair(conf.tag_desLongitude, desLongitude + ""));
                            params.add(new BasicNameValuePair(conf.tag_pcourse, pcourse + ""));
                            params.add(new BasicNameValuePair(conf.tag_ptake, ptake + ""));
                            params.add(new BasicNameValuePair(conf.tag_preturn, preturn + ""));
                            JSONObject jsonX = sr.getJSON(conf.url_addBook, params);
                            if(jsonX != null){
                                try{
                                    String jsonstr = jsonX.getString(conf.response);
                                    Toast.makeText(getActivity(), jsonstr, Toast.LENGTH_LONG).show();
                                    if(jsonX.getBoolean(conf.res)){
                                        JSONObject json = new JSONObject();
                                        try {
                                            json.put(conf.tag_id, jsonX.getString(conf.tag_id));
                                            json.put(conf.tag_pcourse, pcourse);
                                            json.put(conf.tag_ptake, ptake);
                                            json.put(conf.tag_preturn, preturn);
                                            json.put(conf.tag_token, pref.getString(conf.tag_token, ""));
                                            socket.emit(conf.io_endCourse, json);
                                            googleMap.clear();
                                            validDialog.dismiss();
                                            isStart = true; ioBook = true;
                                            ioValid = false; isClick = false; isRoute = false;
                                            sendToServer(latitude, longitude, isStart);
                                        } catch (JSONException e) {
                                        }
                                    }
                                }catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(getActivity(),R.string.serverunvalid,Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                PriceCourse_etxt.addTextChangedListener(new MyTextWatcher(PriceCourse_etxt));
                PriceTake_etxt.addTextChangedListener(new MyTextWatcher(PriceTake_etxt));
                PriceReturn_etxt.addTextChangedListener(new MyTextWatcher(PriceReturn_etxt));
                validDialog.show();
            }
        });
        return v;
    }

    private Emitter.Listener handleIncomingValidRoute = new Emitter.Listener(){
        public void call(final Object... args){
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (isRoute) {
                        JSONObject data = (JSONObject) args[0];
                        final String tokenClient;
                        final Boolean valid;
                        try {
                            valid = data.getBoolean(conf.tag_validRoute);
                            tokenClient = data.getString(conf.tag_tokenClient);
                            if (valid && tokenClient.equals(tokenOfClient)) isClick = false;
                        } catch (JSONException e) { }
                    }
                }
            });
        }
    };

    private Emitter.Listener handleIncomingPreBook = new Emitter.Listener(){
        public void call(final Object... args){
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (isStart && ioBook) {
                        //ioBook = false;
                        JSONObject data = (JSONObject) args[0];
                        final Double lat, lon;
                        final String tokenD, tokenC, fname;
                        try {
                            lat = data.getDouble(conf.tag_latitude);
                            lon = data.getDouble(conf.tag_longitude);
                            tokenD = data.getString(conf.tag_tokenDriver);
                            tokenC = data.getString(conf.tag_tokenClient);
                            fname = data.getString(conf.tag_fname);
                            if (tokenD.equals(pref.getString(conf.tag_token, ""))) {
                                bookDialog = new Dialog(getActivity(), R.style.FullHeightDialog);
                                bookDialog.setContentView(R.layout.book_dialog);
                                bookDialog.setCancelable(true);
                                TextView Username_txt;
                                Button Book_btn, Cancel_btn;
                                Username_txt = (TextView) bookDialog.findViewById(R.id.username_txt);
                                Username_txt.setText(fname);
                                Book_btn = (Button) bookDialog.findViewById(R.id.book_btn);
                                Cancel_btn = (Button) bookDialog.findViewById(R.id.cancel_btn);
                                bookDialog.show();
                                Cancel_btn.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        bookDialog.dismiss();
                                        ioBook = true;
                                    }
                                });
                                Book_btn.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        bookDialog.dismiss();
                                        tokenOfClient = tokenC;
                                        fnameOfClient = fname;
                                        isClick = false;
                                        isStart = false;
                                        isRoute = true;
                                        sendToServer(0, 0, isStart);
                                        googleMap.clear();
                                        MarkerOptions options = new MarkerOptions();
                                        LatLng origin = new LatLng(latitude, longitude);
                                        LatLng dest = new LatLng(lat, lon);
                                        options.position(dest);
                                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title("Me");
                                        googleMap.addMarker(options);
                                        options.position(origin);
                                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(fname);
                                        googleMap.addMarker(options);
                                        String url = getDirectionsUrl(origin, dest);
                                        DownloadTask downloadTask = new DownloadTask();
                                        downloadTask.execute(url);

                                        JSONObject jsonx = new JSONObject();
                                        try {
                                            jsonx.put(conf.tag_latitude, latitude);
                                            jsonx.put(conf.tag_longitude, longitude);
                                            jsonx.put(conf.tag_token, pref.getString(conf.tag_token, ""));
                                            socket.emit(conf.io_validBook, jsonx);
                                            ioValid = true;
                                            isStart = false;
                                        } catch (JSONException e) {
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) { }
                    } else {
                        ioBook = true;
                    }
                }
            });
        }
    };

    private String getDirectionsUrl(LatLng origin,LatLng dest){
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        originLatitude = origin.latitude; originLongitude = origin.longitude;
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        desLatitude = dest.latitude; desLongitude = dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
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
            }
            return data;
        }
        // Executes in UI thread, after the execution of doInBackground()
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
                DirectionMap parser = new DirectionMap();
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
            String distance = "";
            String duration = "";
            if(result.size()<1){
                Toast.makeText(getActivity(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    if(j==0){	// Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
            DistanceDuration_txt.setText("Distance:"+distance + ", Duration:"+duration);
            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
        }
    }

    private void sendToServer(double lat, double lon, boolean work) {
        JSONObject json = new JSONObject();
        try{
            json.put(conf.tag_latitude,lat);
            json.put(conf.tag_longitude, lon);
            json.put(conf.tag_token, pref.getString(conf.tag_token, ""));
            json.put(conf.tag_working, work);
            socket.emit(conf.io_searchTaxi, json);
        }catch(JSONException e){ }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(service.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);// getting GPS status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);// getting network status

            if (!isGPSEnabled) {// no GPS provider is enabled
                showSettingsAlert();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {// First get location from Network Provider
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {// if GPS Enabled get lat/long using GPS Services
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    private boolean validatePriceCourse() {
        if(PriceCourse_etxt.getText().toString().trim().isEmpty()) {
            PriceCourse_input.setError(getString(R.string.priceCourse_err));
            requestFocus(PriceCourse_etxt);
            return false;
        } else {
            PriceCourse_input.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePriceTake() {
        if(PriceTake_etxt.getText().toString().trim().isEmpty()) {
            PriceTake_input.setError(getString(R.string.priceTake_err));
            requestFocus(PriceTake_etxt);
            return false;
        } else {
            PriceTake_input.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePriceReturn() {
        if(PriceReturn_etxt.getText().toString().trim().isEmpty()) {
            PriceReturn_input.setError(getString(R.string.priceReturn_err));
            requestFocus(PriceReturn_etxt);
            return false;
        } else {
            PriceReturn_input.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;
        private MyTextWatcher(View view) { this.view = view; }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.priceCourse_etxt:
                    validatePriceCourse();
                    break;
                case R.id.priceTake_etxt:
                    validatePriceTake();
                    break;
                case R.id.priceReturn_etxt:
                    validatePriceReturn();
                    break;
            }
        }
    }

    public void stopUsingGPS(){//Stop using GPS listener & Calling this function will stop using GPS in your app
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
    }

    public double getLatitude(){//Function to get latitude
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude(){//Function to get longitude
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    //Function to check GPS/wifi enabled
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert(){//Function to show settings alert dialog & On pressing Settings button will lauch Settings Options
        final AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String title = "GPS is settings";// Setting Dialog Title
        final String message = "GPS is not enabled. Do you want open GPS setting?";// Setting Dialog Message
        builder.setTitle(title).setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {// On pressing Settings button
                            public void onClick(DialogInterface d, int id) {
                                getActivity().startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {// on pressing cancel button
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();// Showing Alert Message
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        googleMap.moveCamera(cameraUpdate);
        changeLocation();
    }
    private void changeLocation() {
        if (isStart) {
            sendToServer(latitude, longitude, isStart);
        }
        if (ioValid) {
            JSONObject json = new JSONObject();
            try{
                json.put(conf.tag_latitude,latitude);
                json.put(conf.tag_longitude, longitude);
                json.put(conf.tag_token, pref.getString(conf.tag_token, ""));
                socket.emit(conf.io_postBook, json);
            }catch(JSONException e){ }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}


    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        sendToServer(latitude, longitude, false);
        ioBook = false; ioValid = false; isClick = false; isRoute = false;
        stopUsingGPS();
        socket.disconnect();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendToServer(latitude, longitude, false);
        ioBook = false; ioValid = false; isClick = false; isRoute = false;
        stopUsingGPS();
        socket.disconnect();
        mMapView.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new Home());
        ft.addToBackStack(null);
        ft.commit();
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.home));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
