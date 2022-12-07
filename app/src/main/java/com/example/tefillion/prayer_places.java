package com.example.tefillion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class prayer_places extends AppCompatActivity  {
    private static final String TAG = "prayer_places";
    protected LocationManager locationManager;
    private static final int error = 9001;
    private Boolean mLocationPermissionGranted = false;
    ListView listView;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int permission_code = 1234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_places);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getLocationPermission();
        if (checkGoogleServices()){
            if (mLocationPermissionGranted){
                getDeviceLocation();
                intiView();

            }else {
               getLocationPermission();

            }
        }else {
            buttonSwitchGPS_ON();
        }

    }
    //פונקציה תחזיר TRUE אם שירותי המיקום דולקים
    public boolean isGPSProviderEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
//הפונקציה משמשת לעדכון המשתמש לגביי סטטוס ה GPS
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101){

            if (resultCode==RESULT_OK){
               Toast.makeText(this, "NOW gps in enable", Toast.LENGTH_SHORT).show();

            }
            if (resultCode==RESULT_CANCELED){
                Toast.makeText(this, "DENIED gps in enable", Toast.LENGTH_SHORT).show();

            }
            startActivity(new Intent(this, home_page.class));

        }
    }

    //תחזיר TRUE אם קיים שירותGPS  במכשיר ואם יש כרגע גישה לנתוני מיקום
    private boolean checkGoogleServices(){

        if(isServicesOK()){
            return isGPSProviderEnabled();
        }
        return false;
    }
//תחזיר TRUE אם קיים שירות GPS במכשיר
    public  boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(prayer_places.this);
        if (available == ConnectionResult.SUCCESS)
        {
            return  true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(prayer_places.this,available, error);
            dialog.show();

        }
        else{
            Toast.makeText(this,"You can't make the request",Toast.LENGTH_SHORT).show();
        }   return  false;

    }


    private void init() {

        String apiKey = getString(R.string.maps_key);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));


        autocompleteFragment.setCountry("IL");



        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                List<String> place_names = new ArrayList<>();
                List<String> addresses = new ArrayList<>();
               place_names.add(place.getName());
               Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
               try {
                    String address  = geocoder.getFromLocation(place.getLatLng().latitude,place.getLatLng().longitude, 1).get(0).getAddressLine(0);
                    addresses.add(address);

                } catch (IOException e) {
                    e.printStackTrace();
                }
               get_listView(place_names, addresses);
            }


            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


    }

//פונקציה לקבלת אישורי גישה
    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION,COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;

            } else {
                ActivityCompat.requestPermissions(this, permissions, permission_code);

            }

        } else {
            ActivityCompat.requestPermissions(this, permissions, permission_code);

        }

    }


    //פונקציה להדלקת הGPS בפלאפון
    public void buttonSwitchGPS_ON() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder();

        locationSettingsRequestBuilder.addLocationRequest(locationRequest);
        locationSettingsRequestBuilder.setAlwaysShow(true);
        SettingsClient settingsClient = LocationServices.getSettingsClient(getApplicationContext());

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build());
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {

                    LocationSettingsResponse response = task.getResult(ApiException.class);


                } catch (ApiException e) {
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED)
                    {
                        ResolvableApiException resolvableApiException =(ResolvableApiException)e;
                        try {
                            resolvableApiException.startResolutionForResult(prayer_places.this,101);

                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }
                    }
                    if (e.getStatusCode()== LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE)
                    {
                        Toast.makeText(prayer_places.this, "gps not aveliable in device", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

    }


//הפונצקיה מקבלת נתוני מיקום של מכשיר עדכניים ושולחת אותם
    private void getDeviceLocation() {

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(prayer_places.this);
        try {
            if (mLocationPermissionGranted) {
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        try {
                            if (location != null) {
                                Location current = location;
                                loadNearByPlaces(current.getLatitude(), current.getLongitude());
                            }
                        }
                        catch (SecurityException e)
                        {
                            Log.d("MapActivity", e.getMessage());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d("MapActivity", e.getMessage());
        }
    }


    //פונצקיה יוצרת בקשת HTTP לבתי כנסת ברדיוס 20 קמ ושולחת אותה לביצוע
    private void loadNearByPlaces(double latitude, double longitude) {

        String type = "synagogue";
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + latitude + "," + longitude +
                "&radius=20000" +
                "&types=" + type +
                "&sensor=true" +
                "&language=he"+
                "&key=" + getString(R.string.maps_key);

        new PlaceTask().execute(url);
    }


    private void intiView() {
  listView = findViewById(R.id.listView);
  locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
   init();

    }


//מחלקה לטובת יצירת תצוגה עבור כל פריט במערך הנתונים
    public class MyAdapter extends ArrayAdapter {
        List<String> listTitle;
        List<String> AddressList;
        Context context;


        public MyAdapter(@NonNull Context context, List<String> title, List<String> address) {
            super(context, R.layout.navigation_cards, title);
            this.listTitle = title;
            this.AddressList = address;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

          View view = LayoutInflater.from(context).inflate(R.layout.navigation_cards, parent, false);
            TextView textView = view.findViewById(R.id.textView);
            TextView address = view.findViewById(R.id.adress);
            textView.setText(listTitle.get(position));
            address.setText(AddressList.get(position));
            if (!isServicesOK()) {
                return convertView;
            }
            return view;
        }
    }

 // מחלקה לטובת הוצאה לפועל של בקשת ה HTTP
    private class PlaceTask extends AsyncTask<String, Integer, String> {
        String data = null;
        DownloadURL downloadURL = new DownloadURL();
        @Override
        protected String doInBackground(String... strings) {
            try {
                data = downloadURL.readUrl(strings[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }


        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

// מחלקה לטובת פיענוח בקשת ה HTTP למידע להצגה
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {

            DataParser dataParser = new DataParser();
            List<HashMap<String, String>> theList = null;
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                theList = dataParser.parse(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return theList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            List<String> place_names = new ArrayList<>();
            List<String> addresses = new ArrayList<>();
            for (int i = 0; i < hashMaps.size(); i++) {
                HashMap<String, String> hashMapList = hashMaps.get(i);
                String name = hashMapList.get("place_name");
                String address = hashMapList.get("vicinity");
                place_names.add(name);
                addresses.add(address);
            }
            get_listView(place_names, addresses);
        }
    }

//הפונקציה מבצעת השמה של מקומות תפילה ברשימה המוצגת למשתמש , לכל מקום תפילה היא קושרת אירוע לחיצה
    public void get_listView(List<String> place_names, List<String> addresses) {
        List<String> title = new ArrayList<>();
        List<String> address = new ArrayList<>();


        for (int i = 0; i < place_names.size(); i++) {
            title.add(place_names.get(i));
            address.add(addresses.get(i));
 }
 if (ActivityCompat.checkSelfPermission(prayer_places.this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(prayer_places.this,COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        MyAdapter myAdapter = new MyAdapter(this, title, address);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Object clickItemObj = adapterView.getAdapter().getItem(index);

                display_truck(clickItemObj, location);

            }
        });
    }
//פונצקיה לטובת הצגת מסלול בין המיקום הנוכחי של הנייד לבין המיקום שהתקבל כפרמטר

    private void display_truck(Object clickItemObj, Location location) {


        try {
           String url = "https://www.google.co.il/maps/dir/"+location.getLatitude()+","+ location.getLongitude()+"/"+clickItemObj.toString();
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
  }

